#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <termios.h>
#include <fcntl.h>
#include "../common/structs.h"
#include "manager.h"

// Funções assembly externas
extern int extract_data(char* str, char* token, char* unit, int* value);
extern int enqueue_value(int*, int, int*, int*, int*, int);
extern int move_n_to_array(int*, int, int*, int*, int*, int, int*);
extern int sort_array(int*, int, char);
extern int median(int*, int, int*);

extern SensorData sensors;

int get_temperature_humidity(int *t, int *h) {
    char buffer[256];
    char unit[20];
    int value;

    if (!open_serial_connection()) {
        return 0;
    }

    FILE* raspberry = get_serial_handle();

    fprintf(raspberry, "GTH\n");
    fflush(raspberry);

    if (!fgets(buffer, sizeof(buffer), raspberry)) {
        return 0;
    }

    if (strncmp(buffer, "ERR", 3) == 0) {
        return 0;
    }

    int ok_t = extract_data(buffer, "TEMP", unit, &value);
    if (ok_t) {
        *t = value;
    } else {
        return 0;
    }

    int ok_h = extract_data(buffer, "HUM", unit, &value);
    if (ok_h) {
        *h = value;
    } else {
        return 0;
    }

    return ok_t && ok_h;
}


/* ================= UI ================= */

void ui_get_sensor_data() {

    static int init = 0;
    if (!init) {
            if (!load_sensor_config("../sprint3/common/config.txt")) {
                printf("Erro:  Não foi possível carregar configuração dos sensores.\n");
                return;
            }

            if (!open_serial_connection()) {
                printf("Erro:  Não foi possível abrir conexão serial.\n");
                return;
            }

            init = 1;
        }

    // Verificar se os buffers foram alocados
    if (sensors.temperature.buffer == NULL || sensors.humidity.buffer == NULL) {
        printf("Erro: Buffers dos sensores não foram alocados.\n");
        return;
    }

    printf("\nA recolher dados dos sensores...\n");

    int total = sensors.temperature.bufferLength;

    for (int i = 0; i < total; i++) {
        int t, h;
        if (!get_temperature_humidity(&t, &h)) {
            printf("Erro na leitura do sensor.\n");
            return;
        }

        enqueue_value(sensors.temperature.buffer, sensors.temperature.bufferLength,
                      &sensors.temperature.nelem, &sensors.temperature.tail, &sensors.temperature.head, t);

        enqueue_value(sensors.humidity.buffer, sensors.humidity.bufferLength,
                      &sensors.humidity.nelem, &sensors.humidity.tail, &sensors.humidity.head, h);

        printf("Leitura %d/%d → T=%d | H=%d\n", i+1, total, t, h);
        sleep(2);
    }

    int *tv = malloc(sensors.temperature.medianLength * sizeof(int));
    int *hv = malloc(sensors.humidity.medianLength * sizeof(int));

    if (!tv || !hv) {
        printf("Erro: Falha na alocação de memória para cálculo da mediana.\n");
        free(tv);
        free(hv);
        return;
    }

    if (!move_n_to_array(sensors.temperature.buffer, sensors.temperature.bufferLength,
                         &sensors.temperature.nelem, &sensors.temperature.tail, &sensors.temperature.head,
                         sensors.temperature.medianLength, tv)) {

        printf("Não há dados suficientes para mediana da temperatura.\n");
        free(tv);
        free(hv);
        return;
    }

    if (!move_n_to_array(sensors.humidity.buffer, sensors.humidity.bufferLength,
                         &sensors.humidity.nelem, &sensors.humidity.tail, &sensors.humidity.head,
                         sensors.humidity.medianLength, hv)) {

        printf("Não há dados suficientes para mediana da humidade.\n");
        free(tv);
        free(hv);
        return;
    }

    sort_array(tv, sensors.temperature.medianLength, 1);
    sort_array(hv, sensors.humidity.medianLength, 1);

    int tm, hm;
    median(tv, sensors.temperature.medianLength, &tm);
    median(hv, sensors.humidity.medianLength, &hm);

    printf("\n╔═══════════════════════════╗\n");
    printf("║        MEDIANAS           ║\n");
    printf("╠═══════════════════════════╣\n");
    printf("║ Temperatura: %3d °C       ║\n", tm);
    printf("║ Humidade:    %3d %%        ║\n", hm);
    printf("╚═══════════════════════════╝\n");

    free(tv);
    free(hv);
}