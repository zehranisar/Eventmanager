from django.apps import AppConfig
import threading
import time
from datetime import timedelta


class ReminderService:
    """Background service to automatically send reminder emails"""
    
    def __init__(self):
        self.running = False
        self.thread = None
        self.check_interval = 60 * 5  # Check every 5 minutes
    
    def start(self):
        """Start the reminder service"""
        if self.running:
            return
        
        self.running = True
        self.thread = threading.Thread(target=self._run, daemon=True)
        self.thread.start()
        print("✓ Reminder service started - checking every 5 minutes")
    
    def stop(self):
        """Stop the reminder service"""
        self.running = False
        if self.thread:
            self.thread.join(timeout=5)
        print("Reminder service stopped")
    
    def _run(self):
        """Main loop to check and send reminders"""
        while self.running:
            try:
                self._check_and_send_reminders()
            except Exception as e:
                print(f"Error in reminder service: {e}")
            
            # Sleep for the check interval
            time.sleep(self.check_interval)
    
    def _check_and_send_reminders(self):
        """Check for due reminders and send emails"""
        from django.utils import timezone
        from django.core.mail import send_mail
        from django.conf import settings
        from datetime import datetime
        from api.models import Reminder
        
        # Find reminders that are due and not yet sent
        now = timezone.now()
        due_reminders = Reminder.objects.filter(
            is_sent=False,
            remind_at__lte=now
        ).select_related('user', 'event')
        
        count = due_reminders.count()
        if count == 0:
            return
        
        print(f"Found {count} reminder(s) to send")
        
        sent_count = 0
        for reminder in due_reminders:
            try:
                # Send email notification
                self._send_reminder_email(reminder, settings)
                
                # Mark as sent
                reminder.is_sent = True
                reminder.save()
                
                print(f"✓ Sent reminder to {reminder.user.email} for event: {reminder.event.title}")
                sent_count += 1
                
            except Exception as e:
                print(f"✗ Failed to send reminder to {reminder.user.email}: {str(e)}")
        
        if sent_count > 0:
            print(f"Successfully sent {sent_count} reminder(s)")
    
    def _send_reminder_email(self, reminder, settings):
        """Send reminder email to user"""
        from django.utils import timezone
        from datetime import datetime
        
        user = reminder.user
        event = reminder.event
        
        # Format event date and time
        event_date = event.date.strftime('%B %d, %Y')
        event_time = event.time.strftime('%I:%M %p')
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


# Global reminder service instance
reminder_service = ReminderService()


class ApiConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'api'
    
    def ready(self):
        """Start the reminder service when Django is ready"""
        # Only start when not running migrations or tests
        import sys
        if len(sys.argv) > 1:
            # Skip during migrations, makemigrations, and tests
            skip_commands = ['migrate', 'makemigrations', 'test']
            if any(cmd in sys.argv for cmd in skip_commands):
                return
        
        try:
            reminder_service.start()
        except Exception as e:
            print(f"Warning: Could not start reminder service: {e}")
