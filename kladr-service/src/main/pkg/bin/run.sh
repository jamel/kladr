#!/bin/sh

exec java -Xms256m -Xmx512m -XX:NewRatio=2 -server -showversion \
    -classpath '/var/lib/kladr/libs/*' \
    -Dfile.encoding=UTF-8 \
    -Djava.io.tmpdir=/var/tmp \
    org.jamel.kladr.KladrApplication "$@"
