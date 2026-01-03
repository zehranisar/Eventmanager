"""
Custom admin dashboard views for Event Manager
"""
from django.contrib.admin.views.decorators import staff_member_required
from django.shortcuts import render
from django.db.models import Count, Q
from django.utils import timezone
from datetime import timedelta

from .models import User, Event, EventRegistration, Reminder


@staff_member_required
def admin_dashboard(request):
    """Custom admin dashboard with statistics and details"""
    
    # Total statistics
    total_users = User.objects.count()
    total_students = User.objects.filter(role='student').count()
    total_admins = User.objects.filter(role='admin').count()
    total_events = Event.objects.count()
    active_events = Event.objects.filter(is_active=True).count()
    total_registrations = EventRegistration.objects.count()
    
    # Recent activity (last 7 days)
    seven_days_ago = timezone.now() - timedelta(days=7)
    recent_registrations = EventRegistration.objects.filter(
        registered_at__gte=seven_days_ago
    ).count()
    recent_events = Event.objects.filter(
        created_at__gte=seven_days_ago
    ).count()
    
    # Events with registration counts
    events_with_counts = Event.objects.annotate(
        registration_count=Count('registrations')
    ).order_by('-date', '-registration_count')
    
    # Users with their registrations
    users_with_registrations = User.objects.annotate(
        registration_count=Count('registrations')
    ).filter(registration_count__gt=0).order_by('-registration_count', 'name')
    
    # Top events by registrations
    top_events = Event.objects.annotate(
        registration_count=Count('registrations')
    ).filter(registration_count__gt=0).order_by('-registration_count')[:10]
    
    # Upcoming events (next 30 days)
    today = timezone.now().date()
    next_month = today + timedelta(days=30)
    upcoming_events = Event.objects.filter(
        date__gte=today,
        date__lte=next_month,
        is_active=True
    ).annotate(
        registration_count=Count('registrations')
    ).order_by('date', 'time')
    
    # Registration details by event
    event_registration_details = []
    for event in events_with_counts:
        registrations = EventRegistration.objects.filter(event=event).select_related('user')
        event_registration_details.append({
            'event': event,
            'count': event.registration_count,
            'registrations': registrations,
            'max_participants': event.max_participants,
            'is_full': event.registration_count >= event.max_participants if event.max_participants else False,
        })
    
    # User registration details
    user_registration_details = []
    for user in users_with_registrations:
        registrations = EventRegistration.objects.filter(user=user).select_related('event')
        user_registration_details.append({
            'user': user,
            'count': user.registration_count,
            'registrations': registrations,
        })
    
    context = {
        'total_users': total_users,
        'total_students': total_students,
        'total_admins': total_admins,
        'total_events': total_events,
        'active_events': active_events,
        'total_registrations': total_registrations,
        'recent_registrations': recent_registrations,
        'recent_events': recent_events,
        'events_with_counts': events_with_counts,
        'users_with_registrations': users_with_registrations,
        'top_events': top_events,
        'upcoming_events': upcoming_events,
        'event_registration_details': event_registration_details,
        'user_registration_details': user_registration_details,
    }
    
    return render(request, 'admin/dashboard.html', context)

