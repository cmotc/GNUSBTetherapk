#! /bin/sh
rm -rf jni/slirp/docs/ jni/slirp/src && rm jni/* && mkdir -p jni/
mv jni/slirp jni/bakslirp && mv -f jni/bakslirp/.git jni/slirp/.git && rm -rf jni/bakslirp
cd jni/slirp && git add . && git commit -am "auto-update" && git push origin master && ../..
wget http://ftp.de.debian.org/debian/pool/main/s/slirp/slirp_1.0.17.orig.tar.gz -O slirp.tar.gz && tar -zxvf slirp.tar.gz -C jni/
cd jni && mv slirp-* slirp && cd ../
rm slirp.tar.gz
export NDK="$HOME/.bin/android-ndk-r10d"
export NDK_TOOLCHAIN=${NDK}/toolchains/arm-linux-androideabi-4.6/prebuilt/linux-x86_64/bin/arm-linux-androideabi-
export NDK_SYSROOT=${NDK}/platforms/android-9/arch-arm
cd jni/slirp/src 
./configure #CC=${NDK_TOOLCHAIN}gcc LDFLAGS=-L${NDK_SYSROOT}/usr/lib CFLAGS=-I${NDK_SYSROOT}/usr/include
make CC=${NDK_TOOLCHAIN}gcc CFLAGS=--sysroot=${NDK_SYSROOT} PPPCFLAGS=--sysroot=${NDK_SYSROOT}
