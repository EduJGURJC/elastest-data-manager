#!/bin/bash

# This is a wrapper script, to start the namenode together with the NFS sharing  
service. 
# Author: Nick Gavalas


/usr/local/hadoop/sbin/hadoop-daemon.sh --script hdfs start namenode
/usr/local/hadoop/sbin/hadoop-daemon.sh --script hdfs start portmap
/usr/local/hadoop/sbin/hadoop-daemon.sh --script hdfs start nfs3

# sleep for some time, to allow services to start. Unfortunately this is 
sleep 20
mount -t nfs -o vers=3,proto=tcp,nolock,noacl,sync 127.0.0.1:/ /mnt
sleep 2
cloudcmd --show-config --config /cloudcmd.json

# all the above detach, so stay alive to keep the container running.
# while [ 1 ] ; do
#    sleep 1d
# done

