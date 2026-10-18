#include <stdio.h>
#include "file.h"

void print_state(int* buffer, int length, int head, int tail, int nelem) {
    printf("Buffer: ");
    for(int i = 0; i < length; i++)
        printf("%d ", buffer[i]);

    printf(" | head=%d tail=%d nelem=%d\n", head, tail, nelem);
}

int main() {
    int length = 5;
    int buffer[5] = {0};

    int head = 0;
    int tail = 0;
    int nelem = 0;

    printf("\n=== CASO 1: Inserção com o buffer vazio ===\n");
    enqueue_value(buffer, length, &nelem, &tail, &head, 10);
    print_state(buffer, length, head, tail, nelem);

    printf("\n=== CASO 2: Inserir até encher ===\n");
    enqueue_value(buffer, length, &nelem, &tail, &head, 20);
    print_state(buffer, length, head, tail, nelem);

    enqueue_value(buffer, length, &nelem, &tail, &head, 30);
    print_state(buffer, length, head, tail, nelem);

    enqueue_value(buffer, length, &nelem, &tail, &head, 40);
    print_state(buffer, length, head, tail, nelem);

    enqueue_value(buffer, length, &nelem, &tail, &head, 50);  // agora enche
    print_state(buffer, length, head, tail, nelem);

    printf("\n=== CASO 3: Inserção com overwrite (buffer cheio) ===\n");
    enqueue_value(buffer, length, &nelem, &tail, &head, 60);  // sobrescreve o mais antigo
    print_state(buffer, length, head, tail, nelem);

    enqueue_value(buffer, length, &nelem, &tail, &head, 70);
    print_state(buffer, length, head, tail, nelem);

    printf("\n=== CASO 4: Fazer várias voltas ao buffer ===\n");
    enqueue_value(buffer, length, &nelem, &tail, &head, 80);
    print_state(buffer, length, head, tail, nelem);

    enqueue_value(buffer, length, &nelem, &tail, &head, 90);
    print_state(buffer, length, head, tail, nelem);

    enqueue_value(buffer, length, &nelem, &tail, &head, 100);
    print_state(buffer, length, head, tail, nelem);

    printf("\n=== CASO 5: Testar múltiplos overwrites consecutivos ===\n");
    enqueue_value(buffer, length, &nelem, &tail, &head, 110);
    print_state(buffer, length, head, tail, nelem);

    enqueue_value(buffer, length, &nelem, &tail, &head, 120);
    print_state(buffer, length, head, tail, nelem);

    enqueue_value(buffer, length, &nelem, &tail, &head, 130);
    print_state(buffer, length, head, tail, nelem);

    return 0;
}
