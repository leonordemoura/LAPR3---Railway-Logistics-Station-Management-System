#include <stdio.h>
#include "median.h"

int main() {
    int vec[] = {5, 3, 8, 1, 4};
    int length = 5;
    int me; 
    int med = median(vec, length, &me);
    if(med == 1){
        printf("%d: The median is %d\n", med, me);
    } else {
        printf("Error calculating median\n");
    }

    int vector[] = {1,5,5,6};
    int len = 4;
    int result = median(vector, len, &me);
    if(result == 1){
        printf("%d: The median is %d\n", result, me);
    } else {
        printf("Error calculating median\n");
    }

    len = -1;
    result = median(vector, len, &me);
    if(result == 1){
        printf("%d: The median is %d\n", result, me);
    } else {
        printf("Error calculating median\n");
    }
    return 0;
}