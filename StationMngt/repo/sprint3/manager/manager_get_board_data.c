#include "manager.h"
#include <stdlib.h>
#include <string.h>

int manager_get_board_data(BoardData *data) {
    if (data == NULL) return -1;

    int t, h;

    if (get_temperature_humidity(&t, &h)) {
        data->temperature = t;
        strcpy(data->temp_unit, "°C");

        data->humidity = h;
        strcpy(data->hum_unit, "");
    } else {
        data->temperature = 0;
        strcpy(data->temp_unit, "Sensor indisponível");

        data->humidity = 0;
        strcpy(data->hum_unit, "Sensor indisponível");
    }

    /* Pistas */
    data->num_tracks = num_tracks;

    data->track_states = malloc(num_tracks * sizeof(int));
    data->train_ids   = malloc(num_tracks * sizeof(int));

    if (data->track_states == NULL || data->train_ids == NULL) {
        free(data->track_states);
        free(data->train_ids);
        return -1;
    }

    for (int i = 0; i < num_tracks; i++) {
        data->track_states[i] = tracks[i].state;
        data->train_ids[i]   = tracks[i].trainID;
    }

    return 0;
}

