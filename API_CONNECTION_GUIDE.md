# API Connection Guide - Event Manager App

## Problem: Network Error "Failed to connect to 127.0.0.1:8000"

This error occurs because `127.0.0.1` (localhost) only works in specific scenarios. Here's how to fix it permanently:

## Solution Options

### Option 1: Android Emulator (Automatic)
If you're using an Android Emulator, the app will **automatically** use `10.0.2.2` which maps to your computer's localhost. **No configuration needed!**

### Option 2: Real Device via USB (ADB Reverse)
If you're using a real device connected via USB:

1. Connect your device via USB
2. Enable USB Debugging on your device
3. Open Command Prompt or Terminal
4. Run this command:
   ```
   adb reverse tcp:8000 tcp:8000
   ```
5. The app will use `127.0.0.1` automatically

**Note:** You need to run this command every time you reconnect your device.

### Option 3: Real Device via WiFi (Recommended - Permanent Solution)

This is the **best permanent solution** for real devices:

#### Step 1: Find Your Computer's IP Address

**Windows:**
1. Open Command Prompt
2. Type: `ipconfig`
3. Look for "IPv4 Address" under your active network adapter (usually WiFi or Ethernet)
4. Example: `192.168.1.100`

**Mac/Linux:**
1. Open Terminal
2. Type: `ifconfig` or `ip addr`
3. Look for your network interface (usually `wlan0` or `eth0`)
4. Find the `inet` address

#### Step 2: Make Sure Computer and Phone are on Same WiFi Network

Both your computer and Android device must be connected to the **same WiFi network**.

#### Step 3: Configure the App

You can set the IP address programmatically in your app. Add this code where you initialize the app (e.g., in `SplashActivity` or `MainActivity`):

```java
// Set your computer's IP address
ApiConfig.setServerIp(this, "192.168.1.100"); // Replace with your actual IP
```

Or create a settings screen where users can enter the IP address.

#### Step 4: Start Django Server

Make sure your Django server is running and accessible:

```bash
python manage.py runserver 0.0.0.0:8000
```

The `0.0.0.0` makes the server accessible from other devices on the network.

#### Step 5: Configure Firewall (If Needed)

If you still can't connect, you may need to allow port 8000 through your firewall:

**Windows:**
1. Open Windows Defender Firewall
2. Click "Advanced settings"
3. Click "Inbound Rules" → "New Rule"
4. Select "Port" → Next
5. Select "TCP" and enter port "8000"
6. Allow the connection

**Mac:**
1. System Preferences → Security & Privacy → Firewall
2. Click "Firewall Options"
3. Add Python or allow incoming connections on port 8000

## Quick Test

To test if your server is accessible:

1. On your computer, open a browser
2. Go to: `http://YOUR_IP:8000/api/events/`
3. If you see a response (even an error about authentication), the server is accessible!

## Code Example: Setting IP in App

Add this to your `SplashActivity` or create a settings activity:

```java
// In onCreate() or initialization method
String serverIp = "192.168.1.100"; // Your computer's IP
ApiConfig.setServerIp(this, serverIp);
Log.d("ApiConfig", "Server IP set to: " + serverIp);
```

## Troubleshooting

1. **Still getting connection error?**
   - Make sure Django server is running: `python manage.py runserver 0.0.0.0:8000`
   - Check that both devices are on the same WiFi
   - Verify your computer's IP hasn't changed (it can change when you reconnect to WiFi)
   - Check firewall settings

2. **IP address changed?**
   - Your computer's IP may change when you reconnect to WiFi
   - You can set a static IP on your router, or update the IP in the app

3. **Want to switch between methods?**
   - Use `ApiConfig.resetToDefault(context)` to reset
   - Then set the IP using `ApiConfig.setServerIp(context, ip)`

## Current Configuration

The app automatically detects:
- **Emulator**: Uses `10.0.2.2` automatically
- **Real Device**: Uses `127.0.0.1` (requires ADB reverse) or you can set your computer's IP

Check the current configuration in Logcat:
```
ApiConfig: Base URL: http://...
```

