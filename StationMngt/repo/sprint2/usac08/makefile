# Makefile for USAC08
CC=riscv32-linux-gcc
CFLAGS=-Wall -Wextra -g
ARCHFLAGS=-march=rv32imd -mabi=ilp32d
LIBRV32=${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o sort_array.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o sort_array.o -o program.elf

main.o: main.c sort_array.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

sort_array.o: sort_array.s
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c sort_array.s

clean:
	rm -f *.o *.elf

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf

