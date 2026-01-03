@echo off
echo ========================================
echo   Setting up ADB Reverse Port Forwarding
echo ========================================
echo.

REM Check if device is connected
adb devices

echo.
echo Setting up port forwarding...
adb reverse tcp:8000 tcp:8000

echo.
echo Verifying setup...
adb reverse --list

echo.
echo Done! Your phone should now be able to connect to the server.
echo.
pause

