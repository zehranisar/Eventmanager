# How to Use ADB Reverse Port Forwarding

## What is ADB Reverse?

ADB Reverse allows your Android device (connected via USB) to access services running on your computer's localhost (127.0.0.1). This is useful when your Django server is running on `localhost:8000` and you want your Android app to connect to it.

## Step-by-Step Instructions

### Step 1: Enable USB Debugging on Your Android Device

1. Go to **Settings** on your Android device
2. Scroll down and tap **About phone** (or **About device**)
3. Find **Build number** and tap it **7 times** until you see "You are now a developer!"
4. Go back to Settings
5. Find **Developer options** (usually under System or Advanced)
6. Enable **USB debugging**
7. (Optional) Enable **Stay awake** (keeps screen on while charging)

### Step 2: Connect Your Device via USB

1. Connect your Android device to your computer using a USB cable
2. On your device, you may see a popup asking "Allow USB debugging?" - tap **Allow**
3. Check "Always allow from this computer" if you want to avoid this popup in the future

### Step 3: Verify ADB is Working

**Windows:**
1. Open **Command Prompt** (Press `Win + R`, type `cmd`, press Enter)
2. Type: `adb devices`
3. You should see your device listed, for example:
   ```
   List of devices attached
   ABC123XYZ    device
   ```

**Mac/Linux:**
1. Open **Terminal**
2. Type: `adb devices`
3. You should see your device listed

**If you see "adb is not recognized" or "command not found":**
- You need to install Android SDK Platform Tools
- Download from: https://developer.android.com/studio/releases/platform-tools
- Extract and add to your PATH, or use the full path to adb.exe

### Step 4: Run the ADB Reverse Command

**Windows (Command Prompt):**
```bash
adb reverse tcp:8000 tcp:8000
```

**Mac/Linux (Terminal):**
```bash
adb reverse tcp:8000 tcp:8000
```

**What this does:**
- Maps port 8000 on your Android device to port 8000 on your computer
- Now when your app tries to connect to `127.0.0.1:8000`, it will actually connect to your computer's `localhost:8000`

### Step 5: Verify It's Working

After running the command, you should see:
```
8000
```

This means the port forwarding is active.

### Step 6: Start Your Django Server

Make sure your Django server is running:
```bash
python manage.py runserver
```

Or if you want it accessible from network too:
```bash
python manage.py runserver 0.0.0.0:8000
```

### Step 7: Test Your App

Now your Android app should be able to connect to `127.0.0.1:8000`!

## Important Notes

### ⚠️ You Need to Run This Every Time

- **Every time you disconnect and reconnect your device**, you need to run the command again
- **Every time you restart your computer**, you need to run the command again
- The port forwarding is **not permanent** - it only lasts while your device is connected

### Check Active Port Forwards

To see all active port forwards:
```bash
adb reverse --list
```

### Remove Port Forward

To remove a specific port forward:
```bash
adb reverse --remove tcp:8000
```

To remove all port forwards:
```bash
adb reverse --remove-all
```

## Troubleshooting

### Problem: "adb: command not found"

**Solution:**
1. Install Android SDK Platform Tools
2. Or use the full path to adb.exe:
   - Windows: `C:\Users\YourName\AppData\Local\Android\Sdk\platform-tools\adb.exe reverse tcp:8000 tcp:8000`
   - Mac/Linux: `/path/to/android-sdk/platform-tools/adb reverse tcp:8000 tcp:8000`

### Problem: "device not found" or "no devices/emulators found"

**Solutions:**
1. Make sure USB debugging is enabled on your device
2. Try unplugging and reconnecting the USB cable
3. On your device, check if you see "Allow USB debugging?" popup and tap Allow
4. Try a different USB cable
5. Try a different USB port on your computer
6. Install device drivers (if Windows)

### Problem: "adb server is out of date"

**Solution:**
1. Kill the ADB server: `adb kill-server`
2. Start it again: `adb start-server`
3. Run the reverse command again: `adb reverse tcp:8000 tcp:8000`

### Problem: Still can't connect after running the command

**Solutions:**
1. Make sure Django server is running: `python manage.py runserver`
2. Check if port 8000 is already in use
3. Try restarting ADB: `adb kill-server && adb start-server`
4. Verify the port forward is active: `adb reverse --list`
5. Check your app's API configuration is using `127.0.0.1:8000`

## Alternative: Create a Batch/Script File (Windows)

Create a file called `start-adb-reverse.bat` with this content:
```batch
@echo off
echo Setting up ADB reverse port forwarding...
adb reverse tcp:8000 tcp:8000
if %errorlevel% equ 0 (
    echo Port forwarding successful!
    echo Your app can now connect to localhost:8000
) else (
    echo Failed to set up port forwarding
    echo Make sure your device is connected and USB debugging is enabled
)
pause
```

Double-click this file whenever you connect your device!

## Alternative: Create a Shell Script (Mac/Linux)

Create a file called `start-adb-reverse.sh` with this content:
```bash
#!/bin/bash
echo "Setting up ADB reverse port forwarding..."
adb reverse tcp:8000 tcp:8000
if [ $? -eq 0 ]; then
    echo "Port forwarding successful!"
    echo "Your app can now connect to localhost:8000"
else
    echo "Failed to set up port forwarding"
    echo "Make sure your device is connected and USB debugging is enabled"
fi
```

Make it executable:
```bash
chmod +x start-adb-reverse.sh
```

Run it:
```bash
./start-adb-reverse.sh
```

## Quick Reference

```bash
# Check if device is connected
adb devices

# Set up port forwarding
adb reverse tcp:8000 tcp:8000

# Check active port forwards
adb reverse --list

# Remove port forward
adb reverse --remove tcp:8000

# Restart ADB server
adb kill-server
adb start-server
```

