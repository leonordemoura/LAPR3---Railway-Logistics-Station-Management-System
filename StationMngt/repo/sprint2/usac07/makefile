# Makefile for USAC07
CC=riscv32-linux-gcc
CFLAGS=-Wall -Wextra -g
ARCHFLAGS=-march=rv32imd -mabi=ilp32d
LIBRV32=${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o move_n_to_array.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o move_n_to_array.o -o program.elf

main.o: main.c move_n_to_array.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

move_n_to_array.o: move_n_to_array.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c move_n_to_array.s

clean:
	rm -f *.o *.elf

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf

