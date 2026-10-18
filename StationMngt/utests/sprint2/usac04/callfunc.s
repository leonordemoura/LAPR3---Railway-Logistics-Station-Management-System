    .equ STDOUT,1
    .equ STDERR,2
    .equ WRITE,64
    .equ EXIT,93
    .equ MAGIC_NUMBER,0x2649843a2649843a

    .section .rodata
msg:
    .ascii "Please respect the correct register usage on the RISC-V processors!\n"

    .section .text
    .globl callfunc

# callfunc(f, a1, a2, a3, ...)
# a0 = pointer to function
# a1..a7 = args
# returns whatever f returns in a0
callfunc:
    addi    sp, sp, -64          # frame (multiple of 16)
    sw      ra, 60(sp)
    sw      s0, 56(sp)
    sw      s1, 52(sp)
    sw      s2, 48(sp)
    sw      s3, 44(sp)
    sw      s4, 40(sp)
    sw      s5, 36(sp)
    sw      s6, 32(sp)
    sw      s7, 28(sp)
    sw      s8, 24(sp)
    sw      s9, 20(sp)
    sw      s10,16(sp)
    sw      s11,12(sp)
    sw      gp, 8(sp)
    sw      tp, 4(sp)

    addi    s0, sp, 64           # frame pointer

    li      s1, 0x1234           # sentinels
    li      s2, 0x1234
    li      s3, 0x1234
    li      s4, 0x1234
    li      s5, 0x1234
    li      s6, 0x1234
    li      s7, 0x1234
    li      s8, 0x1234
    li      s9, 0x1234
    li      s10,0x1234
    li      s11,0x1234
    li      gp, 0x1234
    li      tp, 0x1234

    mv      t0, a0               # function pointer
    mv      a0, a1               # shift args
    mv      a1, a2
    mv      a2, a3
    mv      a3, a4
    mv      a4, a5
    mv      a5, a6
    mv      a6, a7               # supports up to 7 args

    jalr    ra, t0               # call function

    li      t0, 0x1234           # check callee-saved regs intact
    bne     t0, s1, reg_error
    bne     t0, s2, reg_error
    bne     t0, s3, reg_error
    bne     t0, s4, reg_error
    bne     t0, s5, reg_error
    bne     t0, s6, reg_error
    bne     t0, s7, reg_error
    bne     t0, s8, reg_error
    bne     t0, s9, reg_error
    bne     t0, s10, reg_error
    bne     t0, s11, reg_error
    bne     t0, gp, reg_error
    bne     t0, tp, reg_error

restore_ok:
    lw      ra, 60(sp)
    lw      s0, 56(sp)
    lw      s1, 52(sp)
    lw      s2, 48(sp)
    lw      s3, 44(sp)
    lw      s4, 40(sp)
    lw      s5, 36(sp)
    lw      s6, 32(sp)
    lw      s7, 28(sp)
    lw      s8, 24(sp)
    lw      s9, 20(sp)
    lw      s10,16(sp)
    lw      s11,12(sp)
    lw      gp, 8(sp)
    lw      tp, 4(sp)
    addi    sp, sp, 64
    ret

reg_error:
    lw      ra, 60(sp)
    lw      s0, 56(sp)
    lw      s1, 52(sp)
    lw      s2, 48(sp)
    lw      s3, 44(sp)
    lw      s4, 40(sp)
    lw      s5, 36(sp)
    lw      s6, 32(sp)
    lw      s7, 28(sp)
    lw      s8, 24(sp)
    lw      s9, 20(sp)
    lw      s10,16(sp)
    lw      s11,12(sp)
    lw      gp, 8(sp)
    lw      tp, 4(sp)
    addi    sp, sp, 64

    li      a0, STDERR           # fd = 2
    la      a1, msg              # buf
    li      a2, 68               # len
    li      a7, WRITE            # write
    ecall

    li      a0, 33               # exit code
    li      a7, EXIT             # exit syscall
    ecall
