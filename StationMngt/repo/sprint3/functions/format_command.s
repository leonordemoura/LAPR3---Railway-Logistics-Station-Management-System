    .text
    .globl format_command

# format_command(char *op, int n, char *cmd)
# a0 = op (entrada, não alterada)
# a1 = n  (0..99 para comandos de 2 letras)
# a2 = cmd (buffer de saída)
format_command:
    addi sp, sp, -48
    sw ra, 44(sp)

    # buffer temporário em sp[0..3]
    addi t5, sp, 0          # t5 -> temp buffer base
    li   t4, 0              # len = 0

# Ignorar espaços iniciais
skip_leading:
    lbu t0, 0(a0)
    beqz t0, fail_clear     # string vazia
    li  t1, ' '
    beq t0, t1, lead_next
    j   parse_token
lead_next:
    addi a0, a0, 1
    j skip_leading

# Ler primeira palavra, convertendo para maiúsculas
# Máximo 3 caracteres (GTH)
parse_token:
    lbu t0, 0(a0)
    beqz t0, end_token
    li  t1, ' '
    beq t0, t1, end_token

    # converter para maiúscula se necessário
    li  t1, 'a'
    li  t2, 'z'
    blt t0, t1, upper_done
    bgt t0, t2, upper_done
    addi t0, t0, -32
upper_done:
    li  t6, 3
    bgt t4, t6, fail_clear   # mais de 3 chars -> inválido
    add  t3, t5, t4          # destino = buffer + len
    sb   t0, 0(t3)
    addi t4, t4, 1
    addi a0, a0, 1
    j parse_token

end_token:
    add  t3, t5, t4
    sb   zero, 0(t3)        # null-terminate
    beqz t4, fail_clear      # nenhuma letra -> inválido

# Comparar comandos
    li  t6, 3
    bne t4, t6, check_two
    # comprimento 3: só GTH é válido
    lbu t0, 0(t5)
    li  t1, 'G'
    bne t0, t1, fail_clear
    lbu t0, 1(t5)
    li  t1, 'T'
    bne t0, t1, fail_clear
    lbu t0, 2(t5)
    li  t1, 'H'
    bne t0, t1, fail_clear
    # copiar "GTH" para cmd
    la  t3, STR_GTH
copy_gth:
    lbu t0, 0(t3)
    sb  t0, 0(a2)
    beqz t0, success
    addi t3, t3, 1
    addi a2, a2, 1
    j copy_gth

check_two:
    li  t6, 2
    bne t4, t6, fail_clear

    # RE ?
    lbu t0, 0(t5)
    li  t1, 'R'
    bne t0, t1, chk_YE
    lbu t0, 1(t5)
    li  t1, 'E'
    bne t0, t1, chk_YE
    j cmd_two_ok

chk_YE:
    lbu t0, 0(t5)
    li  t1, 'Y'
    bne t0, t1, chk_GE
    lbu t0, 1(t5)
    li  t1, 'E'
    bne t0, t1, chk_GE
    j cmd_two_ok

chk_GE:
    lbu t0, 0(t5)
    li  t1, 'G'
    bne t0, t1, chk_RB
    lbu t0, 1(t5)
    li  t1, 'E'
    bne t0, t1, chk_RB
    j cmd_two_ok

chk_RB:
    lbu t0, 0(t5)
    li  t1, 'R'
    bne t0, t1, fail_clear
    lbu t0, 1(t5)
    li  t1, 'B'
    bne t0, t1, fail_clear
    # se chegou aqui, RB é válido

cmd_two_ok:
    # validar n no intervalo [0,99]
    li  t1, 0
    blt a1, t1, fail_clear
    li  t1, 99
    bgt a1, t1, fail_clear

    # escrever CMD
    lbu t0, 0(t5)
    sb  t0, 0(a2)
    lbu t0, 1(t5)
    sb  t0, 1(a2)
    li  t0, ','
    sb  t0, 2(a2)

    # calcular dígitos
    li  t2, 10
    div t3, a1, t2
    addi t3, t3, '0'
    sb  t3, 3(a2)

    rem t3, a1, t2
    addi t3, t3, '0'
    sb  t3, 4(a2)

    sb  zero, 5(a2)
    j success

fail_clear:
    sb zero, 0(a2)
    li a0, 0
    j finish

success:
    li a0, 1

finish:
    lw ra, 44(sp)
    addi sp, sp, 48
    ret

    .data
STR_GTH: .asciz "GTH"