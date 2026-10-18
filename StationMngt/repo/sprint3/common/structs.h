#ifndef STRUCTS_H
#define STRUCTS_H

#define USERNAME_LENGTH 32
#define PASSWORD_LENGTH 32
#define ACTION_LENGTH 128
#define TIMESTAMP_LENGTH 32

typedef enum {
    OPERATOR = 0,
    ADMIN = 1
} UserRole;

typedef struct {
    char name[USERNAME_LENGTH];
    char username[USERNAME_LENGTH];
    char password[PASSWORD_LENGTH];
    int cipher_key;
    UserRole role;
} User;

typedef struct {
    int id;
    char username[USERNAME_LENGTH];
    char action[ACTION_LENGTH];
    char timestamp[TIMESTAMP_LENGTH];
} LogEntry;

typedef struct{
    int bufferLength;
    int medianLength;
    int *buffer;
    int head;
    int tail;
    int nelem;
} sensorConfig;

typedef struct{
    sensorConfig temperature;
    sensorConfig humidity;
} SensorData;

typedef enum {
    TRACK_FREE,
    TRACK_OCCUPIED,
    TRACK_DEPARTING,
    TRACK_NON_OPERATIONAL
} TrackState;

typedef struct{
    int trackID;
    TrackState state;
    int trainID; // 0 se livre
} Track;

typedef struct{
    int trainID;
} Train;

typedef struct {
    int temperature;
    char temp_unit[32];
    int humidity;
    char hum_unit[32];
    int num_tracks;
    int *track_states; // Estados dos trilhos (0, 1, 2, 3)
    int *train_ids;    // IDs dos trens
} BoardData;

#endif