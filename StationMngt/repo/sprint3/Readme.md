StationMngt/repo/sprint3/
├── common/                    # Código partilhado entre componentes
│   ├── structs.h             # Todas as structs (User, LogEntry, Track, Train, etc)
│   ├── constants.h           # Constantes globais (MAX_TRACKS, MAX_USERS, etc)
│   └── config.txt            # Ficheiro de configuração (utilizadores, pistas, sensores, logs) 
│
├── ui/                        # Componente UI (interface utilizador)
│   ├── ui.c                  # UI geral e login
│   ├── ui.h                  # Header com todas as funções da UI
│   ├── ui_export.c           # UI para export de logs
│   ├── ui_setup.c            # UI para visualização de setup
│   ├── ui_manager_tracks.c   # UI para gestão de pistas
│   └── ui_board.c            # UI para board
│
├── manager/                   # Componente Manager (lógica central)
│   ├── manager.h             # Header com todas as funções do manager
│   ├── manager_tracks.c      # USAC16: gestão de pistas
│   ├── manager_setup.c       # USAC11: setup inicial (ler ficheiros)
│   ├── manager_export.c      # USAC12: exportar logs
│   ├── manager_sensors.c     # USAC13: comunicação com sensores
│   ├── manager_lights.c      # USAC14: comunicação com luzes
│   ├── manager_get_board_data.c  # USAC15: obter dados para board
│   ├── manager_connection.c  # Gestão de conexão serial
│   ├── logging. c             # Funções para adicionar logs
│   └── filter_user_logs.o    # Assembly filtro de logs (USAC12)
│
│
├── functions/            # Funções auxiliares em Assembly
│   ├── functions.h       # Header com todas as funções auxiliares
│   ├── decrypt_data.s    
│   ├── dequeue_value.s   
│   ├── encrypt_data.s     
│   ├── enqueue_value.s    
│   ├── extract_data.s     
│   ├── format_command.s   
│   ├── median.s   
│   ├── sort_array.s   
│   └── move_n_to_array.s  
│
├── board/                    # Componente Board (display sinótico)
│   ├── board.c               # Implementação do board
│   └── board.h    
│   
│
├── sensors_lightsigns/        # Código Arduino
│   └── sensors_lightsigns. ino 
│
├── helper/                    
│   └── main.c                
│
├── Makefile                   # Makefile principal (compila tudo)
└── Readme.md                  # Este ficheiro