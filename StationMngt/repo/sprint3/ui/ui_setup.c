#include <stdio.h>
#include <string.h>
#include <time.h>
#include "../common/structs.h"
#include "../manager/manager.h"

void print_header(const char* title) {
    int title_len = strlen(title);
    int padding = (42 - title_len - 2) / 2;
    
    printf("\n╔══════════════════════════════════════════╗\n");
    printf("║%*s%s%*s║\n", padding, "", title, 42 - title_len - padding, "");
    printf("╠══════════════════════════════════════════╣\n");
}

void print_footer() {
    printf("╚══════════════════════════════════════════╝\n");
}

void print_separator() {
    printf("╟──────────────────────────────────────────╢\n");
}

char* get_track_state_string(TrackState state) {
    switch(state) {
        case TRACK_FREE: return "Livre";
        case TRACK_OCCUPIED: return "Ocupado";
        case TRACK_DEPARTING: return "Partida";
        case TRACK_NON_OPERATIONAL: return "Não operacional";
        default: return "Desconhecido";
    }
}

void ui_show_tracks() {
    if (num_tracks == 0) {
        printf("\nNenhum trilho carregado.\n");
        return;
    }

    print_header("TRILHOS");
    
    int free_count = 0, occupied_count = 0, maintenance_count = 0, error_count = 0;
    
    for (int i = 0; i < num_tracks; i++) {
        printf("║ Trilho #%-32d ║\n", tracks[i].trackID);
        printf("║    Estado: %-29s ║\n", get_track_state_string(tracks[i].state));
        
        if (tracks[i].trainID > 0) {
            printf("║    Comboio: #%-27d ║\n", tracks[i].trainID);
        } else {
            printf("║    Comboio: %-28s ║\n", "---");
        }
        
        // Count states
        switch(tracks[i].state) {
            case TRACK_FREE:  free_count++; break;
            case TRACK_OCCUPIED: occupied_count++; break;
            case TRACK_DEPARTING: maintenance_count++; break;
            case TRACK_NON_OPERATIONAL: error_count++; break;
        }
        
        if (i < num_tracks - 1) {
            print_separator();
        }
    }
    
    print_footer();
    
    printf("\nEstatísticas:\n");
    printf("   Livres: %d\n", free_count);
    printf("   Ocupados: %d\n", occupied_count);
    printf("   Manutenção:  %d\n", maintenance_count);
}

void ui_show_trains() {
    if (num_trains == 0) {
        printf("\nNenhum comboio carregado.\n");
        return;
    }

    print_header("COMBOIOS");
    
    for (int i = 0; i < num_trains; i++) {
        // Encontra o trilho associado
        int track_id = -1;
        for (int j = 0; j < num_tracks; j++) {
            if (tracks[j].trainID == trains[i]. trainID) {
                track_id = tracks[j].trackID;
                break;
            }
        }
        
        printf("║ Comboio #%-31d ║\n", trains[i].trainID);
        
        if (track_id >= 0) {
            printf("║    Localização:  Trilho #%-15d ║\n", track_id);
        } else {
            printf("║    Localização: %-26s ║\n", "Não atribuído");
        }
        
        if (i < num_trains - 1) {
            print_separator();
        }
    }
    
    print_footer();
    printf("\nTotal de comboios: %d\n", num_trains);
}

void ui_show_sensors() {
    print_header("SENSORES");
    
    printf("║ Sensor de Temperatura                    ║\n");
    printf("║    Buffer: %-29d ║\n", sensors.temperature. bufferLength);
    printf("║    Janela Mediana: %-21d ║\n", sensors. temperature.medianLength);
    
    print_separator();
    
    printf("║ Sensor de Humidade                       ║\n");
    printf("║    Buffer: %-29d ║\n", sensors. humidity.bufferLength);
    printf("║    Janela Mediana: %-21d ║\n", sensors.humidity. medianLength);
    
    print_footer();
}

void ui_show_logs() {
    if (num_logs == 0) {
        printf("\nNenhum log disponível.\n");
        return;
    }

    print_header("LOGS");
    
    // Mostra os últimos 10 logs
    int start = (num_logs > 10) ? num_logs - 10 : 0;
    
    for (int i = start; i < num_logs; i++) {
        printf("║ Log #%-35d ║\n", logs[i].id);
        printf("║    Utilizador: %-25s ║\n", logs[i].username);
        printf("║    Ação: %-31s ║\n", logs[i].action);
        printf("║    Data: %-31s ║\n", logs[i].timestamp);
        
        if (i < num_logs - 1) {
            print_separator();
        }
    }
    
    print_footer();
    
    if (num_logs > 10) {
        printf("\nA mostrar os últimos 10 de %d logs.\n", num_logs);
    } else {
        printf("\nTotal de logs: %d\n", num_logs);
    }
}

void ui_show_summary() {
    print_header("RESUMO DO SISTEMA");
    
    printf("║ Utilizadores: %-26d ║\n", num_users);
    printf("║ Trilhos: %-31d ║\n", num_tracks);
    printf("║ Comboios: %-30d ║\n", num_trains);
    printf("║ Logs: %-34d ║\n", num_logs);
    
    print_separator();
    
    // Estado dos Trilhos
    int free_count = 0, occupied_count = 0;
    for (int i = 0; i < num_tracks; i++) {
        if (tracks[i].state == TRACK_FREE) free_count++;
        if (tracks[i].state == TRACK_OCCUPIED) occupied_count++;
    }
    
    printf("║ Estado dos Trilhos:                      ║\n");
    printf("║   Livres: %-30d ║\n", free_count);
    printf("║   Ocupados:  %-27d ║\n", occupied_count);
    
    print_separator();
    
    printf("║ Sensores Configurados:                   ║\n");
    printf("║   Temperatura (buf=%d, med=%d)%-12s║\n", 
           sensors.temperature.bufferLength,
           sensors.temperature.medianLength, "");
    printf("║   Humidade (buf=%d, med=%d)%-15s║\n",
           sensors.humidity.bufferLength,
           sensors.humidity. medianLength, "");
    
    print_footer();
    
    // Hora atual
    time_t now = time(NULL);
    char* time_str = ctime(&now);
    time_str[strlen(time_str) - 1] = '\0'; 
    printf("\n\u231A %s\n", time_str);
}

void ui_admin_setup_data(const char* filename){
    import_data(filename);

    ui_show_sensors();
    ui_show_tracks();
    ui_show_trains();
    ui_show_logs();
    ui_show_summary();
}