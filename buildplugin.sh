#!/bin/sh
var0="./plugindemo/build/outputs/apk/plugindemo-debug.apk"
var1="./dyloadlib/src/main/assets/pluginpkg"
echo var0=$var0
echo var1=$var1
echo currentpath:
pwd
{
./gradlew :plugindemo:assembleDebug
rm $var1
mv $var0 $var1
adb shell rm -r /sdcard/DyLoad
}
exit 0

