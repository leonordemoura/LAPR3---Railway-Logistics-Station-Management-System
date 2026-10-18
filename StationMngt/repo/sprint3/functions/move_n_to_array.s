    .section .text
    .globl move_n_to_array
move_n_to_array:
    # a0 = buffer
    # a1 = length
    # a2 = nelem*
    # a3 = tail*
    # a4 = head*
    # a5 = n
    # a6 = array

    # carregar *nelem e comparar com n
    lw t0, 0(a2)           # t0 = *nelem
    blt t0, a5, fail       # se *nelem < n -> falha

    lw t1, 0(a3)           # t1 = *tail
    li t2, 0               # contador i = 0

loop:
    beq t2, a5, end_loop   # se i == n, sai

    # calcular índice do buffer = (tail + i) % length
    add t3, t1, t2
    rem t3, t3, a1

    # carregar elemento do buffer
    slli t4, t3, 2         # t4 = t3 * 4 (índice em bytes)
    add t4, t4, a0         # endereço buffer + t4
    lw t5, 0(t4)           # t5 = buffer[(tail+i)%length]

    # guardar no array[i]
    slli t6, t2, 2
    add t6, t6, a6
    sw t5, 0(t6)

    addi t2, t2, 1
    j loop

end_loop:
    # atualizar *tail = (tail + n) % length
    add t1, t1, a5
    rem t1, t1, a1
    sw t1, 0(a3)

    # atualizar *nelem -= n
    sub t0, t0, a5
    sw t0, 0(a2)

    li a0, 1
    ret

fail:
    li a0, 0
    ret
