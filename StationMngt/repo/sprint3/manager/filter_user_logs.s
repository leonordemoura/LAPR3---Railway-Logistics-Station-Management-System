    .section .text
    .global filter_user_logs
    .equ LOGENTRY_SIZE, 196


#-------------------------------------------------------
# int filter_user_logs(const LogEntry* logs, int n_logs,
#                      const char* username,
#                      LogEntry* user_logs, int* n_user_logs)
#
# Argumentos:
# a0 = logs          -> array de LogEntry
# a1 = n_logs        -> número de logs no array
# a2 = username      -> username a filtrar
# a3 = user_logs     -> array de saída (filtrados)
# a4 = n_user_logs   -> ponteiro para int que recebe número de logs filtrados
#-------------------------------------------------------

filter_user_logs:
    # --- Prologo
    # Reserva espaço na stack e salva registos s0-s7 + ra
    # para preservar variáveis e retorno
    addi sp, sp, -36
    sw ra, 0(sp)
    sw s0, 4(sp)
    sw s1, 8(sp)
    sw s2, 12(sp)
    sw s3, 16(sp)
    sw s4, 20(sp)
    sw s5, 24(sp)
    sw s6, 28(sp)
    sw s7, 32(sp)

    # --- Inicializar variáveis ---
    mv s0, a0                    # s0 = logs
    mv s1, a1                    # s1 = n_logs
    mv s2, a2                    # s2 = username a filtrar
    mv s3, a3                    # s3 = user_logs (array de saída)
    mv s4, a4                    # s4 = n_user_logs (ponteiro)
    li s5, 0                     # s5 = contador de logs filtrados
    li s6, 0                     # s6 = índice i
    li s7, LOGENTRY_SIZE         # s7 = sizeof(LogEntry)

loop_start:
    # se i >= n_logs, termina loop
    bge s6, s1, loop_end

    # calcula endereço de logs[i] = s0 + i * 196
    mul t1, s6, s7
    add t2, s0, t1     # t2 = &logs[i]

    # endereço do campo username dentro de logs[i]
    addi t3, t2, 4     # t3 = &logs[i].username (offset 4)

    # prepara argumentos para strcmp
    mv a0, t3          # a0 = logs[i].username
    mv a1, s2          # a1 = username a filtrar

    # chama strcmp
    call my_strcmp

    # se a0 != 0, strings são diferentes, ignora este log
    bnez a0, next

    # --- strings são iguais, copiar logs[i] para user_logs[s5] ---
    mul t4, s5, s7     # offset no array user_logs
    add t5, s3, t4     # t5 = &user_logs[s5]

    mv a0, t2          # a0 = origem (&logs[i])
    mv a1, t5          # a1 = destino (&user_logs[s5])
    call copy_logentry

    addi s5, s5, 1     # incrementa contador de logs filtrados

next:
    addi s6, s6, 1     # i++
    j loop_start

loop_end:
    # escreve o número de logs filtrados em *n_user_logs
    sw s5, 0(s4)

    # valor de retorno:   1 se encontrou logs, 0 caso contrário
    li a0, 0
    beqz s5, end_filter
    li a0, 1

end_filter:
    # --- Epilogo:
    # Restaura registos salvos e devolve stack ao estado original
    lw ra, 0(sp)
    lw s0, 4(sp)
    lw s1, 8(sp)
    lw s2, 12(sp)
    lw s3, 16(sp)
    lw s4, 20(sp)
    lw s5, 24(sp)
    lw s6, 28(sp)      # restaura s6
    lw s7, 32(sp)      # restaura s7
    addi sp, sp, 36
    ret

#-------------------------------------------------------
# strcmp:  retorna 0 se strings iguais, retorna de 1 caso contrário
# a0 = str1
# a1 = str2
#-------------------------------------------------------
my_strcmp:
    mv t3, a0          # copia ponteiro str1 para t3
    mv t4, a1          # copia ponteiro str2 para t4
strcmp_loop:
    lbu t0, 0(t3)      # carrega byte atual de str1
    lbu t1, 0(t4)      # carrega byte atual de str2
    bne t0, t1, strcmp_diff    # se diferentes, vai para strcmp_diff
    beqz t0, strcmp_eq         # se t0==0 (fim da string), são iguais
    addi t3, t3, 1     # avança ponteiro str1
    addi t4, t4, 1     # avança ponteiro str2
    j strcmp_loop
strcmp_diff:
    li a0, 1      # retorna 1 (strings diferentes)
    ret
strcmp_eq:
    li a0, 0           # retorna 0 (strings iguais)
    ret

#-------------------------------------------------------
# copy_logentry: copia um LogEntry completo (196 bytes)
# a0 = origem
# a1 = destino
#-------------------------------------------------------
copy_logentry:
    mv t3, a0          # t3 = ponteiro origem
    mv t4, a1          # t4 = ponteiro destino
    li t5, 0           # t5 = contador de bytes
    li t1, LOGENTRY_SIZE         # t1 = tamanho total
copy_loop:
    bge t5, t1, copy_done      # se copiou tudo, termina
    lbu t2, 0(t3)              # carrega byte da origem
    sb t2, 0(t4)               # escreve byte no destino
    addi t3, t3, 1             # avança ponteiro origem
    addi t4, t4, 1             # avança ponteiro destino
    addi t5, t5, 1             # incrementa contador
    j copy_loop
copy_done:
    ret