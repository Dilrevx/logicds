# -----------------------------------
# Base Image
# -----------------------------------
    FROM ubuntu:22.04
    LABEL "about"="Docker image for building gnutls"
    
    ARG DEBIAN_FRONTEND=noninteractive
    
    # -----------------------------------
    # Install Dependencies
    # -----------------------------------
    RUN apt-get update && \
    apt-get install -y --no-install-suggests --no-install-recommends \
        build-essential autoconf automake libtool pkg-config git curl \
        libgmp-dev nettle-dev libtasn1-6-dev libp11-kit-dev libunistring-dev \
        libidn2-0-dev zlib1g-dev ca-certificates \
        gettext autopoint gtk-doc-tools libglib2.0-doc bison flex wget && \
    apt-get clean && rm -rf /var/lib/apt/lists/*
    
    

    
    # -----------------------------------
    # Create working directory 
    # -----------------------------------
    RUN mkdir -p /gnutls
    WORKDIR /gnutls
    
    CMD ["/bin/bash"]
    
