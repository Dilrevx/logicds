#!/usr/bin/env python3

import os
import subprocess
import time
import sys
import threading
import queue
import shutil

# Shared queue for output analysis
server_output_queue = queue.Queue()
success_found = False

def print_output_stream(process, prefix, output_queue=None):
    """Print process output in real-time with prefix and optionally add to queue"""
    global success_found
    while True:
        line = process.stdout.readline()
        if not line and process.poll() is not None:
            break
        if line:
            # print(f"{prefix}: {line.strip()}")
            if output_queue is not None:
                output_queue.put(line)
                # Check for success message
                if "Client connected successfully" in line:
                    success_found = True

def build_server():
    """Build the server-52 executable from source"""
    try:
        print("Building server-52...")
        build_process = subprocess.run(
            ["gcc", "-static", "-I/workspace/wolfssl", "-L/workspace/libs/52", 
             "/workspace/wolfssl-examples/tls/server-tls13.c", 
             "-lwolfssl", "-lpthread", "-lm", "-o", "/workspace/servers/server-52"],
            check=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT
        )
        return True
    except subprocess.CalledProcessError as e:
        print(f"Build failed: {e.output.decode() if e.output else 'Unknown error'}")
        with open("/workspace/make_test_output.txt", "w") as f:
            f.write("Error: Test failed.\n")
        return False

def remove_server():
    """Remove the server-52 executable"""
    try:
        if os.path.exists("/workspace/servers/server-52"):
            os.remove("/workspace/servers/server-52")
            print("Server-52 executable removed")
    except Exception as e:
        print(f"Error removing server-52: {e}")

def main():
    global success_found
    timeout = 3  # Increased timeout for more reliable testing
    
    # Initialize output file
    with open("/workspace/make_test_output.txt", "w") as f:
        f.write("Starting CVE-2022-25640 test...\n")
    
    # Build server-52 before testing
    if not build_server():
        print("Result:FAIL")
        sys.exit(1)
    
    # Start server
    original_dir = os.getcwd()
    os.chdir("./servers")
    server_process = subprocess.Popen(
        ["./server-52"],  # Use patched version
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        universal_newlines=True,
        bufsize=1  # Line buffered
    )
    os.chdir(original_dir)

    # Check if server is running
    time.sleep(1)
    if server_process.poll() is not None:
        print("Server failed to start!")
        print("Result:FAIL")
        with open("/workspace/make_test_output.txt", "w") as f:
            f.write("Error: Test failed.\n")
        remove_server()
        sys.exit(1)
    
    # Start a thread to print server output
    server_thread = threading.Thread(
        target=print_output_stream,
        args=(server_process, "SERVER", server_output_queue),
        daemon=True
    )
    server_thread.start()

    # Run client but don't capture its output
    print("Running exploit client...")
    client_process = subprocess.Popen(
        ["sudo", "python3", "client13_keylog.py"],
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL
    )

    # Wait for client to finish or timeout
    elapsed = 0
    
    while elapsed < timeout:
        time.sleep(0.5)
        elapsed += 0.5
        
        # Check if server terminated - this indicates exploit success
        if server_process.poll() is not None:
            print("Server terminated - test complete")
            print("Result:PASS")
            with open("/workspace/make_test_output.txt", "w") as f:
                f.write("test completed successfully.\n")
            cleanup(client_process)
            remove_server()
            sys.exit(0)
            
        # Check for successful connection via the shared variable
        if success_found:
            print("Result:FAIL") 
            break
    
    # Handle timeout or connection without crash
    with open("/workspace/make_test_output.txt", "w") as f:
        f.write("Error: Test failed.\n")
    
    # Always clean up at the end
    cleanup(client_process, server_process)
    remove_server()
    sys.exit(1)

def cleanup(client_process=None, server_process=None):
    """Clean up processes"""
    if client_process and client_process.poll() is None:
        client_process.terminate()
        try:
            client_process.wait(timeout=1)
        except subprocess.TimeoutExpired:
            client_process.kill()
    
    if server_process and server_process.poll() is None:
        server_process.terminate()
        try:
            server_process.wait(timeout=1)
        except subprocess.TimeoutExpired:
            server_process.kill()
    
    # Additional cleanup for any stray processes
    try:
        subprocess.run(["pkill", "-f", "./server-52"], check=False)
    except Exception:
        pass

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        with open("/workspace/make_test_output.txt", "w") as f:
            f.write("Error: Test failed.\n")
        remove_server()
        sys.exit(1)