#include <stdio.h>
#include "ui.h"
#include "../manager/manager.h"

void ui_manager_tracks(const User* user) {

    if (tracks == NULL || num_tracks == 0 ||
            trains == NULL || num_trains == 0 ||
            logs == NULL) {

            printf("\nErro: Sistema não inicializado.\n");
            printf("Execute primeiro o Setup Inicial (USAC11).\n");
            return;
        }

    int option;

    do {
        printf("\n╔══════════════════════════════╗\n");
        printf("║        GESTÃO DE PISTAS      ║\n");
        printf("╠══════════════════════════════╣\n");
        printf("║ 1) Atribuir pista a comboio  ║\n");
        printf("║ 2) Pista não operacional     ║\n");
        printf("║ 3) Libertar pista            ║\n");
        printf("║ 4) Ordem de partida          ║\n");
        printf("║ 0) Voltar                    ║\n");
        printf("╚══════════════════════════════╝\n");
        printf("Opção: ");

        if (scanf("%d", &option) != 1) {
            while (getchar() != '\n');
            continue;
        }

        int track_id, train_id;

        switch (option) {

            case 1:
                printf("ID do comboio: ");
                scanf("%d", &train_id);
                assign_track_to_train(train_id, trains, num_trains, tracks, num_tracks, user, logs, &num_logs);
                break;

            case 2:
                printf("ID da pista: ");
                scanf("%d", &track_id);
                set_track_non_operational(
                    track_id,
                    tracks, num_tracks,
                    user,
                    logs, &num_logs
                );
                break;

            case 3:
                printf("ID da pista: ");
                scanf("%d", &track_id);
                set_track_free(
                    track_id,
                    tracks, num_tracks,
                    user,
                    logs, &num_logs
                );
                break;

            case 4:
                printf("ID da pista: ");
                scanf("%d", &track_id);
                departure_order(
                    track_id,
                    tracks, num_tracks,
                    user,
                    logs, &num_logs
                );
                break;

            case 0:
                break;

            default:
                printf("Opção inválida.\n");
        }

    } while (option != 0);
}
