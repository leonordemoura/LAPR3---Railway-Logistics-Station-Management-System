CC = riscv32-linux-gcc
CFLAGS = -Wall -Wextra -fanalyzer -g
ARCHFLAGS = -march=rv32imd -mabi=ilp32d
LIBRV32 = ${HOME}/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot

# Todos os objetos necessários
UI_OBJS = ui/ui.o ui/ui_export.o ui/ui_board.o ui/ui_setup.o ui/ui_manager_tracks.o
MANAGER_OBJS = manager/manager_tracks.o manager/manager_export.o \
               manager/manager_sensors.o manager/manager_setup.o \
			   manager/manager_lights.o manager/manager_get_board_data.o \
			   manager/logging.o manager/filter_user_logs.o \
			   manager/manager_connection.o

BOARD_OBJS = board/board.o
FUNCTION_OBJS = functions/encrypt_data.o functions/decrypt_data.o \
				functions/extract_data.o functions/format_command.o \
				functions/enqueue_value.o functions/dequeue_value.o \
				functions/move_n_to_array.o functions/sort_array.o \
				functions/median.o

ALL_OBJS = $(UI_OBJS) $(MANAGER_OBJS) $(BOARD_OBJS) $(FUNCTION_OBJS)

# Target principal
program.elf: $(ALL_OBJS)
	$(CC) $(CFLAGS) $(ARCHFLAGS) $(ALL_OBJS) -o program.elf

# Compila objetos da UI
ui/ui.o: ui/ui.c common/structs.h manager/manager.h ui/ui.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c ui/ui.c -o ui/ui.o

ui/ui_export.o: ui/ui_export.c common/structs.h manager/manager.h ui/ui.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c ui/ui_export.c -o ui/ui_export.o

ui/ui_board.o: ui/ui_board.c common/structs.h manager/manager.h ui/ui.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c ui/ui_board.c -o ui/ui_board.o

ui/ui_setup.o: ui/ui_setup.c common/structs.h manager/manager.h ui/ui.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c ui/ui_setup.c -o ui/ui_setup.o

ui/ui_manager_tracks.o: ui/ui_manager_tracks.c common/structs.h manager/manager.h ui/ui.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c ui/ui_manager_tracks.c -o ui/ui_manager_tracks.o

# Compila objetos do Manager
manager/manager_tracks.o: manager/manager_tracks.c manager/manager.h common/structs.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/manager_tracks.c -o manager/manager_tracks.o

manager/manager_export.o: manager/manager_export.c manager/manager.h common/structs.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/manager_export.c -o manager/manager_export.o

manager/manager_lights.o: manager/manager_lights.c manager/manager.h common/structs.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/manager_lights.c -o manager/manager_lights.o

manager/manager_get_board_data.o: manager/manager_get_board_data.c manager/manager.h common/structs.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/manager_get_board_data.c -o manager/manager_get_board_data.o

manager/manager_sensors.o: manager/manager_sensors.c manager/manager.h common/structs.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/manager_sensors.c -o manager/manager_sensors.o

manager/manager_setup.o: manager/manager_setup.c manager/manager.h common/structs.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/manager_setup.c -o manager/manager_setup.o

manager/logging.o: manager/logging.c manager/manager.h common/structs.h common/constants.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/logging.c -o manager/logging.o

manager/filter_user_logs.o: manager/filter_user_logs.S
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/filter_user_logs.S -o manager/filter_user_logs.o

manager/manager_connection.o: manager/manager_connection.c manager/manager.h common/structs.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c manager/manager_connection.c -o manager/manager_connection.o

# Compila objetos do Board
board/board.o: board/board.c
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c board/board.c -o board/board.o

# Compila objetos das Functions
functions/encrypt_data.o: functions/encrypt_data.s functions/functions.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c functions/encrypt_data.s -o functions/encrypt_data.o

functions/decrypt_data.o: functions/decrypt_data.s functions/functions.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c functions/decrypt_data.s -o functions/decrypt_data.o

functions/extract_data.o: functions/extract_data.s functions/functions.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c functions/extract_data.s -o functions/extract_data.o

functions/format_command.o: functions/format_command.s functions/functions.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c functions/format_command.s -o functions/format_command.o

functions/enqueue_value.o: functions/enqueue_value.s functions/functions.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c functions/enqueue_value.s -o functions/enqueue_value.o

functions/dequeue_value.o: functions/dequeue_value.s functions/functions.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c functions/dequeue_value.s -o functions/dequeue_value.o

functions/move_n_to_array.o: functions/move_n_to_array.s functions/functions.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c functions/move_n_to_array.s -o functions/move_n_to_array.o

functions/sort_array.o: functions/sort_array.s functions/functions.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c functions/sort_array.s -o functions/sort_array.o

functions/median.o: functions/median.s functions/functions.h
	$(CC) $(CFLAGS) $(ARCHFLAGS) -c functions/median.s -o functions/median.o

# Limpa tudo
clean:
	rm -f ui/*.o manager/*.o board/*.o functions/*.o data_export/*.txt program.elf

# Corre o programa
run: program.elf
	qemu-riscv32 -L $(LIBRV32) ./program.elf