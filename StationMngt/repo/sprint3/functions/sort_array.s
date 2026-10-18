    .section .text
    .globl sort_array
sort_array:
    # a0 = vec
    # a1 = length
    # a2 = order (1 = asc, 0 = desc)

    blez a1, fail        # if length <= 0 -> return 0

    addi t0, zero, 0     # i = 0
outer_loop:
    add  t1, zero, zero  # j = 0
    add  t2, a1, zero
    addi t2, t2, -1      # limit = length - 1 - i
    sub  t2, t2, t0
    blez t2, next_i      # if limit <= 0, skip inner loop

inner_loop:
    slli t3, t1, 2       # offset = j * 4
    add  t4, a0, t3
    lw   t5, 0(t4)       # vec[j]
    lw   t6, 4(t4)       # vec[j+1]

    beq  a2, zero, desc_check
asc_check:
    ble  t5, t6, no_swap # ascending: if vec[j] <= vec[j+1], no swap
    j    do_swap

desc_check:
    bge  t5, t6, no_swap # descending: if vec[j] >= vec[j+1], no swap
    # else swap
do_swap:
    sw   t6, 0(t4)
    sw   t5, 4(t4)

no_swap:
    addi t1, t1, 1
    blt  t1, t2, inner_loop

next_i:
    addi t0, t0, 1
    blt  t0, a1, outer_loop

    li a0, 1             # success
    jr ra

fail:
    li a0, 0             # failure
    jr ra
