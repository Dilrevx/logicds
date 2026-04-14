# Use Ubuntu as base
FROM ubuntu:22.04

# Set non-interactive mode for apt
ENV DEBIAN_FRONTEND=noninteractive

# Install required tools
RUN apt update && apt install -y \
    build-essential \
    cmake \
    gcc \
    make \
    curl \
    perl \
    && rm -rf /var/lib/apt/lists/*

# Create workdir
WORKDIR /app

# Copy everything into the container
COPY . .

