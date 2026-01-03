# How to Start/Stop Django Server - Step by Step Guide

## 🛑 **STOPPING THE SERVER**

### Method 1: Using Task Manager (Easiest)
1. Press `Ctrl + Shift + Esc` to open Task Manager
2. Find the process named **"python"** or **"python.exe"**
3. Right-click on it → Select **"End Task"**

### Method 2: Using PowerShell/Command Prompt
1. Open PowerShell or Command Prompt
2. Run this command to find the process:
   ```
   Get-Process python
   ```
3. Stop the process using its ID:
   ```
   Stop-Process -Id <PROCESS_ID>
   ```
   (Replace `<PROCESS_ID>` with the actual ID from step 2)

### Method 3: If server is running in a terminal window
- Simply press `Ctrl + C` in that terminal window

---

## ▶️ **STARTING THE SERVER**

### Step-by-Step Instructions:

1. **Open PowerShell**
   - Press `Windows Key + X`
   - Select "Windows PowerShell" or "Terminal"

2. **Navigate to the project directory**
   ```
   cd C:\Users\Dell\EventManagerBackend
   ```

3. **Activate the virtual environment**
   ```
   .\venv\Scripts\Activate.ps1
   ```
   - You should see `(venv)` appear at the beginning of your prompt

4. **Start the Django server**
   ```
   python manage.py runserver 0.0.0.0:8000
   ```

5. **Verify it's running**
   - You should see output like:
     ```
     Starting development server at http://0.0.0.0:8000/
     Quit the server with CTRL-BREAK.
     ```

6. **Set up ADB reverse port forwarding** (for USB connection)
   - Open a **NEW** PowerShell window (keep the server running)
   - Run:
     ```
     adb reverse tcp:8000 tcp:8000
     ```
   - Verify it worked:
     ```
     adb reverse --list
     ```
   - Should show: `UsbFfs tcp:8000 tcp:8000`

---

## 🔄 **QUICK RESTART (All in One)**

If you want to restart quickly, you can use this PowerShell script:

```powershell
# Stop any existing server
Get-Process python -ErrorAction SilentlyContinue | Stop-Process -Force

# Navigate to project
cd C:\Users\Dell\EventManagerBackend

# Activate virtual environment and start server
.\venv\Scripts\Activate.ps1
python manage.py runserver 0.0.0.0:8000
```

Then in a **separate terminal**, run:
```powershell
adb reverse tcp:8000 tcp:8000
```

---

## ✅ **VERIFICATION CHECKLIST**

After starting the server, verify:

- [ ] Server is running (check terminal output)
- [ ] Port 8000 is listening: `netstat -ano | findstr :8000`
- [ ] ADB reverse is active: `adb reverse --list`
- [ ] Phone is connected: `adb devices`
- [ ] Test API: Open browser and go to `http://127.0.0.1:8000/api/events/` (may need authentication)

---

## 🚨 **TROUBLESHOOTING**

### Server won't start?
- Make sure virtual environment is activated (you see `(venv)` in prompt)
- Check if port 8000 is already in use: `netstat -ano | findstr :8000`
- If port is busy, kill the process or use a different port: `python manage.py runserver 0.0.0.0:8001`

### "ModuleNotFoundError: No module named 'django'"
- Virtual environment is not activated
- Run: `.\venv\Scripts\Activate.ps1`

### ADB reverse not working?
- Make sure phone is connected: `adb devices`
- USB debugging must be enabled on phone
- Try: `adb kill-server` then `adb start-server`

### Phone can't connect?
- Verify ADB reverse: `adb reverse --list`
- Check server is running: `netstat -ano | findstr :8000`
- Make sure both phone and computer are on same network (if using WiFi)

---

## 📝 **QUICK REFERENCE**

| Action | Command |
|--------|---------|
| **Stop Server** | `Get-Process python \| Stop-Process -Force` |
| **Start Server** | `cd C:\Users\Dell\EventManagerBackend`<br>`.\venv\Scripts\Activate.ps1`<br>`python manage.py runserver 0.0.0.0:8000` |
| **Setup ADB** | `adb reverse tcp:8000 tcp:8000` |
| **Check Server** | `netstat -ano \| findstr :8000` |
| **Check ADB** | `adb reverse --list` |
| **Check Phone** | `adb devices` |

---

**💡 Tip:** Keep the server terminal window open while developing. You'll see all API requests and any errors in real-time!

