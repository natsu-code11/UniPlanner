.data
msg: .asciz "PREMUTO"

.global _start
_start:

.text
KEY_ISR: 
 	push {r0-r5}
 	ldr r0=0xFF200050
 	ldr r1, [r0]
 
 	tst r1, #1
 	beq fine_key
 
 	ldr r2, =msg
 	ldr r3, =0xFF201000
 
 stampa: 
 	ldrb r4, [r2]
 	cmp r4, #0
 	beq fine_key
 	strb r4, [r3]
 
 	add r2, r2, #1
 	add r3, r3, #1
 	b stampa 
 
 fine_key:
  	str r1, [r0, #0xc]
  	pop {r0-r5}
  	bx lr