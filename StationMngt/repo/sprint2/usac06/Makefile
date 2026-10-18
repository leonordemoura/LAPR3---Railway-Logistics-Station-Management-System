# Makefile for USAC06
CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o dequeue_value.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o dequeue_value.o -o program.elf

main.o: main.c file.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

dequeue_value.o: dequeue_value.s file.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c dequeue_value.s

clean:
	rm -f *.o *.elf

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf
