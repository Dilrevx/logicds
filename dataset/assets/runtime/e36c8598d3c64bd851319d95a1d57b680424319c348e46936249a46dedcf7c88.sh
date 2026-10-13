# Configure and build
if [ ! -f "./configure" ]; then
    autoconf
fi

./configure --prefix=/usr/local/dropbear
make -j"$(nproc)"

echo "✅ Dropbear built to /usr/local/dropbear"