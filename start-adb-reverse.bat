@echo off
echo ========================================
echo   ADB Reverse Port Forwarding Setup
echo ========================================
echo.
echo This will forward port 8000 from your Android device
echo to port 8000 on your computer.
echo.
echo Make sure:
echo   1. Your Android device is connected via USB
echo   2. USB Debugging is enabled on your device
echo.
pause

echo.
echo Checking for connected devices...
adb devices
echo.

echo Setting up port forwarding...
adb reverse tcp:8000 tcp:8000

if %errorlevel% equ 0 (
    echo.
    echo ========================================
    echo   SUCCESS!
    echo ========================================
    echo.
    echo Port forwarding is now active!
    echo Your app can now connect to localhost:8000
    echo.
    echo Note: You need to run this every time you
    echo reconnect your device or restart your computer.
    echo.
) else (
    echo.
    echo ========================================
    echo   ERROR!
    echo ========================================
    echo.
    echo Failed to set up port forwarding.
    echo.
    echo Troubleshooting:
    echo   1. Make sure your device is connected via USB
    echo   2. Enable USB Debugging in Developer Options
    echo   3. Check if you see "Allow USB debugging?" on your device
    echo   4. Try unplugging and reconnecting the USB cable
    echo   5. Make sure ADB is installed and in your PATH
    echo.
)

echo.
echo Press any key to exit...
pause >nul

