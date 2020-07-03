FROM centos:7

MAINTAINER "apolyakov"

# install git, make and dependencies
RUN yum update -y && \
    yum -y install ncurses-devel git make cmake gcc gcc-c++ boost boost-devel mysql-devel mysql-lib

# clone replicator git repository
RUN git clone https://github.com/tarantool/mysql-tarantool-replication.git

# update git submodules and build replicator
RUN cd mysql-tarantool-replication && \
    git submodule update --init --recursive  && \
    cmake . && \
    make && \
    cp replicatord /usr/local/sbin/replicatord

# copy replicator config files
COPY ./replicatord.service /etc/systemd/system
COPY ./replicatord.yml /usr/local/etc/replicatord.yml
COPY ./init.sh /

CMD ["/init.sh"]