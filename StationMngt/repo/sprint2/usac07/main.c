#include <stdio.h>
#include "move_n_to_array.h"

int main() {
    int buffer[8] = {10, 20, 30, 40, 50, 60, 70, 80};
    int length = 8;
    int nelem = 7;
    int tail = 4;
    int head = 0;
    int array[3];

    printf("Before: nelem = %d, tail = %d\n", nelem, tail);

    int result = move_n_to_array(buffer, length, &nelem, &tail, &head, 3, array);

    printf("Result: %d\n", result);
    printf("After: nelem = %d, tail = %d\n", nelem, tail);
    printf("Array: [%d, %d, %d]\n", array[0], array[1], array[2]);

    return 0;
}
