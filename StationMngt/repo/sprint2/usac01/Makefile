CC=riscv32-linux-gcc
CFLAGS=-Wall -Wextra -fanalyzer -g
ARCHFLAGS=-march=rv32imd -mabi=ilp32d
LIBRV32=${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

program.elf: main.o encrypt_data.o 
	$(CC) $(CFLAGS) $(ARCHFLAGS) main.o encrypt_data.o -o program.elf

main.o: main.c caesarCipher.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c main.c

encrypt_data.o: encrypt_data.s caesarCipher.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c encrypt_data.s

clean:
	rm -f *.o *.elf

run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf
