@echo off
echo ========================================
echo   Stopping Django Server
echo ========================================
echo.

REM Stop all Python processes (Django server)
taskkill /F /IM python.exe /T 2>nul

if %errorlevel% == 0 (
    echo Server stopped successfully!
) else (
    echo No server process found or already stopped.
)

echo.
pause

