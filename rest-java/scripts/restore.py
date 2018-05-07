#!/usr/bin/env python
"""
usage: backup.py [-h] [-m] [folders [folders ...]]

positional arguments:`
  folders          List of folders to check for duplicates

optional arguments:
  -h, --help       show this help message and exit
  -m, --summarize  summary information only
"""

import os
# import hashlib
# import argparse
import sys
import subprocess
import tarfile

sys.path.insert(0, '/scripts')
import esutils


def do_restore():
    backup_id = 3
    print("=============> Starting Restore - backup_id = "+str(backup_id))
    print("=============> Restoring - Elasticsearch ...")
    config = esutils.read_config()
    esutils.restore_indices_from_fs_to_dest(config, backup_id)
    print("=============> Restoring - HDFS ...")
    cmd = "alluxio fs copyFromLocal /backup/hdfs/"+str(backup_id)+" /hdfs"
    subprocess.check_output([cmd], shell=True)
    print("=============> Restoring - MySQL ...")
    bfile = 'elastest_mysql.sql'
    cnf = '/scripts/mysqlbackup.cnf'
    dumpdir = "/backup/mysql/"+str(backup_id)
    print("=============> dumpdir = "+dumpdir)
    dumpfilepath = dumpdir + '/' + bfile
    print("=============> dumpfilepath = "+str(dumpfilepath))
    cmd = ['mysql', '--defaults-extra-file='+cnf, '-e', 'source '+dumpfilepath]
    p = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    stdout, stderr = p.communicate()
    if p.returncode > 0:
        print('MySQL Restore Error:')
        print(stderr)
        sys.exit(1)


# Main
if __name__ == "__main__":
    # args = parse_args()
    results = do_restore()
