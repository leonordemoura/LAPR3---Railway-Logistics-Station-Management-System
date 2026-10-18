.section .text
.global enqueue_value

#----------------------------
#int enqueue_value(int* buffer, int length, int* nelem, int* tail, int* head, int value)
#----------------------------
# a0 = buffer, a1 = length, a2 = nelem, a3 = tail, a4 = head, a5 = value
#----------------------------

enqueue_value:
    lw t0, 0(a2)    # t0 = *nelem

    beq t0, a1, full_case  # se forem iguais, o buffer está cheio

    #caso o buffer não esteja cheio

not_full:
    lw t1, 0(a4)    # t1 = *head

    # endereço = buffer + head*4
    slli t2, t1, 2      # t2 = head*4 -> calcula o indice do endereço do elemento buffer[head]
    add t2, a0, t2      # t2 = buffer + head*4 -> calcula o endereço do elemento buffer[head]

    # escrever value no buffer
    sw a5, 0(t2)

    # head = (head + 1) % length
    addi t1, t1, 1      # incrementar head++ após inserir o value no buffer
    rem t1, t1, a1      # se length = 5, caso t1 = 5 (fora do loop [0,4]), t1 = 5%5=0 (loop circular)
    sw t1, 0(a4)        # guarda o valor de t1 no endereço de a4(head)

    #nelem++
    addi t0, t0, 1
    sw t0, 0(a2)

    #retorno: se agora cheio -> 1
    beq t0, a1, ret_1

    li a0, 0
    ret

#--------------------------
# se o buffer tiver cheio
#--------------------------

full_case:
    #avançar tail (remover o mais antigo)
    lw t3, 0(a3)    # t3 = *tail
    addi t3, t3, 1
    rem t3, t3, a1
    sw t3, 0(a3)

    # inserir em head
    lw t1, 0(a4)    # t1 = *head
    slli t2, t1, 2
    add t2, a0, t2
    sw a5, 0(t2)

    # head = (head + 1) % length
    addi t1, t1, 1
    rem t1, t1, a1
    sw t1, 0(a4)

    #nelem permanece a length
    sw a1, 0(a2)

ret_1:
    li a0, 1
    ret
