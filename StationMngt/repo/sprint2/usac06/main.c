#include <stdio.h>
#include "file.h"

void print_state(int* buffer, int length, int head, int tail, int nelem) {
    printf("Buffer: ");
    for (int i = 0; i < length; i++)
        printf("%d ", buffer[i]);
    printf("| head=%d tail=%d nelem=%d\n", head, tail, nelem);
}

int main() {

    int length = 5;
    int buffer[5] = {10, 20, 30, 40, 50};  // pré-carregado MANUALMENTE
    int head = 0;   // nada está a ser inserido, portanto head = posição onde se ENCHERIA
    int tail = 0;   // valor mais antigo está na posição 0
    int nelem = 5;  // buffer cheio

    int value, ret;

    printf("\n=== CASO 1: Remover do buffer cheio ===\n");
    ret = dequeue_value(buffer, length, &nelem, &tail, &head, &value);
    printf("ret=%d value=%d\n", ret, value);
    print_state(buffer, length, head, tail, nelem);


    printf("\n=== CASO 2: Remover novamente (estado semi-cheio) ===\n");
    ret = dequeue_value(buffer, length, &nelem, &tail, &head, &value);
    printf("ret=%d value=%d\n", ret, value);
    print_state(buffer, length, head, tail, nelem);


    printf("\n=== CASO 3: Remover até ficar vazio ===\n");
    while ((ret = dequeue_value(buffer, length, &nelem, &tail, &head, &value)) == 1) {
        printf("value=%d\n", value);
        print_state(buffer, length, head, tail, nelem);
    }
    printf("Ultimo ret=%d (buffer está agora vazio)\n", ret);


    printf("\n=== CASO 4: Tentar remover de buffer vazio ===\n");
    ret = dequeue_value(buffer, length, &nelem, &tail, &head, &value);
    printf("ret=%d (esperado 0)\n", ret);
    print_state(buffer, length, head, tail, nelem);

    return 0;
}
