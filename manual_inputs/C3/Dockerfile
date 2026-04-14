# -----------------------------------
# Base Image
# -----------------------------------
    FROM ubuntu:20.04
    LABEL "about"="Docker image for building mosquitto"
    
    ARG DEBIAN_FRONTEND=noninteractive
    
    # -----------------------------------
    # Install Dependencies
    # -----------------------------------
    RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        build-essential \
        cmake \
        libc-ares-dev \
        libssl-dev \
        uuid-dev \
        xsltproc \
        docbook-xsl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*
    

    # -----------------------------------
    # Set JAVA_HOME
    # -----------------------------------
    ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
    ENV PATH="$JAVA_HOME/bin:$PATH"
    

    
    # -----------------------------------
    # Create working directory 
    # -----------------------------------
    RUN mkdir -p /mosquitto
    WORKDIR /mosquitto
    
    CMD ["/bin/bash"]
    
