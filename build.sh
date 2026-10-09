#!/bin/bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
BUILD="$ROOT/build"
SDK="${ANDROID_SDK_ROOT:-$HOME/Library/Android/sdk}"
BT="$SDK/build-tools/35.0.0"
ANDROID_JAR="$SDK/platforms/android-34/android.jar"
JAVA_HOME="${JAVA_HOME:-$(brew --prefix openjdk)/libexec/openjdk.jdk/Contents/Home}"
export JAVA_HOME PATH="$JAVA_HOME/bin:$PATH" LC_ALL=C
ARTIFACT_NAME="Flashlight-v0.3.2-no-white-preview-dev.apk"
FINAL_APK="$BUILD/$ARTIFACT_NAME"
TEMP_APK="$BUILD/.$ARTIFACT_NAME.tmp"
mkdir -p "$BUILD"
rm -f "$FINAL_APK" "$FINAL_APK.idsig" "$TEMP_APK" "$TEMP_APK.idsig" "$BUILD/SHA256SUMS"
bash "$ROOT/test.sh"
bash "$ROOT/contract-test.sh"
rm -rf "$BUILD/apk-classes" "$BUILD/dex" "$BUILD/generated" "$BUILD/resources.zip" "$BUILD/unsigned.apk" "$BUILD/aligned.apk"
mkdir -p "$BUILD/apk-classes" "$BUILD/dex" "$BUILD/generated"
"$BT/aapt2" compile --dir "$ROOT/res" -o "$BUILD/resources.zip"
"$BT/aapt2" link -o "$BUILD/unsigned.apk" -I "$ANDROID_JAR" --manifest "$ROOT/AndroidManifest.xml" --min-sdk-version 24 --target-sdk-version 30 "$BUILD/resources.zip" --java "$BUILD/generated"
find "$ROOT/src" -name '*.java' -print0 | sort -z | xargs -0 "$JAVA_HOME/bin/javac" -XDuseUnsharedTable=true -source 8 -target 8 -Xlint:-options -bootclasspath "$ANDROID_JAR" -cp "$BUILD/generated" -d "$BUILD/apk-classes" "$BUILD/generated/com/dumbphone/flashlight/R.java"
find "$BUILD/apk-classes/com/dumbphone/flashlight" -name '*.class' -print0 | sort -z | xargs -0 "$BT/d8" --lib "$ANDROID_JAR" --min-api 24 --output "$BUILD/dex"
touch -t 200001010000 "$BUILD/dex/classes.dex"
(cd "$BUILD/dex" && zip -X -q -j "$BUILD/unsigned.apk" classes.dex)
"$BT/zipalign" -f 4 "$BUILD/unsigned.apk" "$BUILD/aligned.apk"
PASS_FILE="$ROOT/.signing-password"; KEYSTORE="$ROOT/screen-flashlight.jks"; ALIAS=screen-flashlight
if { [[ -f "$PASS_FILE" ]] && [[ ! -f "$KEYSTORE" ]]; } || { [[ ! -f "$PASS_FILE" ]] && [[ -f "$KEYSTORE" ]]; }; then
  echo "Signing password and keystore must either both exist or both be absent" >&2; exit 1
fi
if [[ ! -f "$KEYSTORE" ]]; then
  umask 077
  openssl rand -base64 24 > "$PASS_FILE"
  export SCREEN_FLASHLIGHT_SIGNING_PASS="$(<"$PASS_FILE")"
  "$JAVA_HOME/bin/keytool" -genkeypair -noprompt -keystore "$KEYSTORE" -storepass:env SCREEN_FLASHLIGHT_SIGNING_PASS -keypass:env SCREEN_FLASHLIGHT_SIGNING_PASS -alias "$ALIAS" -keyalg RSA -keysize 2048 -validity 10000 -dname "CN=Screen Flashlight,OU=TCL Flip,O=Local,C=US"
  unset SCREEN_FLASHLIGHT_SIGNING_PASS
fi
KEY_PASS_FILE="$BUILD/.key-password"
install -m 600 "$PASS_FILE" "$KEY_PASS_FILE"
BUILD_SUCCEEDED=0
trap 'rm -f "$KEY_PASS_FILE" "$TEMP_APK" "$TEMP_APK.idsig"; if [[ "$BUILD_SUCCEEDED" != "1" ]]; then rm -f "$FINAL_APK" "$FINAL_APK.idsig" "$BUILD/SHA256SUMS"; fi' EXIT
"$BT/apksigner" sign --ks "$KEYSTORE" --ks-key-alias "$ALIAS" --ks-pass "file:$PASS_FILE" --key-pass "file:$KEY_PASS_FILE" --out "$TEMP_APK" "$BUILD/aligned.apk"
"$BT/apksigner" verify --verbose --print-certs "$TEMP_APK"
mv "$TEMP_APK" "$FINAL_APK"
(cd "$BUILD" && shasum -a 256 "$ARTIFACT_NAME" > SHA256SUMS)
cat "$BUILD/SHA256SUMS"
rm -f "$KEY_PASS_FILE"
BUILD_SUCCEEDED=1
trap - EXIT
