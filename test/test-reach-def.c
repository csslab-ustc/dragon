int foo() {
    int s, i;

    s = 0;
    i = 1;

    while (i <= 10) {
        s = s + i;
        i = i + 1;
    }

    return s;
}