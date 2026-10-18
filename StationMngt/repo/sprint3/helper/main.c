#include <stdio.h>
#include <stddef.h>
#include "../common/structs.h"

int main() {
    printf("sizeof(LogEntry) = %lu\n", sizeof(LogEntry));
    printf("offsetof(LogEntry, id) = %lu\n", offsetof(LogEntry, id));
    printf("offsetof(LogEntry, username) = %lu\n", offsetof(LogEntry, username));
    printf("offsetof(LogEntry, action) = %lu\n", offsetof(LogEntry, action));
    printf("offsetof(LogEntry, timestamp) = %lu\n", offsetof(LogEntry, timestamp));
    return 0;
}
