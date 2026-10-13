class X509PolicyChecker {
    static final int X509_PCY_TREE_VALID = 0;
    static final int X509_PCY_TREE_INVALID = 1;
    static final int X509_PCY_TREE_FAILURE = 2;
    static final int X509_PCY_TREE_INTERNAL = 3;

    static int check(X509StoreCtx ctx) {
        return X509_PCY_TREE_INVALID;
    }
}
