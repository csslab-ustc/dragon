int test_deadcode() {
    int a,b,c,x;

    a = 5;
    c = a;
    x = 1;
    if (x) {
        b = 6;
    } else {}
    x = 5 + b;
    return x;
}