# Use Ubuntu as base
FROM ubuntu:22.04

# Set non-interactive mode for apt
ENV DEBIAN_FRONTEND=noninteractive

# Install required tools
RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    gcc \
    wget \
    autoconf \
    automake \
    git \
    ca-certificates \
    coreutils \
    bsdmainutils \
    pkg-config \
    python3 \
    python3-pip \
    python3-dev \
    libssl-dev \
    sudo \
    net-tools \
    tcpdump \
    && rm -rf /var/lib/apt/lists/*

RUN apt update && apt install libtool -y    

# Create workspace directory
WORKDIR /workspace

# Install Python dependencies globally to avoid path issues
RUN pip3 install "cryptography==36.0.0" pyx

# Clone the repository containing all required scripts
RUN git clone https://github.com/Chico233/a12_0601 /workspace/a12_0601

# Create directories for libraries
RUN mkdir -p /workspace/libs/511 /workspace/libs/52

# The wolfSSL code will be mounted at /workspace/wolfssl
# We can build it directly from there
# The mount is set up in docker-compose.yml

# Build the code from the mounted directory (the fixed version from downloadFixed.sh)
# This will run when the container starts
CMD ["/bin/bash", "-c", "cd /workspace/wolfssl && ./autogen.sh && ./configure --enable-static && make && cp src/.libs/* /workspace/libs/52/"]

# Clone wolfSSL examples
RUN git clone https://github.com/wolfSSL/wolfssl-examples && \
    cd wolfssl-examples && \
    mkdir -p /workspace/servers && \
    mkdir -p /workspace/certs && \
    cp -r certs/* /workspace/certs/

# Use Scapy from the cloned repository or clone it directly
RUN if [ -d "/workspace/a12_0601/scapy" ]; then \
        cp -r /workspace/a12_0601/scapy /workspace/ ; \
    else \
        git clone https://github.com/dim0x69/scapy /workspace/scapy ; \
    fi

# Install Scapy in development mode
WORKDIR /workspace/scapy
RUN pip3 install -e .

# Fix the f-string issue in session.py
RUN sed -i 's/f.write(f"{key} {client_random} {value}\\n")/f.write("{} {} {}\\n".format(key, client_random, value))/g' \
    /workspace/scapy/scapy/layers/tls/session.py

# Copy client scripts from the repository
WORKDIR /workspace
RUN if [ -f "/workspace/a12_0601/client13_keylog.py" ]; then \
        cp /workspace/a12_0601/client13_keylog.py /workspace/ ; \
    fi
RUN if [ -f "/workspace/a12_0601/automaton_cli.py" ]; then \
        cp /workspace/a12_0601/automaton_cli.py /workspace/ ; \
    fi

# Copy test script with dynamic server build and testing
COPY test.py /workspace/test.py
RUN chmod +x /workspace/test.py
# Create startup script that runs the test and properly exits
RUN echo '#!/bin/bash\n\
echo "Starting building for CVE-2022-25640..."\n\
\n\
# Build wolfSSL from the mounted directory\n\
cd /workspace/wolfssl\n\
./autogen.sh && ./configure --enable-static && make\n\
cp src/.libs/* /workspace/libs/52/\n\
' > /workspace/start.sh && chmod +x /workspace/start.sh


# Create startup script that runs the test and properly exits
RUN echo '#!/bin/bash\n\
cd /workspace\n\
# Run the test with unbuffered output\n\
echo "Running the test..."\n\
python3 -u test.py\n\
ret=$?\n\
\n\
# Copy the output file to a location accessible to the host\n\
if [ -d "/output" ]; then\n\
  cp /workspace/make_test_output.txt /output/\n\
fi\n\
\n\
# Exit with the actual test result\n\
exit $ret' > /workspace/test.sh && chmod +x /workspace/test.sh

# Set Python path to find the modules
# ENV PYTHONPATH="/workspace:/workspace/scapy:${PYTHONPATH}"
ENV PYTHONPATH="/workspace:/workspace/scapy"

# Expose the server port
EXPOSE 11111

# Set the entrypoint to our startup script
ENTRYPOINT ["/workspace/start.sh"]