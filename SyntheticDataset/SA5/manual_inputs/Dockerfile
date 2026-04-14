# Use an official Java 7 image (Debian-based)
FROM openjdk:7

# Switch to the archived Debian Jessie repository to fix apt issues
RUN echo "deb http://archive.debian.org/debian jessie main" > /etc/apt/sources.list && \
    echo 'Acquire::Check-Valid-Until "false";' > /etc/apt/apt.conf.d/99no-check-valid-until && \
    apt-get update && \
    apt-get install -y --no-install-recommends wget curl git && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /app

# Copy the project files into the container
COPY . .

