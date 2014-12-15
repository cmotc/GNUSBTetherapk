#! /bin/sh
rm -rf jni && mkdir -p jni/
wget http://ftp.de.debian.org/debian/pool/main/s/slirp/slirp_1.0.17.orig.tar.gz -O slirp.tar.gz && tar -zxvf slirp.tar.gz -C jni/
cd jni && mv slirp-* slirp && cd ../
rm slirp.tar.gz
cd jni/slirp/src && ./configure && make