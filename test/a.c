int foo(int x, int y) {
    int i, j, k;

    i = -100 + -200;

    while (i < 100) {
        i = i + 10;
    }

    if (x > 10) {
        y = 20;
    } else {
        y = 100;
    }

    j = foo(y, x);

    return x + y * 10;
}