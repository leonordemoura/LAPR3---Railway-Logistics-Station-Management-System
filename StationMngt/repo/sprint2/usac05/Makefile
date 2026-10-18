# Makefile for USAC05
CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o enqueue_value.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o enqueue_value.o -o program.elf

main.o: main.c file.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

enqueue_value.o: enqueue_value.s file.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c enqueue_value.s

clean:
	rm -f *.o *.elf

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf
