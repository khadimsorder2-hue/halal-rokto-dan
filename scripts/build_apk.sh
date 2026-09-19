#!/bin/bash
# ═══════════════════════════════════════════════════════════
# হালাল রক্ত দান v3.0 — 100% NATIVE Android (no WebView)
# Manual toolchain: aapt2 → javac → d8 → zip → zipalign → apksigner
# ═══════════════════════════════════════════════════════════
set -e

ROOT="$(cd "$(dirname "$0")/.." && pwd)"/android-build
APP="$(dirname "$0")/../app"
BUILD=$ROOT/build-v3
SDK=$ROOT/sdk
BT=$SDK/build-tools/35.0.0
PLATFORM=$SDK/platforms/android-34/android.jar
JDK=/home/z/my-project/android-build/jdk
KEYSTORE=$ROOT/halal-rokto-v3-release.keystore
KS_PASS="halalrokto2026"
OUT_APK=$ROOT/halal-rokto-dan-v3.0.0-native-release.apk

echo "── [1/8] aapt2 compile resources"
rm -rf "$BUILD" && mkdir -p "$BUILD/gen" "$BUILD/classes" "$BUILD/dex"
$BT/aapt2 compile --dir "$APP/res" -o "$BUILD/res.zip"

echo "── [2/8] aapt2 link"
$BT/aapt2 link \
  -o "$BUILD/base.apk" \
  -I "$PLATFORM" \
  --manifest "$APP/AndroidManifest.xml" \
  -R "$BUILD/res.zip" \
  --java "$BUILD/gen" \
  --min-sdk-version 26 \
  --target-sdk-version 34 \
  --auto-add-overlay \
  --version-code 3 \
  --version-name 3.0.0

echo "── [3/8] javac compile (native Java, no WebView)"
find "$BUILD/gen" -name "*.java" > "$BUILD/sources.txt"
find "$APP/java" -name "*.java" >> "$BUILD/sources.txt"
wc -l $(cat "$BUILD/sources.txt") | tail -1
$JDK/bin/javac \
  -source 11 -target 11 \
  -classpath "$PLATFORM" \
  -d "$BUILD/classes" \
  -encoding UTF-8 \
  -Xlint:-options,-cast,-serial,-deprecation \
  @"$BUILD/sources.txt"

echo "── [4/8] d8 dex (release)"
cd "$BUILD/classes" && $JDK/bin/jar cf "$BUILD/classes.jar" . && cd "$ROOT"
$BT/d8 --release \
  --lib "$PLATFORM" \
  --min-api 26 \
  --output "$BUILD/dex" \
  "$BUILD/classes.jar"

echo "── [5/8] package classes.dex"
cd "$BUILD/dex" && zip -q -j "$BUILD/base.apk" classes.dex && cd "$ROOT"

echo "── [6/8] zipalign"
$BT/zipalign -f 4 "$BUILD/base.apk" "$BUILD/aligned.apk"

echo "── [7/8] keystore + sign"
if [ ! -f "$KEYSTORE" ]; then
  keytool -genkeypair \
    -keystore "$KEYSTORE" \
    -storepass "$KS_PASS" -keypass "$KS_PASS" \
    -alias halalrokto \
    -keyalg RSA -keysize 2048 -validity 10950 \
    -dname "CN=Hizly Dighapara Jubo Sangho, OU=Halal Rokto Dan, O=Hizly Dighapara JUBO Sangho, L=Bagatipara, ST=Natore, C=BD"
fi
$BT/apksigner sign \
  --ks "$KEYSTORE" \
  --ks-pass "pass:$KS_PASS" \
  --key-pass "pass:$KS_PASS" \
  --ks-key-alias halalrokto \
  --out "$OUT_APK" \
  "$BUILD/aligned.apk"

echo "── [8/8] verify"
$BT/apksigner verify --print-certs "$OUT_APK" | head -4
ls -lh "$OUT_APK"
echo ""
echo "✅ NATIVE BUILD COMPLETE: $OUT_APK"
