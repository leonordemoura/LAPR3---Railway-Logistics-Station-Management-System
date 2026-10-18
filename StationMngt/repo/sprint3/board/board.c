#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "board.h"

// Desenha o comboio em ASCII
static void board_draw_train() {
    printf("      ====        ________                ___________\n");
    printf("  _D _|  |_______/        \\__I_I_____===__|_________|\n");
    printf("   |(_)---  |   H\\________/ |   |        =|___ ___|      _________________\n");
    printf("   /     |  |   H  |  |     |   |         ||_| |_||     _|                \\_____A\n");
    printf("  |      |  |   H  |__--------------------| [___] |   =|                        |\n");
    printf("  | ________|___H__/__|_____/[][]~\\_______|       |   -|                        |\n");
    printf("  |/ |   |-----------I_____I [][] []  D   |=======|____|________________________|_\n");
    printf("__/ =| o |=-~~\\  /~~\\  /~~\\  /~~\\ ____Y___________|__|___________________________|_\n");
    printf(" |/-=|___|=O=====O=====O=====O   |_____/~\\___/          |_D__D__D_|  |_D__D__D_|\n");
    printf("  \\_/      \\__/  \\__/  \\__/  \\__/      \\_/               \\_/   \\_/    \\_/   \\_/\n\n");
}

// Desenha a estacao em ASCII
static void board_draw_station() {
    printf("              _I_ \n");
    printf("             /   \\ \n");
    printf("             |(O)| \n");
    printf("      _______|___|_______\n");
    printf("     /   __  /   \\  __   \\\n");
    printf("    /___/__\\/_____\\/__\\___\\\n");
    printf("    |  _   _  |   |  _   _  |\n");
    printf("    | |_| |_| |___| |_| |_| |\n");
    printf("    |  _   _  |   |  _   _  |\n");
    printf("    | |_| |_| |___| |_| |_| |\n");
    printf(" ___|_________|___|_________|___\n");
    printf("/   |         /   \\         |   \\\n");
    printf("|___|________/_____\\________|___|\n");
    printf(" _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ _ \n");
    printf("(___) (___) (___) (___) (___) (__)\n");
    printf("==================================\n");
}

// Limpa o terminal
void board_clear() {
    printf("\033[2J\033[H");
}

// Mostra a Board com os dados reais e avisos de sensores
void board_display(BoardData *data) {
    if (data == NULL) return;

    board_clear();
    board_draw_train();

    // Titulo do sistema
    printf("==============================================================\n");
    printf("        RAILWAY STATION MANAGEMENT SYSTEM\n");
    printf("==============================================================\n");

    // Dados dos Sensores
    printf("\n[ DADOS DOS SENSORES ]\n");
    printf("--------------------------------------------------------------\n");

    // Mostra temperatura ou aviso se estiver a 0
    if (data->temperature == 0) {
        printf("Temperatura : %d %s\n", data->temperature, data->temp_unit);
    } else {
        printf("Temperatura : %d %s\n", data->temperature, data->temp_unit);
    }

    // Mostra humidade ou aviso se estiver a 0
    if (data->humidity == 0) {
        printf("Humidade    : Sensor nao ligado\n");
    } else {
        printf("Humidade    : %d %%\n", data->humidity);
    }
    printf("--------------------------------------------------------------\n");

    // Tabela de Trilhos
    printf("\n[ ESTADO DOS TRILHOS ]\n");
    printf("ID      ESTADO            DETALHES\n");
    printf("--      ------------      --------------------\n");

    for (int i = 0; i < data->num_tracks; i++) {
        printf("%02d      ", i + 1);

        switch (data->track_states[i]) {
            case 0: // FREE
                printf("%-12s      ---", "LIVRE");
                break;
            case 1: // BUSY
                printf("%-12s      Comboio ID: %03d", "OCUPADO", data->train_ids[i]);
                break;
            case 2: // DEPARTING
                printf("%-12s      Partida ID: %03d", "PARTIDA", data->train_ids[i]);
                break;
            case 3: // NON_OPERATIONAL
                printf("%-12s      AVARIADO", "INOPERATIVO");
                break;
            default:
                printf("%-12s      ---", "DESCONHECIDO");
                break;
        }
        printf("\n");
    }

    printf("\n");
    board_draw_station();
}