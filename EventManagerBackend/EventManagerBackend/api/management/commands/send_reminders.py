"""
Management command to send reminder notifications to users.

This command should be run periodically (e.g., every hour via cron job)
to check for reminders that are due and send email notifications.

Usage:
    python manage.py send_reminders
"""

from django.core.management.base import BaseCommand
from django.utils import timezone
from django.core.mail import send_mail
from django.conf import settings
from datetime import timedelta, datetime

from api.models import Reminder


class Command(BaseCommand):
    help = 'Send reminder notifications for events that are due'

    def add_arguments(self, parser):
        parser.add_argument(
            '--dry-run',
            action='store_true',
            help='Show what would be sent without actually sending',
        )
        parser.add_argument(
            '--minutes-before',
            type=int,
            default=0,
            help='Send reminders X minutes before remind_at time (default: 0)',
        )

    def handle(self, *args, **options):
        dry_run = options['dry_run']
        minutes_before = options['minutes_before']
        
        # Calculate the time threshold
        # Send reminders that are due now or within the next few minutes
        now = timezone.now()
        threshold_time = now + timedelta(minutes=minutes_before)
        
        # Find reminders that are due and not yet sent
        # We check reminders where remind_at <= threshold_time
        due_reminders = Reminder.objects.filter(
            is_sent=False,
            remind_at__lte=threshold_time
        ).select_related('user', 'event')
        
        count = due_reminders.count()
        
        if count == 0:
            self.stdout.write(
                self.style.SUCCESS('No reminders to send at this time.')
            )
            return
        
        self.stdout.write(
            self.style.WARNING(f'Found {count} reminder(s) to send.')
        )
        
        sent_count = 0
        failed_count = 0
        
        for reminder in due_reminders:
            try:
                if dry_run:
                    self.stdout.write(
                        f'[DRY RUN] Would send reminder to {reminder.user.email} '
                        f'for event: {reminder.event.title}'
                    )
                else:
                    # Send email notification
                    self.send_reminder_email(reminder)
                    
                    # Mark as sent
                    reminder.is_sent = True
                    reminder.save()
                    
                    self.stdout.write(
                        self.style.SUCCESS(
                            f'✓ Sent reminder to {reminder.user.email} '
                            f'for event: {reminder.event.title}'
                        )
                    )
                    sent_count += 1
                    
            except Exception as e:
                self.stdout.write(
                    self.style.ERROR(
                        f'✗ Failed to send reminder to {reminder.user.email} '
                        f'for event: {reminder.event.title}: {str(e)}'
                    )
                )
                failed_count += 1
        
        if not dry_run:
            self.stdout.write(
                self.style.SUCCESS(
                    f'\nSummary: {sent_count} sent, {failed_count} failed'
                )
            )

    def send_reminder_email(self, reminder):
        """Send reminder email to user"""
        user = reminder.user
        event = reminder.event
        
        # Format event date and time
        event_date = event.date.strftime('%B %d, %Y')  # e.g., "January 15, 2024"
        event_time = event.time.strftime('%I:%M %p')  # e.g., "02:30 PM"
        event_datetime = f"{event_date} at {event_time}"
        
        # Calculate time until event
        from datetime import timedelta, timezone as dt_timezone
        
        naive_dt = datetime.combine(event.date, event.time)
        # Use fixed UTC+5 offset for Pakistan (no DST)
        pakistan_tz = dt_timezone(timedelta(hours=5))
        event_datetime_obj = naive_dt.replace(tzinfo=pakistan_tz).astimezone(dt_timezone.utc)
        time_until = event_datetime_obj - timezone.now()
        
        if time_until.days > 0:
            time_message = f"{time_until.days} day(s) from now"
        elif time_until.seconds >= 3600:
            hours = time_until.seconds // 3600
            time_message = f"{hours} hour(s) from now"
        else:
            minutes = time_until.seconds // 60
            time_message = f"{minutes} minute(s) from now"
        
        subject = f'Reminder: {event.title} - Event Manager'
        
        message = f"""
Hello {user.name},

This is a reminder about your upcoming event:

Event: {event.title}
Date & Time: {event_datetime}
Location: {event.location}
Category: {event.get_category_display()}

The event is {time_message}.

Description:
{event.description}

We look forward to seeing you there!

Best regards,
Event Manager Team
        """.strip()
        
        send_mail(
            subject=subject,
            message=message,
            from_email=settings.DEFAULT_FROM_EMAIL,
            recipient_list=[user.email],
            fail_silently=False,
        )

