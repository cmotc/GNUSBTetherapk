GNUSBTetherapk
==============

A fully-free USB tethering app for Android, device side program. It builds upon
the example [here](https://ajasmin.wordpress.com/2011/07/24/android-usb-tethering-with-a-linux-pc/)
and the original source code [here](https://github.com/ajasmin/android-linux-tethering).
This portion of the code will provide an Android project which fits into the 
Android build system and which can be compiled as an apk. It will eventually 
contain the SLiRP source code as a JNI library so it can do PPP in user space, 
and respective pieces of the source code are redistributed under their 
respective licenses without alteration. For now, the native binary is packaged
as an asset and installed and configured by the application. Please see the 
./slirp folder for slirp specific information as it becomes available. SLIRP 
code from Debian Sid. Also [an experimental cross-compilation process for SLiRP on android.](https://github.com/cmotc/slirp-cross-compiler-for-android)