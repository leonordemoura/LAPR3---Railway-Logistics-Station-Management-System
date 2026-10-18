#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "../common/structs.h"
#include "../functions/functions.h"
#include "manager.h"
#include "../common/constants.h"

// variáveis globais
User *users = NULL;
int num_users = 0;

Track *tracks = NULL;
int num_tracks = 0;

Train *trains = NULL;
int num_trains = 0;

SensorData sensors;

LogEntry *logs = NULL;
int num_logs = 0;

// Função utilizada para encriptar dados
int encrypt_data(char* in, int key, char *out);

// Importa os Utilizadores a partir do ficheiro de configuração
User* importUsers(const char* filename) {
    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        return NULL;
    }
    
    char line[256];
    while (fgets(line, sizeof(line), file)) {
        // Salta linhas vazias e comentários
        if (line[0] == '\n' || line[0] == '#') {
            continue;
        }

        line[strcspn(line, "\n")] = 0;
        
        if (strncmp(line, "USER:", 5) == 0) {
            char name[32], username[32], password_plain[32];
            int key, role;
            
            if (sscanf(line + 5, "%31[^,],%31[^,],%31[^,],%d,%d",
                       name, username, password_plain, &key, &role) == 5) {

                if (key < 1 || key > 26) {
                    continue;
                }

                User *temp = (User*) realloc(users, (num_users + 1) * sizeof(User));

                if (temp == NULL) {
                    fclose(file);
                    free(users);
                    return NULL;
                }

                users = temp;
                num_users++;

                strncpy(users[num_users - 1].name, name, sizeof(users[num_users - 1].name) - 1);
                users[num_users - 1].name[sizeof(users[num_users - 1].name) - 1] = '\0';

                strncpy(users[num_users - 1]. username, username, sizeof(users[num_users - 1].username) - 1);
                users[num_users - 1].username[sizeof(users[num_users - 1].username) - 1] = '\0';

                users[num_users - 1].cipher_key = key;

                // Encripta a password
                encrypt_data(password_plain, key, users[num_users - 1].password);
                users[num_users - 1].role = (role == 1) ? ADMIN : OPERATOR;
            }
        }
    }

    fclose(file);
    return users;
}

// Importa dados de sensores, trilhos, comboios e logs
int import_data(const char* filename) {
    FILE *file = fopen(filename, "r");
    if (file == NULL) {
        return 0;
    }

    if (logs == NULL) {
        logs = malloc(MAX_LOGS * sizeof(LogEntry));
        if (!logs) {
            fclose(file);
            return 0;
        }
        num_logs = 0;
    }

    char line[256];
    while (fgets(line, sizeof(line), file)) {
        if (line[0] == '\n' || line[0] == '#') continue;
        line[strcspn(line, "\n")] = 0;

        /* Lê os Sensores de Temperatura e Humidade */
        if (strncmp(line, "SENSOR:", 7) == 0) {
            char type[10];
            int buffer_len, median_len;

            if (sscanf(line + 7, "%9[^,],%d,%d", type, &buffer_len, &median_len) == 3) {

                if (strcmp(type, "TEMP") == 0) {
                    /* liberta buffer pré-existente */
                    if (sensors.temperature.buffer != NULL) {
                        free(sensors.temperature.buffer);
                        sensors.temperature.buffer = NULL;
                    }
                    sensors.temperature.bufferLength = buffer_len;
                    sensors.temperature.medianLength = median_len;

                    if (buffer_len > 0) {
                        sensors.temperature.buffer = calloc(buffer_len, sizeof(int));
                        if (!sensors.temperature.buffer) {
                            fclose(file);
                            return 0;
                        }
                    } else {
                        sensors.temperature.buffer = NULL;
                    }
                    sensors.temperature.head = 0;
                    sensors.temperature.tail = 0;
                    sensors.temperature.nelem = 0;
                }
                else if (strcmp(type, "HUM") == 0) {
                    if (sensors.humidity.buffer != NULL) {
                        free(sensors.humidity.buffer);
                        sensors.humidity.buffer = NULL;
                    }
                    sensors.humidity.bufferLength = buffer_len;
                    sensors.humidity.medianLength = median_len;

                    if (buffer_len > 0) {
                        sensors.humidity.buffer = calloc(buffer_len, sizeof(int));
                        if (!sensors.humidity.buffer) {
                            fclose(file);
                            return 0;
                        }
                    } else {
                        sensors.humidity.buffer = NULL;
                    }
                    sensors.humidity.head = 0;
                    sensors.humidity.tail = 0;
                    sensors.humidity.nelem = 0;
                }
            }
        }

        /* Lê os Trilhos */
        else if (strncmp(line, "TRACK:", 6) == 0) {
            int id, train_id;
            int status_int;

            if (sscanf(line + 6, "%d,%d,%d", &id, &status_int, &train_id) == 3) {
                TrackState status = (TrackState)status_int;

                Track *temp = realloc(tracks, (num_tracks + 1) * sizeof(Track));
                if (! temp) {
                    fclose(file);
                    return 0;
                }

                tracks = temp;
                num_tracks++;

                tracks[num_tracks - 1].trackID = id;
                tracks[num_tracks - 1].state   = status;
                tracks[num_tracks - 1].trainID = train_id;
            }
        }

        /* Lê os Comboios */
        else if (strncmp(line, "TRAIN:", 6) == 0) {
            int id;

            if (sscanf(line + 6, "%d", &id) == 1) {
                Train *temp = (Train*) realloc(trains, (num_trains + 1) * sizeof(Train));

                if (temp == NULL) {
                    fclose(file);
                    return 0;
                }

                trains = temp;
                num_trains++;

                trains[num_trains - 1].trainID = id;
            }
        }

        /* Lê os Logs */
        else if (strncmp(line, "LOG:", 4) == 0){
            int id;
            char username[USERNAME_LENGTH];
            char action[ACTION_LENGTH];
            char timestamp[TIMESTAMP_LENGTH];

            if (sscanf(line + 4, "%d,%31[^,],%127[^,],%31s",
                    &id, username, action, timestamp) == 4) {

                num_logs++;

                logs[num_logs - 1].id = id;
                strncpy(logs[num_logs - 1].username, username, USERNAME_LENGTH - 1);
                logs[num_logs - 1].username[USERNAME_LENGTH - 1] = '\0';
                strncpy(logs[num_logs - 1].action, action, ACTION_LENGTH - 1);
                logs[num_logs - 1].action[ACTION_LENGTH - 1] = '\0';
                strncpy(logs[num_logs - 1].timestamp, timestamp, TIMESTAMP_LENGTH - 1);
                logs[num_logs - 1].timestamp[TIMESTAMP_LENGTH - 1] = '\0';
            }
        }
    }

    fclose(file);
    return 1;
}

// Carrega a configuração dos sensores a partir do ficheiro
int load_sensor_config(const char* filename) {
    FILE* f = fopen(filename, "r");
    if (!f) {
        return 0;
    }

    char line[256];
    int temp_configured = 0, hum_configured = 0;

    while (fgets(line, sizeof(line), f)) {
        if (strncmp(line, "SENSOR:TEMP", 11) == 0) {
            int buf_len, med_len;
            if (sscanf(line, "SENSOR:TEMP,%d,%d", &buf_len, &med_len) == 2) {
                if (sensors.temperature.buffer != NULL) {
                    free(sensors.temperature.buffer);
                    sensors.temperature.buffer = NULL;
                }
                sensors.temperature.bufferLength = buf_len;
                sensors.temperature. medianLength = med_len;

                if (buf_len > 0) {
                    sensors.temperature.buffer = calloc(buf_len, sizeof(int));
                    if (!sensors.temperature.buffer) {
                        fclose(f);
                        return 0;
                    }
                } else {
                    sensors.temperature.buffer = NULL;
                }
                sensors.temperature.head = sensors.temperature.tail = sensors.temperature.nelem = 0;
                temp_configured = 1;
            }
        } else if (strncmp(line, "SENSOR:HUM", 10) == 0) {
            int buf_len, med_len;
            if (sscanf(line, "SENSOR:HUM,%d,%d", &buf_len, &med_len) == 2) {
                if (sensors.humidity.buffer != NULL) {
                    free(sensors.humidity.buffer);
                    sensors.humidity.buffer = NULL;
                }
                sensors.humidity.bufferLength = buf_len;
                sensors.humidity.medianLength = med_len;

                if (buf_len > 0) {
                    sensors.humidity.buffer = calloc(buf_len, sizeof(int));
                    if (!sensors.humidity.buffer) {
                        fclose(f);
                        return 0;
                    }
                } else {
                    sensors.humidity.buffer = NULL;
                }
                sensors.humidity.head = sensors.humidity.tail = sensors.humidity.nelem = 0;
                hum_configured = 1;
            }
        }
    }
    fclose(f);

    return temp_configured && hum_configured;
}