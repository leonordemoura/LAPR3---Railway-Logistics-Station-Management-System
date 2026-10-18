#include <stdio.h>
#include "caesarCipher.h"

int main(){
  char input[]= "KHOOR";
  char decrypted[100];
  int key = 3;

  int result_dec = decrypt_data(encrypted, key, decrypted);

  if(result == 1) {
      printf("Decrypted: %s\n", decrypted);
    } else {
      printf("Decrypt failed (invalid key)\n");
    }

  return 0;
}
