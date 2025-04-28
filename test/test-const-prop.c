int test1(int num) {
    int a, b, c, d;

    a = 0;
    b = a;
    c = num;
    d = a + b;

    return a + b + c + d;
}

int test2() {
    int a;

    a = 0;
    while (0) {
        a = 1;
    }

    return a;
}

int test3() {
    int a;

    a = 0;
    if (1) {
        a = 0;
    } else {
        a = 1;
    }

    return a;
}

int test4() {
    int a, b, c;

    a = 0;
    b = 1;

    if (a == b) {
        c = 0;
    } else {
        c = 1;
    }

    return c;
}