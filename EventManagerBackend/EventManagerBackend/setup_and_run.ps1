# Event Manager Backend - Complete Setup and Run Script
# This script will set up the environment and start the Django server

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Event Manager Backend Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Navigate to project directory
$scriptPath = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptPath
Write-Host "Current directory: $(Get-Location)" -ForegroundColor Green

# Step 1: Check for Python
Write-Host "`n[1/5] Checking for Python..." -ForegroundColor Yellow
$pythonCmd = $null

# Try different Python commands
$pythonCommands = @("python", "python3", "py")
foreach ($cmd in $pythonCommands) {
    try {
        $result = Get-Command $cmd -ErrorAction Stop
        $pythonCmd = $result.Name
        $pythonVersion = & $pythonCmd --version 2>&1
        Write-Host "Found Python: $pythonVersion" -ForegroundColor Green
        break
    } catch {
        continue
    }
}

if (-not $pythonCmd) {
    Write-Host "ERROR: Python is not installed or not in PATH!" -ForegroundColor Red
    Write-Host "Please install Python 3.8 or higher from:" -ForegroundColor Yellow
    Write-Host "  https://www.python.org/downloads/" -ForegroundColor Yellow
    Write-Host "Or use: winget install Python.Python.3.13" -ForegroundColor Yellow
    exit 1
}

# Step 2: Check PostgreSQL connection
Write-Host "`n[2/5] Checking PostgreSQL connection..." -ForegroundColor Yellow
try {
    $pgTest = Test-NetConnection -ComputerName localhost -Port 5432 -InformationLevel Quiet -WarningAction SilentlyContinue
    if ($pgTest) {
        Write-Host "PostgreSQL is running on port 5432" -ForegroundColor Green
    } else {
        Write-Host "WARNING: PostgreSQL might not be running on port 5432" -ForegroundColor Yellow
        Write-Host "Please ensure PostgreSQL is installed and running" -ForegroundColor Yellow
    }
} catch {
    Write-Host "WARNING: Could not verify PostgreSQL connection" -ForegroundColor Yellow
}

# Step 3: Recreate virtual environment
Write-Host "`n[3/5] Setting up virtual environment..." -ForegroundColor Yellow
if (Test-Path "venv") {
    Write-Host "Removing old virtual environment..." -ForegroundColor Yellow
    Remove-Item -Path "venv" -Recurse -Force
}

Write-Host "Creating new virtual environment..." -ForegroundColor Yellow
& $pythonCmd -m venv venv

if (-not (Test-Path "venv\Scripts\python.exe")) {
    Write-Host "ERROR: Failed to create virtual environment!" -ForegroundColor Red
    exit 1
}

Write-Host "Virtual environment created successfully" -ForegroundColor Green

# Step 4: Install dependencies
Write-Host "`n[4/5] Installing dependencies..." -ForegroundColor Yellow
& "venv\Scripts\python.exe" -m pip install --upgrade pip
& "venv\Scripts\python.exe" -m pip install -r requirements.txt

if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Failed to install dependencies!" -ForegroundColor Red
    exit 1
}

Write-Host "Dependencies installed successfully" -ForegroundColor Green

# Step 5: Run migrations
Write-Host "`n[5/5] Running database migrations..." -ForegroundColor Yellow
& "venv\Scripts\python.exe" manage.py migrate

if ($LASTEXITCODE -ne 0) {
    Write-Host "WARNING: Migrations failed. Database might not be set up correctly." -ForegroundColor Yellow
    Write-Host "Please check:" -ForegroundColor Yellow
    Write-Host "  1. PostgreSQL is running" -ForegroundColor Yellow
    Write-Host "  2. Database 'Eventmanager' exists" -ForegroundColor Yellow
    Write-Host "  3. User 'postgres' has password 'zehra123'" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "To create the database, run in psql:" -ForegroundColor Yellow
    Write-Host "  CREATE DATABASE Eventmanager;" -ForegroundColor Cyan
} else {
    Write-Host "Migrations completed successfully" -ForegroundColor Green
}

# Step 6: Start the server
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Starting Django Server" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Server will start on: http://0.0.0.0:8000/" -ForegroundColor Green
Write-Host "Press CTRL+C to stop the server" -ForegroundColor Yellow
Write-Host ""

& "venv\Scripts\python.exe" manage.py runserver 0.0.0.0:8000

