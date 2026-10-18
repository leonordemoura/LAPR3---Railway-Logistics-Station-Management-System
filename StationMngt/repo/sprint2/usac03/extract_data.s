.section .text
    .globl extract_data

extract_data:
    # Prologue: Save registers and allocate stack space
    addi sp, sp, -32        # Allocate 32 bytes on stack
    sw ra, 28(sp)           # Save return address
    sw s0, 24(sp)           # Save s0
    sw s1, 20(sp)           # Save s1
    sw s2, 16(sp)           # Save s2
    sw s3, 12(sp)           # Save s3
    sw s4, 8(sp)            # Save s4

    # Save parameters to saved registers
    mv s0, a0           # s0 = str
    mv s1, a1           # s1 = token
    mv s2, a2           # s2 = unit
    mv s3, a3           # s3 = value pointer
    
    # Search for token in str
    mv s4, s0           # s4 = current position in str

search_token:
    lbu t0, 0(s4)           # Load current character
    beqz t0, not_found      # If null terminator, token not found
    
    # Check if at valid token boundary (start of string or after '#')
    beq s4, s0, try_match     # At start of string
    addi t1, s4, -1           # Get previous position
    lbu t1, 0(t1)             # Load previous character
    li t2, '#'                # Load '#' character
    bne t1, t2, next_pos      # Not after '#', skip this position
    
try_match:
    # Try to match token at current position
    mv a0, s4                 # a0 = current position in str
    mv a1, s1                 # a1 = token to match
    call match_string         # Returns 1 if match, 0 otherwise
    bnez a0, found_token      # If match found, proceed to extraction
    
next_pos:
    addi s4, s4, 1            # Move to next character
    j search_token            # Continue searching
    
found_token:
    # s4 points to token, skip it
    mv a0, s1               # a0 = token string
    call strlen             # Get length of token
    add s4, s4, a0          # s4 now points to character after token
    
# Skip "&unit:"
found_unit_marker:
    addi s4, s4, 6      # skip "&unit:"
    
# Copy unit
copy_unit:
    mv t2, s2               # t2 = unit destination
copy_loop1:
    lbu t0, 0(s4)           # Load current character
    li t1, '&'
    beq t0, t1, done_unit   # If '&' found, unit is complete
    li t1, ' '
    beq t0, t1, check_amp   # If space, check if followed by '&'
    beqz t0, not_found      # If null terminator, format error
    sb t0, 0(t2)            # Store character in unit buffer
    addi t2, t2, 1          # Move to next position in unit buffer
    addi s4, s4, 1          # Move to next character in source
    j copy_loop1            # Continue copying
check_amp:
    lbu t0, 1(s4)           # Load next character
    li t1, '&'
    beq t0, t1, done_unit   # If next is '&', unit is complete
    addi s4, s4, 1          # Skip the space
    j copy_loop1            # Continue copying
done_unit:
    sb zero, 0(t2)          # null terminate
    
# Skip "&value:" 
skip_value_marker:
    addi s4, s4, 7      # skip "&value:"
    
parse_num:
    li t2, 0                # result
    li t3, 0                # sign flag
    
    lbu t0, 0(s4)           # Load first character
    li t1, '-'
    bne t0, t1, parse_loop  # If not '-', start parsing digits
    li t3, 1                # Set sign flag to negative
    addi s4, s4, 1          # Skip the '-' character
parse_loop:
    lbu t0, 0(s4)           # Load current character
    li t1, '0'
    blt t0, t1, done_parse  # If less than '0', not a digit
    li t1, '9'
    bgt t0, t1, done_parse  # If greater than '9', not a digit
    
    li t1, 10
    mul t2, t2, t1          # result *= 10
    addi t0, t0, -'0'       # Convert ASCII to digit (subtract '0')
    add t2, t2, t0          # result += digit
    addi s4, s4, 1          # Move to next character
    j parse_loop            # Continue parsing
done_parse:
    beqz t3, store          # If positive, skip negation
    sub t2, zero, t2        # Negate result (result = -result)
store:
    sw t2, 0(s3)        # Store parsed value to output pointer
    li a0, 1            # success
    j end               # Jump to epilogue
not_found:
    sb zero, 0(s2)      # Set unit buffer to empty string
    sw zero, 0(s3)      # Set value to 0
    li a0, 0            # failure   
end:
    lw ra, 28(sp)       # Restore return address
    lw s0, 24(sp)       # Restore s0
    lw s1, 20(sp)       # Restore s1
    lw s2, 16(sp)       # Restore s2
    lw s3, 12(sp)       # Restore s3
    lw s4, 8(sp)        # Restore s4
    addi sp, sp, 32     # Deallocate stack space
    ret                 # Return to caller

# returns 1 if match, 0 otherwise
# Matches token AND checks that next char is '&'
match_string:
    lbu t0, 0(a0)             # Load character from source string
    lbu t1, 0(a1)             # Load character from token string
    beqz t1, check_boundary   # Token fully matched, check boundary
    bne t0, t1, no_match      # If characters don't match, return failure
    addi a0, a0, 1            # Advance source pointer
    addi a1, a1, 1            # Advance token pointer
    j match_string            # Continue matching
check_boundary:
    # After matching token, next char must be '&'
    lbu t0, 0(a0)             # Load next character in source
    li t1, '&'
    beq t0, t1, match_ok      # If '&', match is valid
    j no_match                # Otherwise, not a valid match
match_ok:
    li a0, 1                  # Return 1 (success)
    ret
no_match:
    li a0, 0                  # Return 0 (failure)
    ret

# finds size of word
strlen:
    li t0, 0                  # t0 = counter = 0
len_loop:
    add t1, a0, t0            # t1 = address of current character
    lbu t2, 0(t1)             # Load current character
    beqz t2, len_done         # If null terminator, done
    addi t0, t0, 1            # Increment counter
    j len_loop                # Continue counting
len_done:
    mv a0, t0                 # Move length to return register
    ret                       # Return to caller
    