#!/usr/bin/python3
import subprocess, time

OPENSSL = "/openssl/apps/openssl "

# gen certs
subprocess.run(
        OPENSSL + 'genrsa -out /openssl/apps/server.key 2048',
        shell=True,
        # stdout=subprocess.DEVNULL,
        # stderr=subprocess.DEVNULL
    )
subprocess.run(
        OPENSSL + 'req -new -key /openssl/apps/server.key -out /openssl/apps/server.csr -subj "/C=US/ST=California/L=Mountain View/O=MyOrg/OU=Test/CN=localhost"',
        shell=True,
        # stdout=subprocess.DEVNULL,
        # stderr=subprocess.DEVNULL
    )
subprocess.run(
        OPENSSL + 'x509 -req -days 365 -in /openssl/apps/server.csr -signkey /openssl/apps/server.key -out /openssl/apps/server.crt',
        shell=True,
        # stdout=subprocess.DEVNULL,
        # stderr=subprocess.DEVNULL
    )

proxy = subprocess.Popen(
        "go run proxy_all.go -host=127.0.0.1 -port 443 -listen_port=9999",
        cwd="/CVE-2014-0224",
        shell=True,
        # stdout=subprocess.DEVNULL,
        # stderr=subprocess.DEVNULL
    )

time.sleep(1)

# run programs
server = subprocess.Popen(
        OPENSSL + "s_server -debug -accept 443 -cert /openssl/apps/server.crt -certform PEM -key /openssl/apps/server.key -cipher RC4-SHA",
        shell=True,
        # stdout=subprocess.DEVNULL,
        # stderr=subprocess.DEVNULL
    )

time.sleep(1)

client = subprocess.Popen(
        OPENSSL + "s_client -connect 127.0.0.1:9999 -debug -cipher RC4-SHA",
        shell=True,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        bufsize=1
    )

time.sleep(1)

if client.stdout:
    for line in iter(client.stdout.readline, ""):
        if not line:
            continue
        print(f"STDOUT: {line.strip()}")
        if "Secure Renegotiation IS supported" in line:
            print("Result: FAIL")
            break
        if "Secure Renegotiation IS NOT supported" in line:
            print("Result: PASS")
            break