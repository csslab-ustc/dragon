int fac(int num) {
              int num_aux;
              if (num < 1)
                 num_aux = 1;
              else
                  num_aux = num * (fac(num-1));
              return num_aux;
}


