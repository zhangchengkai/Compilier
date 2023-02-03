	.text
	.attribute	4, 16
	.attribute	5, "rv32i2p0_m2p0"
	.file	"builtin.c"
	.globl	_f__heap_malloc
	.p2align	2
	.type	_f__heap_malloc,@function
_f__heap_malloc:
	tail	malloc
.Lfunc_end0:
	.size	_f__heap_malloc, .Lfunc_end0-_f__heap_malloc

	.globl	_f__string_merge
	.p2align	2
	.type	_f__string_merge,@function
_f__string_merge:
	addi	sp, sp, -16
	sw	ra, 12(sp)
	sw	s0, 8(sp)
	sw	s1, 4(sp)
	sw	s2, 0(sp)
	mv	s0, a1
	mv	s1, a0
	call	strlen
	mv	s2, a0
	mv	a0, s0
	call	strlen
	add	a0, s2, a0
	addi	a0, a0, 1
	call	malloc
	mv	s2, a0
	mv	a1, s1
	call	strcpy
	mv	a0, s2
	mv	a1, s0
	lw	ra, 12(sp)
	lw	s0, 8(sp)
	lw	s1, 4(sp)
	lw	s2, 0(sp)
	addi	sp, sp, 16
	tail	strcat
.Lfunc_end1:
	.size	_f__string_merge, .Lfunc_end1-_f__string_merge

	.globl	_f__stringcmp_eq
	.p2align	2
	.type	_f__stringcmp_eq,@function
_f__stringcmp_eq:
	addi	sp, sp, -16
	sw	ra, 12(sp)
	call	strcmp
	seqz	a0, a0
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end2:
	.size	_f__stringcmp_eq, .Lfunc_end2-_f__stringcmp_eq

	.globl	_f__stringcmp_neq
	.p2align	2
	.type	_f__stringcmp_neq,@function
_f__stringcmp_neq:
	addi	sp, sp, -16
	sw	ra, 12(sp)
	call	strcmp
	snez	a0, a0
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end3:
	.size	_f__stringcmp_neq, .Lfunc_end3-_f__stringcmp_neq

	.globl	_f__stringcmp_less
	.p2align	2
	.type	_f__stringcmp_less,@function
_f__stringcmp_less:
	addi	sp, sp, -16
	sw	ra, 12(sp)
	call	strcmp
	srli	a0, a0, 31
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end4:
	.size	_f__stringcmp_less, .Lfunc_end4-_f__stringcmp_less

	.globl	_f__stringcmp_leq
	.p2align	2
	.type	_f__stringcmp_leq,@function
_f__stringcmp_leq:
	addi	sp, sp, -16
	sw	ra, 12(sp)
	call	strcmp
	slti	a0, a0, 1
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end5:
	.size	_f__stringcmp_leq, .Lfunc_end5-_f__stringcmp_leq

	.globl	_f__stringcmp_greater
	.p2align	2
	.type	_f__stringcmp_greater,@function
_f__stringcmp_greater:
	addi	sp, sp, -16
	sw	ra, 12(sp)
	call	strcmp
	sgtz	a0, a0
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end6:
	.size	_f__stringcmp_greater, .Lfunc_end6-_f__stringcmp_greater

	.globl	_f__stringcmp_geq
	.p2align	2
	.type	_f__stringcmp_geq,@function
_f__stringcmp_geq:
	addi	sp, sp, -16
	sw	ra, 12(sp)
	call	strcmp
	not	a0, a0
	srli	a0, a0, 31
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end7:
	.size	_f__stringcmp_geq, .Lfunc_end7-_f__stringcmp_geq

	.globl	_class_string_length
	.p2align	2
	.type	_class_string_length,@function
_class_string_length:
	tail	strlen
.Lfunc_end8:
	.size	_class_string_length, .Lfunc_end8-_class_string_length

	.globl	_class_string_substring
	.p2align	2
	.type	_class_string_substring,@function
_class_string_substring:
	addi	sp, sp, -32
	sw	ra, 28(sp)
	sw	s0, 24(sp)
	sw	s1, 20(sp)
	sw	s2, 16(sp)
	sw	s3, 12(sp)
	mv	s0, a1
	mv	s1, a0
	sub	s2, a2, a1
	addi	a0, s2, 1
	call	malloc
	mv	s3, a0
	add	a1, s1, s0
	mv	a2, s2
	call	memcpy
	add	a0, s3, s2
	sb	zero, 0(a0)
	mv	a0, s3
	lw	ra, 28(sp)
	lw	s0, 24(sp)
	lw	s1, 20(sp)
	lw	s2, 16(sp)
	lw	s3, 12(sp)
	addi	sp, sp, 32
	ret
.Lfunc_end9:
	.size	_class_string_substring, .Lfunc_end9-_class_string_substring

	.globl	_class_string_parseInt
	.p2align	2
	.type	_class_string_parseInt,@function
_class_string_parseInt:
	addi	sp, sp, -16
	sw	ra, 12(sp)
	lui	a1, %hi(.L.str)
	addi	a1, a1, %lo(.L.str)
	addi	a2, sp, 8
	call	sscanf
	lw	a0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end10:
	.size	_class_string_parseInt, .Lfunc_end10-_class_string_parseInt

	.globl	_class_string_ord
	.p2align	2
	.type	_class_string_ord,@function
_class_string_ord:
	add	a0, a0, a1
	lbu	a0, 0(a0)
	ret
.Lfunc_end11:
	.size	_class_string_ord, .Lfunc_end11-_class_string_ord

	.globl	_f_print
	.p2align	2
	.type	_f_print,@function
_f_print:
	lui	a1, %hi(.L.str.1)
	addi	a1, a1, %lo(.L.str.1)
	mv	a2, a0
	mv	a0, a1
	mv	a1, a2
	tail	printf
.Lfunc_end12:
	.size	_f_print, .Lfunc_end12-_f_print

	.globl	_f_println
	.p2align	2
	.type	_f_println,@function
_f_println:
	tail	puts
.Lfunc_end13:
	.size	_f_println, .Lfunc_end13-_f_println

	.globl	_f_printInt
	.p2align	2
	.type	_f_printInt,@function
_f_printInt:
	lui	a1, %hi(.L.str)
	addi	a1, a1, %lo(.L.str)
	mv	a2, a0
	mv	a0, a1
	mv	a1, a2
	tail	printf
.Lfunc_end14:
	.size	_f_printInt, .Lfunc_end14-_f_printInt

	.globl	_f_printlnInt
	.p2align	2
	.type	_f_printlnInt,@function
_f_printlnInt:
	lui	a1, %hi(.L.str.3)
	addi	a1, a1, %lo(.L.str.3)
	mv	a2, a0
	mv	a0, a1
	mv	a1, a2
	tail	printf
.Lfunc_end15:
	.size	_f_printlnInt, .Lfunc_end15-_f_printlnInt

	.globl	_f_getString
	.p2align	2
	.type	_f_getString,@function
_f_getString:
	addi	sp, sp, -16
	sw	ra, 12(sp)
	sw	s0, 8(sp)
	li	a0, 256
	call	malloc
	mv	s0, a0
	lui	a0, %hi(.L.str.1)
	addi	a0, a0, %lo(.L.str.1)
	mv	a1, s0
	call	scanf
	mv	a0, s0
	lw	ra, 12(sp)
	lw	s0, 8(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end16:
	.size	_f_getString, .Lfunc_end16-_f_getString

	.globl	_f_getInt
	.p2align	2
	.type	_f_getInt,@function
_f_getInt:
	addi	sp, sp, -16
	sw	ra, 12(sp)
	lui	a0, %hi(.L.str)
	addi	a0, a0, %lo(.L.str)
	addi	a1, sp, 8
	call	scanf
	lw	a0, 8(sp)
	lw	ra, 12(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end17:
	.size	_f_getInt, .Lfunc_end17-_f_getInt

	.globl	_f_toString
	.p2align	2
	.type	_f_toString,@function
_f_toString:
	addi	sp, sp, -16
	sw	ra, 12(sp)
	sw	s0, 8(sp)
	sw	s1, 4(sp)
	mv	s0, a0
	li	a0, 12
	call	malloc
	mv	s1, a0
	lui	a0, %hi(.L.str)
	addi	a1, a0, %lo(.L.str)
	mv	a0, s1
	mv	a2, s0
	call	sprintf
	mv	a0, s1
	lw	ra, 12(sp)
	lw	s0, 8(sp)
	lw	s1, 4(sp)
	addi	sp, sp, 16
	ret
.Lfunc_end18:
	.size	_f_toString, .Lfunc_end18-_f_toString

	.type	STR_BUF_SIZE,@object
	.section	.rodata,"a",@progbits
	.globl	STR_BUF_SIZE
	.p2align	2
STR_BUF_SIZE:
	.word	256
	.size	STR_BUF_SIZE, 4

	.type	INT_BUF_SIZE,@object
	.globl	INT_BUF_SIZE
	.p2align	2
INT_BUF_SIZE:
	.word	20
	.size	INT_BUF_SIZE, 4

	.type	.L.str,@object
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str:
	.asciz	"%d"
	.size	.L.str, 3

	.type	.L.str.1,@object
.L.str.1:
	.asciz	"%s"
	.size	.L.str.1, 3

	.type	.L.str.3,@object
.L.str.3:
	.asciz	"%d\n"
	.size	.L.str.3, 4

	.ident	"Ubuntu clang version 14.0.0-1ubuntu1"
	.section	".note.GNU-stack","",@progbits
	.addrsig
