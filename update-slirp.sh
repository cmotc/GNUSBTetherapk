#! /bin/sh
rm -rf jni/bakslirp
mv -f jni/slirp jni/bakslirp
rm -rf jni/bakslirp/docs/ jni/bakslirp/src
wget http://ftp.de.debian.org/debian/pool/main/s/slirp/slirp_1.0.17.orig.tar.gz -O slirp.tar.gz && tar -zxvf slirp.tar.gz -C jni/
cd jni && mv slirp-* slirp && cd ../
mv -f jni/bakslirp/.git jni/slirp/.git
cd jni/slirp && git add . && git commit -am "auto-update" && git push origin debian
cd ../..
pwd
rm -rf jni/bakslirp
rm slirp.tar.gz
export NDK="$HOME/.bin/android-ndk-r10d"
export NDK_TOOLCHAIN=${NDK}/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86_64/bin/arm-linux-androideabi-
export NDK_SYSROOT=${NDK}/platforms/android-9/arch-arm
export WORK=pwd
cd jni/slirp/src 
export CROSS_COMPILE=${NDK_TOOLCHAIN}gcc 
export CPPFLAGS=-I${NDK_SYSROOT}/usr/include
./configure #CROSS_COMPILE=${NDK_TOOLCHAIN}gcc CPPFLAGS=-I${NDK_SYSROOT}/usr/include
#make CC="$HOME/.bin/android-ndk-r10d/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86_64/bin/arm-linux-androideabi-gcc" CFLAGS=--sysroot="$HOME/.bin/android-ndk-r10d/platforms/android-9/arch-arm" #PPPCFLAGS=--sysroot="$HOME/.bin/android-ndk-r10d/platforms/android-9/arch-arm"
make CPPFLAGS=-I${WORK} #CC=${NDK_TOOLCHAIN}gcc CFLAGS=--sysroot=${NDK_SYSROOT} PPPCFLAGS=--sysroot=${NDK_SYSROOT}
echo ${WORK}
