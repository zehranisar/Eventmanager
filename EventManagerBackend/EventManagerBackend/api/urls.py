from django.urls import path
from rest_framework_simplejwt.views import TokenRefreshView
from . import views

urlpatterns = [
    # ==================== Authentication ====================
    path('auth/register/', views.register, name='register'),
    path('auth/login/', views.login, name='login'),
    path('auth/token/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
    path('auth/forgot-password/', views.forgot_password, name='forgot_password'),
    path('auth/verify-otp/', views.verify_otp, name='verify_otp'),
    path('auth/reset-password/', views.reset_password, name='reset_password'),
    path('auth/change-password/', views.change_password, name='change_password'),
    path('auth/profile/', views.get_profile, name='profile'),
    
    # ==================== Events ====================
    path('events/', views.event_list, name='event_list'),
    path('events/create/', views.event_create, name='event_create'),
    path('events/<int:pk>/', views.event_detail, name='event_detail'),
    path('events/<int:pk>/update/', views.event_update, name='event_update'),
    path('events/<int:pk>/delete/', views.event_delete, name='event_delete'),
    
    # ==================== Registrations ====================
    path('events/<int:pk>/register/', views.register_for_event, name='register_for_event'),
    path('events/<int:pk>/cancel-registration/', views.cancel_registration, name='cancel_registration'),
    path('registrations/', views.my_registrations, name='my_registrations'),
    
    # ==================== Reminders ====================
    path('events/<int:pk>/set-reminder/', views.set_reminder, name='set_reminder'),
    path('events/<int:pk>/cancel-reminder/', views.cancel_reminder, name='cancel_reminder'),
    path('reminders/', views.my_reminders, name='my_reminders'),
    
    # ==================== Dashboard ====================
    path('dashboard/', views.dashboard, name='dashboard'),
    path('admin/dashboard/', views.admin_dashboard, name='admin_dashboard'),
]

