#!/bin/bash
# ═══════════════════════════════════════════════════════════
# হালাল রক্ত দান v2.0 — Release APK build script
# Manual toolchain: aapt2 → javac → d8 → zip → zipalign → apksigner
# ═══════════════════════════════════════════════════════════
set -e

ROOT=/home/z/my-project/android-build
APP=$ROOT/app
BUILD=$ROOT/build
SDK=$ROOT/sdk
BT=$SDK/build-tools/35.0.0
PLATFORM=$SDK/platforms/android-34/android.jar
JDK=$ROOT/jdk
KEYSTORE=$ROOT/halal-rokto-release.keystore
KS_PASS="halalrokto2026"
OUT_APK=$ROOT/halal-rokto-dan-v2.0.0-release.apk

export PATH=$JDK/bin:$PATH

echo "── [1/8] aapt2 compile resources"
rm -rf "$BUILD" && mkdir -p "$BUILD/gen" "$BUILD/classes" "$BUILD/dex"
$BT/aapt2 compile --dir "$APP/res" -o "$BUILD/res.zip"

echo "── [2/8] aapt2 link (manifest + resources + assets)"
$BT/aapt2 link \
  -o "$BUILD/base.apk" \
  -I "$PLATFORM" \
  --manifest "$APP/AndroidManifest.xml" \
  -R "$BUILD/res.zip" \
  -A "$APP/assets" \
  --java "$BUILD/gen" \
  --min-sdk-version 26 \
  --target-sdk-version 34 \
  --auto-add-overlay \
  --version-code 2 \
  --version-name 2.0.0

echo "── [3/8] javac compile"
find "$BUILD/gen" -name "*.java" > "$BUILD/sources.txt"
echo "$APP/java/com/hizlydighapara/halalrokto/MainActivity.java" >> "$BUILD/sources.txt"
$JDK/bin/javac \
  -source 11 -target 11 \
  -classpath "$PLATFORM" \
  -d "$BUILD/classes" \
  -encoding UTF-8 \
  -Xlint:-options,-cast,-serial \
  @"$BUILD/sources.txt"

echo "── [4/8] d8 dex (release)"
cd "$BUILD/classes" && $JDK/bin/jar cf "$BUILD/classes.jar" . && cd "$ROOT"
$BT/d8 --release \
  --lib "$PLATFORM" \
  --min-api 26 \
  --output "$BUILD/dex" \
  "$BUILD/classes.jar"

echo "── [5/8] package classes.dex into APK"
cd "$BUILD/dex" && zip -q -j "$BUILD/base.apk" classes.dex && cd "$ROOT"

echo "── [6/8] zipalign"
$BT/zipalign -f 4 "$BUILD/base.apk" "$BUILD/aligned.apk"

echo "── [7/8] generate keystore (if missing) + sign"
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
$BT/apksigner verify --print-certs "$OUT_APK" | head -6
ls -lh "$OUT_APK"
echo ""
echo "✅ BUILD COMPLETE: $OUT_APK"
