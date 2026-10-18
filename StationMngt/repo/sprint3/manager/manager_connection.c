#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <termios.h>
#include <fcntl.h>
#include "manager.h"

static FILE* raspberry = NULL;

int open_serial_connection(void) {
    if (!raspberry) {
        // Open as file descriptor first
        int fd = open("/dev/ttyACM0", O_RDWR | O_NOCTTY);
        if (fd < 0) {
            printf("Aviso: Raspberry Pi não conectado! Sistema não disponível.\n");
            return 0;
        }

        struct termios tty;
        if (tcgetattr(fd, &tty) != 0) {
            close(fd);
            return 0;
        }

        // Set baud rate to 9600 (matching Arduino)
        cfsetospeed(&tty, B9600);
        cfsetispeed(&tty, B9600);

        // 8N1 mode
        tty.c_cflag &= ~PARENB;        // No parity
        tty.c_cflag &= ~CSTOPB;        // 1 stop bit
        tty.c_cflag &= ~CSIZE;
        tty.c_cflag |= CS8;            // 8 data bits
        tty.c_cflag &= ~CRTSCTS;       // No hardware flow control
        tty.c_cflag |= CREAD | CLOCAL; // Enable reading

        // Raw mode
        tty.c_lflag &= ~ICANON;
        tty.c_lflag &= ~ECHO;
        tty.c_lflag &= ~ISIG;
        tty.c_iflag &= ~(IXON | IXOFF | IXANY);
        tty.c_iflag &= ~(IGNBRK | BRKINT | PARMRK | ISTRIP | INLCR | IGNCR | ICRNL);
        tty.c_oflag &= ~OPOST;

        // Set timeout
        tty.c_cc[VTIME] = 10;  // 1 second timeout
        tty.c_cc[VMIN] = 0;

        // Apply settings
        if (tcsetattr(fd, TCSANOW, &tty) != 0) {
            close(fd);
            return 0;
        }

        // Flush buffers
        tcflush(fd, TCIOFLUSH);

        // Convert to FILE*
        raspberry = fdopen(fd, "r+");
        if (!raspberry) {
            close(fd);
            return 0;
        }

        // Wait for Arduino to reset after serial connection
        usleep(2000000); // 2 seconds
    }
    return 1;
}

void close_serial_connection(void) {
    if (raspberry) {
        fclose(raspberry);
        raspberry = NULL;
    }
}

FILE* get_serial_handle(void) {
    return raspberry;
}

int is_serial_connected(void) {
    return raspberry != NULL;
}