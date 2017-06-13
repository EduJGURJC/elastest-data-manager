[![License badge](https://img.shields.io/badge/license-Apache2-orange.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Documentation badge](https://img.shields.io/badge/docs-latest-brightgreen.svg)](http://elastest.io/docs/)

[![][ElasTest Logo]][ElasTest]

Copyright © 2017-2019 [<member>]. Licensed under
[Apache 2.0 License].

elastest-data-manager
=================

The EDM is responsible for installing, managing and uninstalling the different persistent services available for the whole ElasTest platform.

The persistent services under the responsibility of EDM are the following:

- Relational database (MySQL)
- Persistance control service (API-including Alluxio, S3 & HDFS compatible)
- ElasticSearch (for both logs and metrics)
- API for exporting and importing data

## Start this component using docker-compose
Note: your terminal need to be in the main project folder where the docker-compose.yml is located.

You can start this image using docker-compose. It will start the following:

- One HDFS namenode
- One HDFS datanode
- One Alluxio master
- One Alluxio worker
- An Elasticsearch cluster (with two nodes)
- A Cerebro instance (web tool for monitoring and administering the Elasticsearch cluster )
- A Kibana instance
- A MySQL instance

You have the possibility to scale the number of HDFS datanodes, Alluxio workers and Elasticsearch nodes.

### Preparing the Linux host
If you are in a Linux host, you need to set vm.max_map_count to at least 262144  in order to run the Elasticsearch container. (more info at https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html#docker-cli-run-prod-mode).

You can use the following helper script to do that:

    # From main project folder
    
    # Start component (in detached mode)
    ./prepare-linux-host.sh
    
In order to change your system settings, the script will prompt you for your sudo password.
    
### Starting the component
    # From main project folder
    
    # Start component (in detached mode)
    docker-compose up -d 
    
    # View service status
    docker-compose ps
    
    # View logs
    docker-compose logs

### Accessing the web interfaces
Each component provide its own web UI. Open you browser at one of the URLs below, where `dockerhost` is the name / IP of the host running the docker daemon. If using Linux, this is the IP of your linux box. If using OSX or Windows (via Boot2docker), you can find out your docker host by typing `boot2docker ip`. On my machine, the NameNode UI is accessible at `http://192.168.59.103:50070/`

| Component               | Port                                               |
| ----------------------- | -------------------------------------------------- |
| HDFS NameNode           | [http://localhost:50070](http://localhost:50070) |
| Alluxio Web Interface| [http://localhost:19999](http://localhost:19999) |
| Kibana Web Interface| [http://localhost:5601](http://localhost:5601) |
| Cerebro Web Interface| [http://localhost:9400/#/overview?host=http:%2F%2Felasticsearch:9200](http://localhost:9400/#/overview?host=http:%2F%2Felasticsearch:9200) |

### Scaling the number of instances
If you want to increase the number of HDFS datanodes in your cluster

    docker-compose scale hdfs-datanode=<number of instances>

If you want to increase the number of Alluxio workers in your cluster

    docker-compose scale alluxio-worker=<number of instances>

If you want to increase the number of Elasticsearch nodes in your cluster

    docker-compose scale esnode=<number of instances>
    
### Finding the port for web access of scalable
To allow worker instances (such as the hdfs-datanode, the alluxio-worker or the esnode) to scale, we need to let docker decide the port used on the host machine. 

For example, to find the port for the hdfs-datanode:

    docker-compose port hdfs-datanode 50075

With this port, you can access the web interfaces of the datanode.

### Accessing the Alluxio REST API
The Alluxio REST API is available at http://localhost:39999

You can try the following examples:

	# From main project folder
	
	# Upload file (local)
	curl -v -X POST http://localhost:39999/api/v1/paths//LICENSE/create-file
	curl -v -X POST http://localhost:39999/api/v1/streams/1/write --data-binary @LICENSE -H "Content-Type: application/octet-stream"
	curl -v -X POST http://localhost:39999/api/v1/streams/1/close

	# Upload file (HDFS)
	curl -v -X POST http://localhost:39999/api/v1/paths//hdfs/LICENSE/create-file
	curl -v -X POST http://localhost:39999/api/v1/streams/2/write --data-binary @LICENSE -H "Content-Type: application/octet-stream"
	curl -v -X POST http://localhost:39999/api/v1/streams/2/close

	# Read file (local)
	curl -v -X POST http://localhost:39999/api/v1/paths//LICENSE/open-file
	curl -v -X POST http://localhost:39999/api/v1/streams/3/read
	curl -v -X POST http://localhost:39999/api/v1/streams/3/close
	
	# Read file (HDFS)
	curl -v -X POST http://localhost:39999/api/v1/paths//hdfs/LICENSE/open-file
	curl -v -X POST http://localhost:39999/api/v1/streams/4/read
	curl -v -X POST http://localhost:39999/api/v1/streams/4/close

	# Delete file (local)
	curl -v -X POST http://localhost:39999/api/v1/paths//LICENSE/delete

	# Delete file (HDFS)
	curl -v -X POST http://localhost:39999/api/v1/paths//hdfs/LICENSE/delete


What is ElasTest
-----------------

This repository is part of [ElasTest], which is a flexible open source testing
platform aimed to simplify the end-to-end testing processes for different types
of applications, including web and mobile, among others.

The objective of ElasTest is to provide advance testing capabilities aimed to
increase the scalability, robustness, security and quality of experience of
large distributed systems. All in all, ElasTest will make any software
development team capable of delivering software faster and with fewer defects.

Documentation
-------------

The ElasTest project provides detailed [documentation][ElasTest Doc] including
tutorials, installation and development guide.

Source
------

Source code for other ElasTest projects can be found in the [GitHub ElasTest
Group].

News
----

Check the [ElasTest Blog] and follow us on Twitter [@elastestio][ElasTest Twitter].

Issue tracker
-------------

Issues and bug reports should be posted to the [GitHub ElasTest Bugtracker].

Licensing and distribution
--------------------------

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contribution policy
-------------------

You can contribute to the ElasTest community through bug-reports, bug-fixes,
new code or new documentation. For contributing to the ElasTest community,
you can use GitHub, providing full information about your contribution and its
value. In your contributions, you must comply with the following guidelines

* You must specify the specific contents of your contribution either through a
  detailed bug description, through a pull-request or through a patch.
* You must specify the licensing restrictions of the code you contribute.
* For newly created code to be incorporated in the ElasTest code-base, you
  must accept ElasTest to own the code copyright, so that its open source
  nature is guaranteed.
* You must justify appropriately the need and value of your contribution. The
  ElasTest project has no obligations in relation to accepting contributions
  from third parties.
* The ElasTest project leaders have the right of asking for further
  explanations, tests or validations of any code contributed to the community
  before it being incorporated into the ElasTest code-base. You must be ready
  to addressing all these kind of concerns before having your code approved.

Support
-------

The ElasTest project provides community support through the [ElasTest Public
Mailing List] and through [StackOverflow] using the tag *elastest*.


<p align="center">
  <img src="http://elastest.io/images/logos_elastest/ue_logo-small.png"><br>
  Funded by the European Union
</p>

[Apache 2.0 License]: http://www.apache.org/licenses/LICENSE-2.0
[ElasTest]: http://elastest.io/
[ElasTest Blog]: http://elastest.io/blog/
[ElasTest Doc]: http://elastest.io/docs/
[ElasTest Logo]: http://elastest.io/images/logos_elastest/elastest-logo-gray-small.png
[ElasTest Public Mailing List]: https://groups.google.com/forum/#!forum/elastest-users
[ElasTest Twitter]: https://twitter.com/elastestio
[GitHub ElasTest Group]: https://github.com/elastest
[GitHub ElasTest Bugtracker]: https://github.com/elastest/bugtracker
[StackOverflow]: http://stackoverflow.com/questions/tagged/elastest
[<member>]: <member_url>
