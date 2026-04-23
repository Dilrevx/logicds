# -----------------------------------
# Base Image
# -----------------------------------
    FROM ubuntu:20.04
    LABEL "about"="srsran base img"
    
    ARG DEBIAN_FRONTEND=noninteractive
    
    # -----------------------------------
    # Install Dependencies
    # -----------------------------------
    RUN apt-get update && \
        apt-get install -y --no-install-suggests --no-install-recommends \
        gnupg curl ca-certificates \
        python3-pip python3-setuptools python3-wheel \
        ninja-build build-essential flex bison \
        git cmake iproute2 tmux\
        libsctp-dev libgnutls28-dev libgcrypt-dev libssl-dev \
        libidn11-dev libmongoc-dev libbson-dev libyaml-dev \
        libnghttp2-dev libmicrohttpd-dev libcurl4-gnutls-dev \
        libtins-dev libtalloc-dev lksctp-tools \
        meson wget apt libclang-dev automake autoconf libtool \
        software-properties-common lsb-release make gcc g++ \
        pkg-config libfftw3-dev libmbedtls-dev \
        libyaml-cpp-dev libgtest-dev libzmq3-dev \
        libboost-program-options-dev libconfig++-dev libsctp-dev mongodb-clients
        

    
    
    # -----------------------------------
    # Install Java (OpenJDK 11) and Maven
    # -----------------------------------
    ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 \
        MAVEN_HOME=/usr/share/maven \
        PATH=/usr/share/maven/bin:$PATH

    RUN apt-get update && \
        apt-get install -y --no-install-recommends \
        openjdk-11-jdk \
        maven && \
        rm -rf /var/lib/apt/lists/*
    
    # -----------------------------------
    # Install prerequisites for HTTPS and GPG
    # -----------------------------------
    RUN apt-get update && \
        apt-get install -y --no-install-recommends \
        gnupg \
        ca-certificates \
        curl \
        apt-transport-https && \
        rm -rf /var/lib/apt/lists/*

    # -----------------------------------
    # Add MongoDB 6.0 repo and key
    # -----------------------------------
    RUN curl -fsSL https://pgp.mongodb.com/server-6.0.asc | \
        gpg --dearmor -o /usr/share/keyrings/mongodb-server-6.0.gpg && \
        echo "deb [ arch=amd64,arm64 signed-by=/usr/share/keyrings/mongodb-server-6.0.gpg ] \
        https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/6.0 multiverse" \
        | tee /etc/apt/sources.list.d/mongodb-org-6.0.list


    # -----------------------------------
    # Install MongoDB
    # -----------------------------------
    RUN apt-get update && \
        apt-get install -y --no-install-recommends mongodb-org && \
        rm -rf /var/lib/apt/lists/*
    

    
    # -----------------------------------
    # Create /app and Copy Source Code
    # -----------------------------------
    RUN mkdir -p /app
    
    # -----------------------------------
    # Build libzmq from Source
    # -----------------------------------
    WORKDIR /app
    RUN git clone https://github.com/zeromq/libzmq.git && \
        cd libzmq && \
        ./autogen.sh && \
        ./configure && \
        make -j$(nproc) && \
        make install && \
        ldconfig
    
    # -----------------------------------
    # Build czmq from Source
    # -----------------------------------
    WORKDIR /app
    RUN git clone https://github.com/zeromq/czmq.git && \
        cd czmq && \
        ./autogen.sh && \
        ./configure && \
        make -j$(nproc) && \
        make install && \
        ldconfig

    # -----------------------------------
    # Install zsh
    # -----------------------------------
    RUN sh -c "$(wget -O- https://github.com/deluan/zsh-in-docker/releases/download/v1.2.1/zsh-in-docker.sh)" -- \
    -t gnzh
 
    
    # -----------------------------------
    # Final Steps
    # -----------------------------------
    WORKDIR /app
    CMD ["bash", "-c", "mongod --config /etc/mongod.conf --fork --logpath /var/log/mongod.log && /bin/bash"]

        # -----------------------------------
    # Final Steps: prepare directories and log file
    # -----------------------------------
    # 1) Ensure the default data directory exists and is owned by 'mongodb'
    # RUN mkdir -p /var/lib/mongodb && \
    #     chown -R mongodb:mongodb /var/lib/mongodb

    # # 2) Ensure the log file exists and is owned by 'mongodb'
    # RUN touch /var/log/mongod.log && \
    #     chown mongodb:mongodb /var/log/mongod.log

    # # 3) Expose MongoDB port (optional, for documentation)
    # EXPOSE 27017

    # WORKDIR /app
    # # 4) At container startup, fork mongod into the background, then launch bash
    # CMD ["bash", "-c", "mongod --config /etc/mongod.conf --fork --logpath /var/log/mongod.log --bind_ip_all && exec bash"]
    