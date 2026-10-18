#include <stdio.h>
#include <string.h>
#include "../manager/manager.h"
#include "ui.h"

void ui_admin_export_user_logs(void) {
    char target_user[USERNAME_LENGTH];
    char out_file[256];

    printf("==============================================================================\n");
    printf( "     ═══════════════════════════════════════════════════════════════\n");
    printf( "                      RAILWAY STATION MANAGEMENT SYSTEM                      \n");
    printf( "                           EXPORT DE LOGS                              \n");
    printf( "     ═══════════════════════════════════════════════════════════════\n");
    printf( "==============================================================================\n");
    printf("Nome do utilizador a exportar: ");
    if (scanf("%31s", target_user) != 1) {
        while (getchar() != '\n');
        printf("Input inválido.\n");
        return;
    }
    while (getchar() != '\n'); // limpa resto da linha

    // Verifica se o utilizador existe
    int user_exists = 0;
    for (int i = 0; i < num_users; i++) {
        if (strcmp(users[i].username, target_user) == 0) {
            user_exists = 1;
            break;
        }
    }

    if (!user_exists) {
        printf("\nERRO: Utilizador '%s' não existe no sistema!\n", target_user);
        printf("Utilizadores disponíveis:\n");
        for (int i = 0; i < num_users; i++) {
            printf("   - %s\n", users[i].username);
        }
        return;
    }

    // Gera automaticamente o nome do ficheiro:  username_logs.txt
    snprintf(out_file, sizeof(out_file), "%s_logs.txt", target_user);

    printf("\nA exportar logs do utilizador '%s'.. .\n", target_user);
    printf("Ficheiro: %s\n\n", out_file);

    if (export_user_actions_to_txt(logs, num_logs, target_user, out_file)) {
        printf("Exportação concluída!\n");
        printf("Ficheiro guardado em: data_export/%s\n", out_file);
    } else {
        printf("Falha na exportação!\n");
    }
}