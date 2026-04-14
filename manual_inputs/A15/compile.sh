#!/bin/bash

set -e

cd hostapd

cp defconfig .config

make

cd ..



cd wpa_supplicant

cp defconfig .config

make

cd ..



cd wlantest

make

cd ..











cd wpa_supplicant

cp ../tests/hwsim/example-wpa_supplicant.config .config

make clean

make

cd ../hostapd

cp ../tests/hwsim/example-hostapd.config .config

make clean

make hostapd hostapd_cli hlr_auc_gw

cd ../wlantest

make clean

make











# cd ../../wpa_supplicant

# cp ../tests/hwsim/example-wpa_supplicant.config .config

# make clean

# make

# cd ../hostapd

# cp ../tests/hwsim/example-hostapd.config .config

# make clean

# make hostapd hostapd_cli hlr_auc_gw

# cd ../wlantest

# make clean

# make





echo "Setup complete. Both hostapd and wpa_supplicant have been compiled."