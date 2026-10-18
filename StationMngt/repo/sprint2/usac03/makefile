# Makefile for USAC03
CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o extract_data.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o extract_data.o -o program.elf

main.o : main.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

extract_data.o : extract_data.s extract_data.h
	$(CC) $(CFLAGS) $(ARCHFLAGs) -c extract_data.s

clean :
	rm -f *.o *.elf

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf
