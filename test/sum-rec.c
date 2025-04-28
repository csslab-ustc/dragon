
int sum_rec(int n) {
            int sum;
            if (n < 1)
                sum = 0;
            else
                sum = n + (sum_rec(n - 1));
            return sum;
}

