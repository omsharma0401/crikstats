#!/bin/bash

# --- CONFIGURATION ---
PACKAGE_NAME="com.omsharma.crikstats"
BUNDLETOOL="bundletool-all-1.18.2.jar"
# Fallback path if 'adb' is not in your global PATH
MANUAL_ADB_PATH="/Users/omsharma/Library/Android/sdk/platform-tools/adb"

# --- COLORS ---
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}==========================================${NC}"
echo -e "${GREEN}🚀 STARTING SMART DEPLOYMENT${NC}"
echo -e "${GREEN}==========================================${NC}"

# --- 0. PRE-CHECKS ---

# Check if Bundletool exists
if [ ! -f "$BUNDLETOOL" ]; then
    echo -e "${RED}❌ Error: Bundletool jar not found: $BUNDLETOOL${NC}"
    echo "Please download it or check the path."
    exit 1
fi

# Locate ADB
if command -v adb &> /dev/null; then
    ADB_CMD="adb"
elif [ -f "$MANUAL_ADB_PATH" ]; then
    ADB_CMD="$MANUAL_ADB_PATH"
else
    echo -e "${RED}❌ Error: ADB not found in PATH or at $MANUAL_ADB_PATH${NC}"
    exit 1
fi

# --- DEVICE DETECTION LOGIC ---

echo -e "\n[1/6] Detecting Devices..."

# Get list of devices (excluding the header line and empty lines)
# We use 'adb devices -l' to get model info, which is helpful for selection
raw_devices=$($ADB_CMD devices -l | grep "device " | grep -v "List of devices")

if [ -z "$raw_devices" ]; then
    echo -e "${RED}❌ No devices connected. Please connect a device and try again.${NC}"
    exit 1
fi

# Read device lines into an array
IFS=$'\n' read -rd '' -a device_lines <<< "$raw_devices"

count=${#device_lines[@]}

if [ $count -eq 1 ]; then
    # Only one device, grab the Serial ID (first word)
    DEVICE_ID=$(echo "${device_lines[0]}" | awk '{print $1}')
    MODEL=$(echo "${device_lines[0]}" | grep -o 'model:[^ ]*' | cut -d: -f2)
    echo -e "${GREEN}✅ Auto-detected single device: $MODEL ($DEVICE_ID)${NC}"
else
    # Multiple devices, prompt user
    echo -e "${YELLOW}⚠️  Multiple devices found:${NC}"

    i=1
    for line in "${device_lines[@]}"; do
        serial=$(echo "$line" | awk '{print $1}')
        model=$(echo "$line" | grep -o 'model:[^ ]*' | cut -d: -f2)
        echo "  [$i] $model ($serial)"
        ((i++))
    done

    read -p "👉 Select device number (1-$count): " selection

    # Validate input
    if ! [[ "$selection" =~ ^[0-9]+$ ]] || [ "$selection" -lt 1 ] || [ "$selection" -gt "$count" ]; then
         echo -e "${RED}❌ Invalid selection.${NC}"
         exit 1
    fi

    # Get the selected device ID
    index=$((selection-1))
    DEVICE_ID=$(echo "${device_lines[$index]}" | awk '{print $1}')
    echo -e "Selected: $DEVICE_ID"
fi


# --- BUILD AND DEPLOY ---

# 1. Build the Debug Bundle
echo -e "\n[2/6] Building Debug Bundle with Gradle..."
./gradlew bundleDebug

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Gradle Build Failed!${NC}"
    exit 1
fi

# 2. Convert Bundle to APKs
echo -e "\n[3/6] Converting Bundle to APKs..."
rm -f crikstats_debug.apks

java -jar $BUNDLETOOL build-apks \
  --bundle=app/build/outputs/bundle/debug/app-debug.aab \
  --output=crikstats_debug.apks \
  --local-testing \
  --connected-device \
  --device-id=$DEVICE_ID \
  --adb=$ADB_CMD
# Added --connected-device above: optimizes APKs specifically for the target device (smaller size)

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Build APKs Failed!${NC}"
    exit 1
fi

# 3. Uninstall Old App
echo -e "\n[4/6] Uninstalling previous version..."
$ADB_CMD -s $DEVICE_ID uninstall $PACKAGE_NAME
# We ignore errors here (e.g., if app wasn't installed)

# 4. Install New APKs
echo -e "\n[5/6] Installing new APKs to device..."
java -jar $BUNDLETOOL install-apks \
  --apks=crikstats_debug.apks \
  --adb=$ADB_CMD \
  --device-id=$DEVICE_ID

if [ $? -ne 0 ]; then
    echo -e "${RED}❌ Installation Failed!${NC}"
    exit 1
fi

# 5. Fix Permissions
echo -e "\n[6/6] Granting Permissions..."
$ADB_CMD -s $DEVICE_ID shell appops set --uid $PACKAGE_NAME MANAGE_EXTERNAL_STORAGE allow
$ADB_CMD -s $DEVICE_ID shell pm grant $PACKAGE_NAME android.permission.READ_EXTERNAL_STORAGE
$ADB_CMD -s $DEVICE_ID shell pm grant $PACKAGE_NAME android.permission.WRITE_EXTERNAL_STORAGE

echo -e "\n${GREEN}==========================================${NC}"
echo -e "${GREEN}✅ DEPLOYMENT COMPLETE! Open the app now.${NC}"
echo -e "${GREEN}==========================================${NC}"