@echo off
echo ========================================
echo   Starting Django Server
echo ========================================
echo.

REM Navigate to project directory
cd /d "%~dp0"

REM Activate virtual environment
call venv\Scripts\activate.bat

REM Start Django server
echo Starting server on http://0.0.0.0:8000/
echo Press CTRL+C to stop the server
echo.
python manage.py runserver 0.0.0.0:8000

pause

