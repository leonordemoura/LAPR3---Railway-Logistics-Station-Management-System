#include <stdio.h>
#include <string.h>
#include "format_command.h"

int main() {
    char op[10];
    char cmd[20];
    int res;

    // Teste 1: RB,05
    strcpy(op, " rB ");
    res = format_command(op, 5, cmd);
    printf("Input: '%s' | n=5\n", op);
    printf("Resultado: %d | Saída: '%s'\n\n", res, cmd);

    // Teste 2: YE,25
    strcpy(op, " Ye ");
    res = format_command(op, 25, cmd);
    printf("Input: '%s' | n=25\n", op);
    printf("Resultado: %d | Saída: '%s'\n\n", res, cmd);

    // Teste 3: valor fora do intervalo (n > 99)
    strcpy(op, " Ye ");
    res = format_command(op, 125, cmd);
    printf("Input: '%s' | n=125\n", op);
    printf("Resultado: %d | Saída: '%s'\n\n", res, cmd);

    // Teste 4: comando inválido
    strcpy(op, " aaa ");
    res = format_command(op, 25, cmd);
    printf("Input: '%s' | n=25\n", op);
    printf("Resultado: %d | Saída: '%s'\n\n", res, cmd);

    // Teste 5: GTH
    strcpy(op, " gTh ");
    res = format_command(op, 0, cmd);
    printf("Input: '%s' | n=0\n", op);
    printf("Resultado: %d | Saída: '%s'\n\n", res, cmd);

    printf("=== Fim dos testes ===\n");
    return 0;
}