#!/bin/bash

# --- CONFIGURATION ---
# Your specific device ID and paths
DEVICE_ID="RZCW410VBWA"
ADB_PATH="/Users/omsharma/Library/Android/sdk/platform-tools/adb"
BUNDLETOOL="bundletool-all-1.18.2.jar"
PACKAGE_NAME="com.omsharma.crikstats"

echo "=========================================="
echo "🚀 STARTING AUTOMATED DEPLOYMENT"
echo "=========================================="

# 1. Build the Debug Bundle
echo "\n[1/5] Building Debug Bundle with Gradle..."
./gradlew bundleDebug

if [ $? -ne 0 ]; then
    echo "❌ Gradle Build Failed!"
    exit 1
fi

# 2. Convert Bundle to APKs
echo "\n[2/5] Converting Bundle to APKs (Local Testing)..."
# Remove old file to ensure freshness
rm -f crikstats_debug.apks

java -jar $BUNDLETOOL build-apks \
  --bundle=app/build/outputs/bundle/debug/app-debug.aab \
  --output=crikstats_debug.apks \
  --local-testing

# 3. Uninstall Old App (Prevents conflicts)
echo "\n[3/5] Uninstalling previous version..."
$ADB_PATH -s $DEVICE_ID uninstall $PACKAGE_NAME

# 4. Install New APKs
echo "\n[4/5] Installing new APKs to device..."
java -jar $BUNDLETOOL install-apks \
  --apks=crikstats_debug.apks \
  --adb=$ADB_PATH \
  --device-id=$DEVICE_ID

# 5. Fix Permissions (The 'Nuclear' Fix for I/O Errors)
echo "\n[5/5] Force-Granting Storage Permissions..."
$ADB_PATH -s $DEVICE_ID shell appops set --uid $PACKAGE_NAME MANAGE_EXTERNAL_STORAGE allow
$ADB_PATH -s $DEVICE_ID shell pm grant $PACKAGE_NAME android.permission.READ_EXTERNAL_STORAGE
$ADB_PATH -s $DEVICE_ID shell pm grant $PACKAGE_NAME android.permission.WRITE_EXTERNAL_STORAGE

echo "\n=========================================="
echo "✅ DEPLOYMENT COMPLETE! Open the app now."
echo "=========================================="
