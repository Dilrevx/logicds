# -----------------------------------
# Base Image
# -----------------------------------
    FROM ubuntu:22.04
    LABEL "about"="Docker image for building bind9"
    
    ARG DEBIAN_FRONTEND=noninteractive
    
# -----------------------------------
# Install Dependencies & BIND-9 Build-Deps
# -----------------------------------
# -----------------------------------
# Install Dependencies
# -----------------------------------
    RUN apt-get update && apt update &&\
    apt-get install -y --no-install-suggests --no-install-recommends \
        build-essential \
        autoconf \
        automake \
        libtool \
        git \
        curl \
        ca-certificates \
        wget \
        pkg-config \
        python3 \
        python3-pip \
        python3-ply \
        libssl-dev \
        libuv1-dev \
        libcap-dev \
        libxml2-dev \
        libjson-c-dev \
        libmaxminddb-dev \
        libncurses-dev \
        libreadline-dev \
        libnghttp2-dev \
        libz-dev && \
    pip3 install --no-cache-dir argparse && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

    
    # -----------------------------------
    # Create working directory 
    # -----------------------------------
    RUN mkdir -p /bind9
    WORKDIR /bind9
    
    CMD ["/bin/bash"]
    