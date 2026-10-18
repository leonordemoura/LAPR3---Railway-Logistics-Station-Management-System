.section .text
.global encrypt_data

encrypt_data:
	#a0 = in (input string)
	#a1 = key
	#a2 = out (output string)
  #verificar se key <1
  li t0, 1
  blt a1, t0, invalid_key

  #verificar key>26
  li t0, 26
  bgt a1, t0, invalid_key

loop:
  lbu t1, 0(a0) # t1= *in (ler caracterr atual)
  beqz t1, done # se *in ==\0 fim

  #verificar se é letra maiuscula
  li t2, 65  # 'A'
  blt t1, t2, invalid_key

  li t2, 90 # 'Z'
  bgt t1, t2, invalid_key

  addi t1, t1, -65 # t1= *in - A (A=65)
  add t1, t1, a1  # t1= t1 + key
  li t2, 26        
  rem t1, t1, t2   # t1 = t1 % 26
  addi t1, t1, 65  # t1 = t1 + A (Volta pra letra)

  sb t1, 0(a2)     # *out = car cifrado

  addi a0, a0, 1   # in++
  addi a2, a2, 1   # out++
  j loop


done:
  sb x0, 0(a2)  # *out = \0 - termina string
  li a0, 1      # return 1
  ret


invalid_key:
  sb x0, 0(a2)  # out[0] = \0 - string vazia
  li a0, 0      # return 0
  ret
  
