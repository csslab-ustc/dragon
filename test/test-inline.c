int main(){
    int a, b;
    a = 3;
    b = 4;
    a = foo(a, b);

    a = 5;
    b = 6;
    a = foo(a, b);

    return 0;
}

int foo(int x, int y) {
    int k;
    k = x + y;
    return k;
}
