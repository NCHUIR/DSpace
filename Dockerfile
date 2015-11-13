#name of container: dspace-5.3-nchuir
#versison of container: 0.1
FROM quantumobject/docker-tomcat8

#add repository and update the container
#Installation of nesesary package/software for this containers...
RUN echo "deb http://archive.ubuntu.com/ubuntu $(lsb_release -sc)-backports main restricted " >> /etc/apt/sources.list
RUN apt-get update && apt-get install -y -q --force-yes python-software-properties \
                                            software-properties-common \
                                            postgresql \
                                            openjdk-7-jdk \
                                            ant \
                                            git \
                                            unzip \
                    && apt-get clean \
                    && rm -rf /tmp/* /var/tmp/*  \
                    && rm -rf /var/lib/apt/lists/*

# ADD . /dspace-src

#pre-config scritp for different service that need to be run when container image is create 
#maybe include additional software that need to be installed ... with some service running ... like example mysqld
# COPY docker/dspace_tomcat8.conf /tmp/dspace_tomcat8.conf
# COPY pre-conf.sh /sbin/pre-conf
# RUN chmod +x /sbin/pre-conf \
#     && /bin/bash -c /sbin/pre-conf \
#     && rm /sbin/pre-conf

# to allow access from outside of the container  to the container service
# at that ports need to allow access from firewall if need to access it outside of the server. 
EXPOSE 8080

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]

