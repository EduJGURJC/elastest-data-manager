# https://github.com/elastic/elasticsearch-docker
FROM docker.elastic.co/elasticsearch/elasticsearch:6.2.3

# Add your elasticsearch plugins setup here
# Example: RUN elasticsearch-plugin install analysis-icu
RUN elasticsearch-plugin remove x-pack

ADD config/elasticsearch.yml /usr/share/elasticsearch/config/
USER root
RUN chown elasticsearch:elasticsearch config/elasticsearch.yml
USER elasticsearch
