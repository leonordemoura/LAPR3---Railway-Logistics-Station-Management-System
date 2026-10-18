.section .text
    .globl median
    
median:
    blez a1, ret_failure       # if n <= 0, error
    
sort:
    addi t0, a1, -1            # t0 = (n-1)
outer_loop:
    beqz t0, done_sort         # if t0 == 0, end sort
    li t1, 0                   

inner_loop:
    add t2, a0, t1             # address of element i
    lw t3, 0(t2)               # element
    lw t4, 4(t2)               # next element
    ble t3, t4, no_swap        # if element <= next element, don't swap
    # Swap
    sw t4, 0(t2)
    sw t3, 4(t2)
no_swap:
    addi t1, t1, 4             
    slli t5, t0, 2             # t5 = t0 * 4 
    blt t1, t5, inner_loop     # continue while i < (n-1)*4
    addi t0, t0, -1            # reduce outer limit
    j outer_loop

done_sort:
    li t0, 2
    rem t1, a1, t0             # t1 = n % 2
    beqz t1, even_median       # if even → even_median
    j odd_median               # if odd → odd_median

even_median:
    li t0, 2
    div t1, a1, t0             # t1 = n / 2
    addi t3, t1, -1            # t3 = (n/2) - 1
    slli t3, t3, 2             # convert index to bytes
    slli t1, t1, 2             # t1 = (n/2) in bytes
    add t3, a0, t3             
    add t4, a0, t1             
    lw t5, 0(t3)               # load element at (n/2)-1
    lw t6, 0(t4)               # load element at n/2
    add t5, t5, t6             # sum of two central values
    
    # Check if sum is negative and odd (need to adjust for floor division)
    bltz t5, negative_sum      # if sum < 0, handle specially
    div t5, t5, t0             # positive: normal division
    j store_even_median
    
negative_sum:
    # For negative numbers, check if we need to round down
    rem t6, t5, t0             # get remainder
    div t5, t5, t0             # divide
    beqz t6, store_even_median # if remainder is 0, no adjustment needed
    addi t5, t5, -1            # adjust for floor division
    
store_even_median:
    sw t5, 0(a2)               # store median in *me
    j ret_success

odd_median:
    li t0, 2
    div t1, a1, t0             # t1 = n / 2
    slli t1, t1, 2             # index → bytes
    add t1, a0, t1             # address of central element
    lw t3, 0(t1)               # load median
    sw t3, 0(a2)               # store median in *me
    j ret_success

ret_failure:
    li a0, 0                   # return 0 on failure
    ret

ret_success:
    li a0, 1                   # return 1 on success
    ret
    