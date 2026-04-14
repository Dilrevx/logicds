# -----------------------------------
# Base Image
# -----------------------------------
    FROM ubuntu:20.04
    LABEL "about"="Docker image for building Dropbear"
    
    ARG DEBIAN_FRONTEND=noninteractive
    
    # -----------------------------------
    # Install Dependencies
    # -----------------------------------
    RUN apt-get update && \
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
        libz-dev && \
    apt-get clean && rm -rf /var/lib/apt/lists/*
    
    
    # -----------------------------------
    # Create working directory 
    # -----------------------------------
    RUN mkdir -p /openssl
    WORKDIR /openssl
    
    CMD ["/bin/bash"]
    