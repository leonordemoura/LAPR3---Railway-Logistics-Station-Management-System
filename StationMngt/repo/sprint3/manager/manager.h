#ifndef MANAGER_H
#define MANAGER_H

#include "../common/structs.h"
#include <stdio.h>

// Declare globals as extern
extern User *users;
extern int num_users;
extern Track *tracks;
extern int num_tracks;
extern Train *trains;
extern int num_trains;
extern SensorData sensors;
extern LogEntry *logs;
extern int num_logs;

// USAC11: Setup inicial
int import_data(const char* filename);
User* importUsers(const char* filename);  // Changed from int to User*

// USAC12: Exportar logs de utilizador
int export_user_actions_to_txt(const LogEntry* logs, int n_logs, const char* username, const char* filename);
int filter_user_logs(const LogEntry* logs, int n_logs, const char* username, LogEntry* user_logs, int* n_user_logs);

// USAC13: Sensores
int open_serial_connection(void);
void close_serial_connection(void);
int get_temperature_humidity(int *t, int *h);
void reset_sensor_buffers(void);
int process_sensor_reading(int temp, int hum, int* temp_med, int* hum_med);
int manager_sensor_main(void);
int load_sensor_config(const char* filename);
void ui_get_sensor_data(void);

// USAC14: LightSigns
void send_cmd_to_lightsigns(const char* cmd, int track_id);
void ui_get_lightSign_data(void);

// USAC15: Board
int manager_get_board_data(BoardData *data);

// USAC16: Gestão de pistas
int assign_track_to_train(int train_id, Train* trains, int n_trains, Track* tracks, int n_tracks, const User* user, LogEntry* logs, int* n_logs);
int train_exists(int train_id, Train* trains, int n_trains);
int train_already_assigned(int train_id, Track* tracks, int n_tracks);
int set_track_non_operational(int track_id, Track* tracks, int n_tracks,
                               const User* user, LogEntry* logs, int* n_logs);
int set_track_free(int track_id, Track* tracks, int n_tracks,
                   const User* user, LogEntry* logs, int* n_logs);
int departure_order(int track_id, Track* tracks, int n_tracks,
                    const User* user, LogEntry* logs, int* n_logs);

// Logging
void add_log_entry(LogEntry* logs, int* n_logs, const User* user, const char* action, ...);

// Conexão serial
int open_serial_connection(void);
void close_serial_connection(void);
FILE* get_serial_handle(void);
int is_serial_connected(void);

#endif