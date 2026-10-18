.section .text
.global dequeue_value

#---------------------------------------------------------------------------------------
#int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value)
#---------------------------------------------------------------------------------------
# a0 = buffer, a1 = length, a2 = nelem, a3 = tail, a4 = head, a5 = value
#---------------------------------------------------------------------------------------

dequeue_value:
    lw t0, 0(a2)        # t0 = nelem
    beq t0, x0, empty   # se nelem == 0 -> vazio

    lw t1, 0(a3)        # t1 = tail
    slli t2, t1, 2      # t2 = tail*4
    add t2, a0, t2      # t2 = &buffer[tail]

    lw t3, 0(t2)        # t3 = buffer[tail]
    sw t3, 0(a5)        # *value =  t3


    # NOTA IMPORTANTE: LIMPAR VALORES ELIMINADOS DA FILA COM 0?
    # Zero pode ser um valor válido no buffer,
    # portanto limpar com zero pode criar ambiguidade.
    # Consultar o professor sobre a melhor abordagem.
    # sw x0, 0(t2)


    addi t1, t1, 1      # tail + 1
    rem t1, t1, a1      # tail = (tail + 1) % length
    sw t1, 0(a3)        # atualizar o tail

    addi t0, t0, -1
    sw t0, 0(a2)        # atualizar nelem

    li a0, 1            # sucesso
    ret

empty:
    li a0, 0            # falha: buffer vazio
    ret
