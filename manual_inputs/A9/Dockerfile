# Use Ubuntu 22.04 as base
FROM ubuntu:22.04

# Set non-interactive mode for apt
ENV DEBIAN_FRONTEND=noninteractive

# Install base build tools and dependencies
RUN apt update && apt install -y \
    build-essential \
    cmake \
    gcc \
    g++ \
    make \
    curl \
    wget \
    perl \
    dash \
    git-core \
    autoconf \
    autogen \
    libtool \
    gettext \
    autopoint \
    automake \
    python3 \
    nettle-dev \
    libp11-kit-dev \
    libtspi-dev \
    libunistring-dev \
    libtasn1-bin \
    libtasn1-6-dev \
    libidn2-0-dev \
    gawk \
    gperf \
    libtss2-dev \
    libunbound-dev \
    dns-root-data \
    bison \
    gtk-doc-tools \
    texinfo \
    texlive \
    texlive-plain-generic \
    texlive-extra-utils \
    valgrind \
    nodejs \
    softhsm2 \
    faketime \
    lcov \
    libssl-dev \
    libcmocka-dev \
    expect \
    libev-dev \
    dieharder \
    openssl \
    abigail-tools \
    socat \
    net-tools \
    ppp \
    util-linux \
    && rm -rf /var/lib/apt/lists/*

# Set the working directory
WORKDIR /app

# Copy source code into the container
COPY . .

