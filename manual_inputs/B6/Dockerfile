# -----------------------------------
# Base Image
# -----------------------------------
    FROM ubuntu:20.04
    LABEL "about"="Docker image for building mbedtls"
    
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
        zlib1g-dev \
        cmake \
        python3 \
        python3-pytest \
        perl \
        bc \
        doxygen && \
    apt-get clean && rm -rf /var/lib/apt/lists/*
    

    
    # -----------------------------------
    # Create working directory 
    # -----------------------------------
    RUN mkdir -p /mbedtls
    WORKDIR /mbedtls
    
    CMD ["/bin/bash"]
    
