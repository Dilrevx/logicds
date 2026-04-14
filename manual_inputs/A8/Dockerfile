# Use Ubuntu as base
FROM ubuntu:22.04

# Set non-interactive mode for apt
ENV DEBIAN_FRONTEND=noninteractive

# Install required tools and libssh test dependencies
RUN apt update && apt install -y \
    build-essential \
    cmake \
    gcc \
    g++ \
    make \
    curl \
    perl \
    pkg-config \
    check \
    libssl-dev \
    zlib1g-dev \
    libcmocka-dev \
    libsocket-wrapper \
    libnss-wrapper \
    libuid-wrapper \
    libpam-wrapper \
    openssh-server \
    sudo \
    git \
    && rm -rf /var/lib/apt/lists/*

# Create workdir
WORKDIR /app

# Copy local source into container
COPY . .

