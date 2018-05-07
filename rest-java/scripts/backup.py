#!/usr/bin/env python
"""
usage: backup.py [-h] [-m] [folders [folders ...]]

positional arguments:`
  folders          List of folders to check for duplicates

optional arguments:
  -h, --help       show this help message and exit
  -m, --summarize  summary information only
"""

import sys
import os
# import hashlib
# import argparse
# import sys
import subprocess
import tarfile

sys.path.insert(0, '/scripts')
import esutils


def do_backup():
    backup_id = 3
    print("=============> Starting Backup - backup_id = "+str(backup_id))
    print("=============> Backing up - Elasticsearch ...")
    config = esutils.read_config()
    esutils.snapshot_indices_from_src_to_fs(config, backup_id)
    print("=============> Backing up - HDFS ...")
    alluxio_backup_cmd = "alluxio fs copyToLocal /hdfs /backup/hdfs/"+str(backup_id)
    # alluxio_backup_cmd = "echo alluxio fs copyToLocal /hdfs /backup/hdfs/"+str(backup_id)
    subprocess.check_output([alluxio_backup_cmd], shell=True)
    print("=============> Backing up - MySQL ...")
    bfile = 'elastest_mysql.sql'
    cnf = '/scripts/mysqlbackup.cnf'
    dumpdir = "/backup/mysql/"+str(backup_id)
    print("=============> dumpdir = "+dumpdir)
    if not os.path.exists(dumpdir):
        print("=============> Creating dumpdir - "+dumpdir+" ...")
        os.makedirs(dumpdir)
    dumpfilepath = dumpdir + '/' + bfile
    dumpfile = open(dumpfilepath, 'w')
    print("=============> dumpfile = "+str(dumpfile))
    cmd = ['mysqldump', '--defaults-extra-file='+cnf,
           '--all-databases', '--events']
    p = subprocess.Popen(cmd, stdout=dumpfile)
    retcode = p.wait()
    dumpfile.close()
    if retcode > 0:
        print('Error: MySQL Backup error')
    return backup_id


# Main
if __name__ == "__main__":
    # args = parse_args()
    results = do_backup()
