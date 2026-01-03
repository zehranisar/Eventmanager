from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from django.utils.html import format_html
from .models import User, Event, EventRegistration, Reminder, OTP


@admin.register(User)
class UserAdmin(BaseUserAdmin):
    """Admin configuration for User model"""
    
    list_display = ['email', 'name', 'role', 'is_active', 'is_staff', 'created_at']
    list_filter = ['role', 'is_active', 'is_staff', 'created_at']
    search_fields = ['email', 'name']
    ordering = ['-created_at']
    
    fieldsets = (
        (None, {'fields': ('email', 'password')}),
        ('Personal Info', {'fields': ('name', 'role')}),
        ('Permissions', {'fields': ('is_active', 'is_staff', 'is_superuser', 'groups', 'user_permissions')}),
        ('Important dates', {'fields': ('last_login',)}),
    )
    
    add_fieldsets = (
        (None, {
            'classes': ('wide',),
            'fields': ('email', 'name', 'role', 'password1', 'password2'),
        }),
    )


# Inline for showing registrations within Event admin
class EventRegistrationInline(admin.TabularInline):
    """Inline to show all registered users for an event"""
    model = EventRegistration
    extra = 0  # Don't show empty forms
    readonly_fields = ['user', 'name', 'email', 'phone', 'student_id', 'registered_at']
    fields = ['user', 'name', 'email', 'phone', 'student_id', 'registered_at']
    can_delete = True
    
    def has_add_permission(self, request, obj=None):
        return False  # Don't allow adding from admin


@admin.register(Event)
class EventAdmin(admin.ModelAdmin):
    """Admin configuration for Event model"""
    
    list_display = ['title', 'category', 'date', 'time', 'location', 'created_by', 
                    'registration_count', 'is_active']
    list_filter = ['category', 'is_active', 'date', 'created_at']
    search_fields = ['title', 'description', 'location']
    ordering = ['-date']
    date_hierarchy = 'date'
    
    # Show registered users within event detail page
    inlines = [EventRegistrationInline]
    
    fieldsets = (
        ('Event Information', {
            'fields': ('title', 'description', 'category')
        }),
        ('Date & Location', {
            'fields': ('date', 'time', 'location')
        }),
        ('Settings', {
            'fields': ('max_participants', 'is_active', 'created_by')
        }),
    )
    
    def registration_count(self, obj):
        """Show number of registrations for this event"""
        count = obj.registrations.count()
        if count > 0:
            return format_html(
                '<span style="color: green; font-weight: bold;">{}</span>',
                count
            )
        return count
    registration_count.short_description = 'Registrations'


@admin.register(EventRegistration)
class EventRegistrationAdmin(admin.ModelAdmin):
    """Admin configuration for EventRegistration model - shows all registration details"""
    
    list_display = ['id', 'event_title', 'user_account', 'name', 'email', 'phone', 
                    'student_id', 'registered_at']
    list_filter = ['registered_at', 'event']
    search_fields = ['user__email', 'user__name', 'event__title', 'name', 'email', 
                     'phone', 'student_id']
    ordering = ['-registered_at']
    
    # Group fields nicely
    fieldsets = (
        ('Event & User', {
            'fields': ('event', 'user')
        }),
        ('Registration Details', {
            'fields': ('name', 'email', 'phone', 'student_id'),
            'description': 'Details provided during registration'
        }),
        ('Timestamp', {
            'fields': ('registered_at',)
        }),
    )
    
    readonly_fields = ['registered_at']
    
    def event_title(self, obj):
        """Show event title"""
        return obj.event.title
    event_title.short_description = 'Event'
    event_title.admin_order_field = 'event__title'
    
    def user_account(self, obj):
        """Show user account email"""
        return obj.user.email
    user_account.short_description = 'User Account'
    user_account.admin_order_field = 'user__email'


@admin.register(Reminder)
class ReminderAdmin(admin.ModelAdmin):
    """Admin configuration for Reminder model"""
    
    list_display = ['user', 'event', 'remind_at', 'is_sent']
    list_filter = ['is_sent', 'remind_at']
    search_fields = ['user__email', 'user__name', 'event__title']
    ordering = ['remind_at']


@admin.register(OTP)
class OTPAdmin(admin.ModelAdmin):
    """Admin configuration for OTP model"""
    
    list_display = ['email', 'otp_code', 'is_used', 'created_at', 'expires_at']
    list_filter = ['is_used', 'created_at']
    search_fields = ['email']
    ordering = ['-created_at']


# Customize admin site header
admin.site.site_header = "Event Manager Admin"
admin.site.site_title = "Event Manager"
admin.site.index_title = "Welcome to Event Manager Administration"
