#ifndef FUNCTIONS_H
#define FUNCTIONS_H

int encrypt_data(char* in, int key, char *out);
int decrypt_data(char* in, int key, char *out);
int extract_data(char *str, char *token, char *unit, int *value);
int format_command(char* op, int n, char* cmd);
int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value);
int dequeue_value(int* buffer, int length, int* nelem, int* tail, int* head, int *value);
int move_n_to_array(int* buffer, int length, int *nelem, int *tail, int *head, int n, int* array);
int sort_array(int* vec, int length, char order);
int median(int *vec, int length, int *me);

#endif