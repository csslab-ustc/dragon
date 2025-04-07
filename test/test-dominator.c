
int test_if(int a) {
    int x;

    if (a > 0) {
        x = 10;
    } else {
        x = 20;
    }
    return x;
}

int test_if2(int a, int b) {
    int x;

    if (a > 0) {
        if (b > 0) {
            x = a + b;
        } else {
            x = a - b;
        }
    } else {
        if (b < 0) {
            x = a * b;
        } else {
            x = a / b;
        }
    }
    return x;
}

int test_while(int a) {
    int x;

    x = 0;
    while (a > 0) {
        x = x + a;
        a = a - 1;
    }
    return x;
}

int test_while2(int a) {
    int x;

    x = 0;
   while (a > 0) {
        x = x + 1;
        if (a > 10) {
            x = x * a;
        } else {
            x = x - a;
        }
        a = a - 1;
   }
   return x;
}
