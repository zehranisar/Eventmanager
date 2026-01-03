from rest_framework import status, generics
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.response import Response
from rest_framework_simplejwt.tokens import RefreshToken
from django.utils import timezone
from django.core.mail import send_mail
from django.conf import settings
from datetime import timedelta

from .models import User, Event, EventRegistration, Reminder, OTP
from .serializers import (
    UserSerializer, UserRegistrationSerializer, LoginSerializer,
    ForgotPasswordSerializer, VerifyOTPSerializer, ResetPasswordSerializer,
    EventSerializer, EventCreateSerializer, EventRegistrationSerializer,
    ReminderSerializer, SetReminderSerializer, ChangePasswordSerializer
)


# ==================== AUTHENTICATION APIs ====================

@api_view(['POST'])
@permission_classes([AllowAny])
def register(request):
    """Register a new user (student by default)"""
    serializer = UserRegistrationSerializer(data=request.data)
    
    if serializer.is_valid():
        user = serializer.save()
        refresh = RefreshToken.for_user(user)
        
        return Response({
            'success': True,
            'message': 'Registration successful',
            'user': UserSerializer(user).data,
            'tokens': {
                'refresh': str(refresh),
                'access': str(refresh.access_token),
            }
        }, status=status.HTTP_201_CREATED)
    
    return Response({
        'success': False,
        'message': 'Registration failed',
        'errors': serializer.errors
    }, status=status.HTTP_400_BAD_REQUEST)


@api_view(['POST'])
@permission_classes([AllowAny])
def login(request):
    """Login user and return JWT tokens"""
    serializer = LoginSerializer(data=request.data)
    
    if serializer.is_valid():
        user = serializer.validated_data['user']
        refresh = RefreshToken.for_user(user)
        
        return Response({
            'success': True,
            'message': 'Login successful',
            'user': UserSerializer(user).data,
            'tokens': {
                'refresh': str(refresh),
                'access': str(refresh.access_token),
            }
        }, status=status.HTTP_200_OK)
    
    return Response({
        'success': False,
        'message': 'Login failed',
        'errors': serializer.errors
    }, status=status.HTTP_400_BAD_REQUEST)


@api_view(['POST'])
@permission_classes([AllowAny])
def forgot_password(request):
    """Send OTP to user's email for password reset"""
    serializer = ForgotPasswordSerializer(data=request.data)
    
    if serializer.is_valid():
        email = serializer.validated_data['email']
        
        # Get user for name in email
        try:
            user = User.objects.get(email=email)
        except User.DoesNotExist:
            return Response({
                'success': False,
                'message': 'No account found with this email'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        # Generate OTP
        otp_code = OTP.generate_otp()
        
        # Delete any existing OTPs for this email
        OTP.objects.filter(email=email).delete()
        
        # Create new OTP (valid for 10 minutes)
        OTP.objects.create(
            email=email,
            otp_code=otp_code,
            expires_at=timezone.now() + timedelta(minutes=10)
        )
        
        # Send OTP via email
        try:
            subject = 'Password Reset OTP - Event Manager'
            message = f"""
Hello {user.name},

You have requested to reset your password for your Event Manager account.

Your OTP (One-Time Password) is: {otp_code}

This OTP is valid for 10 minutes only. Please do not share this OTP with anyone.

If you did not request this password reset, please ignore this email.

Best regards,
Event Manager Team
            """.strip()
            
            send_mail(
                subject=subject,
                message=message,
                from_email=settings.DEFAULT_FROM_EMAIL,
                recipient_list=[email],
                fail_silently=False,
            )
            
            return Response({
                'success': True,
                'message': 'OTP has been sent to your email address'
            }, status=status.HTTP_200_OK)
            
        except Exception as e:
            # Log the error in production
            return Response({
                'success': False,
                'message': 'Failed to send email. Please try again later.'
            }, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
    
    return Response({
        'success': False,
        'message': 'Failed to send OTP',
        'errors': serializer.errors
    }, status=status.HTTP_400_BAD_REQUEST)


@api_view(['POST'])
@permission_classes([AllowAny])
def verify_otp(request):
    """Verify OTP for password reset"""
    serializer = VerifyOTPSerializer(data=request.data)
    
    if serializer.is_valid():
        email = serializer.validated_data['email']
        otp_code = serializer.validated_data['otp']
        
        try:
            otp = OTP.objects.get(email=email, otp_code=otp_code)
            
            if not otp.is_valid():
                return Response({
                    'success': False,
                    'message': 'OTP has expired or already used'
                }, status=status.HTTP_400_BAD_REQUEST)
            
            return Response({
                'success': True,
                'message': 'OTP verified successfully'
            }, status=status.HTTP_200_OK)
            
        except OTP.DoesNotExist:
            return Response({
                'success': False,
                'message': 'Invalid OTP'
            }, status=status.HTTP_400_BAD_REQUEST)
    
    return Response({
        'success': False,
        'message': 'Verification failed',
        'errors': serializer.errors
    }, status=status.HTTP_400_BAD_REQUEST)


@api_view(['POST'])
@permission_classes([AllowAny])
def reset_password(request):
    """Reset password using verified OTP"""
    serializer = ResetPasswordSerializer(data=request.data)
    
    if serializer.is_valid():
        email = serializer.validated_data['email']
        otp_code = serializer.validated_data['otp']
        new_password = serializer.validated_data['new_password']
        
        try:
            otp = OTP.objects.get(email=email, otp_code=otp_code)
            
            if not otp.is_valid():
                return Response({
                    'success': False,
                    'message': 'OTP has expired or already used'
                }, status=status.HTTP_400_BAD_REQUEST)
            
            # Update user password
            user = User.objects.get(email=email)
            user.set_password(new_password)
            user.save()
            
            # Mark OTP as used
            otp.is_used = True
            otp.save()
            
            return Response({
                'success': True,
                'message': 'Password reset successful'
            }, status=status.HTTP_200_OK)
            
        except OTP.DoesNotExist:
            return Response({
                'success': False,
                'message': 'Invalid OTP'
            }, status=status.HTTP_400_BAD_REQUEST)
        except User.DoesNotExist:
            return Response({
                'success': False,
                'message': 'User not found'
            }, status=status.HTTP_404_NOT_FOUND)
    
    return Response({
        'success': False,
        'message': 'Password reset failed',
        'errors': serializer.errors
    }, status=status.HTTP_400_BAD_REQUEST)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def get_profile(request):
    """Get current user profile"""
    serializer = UserSerializer(request.user)
    return Response({
        'success': True,
        'user': serializer.data
    }, status=status.HTTP_200_OK)


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def change_password(request):
    """Change password for logged-in user"""
    serializer = ChangePasswordSerializer(data=request.data)
    
    if serializer.is_valid():
        user = request.user
        
        if not user.check_password(serializer.validated_data['old_password']):
            return Response({
                'success': False,
                'message': 'Current password is incorrect'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        user.set_password(serializer.validated_data['new_password'])
        user.save()
        
        return Response({
            'success': True,
            'message': 'Password changed successfully'
        }, status=status.HTTP_200_OK)
    
    return Response({
        'success': False,
        'message': 'Password change failed',
        'errors': serializer.errors
    }, status=status.HTTP_400_BAD_REQUEST)


# ==================== EVENT APIs ====================

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def event_list(request):
    """Get all active events"""
    events = Event.objects.filter(is_active=True)
    serializer = EventSerializer(events, many=True, context={'request': request})
    return Response({
        'success': True,
        'count': events.count(),
        'events': serializer.data
    }, status=status.HTTP_200_OK)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def event_detail(request, pk):
    """Get single event details"""
    try:
        event = Event.objects.get(pk=pk, is_active=True)
        serializer = EventSerializer(event, context={'request': request})
        return Response({
            'success': True,
            'event': serializer.data
        }, status=status.HTTP_200_OK)
    except Event.DoesNotExist:
        return Response({
            'success': False,
            'message': 'Event not found'
        }, status=status.HTTP_404_NOT_FOUND)


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def event_create(request):
    """Create new event (Admin only)"""
    if not request.user.is_admin():
        return Response({
            'success': False,
            'message': 'Only admins can create events'
        }, status=status.HTTP_403_FORBIDDEN)
    
    serializer = EventCreateSerializer(data=request.data, context={'request': request})
    
    if serializer.is_valid():
        event = serializer.save()
        return Response({
            'success': True,
            'message': 'Event created successfully',
            'event': EventSerializer(event, context={'request': request}).data
        }, status=status.HTTP_201_CREATED)
    
    return Response({
        'success': False,
        'message': 'Failed to create event',
        'errors': serializer.errors
    }, status=status.HTTP_400_BAD_REQUEST)


@api_view(['PUT'])
@permission_classes([IsAuthenticated])
def event_update(request, pk):
    """Update event (Admin only)"""
    if not request.user.is_admin():
        return Response({
            'success': False,
            'message': 'Only admins can update events'
        }, status=status.HTTP_403_FORBIDDEN)
    
    try:
        event = Event.objects.get(pk=pk)
        serializer = EventCreateSerializer(event, data=request.data, partial=True, context={'request': request})
        
        if serializer.is_valid():
            serializer.save()
            return Response({
                'success': True,
                'message': 'Event updated successfully',
                'event': EventSerializer(event, context={'request': request}).data
            }, status=status.HTTP_200_OK)
        
        return Response({
            'success': False,
            'message': 'Failed to update event',
            'errors': serializer.errors
        }, status=status.HTTP_400_BAD_REQUEST)
        
    except Event.DoesNotExist:
        return Response({
            'success': False,
            'message': 'Event not found'
        }, status=status.HTTP_404_NOT_FOUND)


@api_view(['DELETE'])
@permission_classes([IsAuthenticated])
def event_delete(request, pk):
    """Delete event (Admin only)"""
    if not request.user.is_admin():
        return Response({
            'success': False,
            'message': 'Only admins can delete events'
        }, status=status.HTTP_403_FORBIDDEN)
    
    try:
        event = Event.objects.get(pk=pk)
        event.delete()
        return Response({
            'success': True,
            'message': 'Event deleted successfully'
        }, status=status.HTTP_200_OK)
    except Event.DoesNotExist:
        return Response({
            'success': False,
            'message': 'Event not found'
        }, status=status.HTTP_404_NOT_FOUND)


# ==================== REGISTRATION APIs ====================

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def register_for_event(request, pk):
    """Register current user for an event with details"""
    try:
        event = Event.objects.get(pk=pk, is_active=True)
        
        # Check if already registered
        if EventRegistration.objects.filter(user=request.user, event=event).exists():
            return Response({
                'success': False,
                'message': 'You are already registered for this event'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        # Check if event is full
        if event.is_full:
            return Response({
                'success': False,
                'message': 'This event is full'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        # Get registration details from request
        name = request.data.get('name', request.user.name)
        email = request.data.get('email', request.user.email)
        phone = request.data.get('phone', '')
        student_id = request.data.get('student_id', '')
        
        # Create registration with details
        registration = EventRegistration.objects.create(
            user=request.user,
            event=event,
            name=name,
            email=email,
            phone=phone,
            student_id=student_id
        )
        
        return Response({
            'success': True,
            'message': 'Successfully registered for event',
            'registration': EventRegistrationSerializer(registration).data
        }, status=status.HTTP_201_CREATED)
        
    except Event.DoesNotExist:
        return Response({
            'success': False,
            'message': 'Event not found'
        }, status=status.HTTP_404_NOT_FOUND)


@api_view(['DELETE'])
@permission_classes([IsAuthenticated])
def cancel_registration(request, pk):
    """Cancel registration for an event"""
    try:
        registration = EventRegistration.objects.get(user=request.user, event_id=pk)
        registration.delete()
        
        return Response({
            'success': True,
            'message': 'Registration cancelled successfully'
        }, status=status.HTTP_200_OK)
        
    except EventRegistration.DoesNotExist:
        return Response({
            'success': False,
            'message': 'Registration not found'
        }, status=status.HTTP_404_NOT_FOUND)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def my_registrations(request):
    """Get all events registered by current user"""
    registrations = EventRegistration.objects.filter(user=request.user)
    serializer = EventRegistrationSerializer(registrations, many=True)
    return Response({
        'success': True,
        'count': registrations.count(),
        'registrations': serializer.data
    }, status=status.HTTP_200_OK)


# ==================== REMINDER APIs ====================

@api_view(['POST'])
@permission_classes([IsAuthenticated])
def set_reminder(request, pk):
    """Set reminder for an event with custom timing"""
    try:
        event = Event.objects.get(pk=pk, is_active=True)
        
        # Check if reminder already exists
        if Reminder.objects.filter(user=request.user, event=event).exists():
            return Response({
                'success': False,
                'message': 'Reminder already set for this event'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        # Validate timing option
        serializer = SetReminderSerializer(data=request.data)
        if not serializer.is_valid():
            return Response({
                'success': False,
                'message': 'Invalid timing option',
                'errors': serializer.errors
            }, status=status.HTTP_400_BAD_REQUEST)
        
        timing = serializer.validated_data.get('timing', '1_day')
        
        # Calculate remind_at based on timing option
        from datetime import datetime, timedelta
        from django.conf import settings
        
        # Combine date and time (naive datetime)
        # event.date and event.time are stored as naive DateField and TimeField
        # These represent the local time in Pakistan (UTC+5)
        event_datetime_naive = datetime.combine(event.date, event.time)
        
        # IMPORTANT: The naive datetime is already in Pakistan local time
        # We need to treat it as UTC+5 and convert to UTC for storage
        # Pakistan is UTC+5, so to convert PKT to UTC, we subtract 5 hours
        # Example: 11:40 PM PKT = 6:40 PM UTC (11:40 PM - 5 hours)
        
        # Convert Pakistan local time directly to UTC
        # Since PKT = UTC+5, we subtract 5 hours to get UTC
        pakistan_offset_hours = 5
        event_datetime_utc_naive = event_datetime_naive - timedelta(hours=pakistan_offset_hours)
        
        # Make it timezone-aware in UTC (Django stores in UTC when USE_TZ=True)
        from datetime import timezone as dt_timezone
        event_datetime = event_datetime_utc_naive.replace(tzinfo=dt_timezone.utc)
        
        # Calculate reminder time based on selected option
        timing_map = {
            '1_day': timedelta(days=1),
            '12_hours': timedelta(hours=12),
            '6_hours': timedelta(hours=6),
            '3_hours': timedelta(hours=3),
            '1_hour': timedelta(hours=1),
            '30_minutes': timedelta(minutes=30),
            '15_minutes': timedelta(minutes=15),
        }
        
        remind_at = event_datetime - timing_map.get(timing, timedelta(days=1))
        
        # Debug: Print times to help diagnose timezone issues
        import sys
        from datetime import timezone as dt_timezone
        pakistan_tz = dt_timezone(timedelta(hours=5))
        naive_dt = datetime.combine(event.date, event.time)
        print(f"[DEBUG] ===== REMINDER TIMEZONE DEBUG =====", file=sys.stderr)
        print(f"[DEBUG] Event date: {event.date}, time: {event.time}", file=sys.stderr)
        print(f"[DEBUG] Event datetime (naive/PKT): {naive_dt}", file=sys.stderr)
        print(f"[DEBUG] Event datetime (UTC in DB): {event_datetime}", file=sys.stderr)
        print(f"[DEBUG] Event datetime (back to PKT): {event_datetime.astimezone(pakistan_tz)}", file=sys.stderr)
        print(f"[DEBUG] Remind at (UTC in DB): {remind_at}", file=sys.stderr)
        print(f"[DEBUG] Remind at (PKT): {remind_at.astimezone(pakistan_tz)}", file=sys.stderr)
        print(f"[DEBUG] Current time (UTC): {timezone.now()}", file=sys.stderr)
        print(f"[DEBUG] Current time (PKT): {timezone.now().astimezone(pakistan_tz)}", file=sys.stderr)
        print(f"[DEBUG] Settings TIME_ZONE: {settings.TIME_ZONE}", file=sys.stderr)
        print(f"[DEBUG] ====================================", file=sys.stderr)
        
        # Don't set reminder in the past
        if remind_at <= timezone.now():
            return Response({
                'success': False,
                'message': 'Cannot set reminder in the past. Please choose a different timing option.'
            }, status=status.HTTP_400_BAD_REQUEST)
        
        reminder = Reminder.objects.create(
            user=request.user,
            event=event,
            remind_at=remind_at
        )
        
        # Format timing for response message
        timing_display = dict(SetReminderSerializer.TIMING_CHOICES).get(timing, timing)
        
        return Response({
            'success': True,
            'message': f'Reminder set successfully for {timing_display}',
            'reminder': ReminderSerializer(reminder).data
        }, status=status.HTTP_201_CREATED)
        
    except Event.DoesNotExist:
        return Response({
            'success': False,
            'message': 'Event not found'
        }, status=status.HTTP_404_NOT_FOUND)


@api_view(['DELETE'])
@permission_classes([IsAuthenticated])
def cancel_reminder(request, pk):
    """Cancel reminder for an event"""
    try:
        reminder = Reminder.objects.get(user=request.user, event_id=pk)
        reminder.delete()
        
        return Response({
            'success': True,
            'message': 'Reminder cancelled successfully'
        }, status=status.HTTP_200_OK)
        
    except Reminder.DoesNotExist:
        return Response({
            'success': False,
            'message': 'Reminder not found'
        }, status=status.HTTP_404_NOT_FOUND)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def my_reminders(request):
    """Get all reminders set by current user"""
    reminders = Reminder.objects.filter(user=request.user, is_sent=False)
    serializer = ReminderSerializer(reminders, many=True)
    return Response({
        'success': True,
        'count': reminders.count(),
        'reminders': serializer.data
    }, status=status.HTTP_200_OK)


# ==================== DASHBOARD API ====================

@api_view(['GET'])
@permission_classes([IsAuthenticated])
def dashboard(request):
    """Get dashboard statistics"""
    user = request.user
    
    # Common stats
    total_events = Event.objects.filter(is_active=True).count()
    my_registrations_count = EventRegistration.objects.filter(user=user).count()
    my_reminders_count = Reminder.objects.filter(user=user, is_sent=False).count()
    
    data = {
        'success': True,
        'stats': {
            'total_events': total_events,
            'my_registrations': my_registrations_count,
            'my_reminders': my_reminders_count,
        },
        'user': UserSerializer(user).data
    }
    
    # Admin specific stats
    if user.is_admin():
        data['stats']['events_created'] = Event.objects.filter(created_by=user).count()
        data['stats']['total_users'] = User.objects.filter(role='student').count()
    
    return Response(data, status=status.HTTP_200_OK)


@api_view(['GET'])
@permission_classes([IsAuthenticated])
def admin_dashboard(request):
    """Get detailed admin dashboard data with all statistics and registrations"""
    user = request.user
    
    # Check if user is admin
    if not user.is_admin():
        return Response({
            'success': False,
            'message': 'Only admins can access this dashboard'
        }, status=status.HTTP_403_FORBIDDEN)
    
    from django.db.models import Count
    from django.utils import timezone
    from datetime import timedelta
    
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
    
    # Events with registration counts and details
    events_with_counts = Event.objects.annotate(
        registration_count=Count('registrations')
    ).order_by('-date', '-registration_count')
    
    # Build event registration details
    event_registration_details = []
    for event in events_with_counts:
        registrations = EventRegistration.objects.filter(event=event).select_related('user')
        registration_list = []
        for reg in registrations:
            registration_list.append({
                'id': reg.id,
                'user_id': reg.user.id,
                'user_name': reg.user.name,
                'user_email': reg.user.email,
                'name': reg.name,
                'email': reg.email,
                'phone': reg.phone,
                'student_id': reg.student_id,
                'registered_at': reg.registered_at.isoformat() if reg.registered_at else None,
            })
        
        event_registration_details.append({
            'event_id': event.id,
            'event_title': event.title,
            'event_date': event.date.isoformat(),
            'event_time': event.time.isoformat(),
            'event_location': event.location,
            'event_category': event.get_category_display(),
            'registration_count': event.registration_count,
            'max_participants': event.max_participants,
            'is_full': event.registration_count >= event.max_participants if event.max_participants else False,
            'is_active': event.is_active,
            'registrations': registration_list,
        })
    
    # Users with their registrations
    users_with_registrations = User.objects.annotate(
        registration_count=Count('registrations')
    ).filter(registration_count__gt=0).order_by('-registration_count', 'name')
    
    user_registration_details = []
    for user in users_with_registrations:
        registrations = EventRegistration.objects.filter(user=user).select_related('event')
        registration_list = []
        for reg in registrations:
            registration_list.append({
                'id': reg.id,
                'event_id': reg.event.id,
                'event_title': reg.event.title,
                'event_date': reg.event.date.isoformat(),
                'event_time': reg.event.time.isoformat(),
                'event_location': reg.event.location,
                'name': reg.name,
                'email': reg.email,
                'phone': reg.phone,
                'student_id': reg.student_id,
                'registered_at': reg.registered_at.isoformat() if reg.registered_at else None,
            })
        
        user_registration_details.append({
            'user_id': user.id,
            'user_name': user.name,
            'user_email': user.email,
            'user_role': user.role,
            'registration_count': user.registration_count,
            'registrations': registration_list,
        })
    
    # Top events by registrations
    top_events = Event.objects.annotate(
        registration_count=Count('registrations')
    ).filter(registration_count__gt=0).order_by('-registration_count')[:10]
    
    top_events_list = []
    for event in top_events:
        top_events_list.append({
            'event_id': event.id,
            'event_title': event.title,
            'event_date': event.date.isoformat(),
            'registration_count': event.registration_count,
        })
    
    return Response({
        'success': True,
        'stats': {
            'total_users': total_users,
            'total_students': total_students,
            'total_admins': total_admins,
            'total_events': total_events,
            'active_events': active_events,
            'total_registrations': total_registrations,
            'recent_registrations': recent_registrations,
            'recent_events': recent_events,
        },
        'event_registration_details': event_registration_details,
        'user_registration_details': user_registration_details,
        'top_events': top_events_list,
    }, status=status.HTTP_200_OK)
