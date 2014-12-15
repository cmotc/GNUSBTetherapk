#! /bin/sh
rm -rf jni && mkdir -p jni/
wget http://ftp.de.debian.org/debian/pool/main/s/slirp/slirp_1.0.17.orig.tar.gz -O slirp.tar.gz && tar -zxvf slirp.tar.gz -C jni/
cd jni && mv slirp-* slirp && cd ../
rm slirp.tar.gz
export NDK="$HOME/.bin/android-ndk-r10d"
export NDK_TOOLCHAIN=${NDK}/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86_64/bin/arm-linux-androideabi-
export NDK_SYSROOT=${NDK}/platforms/android-9/arch-arm
cd jni/slirp/src 
./configure 
make CC=${NDK_TOOLCHAIN}gcc CFLAGS=--sysroot=${NDK_SYSROOT}
echo ${NDK_TOOLCHAIN}gcc 
ls ${NDK_TOOLCHAIN}gcc 
echo ${NDK_SYSROOT}
ls ${NDK_SYSROOT}