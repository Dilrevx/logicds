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
    build-essential \
    git \
    libidn11-dev \
    libdbus-1-dev \
    libnetfilter-conntrack-dev \
    pkg-config \
    libcap-dev && \

    apt-get clean && rm -rf /var/lib/apt/lists/*
    

    
    # -----------------------------------
    # Create working directory 
    # -----------------------------------
    RUN mkdir -p /dnsmasq
    WORKDIR /dnsmasq
    
    CMD ["/bin/bash"]
    
