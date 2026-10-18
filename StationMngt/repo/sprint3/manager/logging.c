#include <stdio.h>
#include <string.h>
#include <time.h>
#include <stdarg.h>
#include "manager.h"
#include "../common/constants.h"

void add_log_entry(LogEntry* logs, int* n_logs, const User* user, const char* action, ...) {
    if (*n_logs >= MAX_LOGS) return;

    LogEntry* log = &logs[*n_logs];
    log->id = *n_logs + 1;

    strncpy(log->username, user->username, USERNAME_LENGTH - 1);
    log->username[USERNAME_LENGTH - 1] = '\0';

    va_list args;
    va_start(args, action);
    vsnprintf(log->action, ACTION_LENGTH, action, args);
    va_end(args);
    log->action[ACTION_LENGTH - 1] = '\0';  // Garantir terminação

    time_t now = time(NULL);
    struct tm* t = localtime(&now);
    if (t) {
        if (strftime(log->timestamp, TIMESTAMP_LENGTH, "%Y-%m-%d %H:%M:%S", t) == 0) {
            log->timestamp[TIMESTAMP_LENGTH - 1] = '\0';
        }
    } else {
        log->timestamp[0] = '\0';
    }

    (*n_logs)++;
}