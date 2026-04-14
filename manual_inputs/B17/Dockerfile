# -----------------------------------
# Base Image
# -----------------------------------
    FROM ubuntu:20.04
    LABEL "about"="Docker image for building dnsmasq"
    
    ARG DEBIAN_FRONTEND=noninteractive
    
    # -----------------------------------
    # Install Dependencies
    # -----------------------------------
    RUN apt-get update && \
    apt update && apt install -y \
    git \
    build-essential \
    autoconf \
    libtool \
    pkg-config \
    libssl-dev \
    libncurses5-dev
    libcap-dev && \
    apt-get clean && rm -rf /var/lib/apt/lists/*
    

    
    # -----------------------------------
    # Create working directory 
    # -----------------------------------
    RUN mkdir -p /dnsmasq
    WORKDIR /dnsmasq
    
    CMD ["/bin/bash"]
    
