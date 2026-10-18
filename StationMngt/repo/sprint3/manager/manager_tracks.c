#include <stdio.h>
#include <stdlib.h>
#include "manager.h"

extern int format_command(char* op, int n, char* cmd);

// Atribuir pista a comboio que chega
int assign_track_to_train(int train_id, Train* trains, int n_trains, Track* tracks, int n_tracks, const User* user, LogEntry* logs, int* n_logs){


    // Verificar se o comboio existe
    if (!train_exists(train_id, trains, n_trains)) {
        printf("Erro: Comboio %d não existe.\n", train_id);

        add_log_entry(logs, n_logs, user,
            "Tentativa de atribuição a comboio inexistente (%d)", train_id);

        return -1;
    }

    // Verificar se o comboio já está atribuído
    if (train_already_assigned(train_id, tracks, n_tracks)) {
        printf("Erro: Comboio %d já está atribuído a uma pista.\n", train_id);

        add_log_entry(logs, n_logs, user,
            "Tentativa de dupla atribuição do comboio %d", train_id);

        return -1;
    }

    // Procura pista livre
    for (int i = 0; i < n_tracks; i++) {
        if (tracks[i]. state == TRACK_FREE) {
            tracks[i].state = TRACK_OCCUPIED;
            tracks[i].trainID = train_id;

            char* cmd = malloc(32);
            if (!cmd) return 0;
            // Envia comando ao LightSigns (LED vermelho)
            format_command("RE", tracks[i].trackID, cmd);
            send_cmd_to_lightsigns(cmd, tracks[i].trackID);
            free(cmd);


            // Regista log
            add_log_entry(logs, n_logs, user,
                       "Atribuiu comboio %d à pista %d", train_id, tracks[i].trackID);

            printf("Comboio %d atribuído à pista %d\n", train_id, tracks[i].trackID);
            return tracks[i].trackID;
        }
    }

    // Não há pistas livres - EMERGÊNCIA
        printf("\n");
        printf("╔════════════════════════════════════════════╗\n");
        printf("║    EMERGÊNCIA - PARAGEM DE COMBOIO!        ║\n");
        printf("║                                            ║\n");
        printf("║   Comboio %d: SEM PISTAS DISPONÍVEIS       ║\n", train_id);
        printf("╚════════════════════════════════════════════╝\n");
        printf("\n");

    //registo de log
    add_log_entry(logs, n_logs, user,
                            "Paragem de emergência - comboio %d", train_id);

    printf("EMERGÊNCIA: Não há pistas disponíveis para o comboio %d\n", train_id);
    return -1;
}

// Definir pista como não operacional
int set_track_non_operational(int track_id, Track* tracks, int n_tracks,
                               const User* user, LogEntry* logs, int* n_logs) {
    for (int i = 0; i < n_tracks; i++) {
        if (tracks[i].trackID == track_id) {
            if (tracks[i].state == TRACK_OCCUPIED) {
                printf("Pista %d está ocupada!\n", track_id);
                return 0;
            }

            tracks[i].state = TRACK_NON_OPERATIONAL;

            // LED amarelo
             char* cmd = malloc(32);
             if (!cmd) return 0;
            format_command("RB", track_id, cmd);
            send_cmd_to_lightsigns(cmd, track_id);
            free(cmd);

            // Log
            add_log_entry(logs, n_logs, user, "Definiu pista %d como não operacional", track_id);

            printf("Pista %d agora não operacional\n", track_id);
            return 1;
        }
    }
    printf("Pista %d não encontrada\n", track_id);
    return 0;
}

// Libertar pista
int set_track_free(int track_id, Track* tracks, int n_tracks,
                   const User* user, LogEntry* logs, int* n_logs) {
    for (int i = 0; i < n_tracks; i++) {
        if (tracks[i]. trackID == track_id) {
            tracks[i].state = TRACK_FREE;
            tracks[i].trainID = -1;

            // LED verde
             char* cmd = malloc(32);
             if (!cmd) return 0;
            format_command("GE", track_id, cmd);
            send_cmd_to_lightsigns(cmd, track_id);
            free(cmd);

            // Log
            add_log_entry(logs, n_logs, user,"Libertou pista %d", track_id);

            printf("Pista %d agora livre\n", track_id);
            return 1;
        }
    }
    printf("Pista %d não encontrada\n", track_id);
    return 0;
}

// Ordem de partida
int departure_order(int track_id, Track* tracks, int n_tracks,
                    const User* user, LogEntry* logs, int* n_logs) {
    for (int i = 0; i < n_tracks; i++) {
        if (tracks[i].trackID == track_id) {
            if (tracks[i].state != TRACK_OCCUPIED) {
                printf("Pista %d não tem comboio!\n", track_id);
                return 0;
            }

            int train_id = tracks[i]. trainID;
            tracks[i].state = TRACK_FREE;
            tracks[i].trainID = -1;

            // LED verde
             char* cmd = malloc(32);
             if (!cmd) return 0;
            format_command("GE", track_id, cmd);
            send_cmd_to_lightsigns(cmd, track_id);
            free(cmd);

            // Log
            add_log_entry(logs, n_logs, user,"Ordem de partida - comboio %d da pista %d", train_id, track_id);

            printf("Comboio %d partiu da pista %d\n", train_id, track_id);
            return 1;
        }
    }
    printf("Pista %d não encontrada\n", track_id);
    return 0;
}

int train_exists(int train_id, Train* trains, int n_trains) {
    for (int i = 0; i < n_trains; i++) {
        if (trains[i].trainID == train_id) {
            return 1;
        }
    }
    return 0;
}

int train_already_assigned(int train_id, Track* tracks, int n_tracks) {
    for (int i = 0; i < n_tracks; i++) {
        if (tracks[i].state == TRACK_OCCUPIED &&
            tracks[i].trainID == train_id) {
            return 1;
        }
    }
    return 0;
}
