#ifndef UI_H
#define UI_H

#include "../common/structs.h"

void ui_admin_export_user_logs(void);
void ui_display_board(void);
void ui_admin_setup_data(const char* filename);
void ui_manager_tracks(const User* user);

#endif