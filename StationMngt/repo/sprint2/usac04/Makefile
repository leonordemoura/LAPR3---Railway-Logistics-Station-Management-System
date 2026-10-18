CC=riscv32-linux-gcc
CFLAGS=-Wall -Wextra -fanalyzer -g
ARCHFLAGS=-march=rv32imd -mabi=ilp32d
LIBRV32=${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o format_command.o
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o format_command.o -o program.elf

main.o: main.c format_command.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

format_command.o: format_command.s format_command.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c format_command.s

clean:
	rm -f *.o *.elf

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf
