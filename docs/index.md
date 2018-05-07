# ElasTest Data Manager (EDM)

ElasTest Data Manager is responsible for providing the different persistent services available to the ElasTest platform.

The persistent services under the responsibility of EDM are the following:

- Relational database (MySQL)
- Persistance control service (API-including Alluxio, S3 & HDFS compatible)
- ElasticSearch (for both logs and metrics)
- API for exporting and importing data


## How to run

Elastest Data Manager (EDM) is integrated in ElasTest TORM. To execute EDM follow the [instructions to execute ElasTest](https://github.com/elastest/elastest-torm/blob/master/docs/index.md).


## Development documentation

### Architecture

The following diagram depicts the architecture of the EDM and its interaction with other components of the Elastest platform:

![](images/edm_architecture_diagram.png) 

- **Kibana**: An open source data visualization plugin for Elasticsearch. It provides visualization capabilities on top of the content indexed on an Elasticsearch cluster. Users can create bar, line and scatter plots, or pie charts and maps on top of large volumes of data.

- **Cerebro**: An open source (MIT License) elasticsearch web admin tool built using Scala, Play Framework, AngularJS and Bootstrap.

- **Elasticsearch**: A search engine based on Lucene. It provides a distributed, multitenant-capable full-text search engine with an HTTP web interface and schema-free JSON documents.

- **HDFS**: The Hadoop Distributed File System (HDFS) is a distributed file system designed to run on commodity hardware. It has many similarities with existing distributed file systems. However, the differences from other distributed file systems are significant. HDFS is highly fault-tolerant and is designed to be deployed on low-cost hardware. HDFS provides high throughput access to application data and is suitable for applications that have large data sets. HDFS relaxes a few POSIX requirements to enable streaming access to file system data. HDFS was originally built as infrastructure for the Apache Nutch web search engine project. HDFS is an Apache Hadoop subproject. 

- **Alluxio**: Formerly known as Tachyon, Alluxio is the world’s first memory speed virtual distributed storage system. It unifies data access and bridges computation frameworks and underlying storage systems. Applications only need to connect with Alluxio to access data stored in any underlying storage systems. Additionally, Alluxio’s memory-centric architecture enables data access orders of magnitude faster than existing solutions. In the big data ecosystem, Alluxio lies between computation frameworks or jobs, such as Apache Spark, Apache MapReduce, Apache HBase, Apache Hive, or Apache Flink, and various kinds of storage systems, such as Amazon S3, Google Cloud Storage, OpenStack Swift, GlusterFS, HDFS, MaprFS, Ceph, NFS, and Alibaba OSS

- **MySQL**: MySQL is an open source relational database management system (RDBMS) based on Structured Query Language (SQL). MySQL runs on virtually all platforms, including Linux, UNIX, and Windows.


### Prepare development environment

#### Prerequisites

For developing the REST API, you will need to first follow the instructions above in order to start the ElasTest Data Manager (EDM) component using docker-compose. 

### Development procedure

Once everything is up, you can edit the Python project residing in `elastest-data-manager/rest` folder using the IDE of your choice. Inside the rest-api container the folder `elastest-data-manager/rest/rest_api_project` is mounted as `/usr/src/app`. Whenever you make changes the application is reloaded automatically. Below, you will find a short description of the individual technologies used for the REST API development. 

The EDM REST API was created using Python, Flask and Flask-RESTPlus. These tools combine into a framework, which automates common tasks:

- API input validation
- formatting output (as JSON)
- generating interactive documentation (with Swagger UI)
- turning Python exceptions into machine-readable HTTP responses

**Flask**

[Flask](http://flask.readthedocs.io/) is a web micro-framework written in Python. Since it’s a micro-framework, Flask does very little by itself. In contrast to a framework like [Django](http://www.djangoproject.com/), which takes the “batteries included” approach, Flask does not come with an ORM, serializers, user management or built-in internationalization. All these features and many others are available as [Flask extensions](http://flask.pocoo.org/extensions/), which make up a rich, but loosely coupled ecosystem.

**Flask-RESTPlus**

[Flask-RESTPlus](https://github.com/noirbizarre/flask-restplus) aims to make building REST APIs quick and easy. It provides just enough syntactic sugar to make your code readable and easy to maintain. The killer feature of RESTPlus is its ability to automatically generate an interactive documentation for the API using Swagger UI.

**Swagger UI**

[Swagger UI](http://swagger.io/swagger-ui/) is part of a suite of technologies for documenting RESTful web services. Swagger has evolved into the [OpenAPI specification](https://openapis.org/), currently curated by the Linux Foundation. Once you have an OpenAPI description of your web service, you can use a variety of tool to generate documentation or even boilerplate code in a variety of languages. Take a look at [swagger.io](http://swagger.io/) for more information.

#### Running the tests
The tests can be run as follows:

    # From main project folder

    # Run the tests
    bin/run-tests.sh

 The output should be a list of tests with status ok. The code for the tests can be found inside the `elastest-data-manager/rest/rest-api-project/tests/test_endpoinds.py` file.
 
#### Accessing the Swagger UI
You can access the REST API Swagger UI at [http://localhost:8000/api/](http://localhost:8000/api/). The documentation is dynamically updated when you make changes to the code.


