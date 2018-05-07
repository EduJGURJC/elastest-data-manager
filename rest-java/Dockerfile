FROM tomcat:8.5-jre8
MAINTAINER Savas Gioldasis (s.gioldasis@gmail.com)

# RUN ["rm", "-fr", "/usr/local/tomcat/webapps/ROOT"]
# COPY ./target/your-webapp-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

CMD ["catalina.sh", "run"]


# #### Environmental Variables
ENV HADOOP_VERSION      2.7
ENV ALLUXIO_VERSION     1.5.0
ENV ALLUXIO_HOME        /opt/alluxio
ENV PATH                $PATH:$ALLUXIO_HOME/bin
ENV JAVA_HOME           /usr/lib/jvm/java-8-oracle

#####adding conf files [to be used by alluxio, hadoop, spark]
ENV PATH                /scripts:$PATH
ADD scripts/* /scripts/

# add webupd8 repository
RUN \
    echo "===> add webupd8 repository..."  && \
    echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee /etc/apt/sources.list.d/webupd8team-java.list  && \
    echo "deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" | tee -a /etc/apt/sources.list.d/webupd8team-java.list  && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886  && \
    apt-get update  && \
    \
    \
    echo "===> install Java"  && \
    echo debconf shared/accepted-oracle-license-v1-1 select true | debconf-set-selections  && \
    echo debconf shared/accepted-oracle-license-v1-1 seen true | debconf-set-selections  && \
    DEBIAN_FRONTEND=noninteractive  apt-get install -y --force-yes oracle-java8-installer oracle-java8-set-default  && \
    \
    \
    echo "===> install various tools"  && \
    DEBIAN_FRONTEND=noninteractive  apt-get install -y --force-yes wget tar maven mysql-client python python-lxml python-setuptools python-pip && \
    pip install -r /scripts/requirements.txt && \
    \
    \
    echo "===> install Alluxio"  && \
    cd / && \
    mkdir -p /opt && \
	wget http://alluxio.org/downloads/files/${ALLUXIO_VERSION}/alluxio-${ALLUXIO_VERSION}-hadoop-${HADOOP_VERSION}-bin.tar.gz && \
	tar xvfz alluxio-${ALLUXIO_VERSION}-hadoop-${HADOOP_VERSION}-bin.tar.gz && \
	rm alluxio-${ALLUXIO_VERSION}-hadoop-${HADOOP_VERSION}-bin.tar.gz && \
	mv alluxio-${ALLUXIO_VERSION}-hadoop-${HADOOP_VERSION} $ALLUXIO_HOME && \
    \
    \
    echo "===> clean up..."  
    # && \
    # rm -rf /var/cache/oracle-jdk8-installer  && \
    # apt-get clean  && \
    # rm -rf /var/lib/apt/lists/*


# #### Alluxio
# # RUN cd / \
# # 	&& wget http://alluxio.org/downloads/files/${ALLUXIO_VERSION}/alluxio-${ALLUXIO_VERSION}-hadoop-${HADOOP_VERSION}-bin.tar.gz \
# # 	&& tar xvfz alluxio-${ALLUXIO_VERSION}-hadoop-${HADOOP_VERSION}-bin.tar.gz \
# # 	&& rm alluxio-${ALLUXIO_VERSION}-hadoop-${HADOOP_VERSION}-bin.tar.gz \
# # 	&& mv alluxio-${ALLUXIO_VERSION}-hadoop-${HADOOP_VERSION} $ALLUXIO_HOME
# # ENV PATH          $PATH:$ALLUXIO_HOME/bin

# # Rest API
# RUN mkdir -p /usr/src/app
# WORKDIR /usr/src/app

# COPY rest_api_project /usr/src/app
# #COPY ./rest_api_project/requirements.txt /usr/src/app/
# RUN pip install --no-cache-dir -r /usr/src/app/requirements.txt

# #COPY ./rest_api_project/ /usr/src/app

# ENV PYTHONPATH=.:/usr/src/app

# EXPOSE 8000

# CMD cd /usr/src/app && python rest_api_app/app.py
