#!/usr/bin/env bash

ant debug &> build_fail.out
if [ "$?" == "0" ]; then
    echo "Build Successful, installing to virtual device."
    adb -d install -r bin/*-debug.apk &> adb_device.out
    if [ "$?" == "0" ]; then
        echo "Installed successfully to device!"
    else
        echo "Device must not be attached. check adb_device.out for more info"
    fi
    adb -e install -r bin/*-debug.apk &> adb_device.out
    if [ "$?" == "0" ]; then
        echo "Installed successfully to emulator!"
    else
        echo "Emulator must not be started. check adb_device.out for more info"
    fi
else
    cat build_fail.out
fi
