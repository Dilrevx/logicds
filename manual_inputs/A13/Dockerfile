# Use Ubuntu as base
FROM ubuntu:22.04

# Set non-interactive mode for apt
ENV DEBIAN_FRONTEND=noninteractive

# Install system dependencies for building BIND 9 and running tests
RUN apt update && apt install -y \
    build-essential \
    gcc \
    g++ \
    make \
    autoconf \
    automake \
    libtool \
    bison \
    flex \
    git \
    cmake \
    curl \
    pkg-config \
    libssl-dev \
    libuv1-dev \
    libjemalloc-dev \
    libcap-dev \
    libjson-c-dev \
    libnghttp2-dev \
    python3 \
    python3-pip \
    perl \
    python3-dnspython \
    python3-jinja2 \
    iproute2 \
    net-tools \
    nano \
    python2 \
    && rm -rf /var/lib/apt/lists/*

# Install Python dependencies
RUN pip3 install --no-cache-dir \
    pytest \
    pytest-xdist \
    ply

# Set working directory
WORKDIR /app

# Copy source code into the container
COPY . .

# Default shell for debugging
CMD ["/bin/bash"]

