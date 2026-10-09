#!/bin/bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
MANIFEST="$ROOT/AndroidManifest.xml"
ACTIVITY="$ROOT/src/com/dumbphone/flashlight/FlashlightActivity.java"
POLICY="$ROOT/src/com/dumbphone/flashlight/ColorModePolicy.java"
BUILD="$ROOT/build.sh"
STYLES="$ROOT/res/values/styles.xml"
ICON="$ROOT/res/drawable/ic_flashlight.xml"
[[ -f "$MANIFEST" && -f "$ACTIVITY" && -f "$POLICY" && -f "$BUILD" && -f "$STYLES" && -f "$ICON" ]]
grep -Fq 'package="com.dumbphone.flashlight"' "$MANIFEST"
grep -Fq 'android:versionCode="15"' "$MANIFEST"
grep -Fq 'android:versionName="0.3.2"' "$MANIFEST"
grep -Fq 'android:name=".FlashlightActivity"' "$MANIFEST"
grep -Fq 'android:exported="false"' "$MANIFEST"
grep -Fq 'android:name=".FlashlightAlias"' "$MANIFEST"
grep -Fq 'android:enabled="true"' "$MANIFEST"
grep -Fq 'android:exported="true"' "$MANIFEST"
grep -Fq 'android:label="Flashlight"' "$MANIFEST"
grep -Fq 'android:fillColor="#111111"' "$ICON"
grep -Fq 'android:pathData="M2,2h44v44h-44z"' "$ICON"
grep -Fq 'android:fillColor="#FFC107"' "$ICON"
grep -Fq 'android:fillColor="#FFFFFF"' "$ICON"
grep -Fq 'android:pivotX="24" android:pivotY="24"' "$ICON"
grep -Fq 'android:scaleX="0.8" android:scaleY="0.8"' "$ICON"
grep -Fq 'M12,6h24l-4,12h-4v24h-8V18h-4z' "$ICON"
if grep -Fq 'android:rotation=' "$ICON"; then
  echo 'flashlight icon must point straight up' >&2
  exit 1
fi
grep -Fq 'WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON' "$ACTIVITY"
grep -Fq 'WHITE("white", "White", 0xffffffff)' "$POLICY"
grep -Fq 'BrightnessPolicy.windowBrightness' "$ACTIVITY"
grep -Fq 'KeyEvent.KEYCODE_DPAD_UP' "$ACTIVITY"
grep -Fq 'KeyEvent.KEYCODE_DPAD_DOWN' "$ACTIVITY"
grep -Fq 'KeyEvent.KEYCODE_DPAD_LEFT' "$ACTIVITY"
grep -Fq 'KeyEvent.KEYCODE_DPAD_RIGHT' "$ACTIVITY"
grep -Fq 'getSharedPreferences("flashlight", MODE_PRIVATE)' "$ACTIVITY"
grep -Fq 'getString("color_mode", null)' "$ACTIVITY"
grep -Fq 'getInt("brightness_percent", BrightnessPolicy.initialPercent())' "$ACTIVITY"
grep -Fq 'putString("color_mode", colorMode.preferenceValue()).commit()' "$ACTIVITY"
grep -Fq 'putInt("brightness_percent", brightnessPercent).commit()' "$ACTIVITY"
grep -Fq 'whiteScreen.setBackgroundColor(colorMode.argb())' "$ACTIVITY"
grep -Fq 'getIdentifier("decor_menu_bar", "id", "android")' "$ACTIVITY"
grep -Fq 'colorDecorMenuBar(menuBar, colorMode.argb())' "$ACTIVITY"
grep -Fq 'getWindow().setStatusBarColor(colorMode.argb())' "$ACTIVITY"
grep -Fq 'getWindow().setNavigationBarContrastEnforced(false)' "$ACTIVITY"
grep -Fq 'View.SYSTEM_UI_FLAG_LAYOUT_STABLE' "$ACTIVITY"
if grep -Eq 'SYSTEM_UI_FLAG_HIDE_NAVIGATION|SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|SYSTEM_UI_FLAG_IMMERSIVE' "$ACTIVITY"; then
  echo 'hidden TCL navigation region renders black; leave it visible and color-match it' >&2
  exit 1
fi
grep -Fq '<item name="android:navigationBarColor">#000000</item>' "$STYLES"
grep -Fq '<item name="android:windowDisablePreview">true</item>' "$STYLES"
grep -Fq 'brightnessLabel.setText(brightnessPercent + "%")' "$ACTIVITY"
grep -Fq 'applyColorMode(false)' "$ACTIVITY"
if grep -Fq 'colorMode.label()' "$ACTIVITY"; then
  echo 'color changes must not show redundant labels' >&2
  exit 1
fi
if grep -Eq 'FLAG_SHOW_WHEN_LOCKED|setShowWhenLocked|requestDismissKeyguard|FLAG_TURN_SCREEN_ON|setTurnScreenOn' "$ACTIVITY" "$MANIFEST"; then
  echo 'flashlight must not bypass keyguard or wake the screen' >&2
  exit 1
fi
grep -Fq 'Flashlight-v0.3.2.apk' "$BUILD"
echo 'screen-flashlight contract PASS'
