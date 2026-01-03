@echo off
echo ========================================
echo   Sending Event Reminders
echo ========================================
echo.

cd /d "%~dp0"

echo Running reminder command...
python manage.py send_reminders

if %errorlevel% equ 0 (
    echo.
    echo Reminders processed successfully!
) else (
    echo.
    echo Error occurred while sending reminders.
)

echo.
pause

