#include <stdio.h>

int sort_array(int* vec, int length, char order);

void print_array(int* vec, int length) {
    for (int i = 0; i < length; i++) {
        printf("%d ", vec[i]);
    }
    printf("\n");
}

int main() {
    int vec1[] = {5, 2, 9, 1, 7};
    int vec2[] = {3, 8, 4, 6, 2};
    int len = 5;

    printf("=== Test 1: Ascending Order ===\n");
    printf("Before: ");
    print_array(vec1, len);

    int result1 = sort_array(vec1, len, 1); // 1 = ascending

    printf("After:  ");
    print_array(vec1, len);
    printf("Return: %d\n\n", result1);

    printf("=== Test 2: Descending Order ===\n");
    printf("Before: ");
    print_array(vec2, len);

    int result2 = sort_array(vec2, len, 0); // 0 = descending

    printf("After:  ");
    print_array(vec2, len);
    printf("Return: %d\n", result2);

    return 0;
}
