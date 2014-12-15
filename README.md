GNUSBTetherapk
==============

A fully-free USB tethering app for Android, device side program. It builds upon
the example [here](https://ajasmin.wordpress.com/2011/07/24/android-usb-tethering-with-a-linux-pc/)
and the original source code [here](https://github.com/ajasmin/android-linux-tethering).
This portion of the code will provide an Android project which fits into the 
Android build system and which can be compiled as an apk. It contains the SLIRP 
source code so it can do PPP in user space, respective pieces of the source code
are redistributed under their respective licenses without alteration. Please
see the ./slirp folder for related information. SLIRP code from Debian Sid.