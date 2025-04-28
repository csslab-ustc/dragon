int fib(int n) {
              int num_aux;
              if (num <= 1)
                 num_aux = 1;
              else
                  num_aux = fib(num-1) + fib(n-2);
              return num_aux;
}




