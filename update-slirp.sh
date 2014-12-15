#! /bin/sh
rm -rf jni/bakslirp
mv -f jni/slirp jni/bakslirp && rm -rf jni/slirp
mv -f jni/bakslirp/.git jni/slirp/.git
rm -rf jni/bakslirp/docs/ jni/bakslirp/src
wget http://ftp.de.debian.org/debian/pool/main/s/slirp/slirp_1.0.17.orig.tar.gz -O slirp.tar.gz && tar -zxvf slirp.tar.gz -C jni/
cd jni && mv slirp-* slirp && cd ../
mv -f jni/bakslirp/.git jni/slirp/.git
cd jni/slirp && git checkout debian && git add . && git commit -am "auto-update" && git push origin debian && cd ../..
rm -rf jni/bakslirp
rm slirp.tar.gz
export NDK="$HOME/.bin/android-ndk-r10d"
export NDK_TOOLCHAIN=${NDK}/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86_64/bin/arm-linux-androideabi-
export NDK_SYSROOT=${NDK}/platforms/android-9/arch-arm
cd jni/slirp/src 
#./configure #CC=${NDK_TOOLCHAIN}gcc LDFLAGS=-L${NDK_SYSROOT}/usr/lib CFLAGS=-I${NDK_SYSROOT}/usr/include
#make CC=${NDK_TOOLCHAIN}gcc CFLAGS=--sysroot=${NDK_SYSROOT} PPPCFLAGS=--sysroot=${NDK_SYSROOT}
