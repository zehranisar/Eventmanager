@echo off
echo Installing requirements from requirements.txt...
echo.

REM Navigate to the script directory
cd /d "%~dp0"

REM Check if venv exists
if not exist "venv\Scripts\pip.exe" (
    echo ERROR: Virtual environment not found at venv\Scripts\pip.exe
    echo Please make sure you are in the EventManagerBackend directory.
    pause
    exit /b 1
)

REM Check if requirements.txt exists
if not exist "requirements.txt" (
    echo ERROR: requirements.txt not found!
    pause
    exit /b 1
)

REM Install requirements
echo Using pip from: %CD%\venv\Scripts\pip.exe
echo Installing from: %CD%\requirements.txt
echo.

"venv\Scripts\pip.exe" install -r requirements.txt

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Requirements installed successfully!
    echo ========================================
) else (
    echo.
    echo ========================================
    echo ERROR: Installation failed!
    echo ========================================
)

pause

