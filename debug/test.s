	.text
	.globl	main
	.p2align	1
	.type	main,@function
main:
.main_block:
	sw	s0, -4(sp)
	addi	sp, sp, -28
	addi	s0, sp, 28
	sw	ra, -28(s0)
	sw	s4, -24(s0)
	call	_GLOBAL_
	li	t6,1
	sw	t6, -12(s0)
	j	.for_condition_block

.main_block1:
	lw	a0, -8(s0)
	lw	s4, -24(s0)
	lw	ra, -28(s0)
	addi	sp, sp, 28
	lw	s0, -4(sp)
	ret

.for_condition_block:
	la	t6,N_glo
	lw	t5, 0(t6)
	lw	t6, -12(s0)
	slt	t6, t5, t6
	xori	t6, t6, 1
	bne	t6,zero,.for_body_block
	j	.main_block2

.for_iter_block:
	lw	t6, -12(s0)
	addi	t6, t6, 1
	sw	t6, -12(s0)
	j	.for_condition_block

.for_body_block:
	la	t6,b_glo
	lw	t4, 0(t6)
	lw	t5, -12(s0)
	li	t6,1
	mul	t6, t5, t6
	add	t5, t6, t4
	li	t6,1
	sw	t6, 0(t5)
	j	.for_iter_block

.main_block2:
	li	t6,2
	sw	t6, -12(s0)
	j	.for_condition_block1

.for_condition_block1:
	la	t6,N_glo
	lw	t5, 0(t6)
	lw	t6, -12(s0)
	slt	t6, t5, t6
	xori	t6, t6, 1
	bne	t6,zero,.for_body_block1
	j	.main_block3

.for_iter_block1:
	lw	t6, -12(s0)
	addi	t6, t6, 1
	sw	t6, -12(s0)
	j	.for_condition_block1

.for_body_block1:
	la	t6,b_glo
	lw	t4, 0(t6)
	lw	t5, -12(s0)
	li	t6,1
	mul	t6, t5, t6
	add	t6, t6, t4
	lw	t6, 0(t6)
	bne	t6,zero,.if_then_block
	j	.if_else_block

.main_block3:
	la	t6,resultCount_glo
	lw	a0, 0(t6)
	call	_f_toString
	mv	a1,a0
	la	a0,_str1
	call	_f__string_merge
	call	_f_println
	li	t6,0
	sw	t6, -8(s0)
	j	.main_block1

.if_then_block:
	li	a0,0
	call	_f_toString
	call	_f_println
	j	.main_block4

.main_block4:
	la	t6,b_glo
	lw	t4, 0(t6)
	lw	t5, -12(s0)
	li	t6,1
	mul	t6, t5, t6
	add	t6, t6, t4
	lw	t6, 0(t6)
	bne	t6,zero,.if_then_block1
	j	.main_block5

.if_else_block:
	li	a0,1
	call	_f_toString
	call	_f_println
	j	.main_block4

.if_then_block1:
	li	t6,2
	sw	t6, -16(s0)
	lw	t5, -12(s0)
	li	t6,3
	slt	t6, t6, t5
	bne	t6,zero,._sBlock_block
	j	._dBlock_block

.main_block5:
	j	.for_iter_block1

.if_then_block2:
	la	t6,resultCount_glo
	lw	t6, 0(t6)
	addi	t5, t6, 1
	la	t6,resultCount_glo
	sw	t5, 0(t6)
	lw	a0, -12(s0)
	call	_f_toString
	mv	s4,a0
	lw	t6, -12(s0)
	li	t5,2
	sub	a0, t6, t5
	call	_f_toString
	la	a1,_str
	call	_f__string_merge
	mv	a1,s4
	call	_f__string_merge
	call	_f_println
	j	.main_block6

.main_block6:
	j	.while_condition_block

._dBlock_block:
	sw	t6, -20(s0)
	j	._tBlock_block

._sBlock_block:
	la	t6,b_glo
	lw	t5, 0(t6)
	lw	t6, -12(s0)
	li	t4,2
	sub	t4, t6, t4
	li	t6,1
	mul	t6, t4, t6
	add	t6, t6, t5
	lw	t6, 0(t6)
	sw	t6, -20(s0)
	j	._tBlock_block

._tBlock_block:
	lw	t6, -20(s0)
	bne	t6,zero,.if_then_block2
	j	.main_block6

.while_condition_block:
	la	t6,N_glo
	lw	t6, 0(t6)
	lw	t4, -16(s0)
	lw	t5, -12(s0)
	mul	t5, t5, t4
	slt	t6, t6, t5
	xori	t6, t6, 1
	bne	t6,zero,.while_body_block
	j	.main_block7

.while_body_block:
	la	t6,b_glo
	lw	t6, 0(t6)
	lw	t4, -16(s0)
	lw	t5, -12(s0)
	mul	t5, t5, t4
	li	t4,1
	mul	t5, t5, t4
	add	t6, t5, t6
	li	t5,0
	sw	t5, 0(t6)
	lw	t6, -16(s0)
	addi	t6, t6, 1
	sw	t6, -16(s0)
	j	.while_condition_block

.main_block7:
	j	.main_block5

	.size	main, .-main
			 # -- End function
	.globl	_global_var_init
	.p2align	1
	.type	_global_var_init,@function
_global_var_init:
.N_block:
	sw	s0, -4(sp)
	addi	sp, sp, -4
	addi	s0, sp, 4
	li	t6,15000
	la	t5,N_glo
	sw	t6, 0(t5)
	j	.N_block1

.N_block1:
	addi	sp, sp, 4
	lw	s0, -4(sp)
	ret

	.size	_global_var_init, .-_global_var_init
			 # -- End function
	.globl	_global_var_init1
	.p2align	1
	.type	_global_var_init1,@function
_global_var_init1:
.b_block:
	sw	s0, -4(sp)
	addi	sp, sp, -4
	addi	s0, sp, 4
	mv	s0,ra
	li	t5,15001
	li	t6,1
	mul	t6, t5, t6
	addi	a0, t6, 4
	call	_f__heap_malloc
	li	t6,15001
	sw	t6, 0(a0)
	addi	t6, a0, 4
	la	t5,b_glo
	sw	t6, 0(t5)
	j	.b_block1

.b_block1:
	mv	ra,s0
	addi	sp, sp, 4
	lw	s0, -4(sp)
	ret

	.size	_global_var_init1, .-_global_var_init1
			 # -- End function
	.globl	_global_var_init2
	.p2align	1
	.type	_global_var_init2,@function
_global_var_init2:
.resultCount_block:
	sw	s0, -4(sp)
	addi	sp, sp, -4
	addi	s0, sp, 4
	li	t6,0
	la	t5,resultCount_glo
	sw	t6, 0(t5)
	j	.resultCount_block1

.resultCount_block1:
	addi	sp, sp, 4
	lw	s0, -4(sp)
	ret

	.size	_global_var_init2, .-_global_var_init2
			 # -- End function
	.globl	_GLOBAL_
	.p2align	1
	.type	_GLOBAL_,@function
_GLOBAL_:
._GLOBAL__block:
	sw	s0, -4(sp)
	addi	sp, sp, -4
	addi	s0, sp, 4
	mv	s0,ra
	call	_global_var_init
	call	_global_var_init1
	call	_global_var_init2
	mv	ra,s0
	addi	sp, sp, 4
	lw	s0, -4(sp)
	ret

	.size	_GLOBAL_, .-_GLOBAL_
			 # -- End function
	.type	_str,@object
	.section	.rodata
_str:
	.asciz	" "
	.size	_str, 2
	.type	_str1,@object
	.section	.rodata
_str1:
	.asciz	"Total: "
	.size	_str1, 8
	.type	N_glo,@object
	.section	.bss
	.globl	N_glo
N_glo:
	.word	0
	.size	N_glo, 4
	.type	b_glo,@object
	.section	.bss
	.globl	b_glo
b_glo:
	.word	0
	.size	b_glo, 4
	.type	resultCount_glo,@object
	.section	.bss
	.globl	resultCount_glo
resultCount_glo:
	.word	0
	.size	resultCount_glo, 4

