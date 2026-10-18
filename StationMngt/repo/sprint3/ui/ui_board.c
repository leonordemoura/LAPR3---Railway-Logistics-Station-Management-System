#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "../common/structs.h"
#include "../manager/manager.h"
#include "../board/board.h"
#include "ui.h"

extern Track *tracks;
extern int num_tracks;
extern SensorData sensors;

void ui_display_board() {
    BoardData board_data;

    if (manager_get_board_data(&board_data) != 0) {
        printf("Erro ao obter dados da board.\n");
        return;
    }

    board_display(&board_data);

    free(board_data.track_states);
    free(board_data.train_ids);
}
