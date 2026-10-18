#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "manager.h"


// Exporta ações realizadas por um user para um ficheiro texto
int export_user_actions_to_txt(const LogEntry* logs, int n_logs, const char* username, const char* filename) {
    if (!logs || !username || !filename || n_logs <= 0) return 0; //ver se ponteiros (!) ou nlogs sao nulos
    LogEntry* filtered = malloc(n_logs * sizeof(LogEntry)); //reserva a memoria
    if (!filtered) return 0;
    int n_filtered = 0;
    int res = filter_user_logs(logs, n_logs, username, filtered, &n_filtered);

    if (res == 0 || n_filtered == 0) {
        free(filtered);
        return 0;
    }
    char *full_path = malloc(512);
    if (!full_path) { free(filtered); return 0; }

    snprintf(full_path, 512, "data_export/%s", filename);
    FILE *f = fopen(full_path, "w");

    free(full_path);

    if (!f) { free(filtered); return 0; }

    for (int i = 0; i < n_filtered; ++i) {
        fprintf(f, "%s | %s | %s\n", filtered[i].timestamp, filtered[i].username, filtered[i].action);
    }
    fclose(f);
    free(filtered);


    return 1;
}