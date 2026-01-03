# PowerShell script to push EventManager to GitHub
# This script will help you authenticate and push your project

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Push EventManager to GitHub" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if already authenticated
Write-Host "Checking git status..." -ForegroundColor Yellow
git status

Write-Host ""
Write-Host "Attempting to push to GitHub..." -ForegroundColor Yellow
Write-Host ""

# Try to push - Windows Credential Manager should prompt for credentials
git push -u origin main

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "✓ Successfully pushed to GitHub!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "View your repository at:" -ForegroundColor Cyan
    Write-Host "https://github.com/zehranisar/EventManager" -ForegroundColor Yellow
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "Authentication required!" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "If prompted:" -ForegroundColor Yellow
    Write-Host "  Username: zehranisar" -ForegroundColor White
    Write-Host "  Password: Use a Personal Access Token (NOT your GitHub password)" -ForegroundColor White
    Write-Host ""
    Write-Host "To create a Personal Access Token:" -ForegroundColor Yellow
    Write-Host "  1. Visit: https://github.com/settings/tokens" -ForegroundColor White
    Write-Host "  2. Click Generate new token -> Generate new token (classic)" -ForegroundColor White
    Write-Host "  3. Select 'repo' scope" -ForegroundColor White
    Write-Host "  4. Generate and copy the token" -ForegroundColor White
    Write-Host "  5. Use the token as your password when prompted" -ForegroundColor White
    Write-Host ""
    Write-Host "Alternative: Update remote URL with token" -ForegroundColor Yellow
    Write-Host "  git remote set-url origin https://YOUR_TOKEN@github.com/zehranisar/EventManager.git" -ForegroundColor White
    Write-Host "  git push -u origin main" -ForegroundColor White
}

