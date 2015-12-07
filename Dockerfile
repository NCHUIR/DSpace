#name of container: dspace-5.3-nchuir-src
#versison of container: 0.1
FROM quantumobject/docker-tomcat8

#add repository and update the container
#Installation of nesesary package/software for this containers...
RUN echo "deb http://archive.ubuntu.com/ubuntu $(lsb_release -sc)-backports main restricted " >> /etc/apt/sources.list
RUN apt-get update && apt-get install -y -q --force-yes python-software-properties \
                                            software-properties-common \
                                            postgresql-client \
                                            openjdk-7-jdk \
                                            ant \
                                            git \
                                            unzip \
                    && apt-get clean \
                    && rm -rf /tmp/* /var/tmp/*  \
                    && rm -rf /var/lib/apt/lists/*

# === ADD source code ===
# ADD . /dspace-src

# === ADD deploy scripts ===
ADD docker /deploy

# === RUN pre-conf.sh ===
RUN bash /deploy/setup.sh

# Use baseimage-docker's init system.
CMD ["/sbin/my_init"]

