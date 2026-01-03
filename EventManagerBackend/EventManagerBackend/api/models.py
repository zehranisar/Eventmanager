from django.db import models
from django.contrib.auth.models import AbstractBaseUser, BaseUserManager, PermissionsMixin
from django.utils import timezone
import random
import string


class UserManager(BaseUserManager):
    """Custom user manager for User model"""
    
    def create_user(self, email, name, password=None, role='student'):
        if not email:
            raise ValueError('Users must have an email address')
        
        user = self.model(
            email=self.normalize_email(email),
            name=name,
            role=role,
        )
        user.set_password(password)
        user.save(using=self._db)
        return user
    
    def create_superuser(self, email, name, password=None):
        user = self.create_user(
            email=email,
            name=name,
            password=password,
            role='admin',
        )
        user.is_staff = True
        user.is_superuser = True
        user.save(using=self._db)
        return user


class User(AbstractBaseUser, PermissionsMixin):
    """Custom User model with role (admin/student)"""
    
    ROLE_CHOICES = [
        ('admin', 'Admin'),
        ('student', 'Student'),
    ]
    
    id = models.AutoField(primary_key=True)
    email = models.EmailField(unique=True)
    name = models.CharField(max_length=100)
    role = models.CharField(max_length=10, choices=ROLE_CHOICES, default='student')
    is_active = models.BooleanField(default=True)
    is_staff = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    
    objects = UserManager()
    
    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = ['name']
    
    def __str__(self):
        return f"{self.name} ({self.email})"
    
    def is_admin(self):
        return self.role == 'admin'
    
    class Meta:
        db_table = 'users'
        ordering = ['-created_at']


class Event(models.Model):
    """Event model for university events"""
    
    CATEGORY_CHOICES = [
        ('academic', 'Academic'),
        ('cultural', 'Cultural'),
        ('sports', 'Sports'),
        ('workshop', 'Workshop'),
        ('seminar', 'Seminar'),
        ('other', 'Other'),
    ]
    
    id = models.AutoField(primary_key=True)
    title = models.CharField(max_length=200)
    description = models.TextField()
    date = models.DateField()
    time = models.TimeField()
    location = models.CharField(max_length=200)
    category = models.CharField(max_length=20, choices=CATEGORY_CHOICES, default='other')
    created_by = models.ForeignKey(User, on_delete=models.CASCADE, related_name='created_events')
    max_participants = models.IntegerField(default=100)
    is_active = models.BooleanField(default=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)
    
    def __str__(self):
        return self.title
    
    @property
    def registered_count(self):
        return self.registrations.count()
    
    @property
    def is_full(self):
        return self.registered_count >= self.max_participants
    
    class Meta:
        db_table = 'events'
        ordering = ['date', 'time']


class EventRegistration(models.Model):
    """Event Registration model - tracks user registrations for events"""
    
    id = models.AutoField(primary_key=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='registrations')
    event = models.ForeignKey(Event, on_delete=models.CASCADE, related_name='registrations')
    name = models.CharField(max_length=100, blank=True, null=True)  # Registration name
    email = models.EmailField(blank=True, null=True)  # Registration email
    phone = models.CharField(max_length=20, blank=True, null=True)  # Phone number
    student_id = models.CharField(max_length=50, blank=True, null=True)  # Student ID
    registered_at = models.DateTimeField(auto_now_add=True)
    
    def __str__(self):
        return f"{self.user.name} - {self.event.title}"
    
    class Meta:
        db_table = 'event_registrations'
        unique_together = ['user', 'event']  # Prevent duplicate registrations
        ordering = ['-registered_at']


class Reminder(models.Model):
    """Reminder model - tracks reminders set by users"""
    
    id = models.AutoField(primary_key=True)
    user = models.ForeignKey(User, on_delete=models.CASCADE, related_name='reminders')
    event = models.ForeignKey(Event, on_delete=models.CASCADE, related_name='reminders')
    remind_at = models.DateTimeField()
    is_sent = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)
    
    def __str__(self):
        return f"Reminder: {self.user.name} - {self.event.title}"
    
    class Meta:
        db_table = 'reminders'
        unique_together = ['user', 'event']
        ordering = ['remind_at']


class OTP(models.Model):
    """OTP model for password reset"""
    
    id = models.AutoField(primary_key=True)
    email = models.EmailField()
    otp_code = models.CharField(max_length=6)
    is_used = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)
    expires_at = models.DateTimeField()
    
    def __str__(self):
        return f"OTP for {self.email}"
    
    @staticmethod
    def generate_otp():
        """Generate a 6-digit OTP"""
        return ''.join(random.choices(string.digits, k=6))
    
    def is_valid(self):
        """Check if OTP is valid (not used and not expired)"""
        return not self.is_used and self.expires_at > timezone.now()
    
    class Meta:
        db_table = 'otps'
        ordering = ['-created_at']
