#include <stdio.h>
#include "caesarCipher.h"

int main(){
  char input[]= "HELLO";
  char encrypted[100];
  int key = 3;

  int result =encrypt_data(input, key, encrypted);

  if(result == 1) {
    printf("Input: %s\n", input);
    printf("Key: %d\n", key);
    printf("Output: %s\n", encrypted);
    
  } else {
    printf("Invalid key: %d\n",key);
  }

  return 0;
}
