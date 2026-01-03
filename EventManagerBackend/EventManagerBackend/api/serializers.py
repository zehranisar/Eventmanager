from rest_framework import serializers
from django.contrib.auth import authenticate
from .models import User, Event, EventRegistration, Reminder, OTP


class UserSerializer(serializers.ModelSerializer):
    """Serializer for User model"""
    
    class Meta:
        model = User
        fields = ['id', 'email', 'name', 'role', 'created_at']
        read_only_fields = ['id', 'created_at']


class UserRegistrationSerializer(serializers.ModelSerializer):
    """Serializer for user registration"""
    
    password = serializers.CharField(write_only=True, min_length=6)
    confirm_password = serializers.CharField(write_only=True, min_length=6)
    
    class Meta:
        model = User
        fields = ['email', 'name', 'password', 'confirm_password', 'role']
    
    def validate(self, data):
        if data['password'] != data['confirm_password']:
            raise serializers.ValidationError({'confirm_password': 'Passwords do not match'})
        return data
    
    def create(self, validated_data):
        validated_data.pop('confirm_password')
        user = User.objects.create_user(
            email=validated_data['email'],
            name=validated_data['name'],
            password=validated_data['password'],
            role=validated_data.get('role', 'student')
        )
        return user


class LoginSerializer(serializers.Serializer):
    """Serializer for user login"""
    
    email = serializers.EmailField()
    password = serializers.CharField(write_only=True)
    role = serializers.ChoiceField(choices=['admin', 'student'])
    
    def validate(self, data):
        email = data.get('email')
        password = data.get('password')
        role = data.get('role')
        
        if email and password:
            user = authenticate(username=email, password=password)
            
            if not user:
                raise serializers.ValidationError('Invalid email or password')
            
            if not user.is_active:
                raise serializers.ValidationError('User account is disabled')
            
            if user.role != role:
                raise serializers.ValidationError(f'This account is not registered as {role}')
            
            data['user'] = user
        else:
            raise serializers.ValidationError('Email and password are required')
        
        return data


class ForgotPasswordSerializer(serializers.Serializer):
    """Serializer for forgot password - send OTP"""
    
    email = serializers.EmailField()
    
    def validate_email(self, value):
        if not User.objects.filter(email=value).exists():
            raise serializers.ValidationError('No account found with this email')
        return value


class VerifyOTPSerializer(serializers.Serializer):
    """Serializer for OTP verification"""
    
    email = serializers.EmailField()
    otp = serializers.CharField(max_length=6, min_length=6)


class ResetPasswordSerializer(serializers.Serializer):
    """Serializer for password reset"""
    
    email = serializers.EmailField()
    otp = serializers.CharField(max_length=6, min_length=6)
    new_password = serializers.CharField(min_length=6, write_only=True)
    confirm_password = serializers.CharField(min_length=6, write_only=True)
    
    def validate(self, data):
        if data['new_password'] != data['confirm_password']:
            raise serializers.ValidationError({'confirm_password': 'Passwords do not match'})
        return data


class EventSerializer(serializers.ModelSerializer):
    """Serializer for Event model"""
    
    created_by_name = serializers.CharField(source='created_by.name', read_only=True)
    registered_count = serializers.IntegerField(read_only=True)
    is_full = serializers.BooleanField(read_only=True)
    is_registered = serializers.SerializerMethodField()
    has_reminder = serializers.SerializerMethodField()
    
    class Meta:
        model = Event
        fields = [
            'id', 'title', 'description', 'date', 'time', 
            'location', 'category', 'created_by', 'created_by_name',
            'max_participants', 'registered_count', 'is_full',
            'is_active', 'is_registered', 'has_reminder', 'created_at'
        ]
        read_only_fields = ['id', 'created_by', 'created_at']
    
    def get_is_registered(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            return EventRegistration.objects.filter(user=request.user, event=obj).exists()
        return False
    
    def get_has_reminder(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            return Reminder.objects.filter(user=request.user, event=obj).exists()
        return False


class EventCreateSerializer(serializers.ModelSerializer):
    """Serializer for creating events (Admin only)"""
    
    class Meta:
        model = Event
        fields = ['title', 'description', 'date', 'time', 'location', 'category', 'max_participants']
    
    def create(self, validated_data):
        validated_data['created_by'] = self.context['request'].user
        return super().create(validated_data)


class EventRegistrationSerializer(serializers.ModelSerializer):
    """Serializer for Event Registration"""
    
    event_title = serializers.CharField(source='event.title', read_only=True)
    event_date = serializers.DateField(source='event.date', read_only=True)
    event_time = serializers.TimeField(source='event.time', read_only=True)
    event_location = serializers.CharField(source='event.location', read_only=True)
    user_name = serializers.CharField(source='user.name', read_only=True)
    
    class Meta:
        model = EventRegistration
        fields = ['id', 'user', 'event', 'event_title', 'event_date', 
                  'event_time', 'event_location', 'user_name', 'name', 
                  'email', 'phone', 'student_id', 'registered_at']
        read_only_fields = ['id', 'user', 'registered_at']


class EventRegistrationCreateSerializer(serializers.Serializer):
    """Serializer for creating event registration with details"""
    
    name = serializers.CharField(max_length=100)
    email = serializers.EmailField()
    phone = serializers.CharField(max_length=20)
    student_id = serializers.CharField(max_length=50)


class SetReminderSerializer(serializers.Serializer):
    """Serializer for setting reminder with timing option"""
    
    TIMING_CHOICES = [
        ('1_day', '1 Day Before'),
        ('12_hours', '12 Hours Before'),
        ('6_hours', '6 Hours Before'),
        ('3_hours', '3 Hours Before'),
        ('1_hour', '1 Hour Before'),
        ('30_minutes', '30 Minutes Before'),
        ('15_minutes', '15 Minutes Before'),
    ]
    
    timing = serializers.ChoiceField(choices=TIMING_CHOICES, default='1_day')
    
    def validate_timing(self, value):
        """Validate timing option"""
        valid_options = [choice[0] for choice in self.TIMING_CHOICES]
        if value not in valid_options:
            raise serializers.ValidationError('Invalid timing option')
        return value


class ReminderSerializer(serializers.ModelSerializer):
    """Serializer for Reminder model"""
    
    event_title = serializers.CharField(source='event.title', read_only=True)
    event_date = serializers.DateField(source='event.date', read_only=True)
    event_time = serializers.TimeField(source='event.time', read_only=True)
    event_location = serializers.CharField(source='event.location', read_only=True)
    event_category = serializers.CharField(source='event.get_category_display', read_only=True)
    
    class Meta:
        model = Reminder
        fields = ['id', 'user', 'event', 'event_title', 'event_date', 
                  'event_time', 'event_location', 'event_category',
                  'remind_at', 'is_sent', 'created_at']
        read_only_fields = ['id', 'user', 'is_sent', 'created_at']


class ChangePasswordSerializer(serializers.Serializer):
    """Serializer for changing password"""
    
    old_password = serializers.CharField(write_only=True)
    new_password = serializers.CharField(write_only=True, min_length=6)
    confirm_password = serializers.CharField(write_only=True, min_length=6)
    
    def validate(self, data):
        if data['new_password'] != data['confirm_password']:
            raise serializers.ValidationError({'confirm_password': 'Passwords do not match'})
        return data

