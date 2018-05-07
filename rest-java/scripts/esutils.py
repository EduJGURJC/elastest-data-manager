#!/usr/bin/env python

from elasticsearch import Elasticsearch
from elasticsearch.exceptions import NotFoundError
from time import strftime
# import ConfigParser
import configparser
import argparse

# Note: You MUST at least enter valid AWS API Keys in this file:
CONFIG_FILE = '/scripts/es-fs-snapshot.conf'

# Usage/Setup:
# 1. Put your AWS creds in the CONFIG_FILE file located in the same subdirectory as this file
# (This step is MANDATORY)
#
# 2. Review the other config params in the CONFIG_FILE.
# The default params should be okay in most cases, but take a look to be sure.


def snapshot_indices_from_src_to_fs(config, backup_id):
    """
    Take a snapshot of all the indices specified in the config file.

    The specified indices are backed up from the ElasticSearch Node on which backup is initiated
    and are stored at the filesystem location specified in the config file.

    Parameters:
        config: dictionary storing the configuration details

    """

    src_seed1 = config['elasticsearch_config']['es_src_seed1']
    es_fs_repo = config['elasticsearch_config']['es_repository_name']

    try:
        src_seed2 = config['elasticsearch_config']['es_src_seed2']
        src_seed3 = config['elasticsearch_config']['es_src_seed3']
    except KeyError: # running in test mode? use a single node
        print("\n[WARN] Only one SOURCE seed node found in the config, falling back to single SOURCE seed...")
        src_seed2 = src_seed3 = src_seed1

    try:
        src_es = Elasticsearch([src_seed1, src_seed2, src_seed3],
        # sniff_on_start=False, sniff_on_connection_fail=False, sniffer_timeout=0)
        sniff_on_start=True, sniff_on_connection_fail=True, sniffer_timeout=60)

        print("\n[INFO] Connected to src ES cluster: %s" %(src_es.info()))

        print("\n[INFO] Creating repository ["+es_fs_repo+"] ...\n")
        src_es.snapshot.create_repository(repository=es_fs_repo,
            body={
                "type": "fs",
                "settings": {
                  "compress": "true",
                  "location": str(backup_id)
                  # "location": config['fs_config']['fs_repository_location']
                }
            },
            request_timeout=30,
            verify=False)

        snapshot_name = config['elasticsearch_config']['snapshot_prefix'] + "_" + str(backup_id)

        print("\n[INFO] Looking for snapshot ["+snapshot_name+"] ...\n")
        try:
            snapshot_get = src_es.snapshot.get(repository=es_fs_repo,
                snapshot=snapshot_name)
            print("\n[INFO] Deleting snapshot ["+snapshot_name+"] ...\n")
            src_es.snapshot.delete(repository=es_fs_repo,
                snapshot=snapshot_name)
        except Exception as e:
            print("\n\n[INFO] Snapshot ["+snapshot_name+"] does not exist. Proceeding ...\n")


        print("\n[INFO] Snapshotting ES cluster to ["+snapshot_name+"] ...\n")
        src_es.snapshot.create(repository=es_fs_repo,
            snapshot=snapshot_name,
            wait_for_completion=False)

    except Exception as e:
        print("\n\n[ERROR] Unexpected error: %s" %(str(e)))



def restore_indices_from_fs_to_dest(config, backup_id):
    """
    Restore the specified indices from the snapshot specified in the config file.

    The indices are restored at the specified 'dest' ElasticSearch Node.
    ElasticSearch automatically replicates the indices across the ES cluster after the restore.

    Parameters:
        config: dictionary storing the configuration details

    """

    dest_seed1 = config['elasticsearch_config']['es_dest_seed1']
    es_fs_repo = config['elasticsearch_config']['es_repository_name']
    index_list = config['elasticsearch_config']['index_names'].split(',')

    try:
        dest_seed2 = config['elasticsearch_config']['es_dest_seed2']
        dest_seed3 = config['elasticsearch_config']['es_dest_seed3']
    except KeyError: # running in test mode? use a single node
        print("\n[WARN] Are you running in test mode? Have you defined >1 dest node in the conf?")
        print("\n[WARN] Falling back to a single dest node...")
        dest_seed2 = dest_seed3 = dest_seed1

    try:
        # specify all 3 dest ES nodes in the connection string
        dest_es = Elasticsearch([dest_seed1, dest_seed2, dest_seed3],
        # sniff_on_start=False, sniff_on_connection_fail=False, sniffer_timeout=0)
        sniff_on_start=True, sniff_on_connection_fail=True, sniffer_timeout=60)

        dest_es.snapshot.create_repository(repository=es_fs_repo,
            body={
                "type": "fs",
                "settings": {
                  "compress": "true",
                  "location": str(backup_id)
                  # "location": config['fs_config']['fs_repository_location']
                }
                # "settings": {
                #     "region": config['aws_fs_config']['aws_region'],
                #     "bucket": config['aws_fs_config']['fs_bucket_name'],
                #     "base_path": config['aws_fs_config']['fs_base_path'],
                #     "access_key": config['aws_api_keys']['aws_access_key'],
                #     "secret_key": config['aws_api_keys']['aws_secret_key']
                # }
            },
            request_timeout=30,
            verify=False)

        print("\n[INFO] Connected to dest ES cluster: %s" %(dest_es.info()))

        try:
            print("[INFO] Closing indexes ...")
            dest_es.indices.close(index="_all", ignore_unavailable=True)
        except NotFoundError:
            print("\n\n[WARN] Could not close all indexes ...")
        except Exception as e:
            print("\n\n[ERROR] Unexpected errorwhile trying to close indexes: '%s'" %(str(e)))

        # must close indices before restoring:
        # for index in index_list:
        #     try:
        #         print "[INFO] Closing index: '%s'" %(index)
        #         dest_es.indices.close(index=index, ignore_unavailable=True)
        #     except NotFoundError:
        #         print "\n\n[WARN] Index '%s' not present on Target ES cluster - could not close it." %(index)
        #     except Exception, e:
        #         print "\n\n[ERROR] Unexpected error '%s' while trying to close index: '%s'" %(str(e))
        #         #reopen_indices(dest_es, index_list)

        snapshot_name = config['elasticsearch_config']['snapshot_prefix'] + "_" + str(backup_id)

        print("\n[INFO] Restoring ES cluster from snapshot ["+snapshot_name+"] ...\n")
        # print "\n[INFO] Restoring ES indices: '%s' from filesystem snapshot...\n" %(config['elasticsearch_config']['index_names'])

        dest_es.snapshot.restore(repository=es_fs_repo,
            snapshot=snapshot_name,
            # body={"indices": config['elasticsearch_config']['index_names']},
            wait_for_completion=False)

        reopen_indices(dest_es)

    except Exception as e:
        print("\n\n[ERROR] Unexpected error: %s" %(str(e)))
        raise Exception('Restore FAILED !!!')
    finally:
        print("\n[INFO] (finally) Finished")
        # print("\n[INFO] (finally) Re-opening all indices")
        # reopen_indices(dest_es)



def reopen_indices(es):
    """
    Re-open indices
    (used to ensure indices are re-opened after any restore operation)

    Parameters:
        es         : ElasticSearch connection object
        index_list : List of ElasticSearch indices that needs to be open
    """

    try:
        print("[INFO] reopen_indices(): Opening all indexes")
        es.indices.open(index="_all", ignore_unavailable=True)
    except NotFoundError:
        print("\n\n[WARN] Could not reopen all indices on Target ES cluster")
    except Exception as e:
        print("\n\n[ERROR] Unexpected error in reopen_indices(): %s" %(str(e)))



def read_config():
    """
    Parse the config file. Return a dictionary object containing the config.
    """

    cfg = configparser.ConfigParser()
    cfg.read(CONFIG_FILE)

    # get a normal dictionary out of the ConfigParser object
    config = {section:{k:v for k,v in cfg.items(section)} for section in cfg.sections()}

    return config


def main():
    # parse command line args
    parser = argparse.ArgumentParser(
        description='Push specified Elasticsearch indices from SOURCE to DESTINATION as per config in the `es-fs-snapshot.conf` file.')

    requiredNamed = parser.add_argument_group('required named arguments')
    requiredNamed.add_argument('-m', '--mode',
        help="Mode of operation. Choose 'backup' on your SOURCE cluster. \
            Choose 'restore' on your DESTINATION cluster",
        choices=['backup','restore'], required=True)

    args = parser.parse_args()

    # parse config
    config = read_config()

    # set default value of snapshot_prefix if missing from config
    if not 'snapshot_prefix' in config['elasticsearch_config']:
        snapshot_prefix = 'elastest_snapshot'
        config['elasticsearch_config']['snapshot_prefix'] = snapshot_prefix

    backup_id = 1

    if args.mode == 'backup':
        snapshot_indices_from_src_to_fs(config, backup_id)

    if args.mode == 'restore':
        restore_indices_from_fs_to_dest(config, backup_id)

    print('\n\n[All done!]')



if __name__ == "__main__":
    main()
