# -----------------------------------
# Base Image
# -----------------------------------
FROM ubuntu:22.04
LABEL "about"="Mukit base img"

ARG DEBIAN_FRONTEND=noninteractive

# -----------------------------------
# Install Dependencies
# -----------------------------------
RUN apt-get update && \
    apt-get install -y --no-install-suggests --no-install-recommends \
        gnupg curl ca-certificates \
        python3-pip python3-setuptools python3-wheel \
        ninja-build build-essential flex bison \
        git cmake iproute2 \
        libsctp-dev libgnutls28-dev libgcrypt-dev libssl-dev \
        libidn11-dev libbson-dev libyaml-dev \
        libnghttp2-dev libmicrohttpd-dev libcurl4-gnutls-dev \
        libtins-dev libtalloc-dev lksctp-tools \
        meson wget apt libnl-3-dev libnl-genl-3-dev libnl-route-3-dev \
        perl golang && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# -----------------------------------
# Create working directory 
# -----------------------------------

WORKDIR /
RUN git clone https://github.com/secretnonempty/CVE-2014-0224

RUN mkdir -p /openssl
WORKDIR /openssl

COPY test.py /tmp/test.py

CMD ["/bin/bash"]
    