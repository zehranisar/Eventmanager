# PowerShell script to install requirements
# This script bypasses the activation and uses pip directly

Write-Host "Installing requirements from requirements.txt..." -ForegroundColor Cyan
Write-Host ""

# Get the script directory
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $scriptDir

# Check if venv exists
$pipPath = Join-Path $scriptDir "venv\Scripts\pip.exe"
if (-not (Test-Path $pipPath)) {
    Write-Host "ERROR: Virtual environment not found at venv\Scripts\pip.exe" -ForegroundColor Red
    Write-Host "Please make sure you are in the EventManagerBackend directory." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if requirements.txt exists
$reqPath = Join-Path $scriptDir "requirements.txt"
if (-not (Test-Path $reqPath)) {
    Write-Host "ERROR: requirements.txt not found!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# Install requirements
Write-Host "Using pip from: $pipPath" -ForegroundColor Green
Write-Host "Installing from: $reqPath" -ForegroundColor Green
Write-Host ""

& $pipPath install -r $reqPath

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "Requirements installed successfully!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "ERROR: Installation failed!" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
}

Read-Host "Press Enter to exit"

