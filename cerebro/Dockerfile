FROM openjdk:8-jre

# grab gosu for easy step-down from root
# ENV GOSU_VERSION 1.7
# RUN set -x \
# 	&& wget -O /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$(dpkg --print-architecture)" \
# 	&& wget -O /usr/local/bin/gosu.asc "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$(dpkg --print-architecture).asc" \
# 	&& export GNUPGHOME="$(mktemp -d)" \
# 	&& gpg --keyserver ha.pool.sks-keyservers.net --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4 \
# 	&& gpg --batch --verify /usr/local/bin/gosu.asc /usr/local/bin/gosu \
# 	&& rm -r "$GNUPGHOME" /usr/local/bin/gosu.asc \
# 	&& chmod +x /usr/local/bin/gosu \
# 	&& gosu nobody true

ENV GOSU_VERSION 1.10
RUN set -ex; \
	\
	fetchDeps=' \
		ca-certificates \
		wget \
	'; \
	apt-get update; \
	apt-get install -y --no-install-recommends $fetchDeps; \
	rm -rf /var/lib/apt/lists/*; \
	\
	dpkgArch="$(dpkg --print-architecture | awk -F- '{ print $NF }')"; \
	wget -O /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch"; \
	wget -O /usr/local/bin/gosu.asc "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch.asc"; \
	\
# verify the signature
	export GNUPGHOME="$(mktemp -d)"; \
        for server in $(shuf -e ha.pool.sks-keyservers.net \
                           hkp://p80.pool.sks-keyservers.net:80 \
                           keyserver.ubuntu.com \
                           hkp://keyserver.ubuntu.com:80 \
                           pgp.mit.edu) ; do \
            gpg --keyserver "$server" --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4 && break || : ; \
        done \
	&& gpg --batch --verify /usr/local/bin/gosu.asc /usr/local/bin/gosu; \
	# rm -r "$GNUPGHOME" /usr/local/bin/gosu.asc; \
	\
	chmod +x /usr/local/bin/gosu; \
# verify that the binary works
	gosu nobody true; 

ENV CEREBRO_VERSION 0.6.5
RUN cd /opt/ \
    && wget -O cerebro-${CEREBRO_VERSION}.tgz https://github.com/lmenezes/cerebro/releases/download/v${CEREBRO_VERSION}/cerebro-${CEREBRO_VERSION}.tgz \
    && tar zxvf cerebro-${CEREBRO_VERSION}.tgz \
    && rm cerebro-${CEREBRO_VERSION}.tgz \
    && mkdir cerebro-${CEREBRO_VERSION}/logs \
    && mv cerebro-${CEREBRO_VERSION} cerebro 
 #    \
	# && apt-get purge -y --auto-remove ca-certificates wget

WORKDIR /opt/cerebro
EXPOSE 9000
CMD ["./bin/cerebro"]
