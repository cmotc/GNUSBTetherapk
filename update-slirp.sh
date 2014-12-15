#! /bin/sh
wget http://ftp.de.debian.org/debian/pool/main/s/slirp/slirp_1.0.17.orig.tar.gz -O slirp.tar.gz && rm -rf slirp && mkdir slirp && tar -zxvf slirp.tar.gz -C slirp
cd slirp/slirp-*/src && ./configure && make