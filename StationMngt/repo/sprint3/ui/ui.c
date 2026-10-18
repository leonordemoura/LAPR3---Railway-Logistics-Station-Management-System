#include <stdio.h>
#include <string.h> // para o login
#include <stdlib.h>
#include "../common/structs.h"
#include "../manager//manager.h"
#include "ui.h"

//sprint 2
extern int decrypt_data(char* in, int key, char* out);

const char* filename = "common/config.txt"; // Alterado para o caminho correto

// Função para login
User* login(User* users, int n_users, int counter) {
    char input_user[USERNAME_LENGTH], input_pw[PASSWORD_LENGTH];
    printf("Nome de Utilizador: ");
    scanf("%31s", input_user);
    printf("Palavra-Passe: ");
    scanf("%31s", input_pw);

    for (int i=0; i<n_users; ++i) {
        char decrypted[PASSWORD_LENGTH];
        if (strcmp(users[i].username, input_user)==0) {
            if (decrypt_data(users[i].password, users[i].cipher_key, decrypted)) {
                if (strcmp(decrypted, input_pw)==0) {
                    printf("Login OK (%s)\n", users[i].role==ADMIN?"Administrador":"Operador");
                    return &users[i];
                }
            }
        }
    }
    printf("Credenciais inválidas.\n");
    printf("Tentativa %d de 3\n", counter+1);
    return NULL;
}

// Mostra menu de acordo com o role
void menu(const User* user) {
    printf("\n╔═══════════════════════════════════════╗\n");
    printf("║     RAILWAY STATION MANAGER           ║\n");
    printf("╠═══════════════════════════════════════╣\n");
    if (user->role==ADMIN) {
        printf("║  Administrador                        ║\n");
        printf("╠═══════════════════════════════════════╣\n");
        printf("║  1)  Setup inicial (USAC11)           ║\n");
        printf("║  2)  Exportar logs (USAC12)           ║\n");
        printf("║  3)  Board (USAC15)                   ║\n");
        printf("║  4)  Gerir pistas (USAC16)            ║\n");
        printf("║  0)  Sair                             ║\n");
    } else {
        printf("║  Operador                             ║\n");
        printf("╠═══════════════════════════════════════╣\n");
        printf("║  1)  Ler sensores (USAC13)            ║\n");
        printf("║  2)  Sinalizar pistas (USAC14)        ║\n");
        printf("║  3)  Board (USAC15)                   ║\n");
        printf("║  4)  Gerir pistas (USAC16)            ║\n");
        printf("║  0)  Sair                             ║\n");
    }
    printf("╚═══════════════════════════════════════╝\n");
}

//interface
int main(void) {
    // Banner inicial
    printf("\n");
    printf("╔════════════════════════════════════════════════════════════╗\n");
    printf("║                                                            ║\n");
    printf("║     ═══════════════════════════════════════════════════    ║\n");
    printf("║                                                            ║\n");
    printf("║          RAILWAY STATION MANAGEMENT SYSTEM                 ║\n");
    printf("║                                                            ║\n");
    printf("║     ═══════════════════════════════════════════════════    ║\n");
    printf("║                                                            ║\n");
    printf("╚════════════════════════════════════════════════════════════╝\n");
    printf("\n");
    // Importar users do ficheiro
    if(!importUsers(filename)){
        printf("Failed to import users!\n");
        return 0;
    }

    int counter = 0;

    printf("╔═══════════════════════════════════════╗\n");
    printf("║            AUTENTICAÇÃO               ║\n");
    printf("╚═══════════════════════════════════════╝\n\n");

    User* user = NULL;
    do {
        user = login(users, num_users, counter);
    } while(!user && counter++<2);

    if(!user) {
        printf("\n");
        printf("╔═══════════════════════════════════════╗\n");
        printf("║  ACESSO NEGADO                        ║\n");
        printf("║  Número máximo de tentativas atingido ║\n");
        printf("╚═══════════════════════════════════════╝\n");
        printf("\nA sair.. .\n");

        if (users) free(users);
        if (tracks) free(tracks);
        if (trains) free(trains);
        if (logs) free(logs);
        return 0;
    }

    printf("\n");
    printf("╔═══════════════════════════════════════╗\n");
    printf("║  BEM-VINDO, %-22s    ║\n", user->name);
    printf("╚═══════════════════════════════════════╝\n");

    int opc;
    do {
        menu(user);
        printf("Escolha opção: ");
        if (scanf("%d", &opc)!=1) opc = 0;

        // Limpa stdin para não interferir com próximo input de string
        while(getchar()!='\n');

        if (user->role==ADMIN) {
            switch(opc) {
                case 1:
                    printf("-> (USAC11) Setup inicial: \n");
                    ui_admin_setup_data(filename);
                    break;
                case 2: {
                    printf("-> (USAC12) logs\n");
                    ui_admin_export_user_logs();
                    break;
                }

                case 3:
                    printf("-> (USAC15) Send data to board.\n");
                    ui_display_board();
                    break;

                case 4:
                    printf("-> (USAC16) Gestão de pistas: \n");
                    ui_manager_tracks(user);
                    break;

                case 0:
                    printf("A sair...\n");
                    break;
                default:
                    printf("Opção inválida.\n");
            }
        } else {
            switch(opc) {
                case 1:
                    printf("-> (USAC13) Recolher dados dos Sensores: \n");
                    ui_get_sensor_data();
                    break;
                case 2:
                    printf("-> (USAC14) Sinalizar pistas: \n");
                    ui_get_lightSign_data();
                    break;
                case 3:
                    printf("-> (USAC15) Send data to board\n");
                    ui_display_board();
                    break;
                case 0:
                    printf("A sair...\n");
                    break;
                default:
                    printf("Opção inválida.\n");
            }
        }
    } while(opc!=0);

    if (users) free(users);
    if (tracks) free(tracks);
    if (trains) free(trains);
    if (logs) free(logs);

    printf("\n");
    printf("╔════════════════════════════════════════════════════════════════╗\n");
    printf("║                                                                ║\n");
    printf("║      ═══════════════════════════════════════════════════       ║\n");
    printf("║                                                                ║\n");
    printf("║                OBRIGADO POR USAR O SISTEMA!                    ║\n");
    printf("║                                                                ║\n");
    printf("║      ═══════════════════════════════════════════════════       ║\n");
    printf("║                                                                ║\n");
    printf("╚════════════════════════════════════════════════════════════════╝\n");
    printf("\n");
    return 0;
}


