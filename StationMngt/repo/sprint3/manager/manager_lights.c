#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <termios.h>
#include <fcntl.h>
#include <ctype.h>
#include "manager.h"

int format_command(char* op, int n, char* cmd);


void clear_input_buffer() {
    int c;
    while ((c = getchar()) != '\n' && c != EOF);
}

char* get_track_state(TrackState state) {
    switch(state) {
        case TRACK_FREE: return "Livre";
        case TRACK_OCCUPIED: return "Ocupado";
        case TRACK_DEPARTING: return "de Partida";
        case TRACK_NON_OPERATIONAL: return "Não operacional";
        default: return "Desconhecido";
    }
}

int find_track_index(int track_id) {
    for (int i = 0; i < num_tracks; i++) {
        if (tracks[i].trackID == track_id) {
            return i;
        }
    }
    return -1;
}


void extract_base_command(const char* formatted_cmd, char* base_cmd) {
    int i = 0;
    while (formatted_cmd[i] && isalpha(formatted_cmd[i]) && i < 2) {
        base_cmd[i] = formatted_cmd[i];
        i++;
    }
    base_cmd[i] = '\0';
}

// Atualizar o estado do trilho com base no comando enviado
void changeTrackState(int track_idx, const char* cmd) {
    char base_cmd[3];
    extract_base_command(cmd, base_cmd);

    if (strcmp(base_cmd, "GE") == 0) {
        tracks[track_idx].state = TRACK_FREE;
        tracks[track_idx].trainID = 0;
    } else if (strcmp(base_cmd, "YE") == 0) {
        tracks[track_idx].state = TRACK_DEPARTING;
    } else if (strcmp(base_cmd, "RE") == 0) {
        tracks[track_idx].state = TRACK_OCCUPIED;
    } else if (strcmp(base_cmd, "RB") == 0) {
        tracks[track_idx].state = TRACK_NON_OPERATIONAL;
        tracks[track_idx].trainID = 0;
    } else {
        printf("[ERRO] Comando desconhecido: '%s'\n", base_cmd);
    }
}

// Simulação do envio de comando para LightSigns
void send_cmd_to_lightsigns(const char* cmd, int track_id){
    if (!is_serial_connected() && !open_serial_connection()) {
        printf("Erro: Não foi possível conectar ao sistema de sinalização.\n");
        return;
    }

    FILE* raspberry = get_serial_handle();
    int fd = fileno(raspberry);
    tcflush(fd, TCIFLUSH);

    int idx = find_track_index(track_id);

    if (idx == -1) {
        printf("Erro: Trilho %d não encontrado.\n", track_id);
        return;
    }

    // Envia o comando para o Raspberry Pi
    fprintf(raspberry, "%s\n", cmd);
    fflush(raspberry);

    printf("Comando '%s' enviado para o trilho %d.\n", cmd, track_id);

    changeTrackState(idx, cmd);

    usleep(200000);

    // Aguarda e lê a resposta do Raspberry Pi
    char buffer[256];

    if (fgets(buffer, sizeof(buffer), raspberry)) {

        printf("Sistema: %s", buffer);
    } else {
        printf("Aviso: Nenhuma resposta recebida do sistema.\n");
    }
}


void ui_get_lightSign_data(){
    printf("\n=== Sistema de Sinalização ===\n");
    printf("Comandos disponíveis:\n");
    printf("  GE - Verde Ativado\n");
    printf("  YE - Amarelo Ativado\n");
    printf("  RE - Vermelho Ativado\n");
    printf("  RB - Vermelho Intermitente\n");
    printf("==============================\n\n");

    if(num_tracks == 0){
        import_data("../sprint3/common/config.txt");
    }

    int track_id;
    char* cmd = malloc(20);
    if (cmd == NULL) return;

    printf("Introduza o ID do Trilho: ");
    if (scanf(" %d", &track_id) != 1) {
        printf("Erro: Entrada inválida.\n");
        free(cmd);
        return;
    }

    printf("Introduza o comando para o LightSign (GE/YE/RE/RB): ");
    if (scanf(" %19s", cmd) != 1) {
        printf("Erro: Entrada inválida.\n");
        free(cmd);
        return;
    }

    char* formatted_cmd = malloc(20);
    if(!format_command(cmd, track_id, formatted_cmd)){
        printf("Erro: Comando inválido.\n");
        free(cmd);
        free(formatted_cmd);
        return;
    }

    printf("A enviar a informação para o Sistema...\n");
    send_cmd_to_lightsigns(formatted_cmd, track_id);
    free(cmd);
    free(formatted_cmd);

}