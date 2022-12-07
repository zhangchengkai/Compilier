%class_Edge = type { i32, i32, i32 }
@n_glo = global i32 zeroinitializer, align 4
@m_glo = global i32 zeroinitializer, align 4
@ans_glo = global i32 zeroinitializer, align 4
@fa_glo = global i32* zeroinitializer, align 8
@rk_glo = global i32* zeroinitializer, align 8
@e_glo = global %class_Edge** zeroinitializer, align 8
define void @_global_var_init()	{
ans_bb:
	store i32 0, i32* @ans_glo, align 4
	br label %ans_bb1
ans_bb1:					 ;preds = %ans_bb
	ret void
}

define void @_global_var_init1()	{
fa_bb:
	store i32* null, i32** @fa_glo, align 8
	br label %fa_bb1
fa_bb1:					 ;preds = %fa_bb
	ret void
}

define void @_global_var_init2()	{
rk_bb:
	store i32* null, i32** @rk_glo, align 8
	br label %rk_bb1
rk_bb1:					 ;preds = %rk_bb
	ret void
}

define void @_global_var_init3()	{
e_bb:
	store %class_Edge** null, %class_Edge*** @e_glo, align 8
	br label %e_bb1
e_bb1:					 ;preds = %e_bb
	ret void
}

define void @_GLOBAL_()	{
_GLOBAL__bb:
	call void @_global_var_init()
	call void @_global_var_init1()
	call void @_global_var_init2()
	call void @_global_var_init3()
	ret void
}

define void @_f_qsort(%class_Edge** %_arg1, i32 %_arg2, i32 %_arg3)	{
_f_qsort_bb:
	%e.alloc = alloca %class_Edge**, align 8
	store %class_Edge** %_arg1, %class_Edge*** %e.alloc, align 8
	%l.alloc = alloca i32, align 4
	store i32 %_arg2, i32* %l.alloc, align 4
	%r.alloc = alloca i32, align 4
	store i32 %_arg3, i32* %r.alloc, align 4
	%r.load = load i32, i32* %r.alloc, align 4
	%l.load = load i32, i32* %l.alloc, align 4
	%slt = icmp slt i32 %l.load, %r.load
	%zext_ = zext i1 %slt to i8
	%trunc_ = trunc i8 %zext_ to i1
	%i.alloc = alloca i32, align 4
	%j.alloc = alloca i32, align 4
	%x.alloc = alloca %class_Edge*, align 8
	%logic_and.alloc = alloca i8, align 1
	%logic_and.alloc1 = alloca i8, align 1
	br i1 %trunc_, label %if_then_bb, label %_f_qsort_bb2
_f_qsort_bb1:					 ;preds = %_f_qsort_bb2
	ret void
if_then_bb:					 ;preds = %_f_qsort_bb
	%l.load1 = load i32, i32* %l.alloc, align 4
	store i32 %l.load1, i32* %i.alloc, align 4
	%r.load1 = load i32, i32* %r.alloc, align 4
	store i32 %r.load1, i32* %j.alloc, align 4
	%_array.load = load %class_Edge**, %class_Edge*** %e.alloc, align 8
	%l.load2 = load i32, i32* %l.alloc, align 4
	%gep = getelementptr inbounds %class_Edge*, %class_Edge** %_array.load, i32 %l.load2
	%_array.load1 = load %class_Edge*, %class_Edge** %gep, align 8
	store %class_Edge* %_array.load1, %class_Edge** %x.alloc, align 8
	br label %while_condition_bb
_f_qsort_bb2:					 ;preds = %_f_qsort_bb, %_f_qsort_bb3
	br label %_f_qsort_bb1
while_condition_bb:					 ;preds = %if_then_bb, %_f_qsort_bb7
	%j.load = load i32, i32* %j.alloc, align 4
	%i.load = load i32, i32* %i.alloc, align 4
	%slt1 = icmp slt i32 %i.load, %j.load
	%zext_1 = zext i1 %slt1 to i8
	%trunc_1 = trunc i8 %zext_1 to i1
	br i1 %trunc_1, label %while_body_bb, label %_f_qsort_bb3
while_body_bb:					 ;preds = %while_condition_bb
	br label %while_condition_bb1
_f_qsort_bb3:					 ;preds = %while_condition_bb
	%x.load2 = load %class_Edge*, %class_Edge** %x.alloc, align 8
	%_array.load12 = load %class_Edge**, %class_Edge*** %e.alloc, align 8
	%i.load10 = load i32, i32* %i.alloc, align 4
	%gep11 = getelementptr inbounds %class_Edge*, %class_Edge** %_array.load12, i32 %i.load10
	store %class_Edge* %x.load2, %class_Edge** %gep11, align 8
	%e.load = load %class_Edge**, %class_Edge*** %e.alloc, align 8
	%l.load3 = load i32, i32* %l.alloc, align 4
	%i.load11 = load i32, i32* %i.alloc, align 4
	%sub = sub i32 %i.load11, 1
	call void @_f_qsort(%class_Edge** %e.load, i32 %l.load3, i32 %sub)
	%e.load1 = load %class_Edge**, %class_Edge*** %e.alloc, align 8
	%i.load12 = load i32, i32* %i.alloc, align 4
	%add4 = add i32 %i.load12, 1
	%r.load2 = load i32, i32* %r.alloc, align 4
	call void @_f_qsort(%class_Edge** %e.load1, i32 %add4, i32 %r.load2)
	br label %_f_qsort_bb2
while_condition_bb1:					 ;preds = %while_body_bb, %while_body_bb1
	%j.load1 = load i32, i32* %j.alloc, align 4
	%i.load1 = load i32, i32* %i.alloc, align 4
	%slt2 = icmp slt i32 %i.load1, %j.load1
	%zext_2 = zext i1 %slt2 to i8
	%trunc_2 = trunc i8 %zext_2 to i1
	br i1 %trunc_2, label %_sBlock_bb, label %_dBlock_bb
while_body_bb1:					 ;preds = %_tBlock_bb
	%j.load3 = load i32, i32* %j.alloc, align 4
	%add = add i32 %j.load3, -1
	store i32 %add, i32* %j.alloc, align 4
	br label %while_condition_bb1
_f_qsort_bb4:					 ;preds = %_tBlock_bb
	%j.load4 = load i32, i32* %j.alloc, align 4
	%i.load2 = load i32, i32* %i.alloc, align 4
	%slt3 = icmp slt i32 %i.load2, %j.load4
	%zext_4 = zext i1 %slt3 to i8
	%trunc_4 = trunc i8 %zext_4 to i1
	br i1 %trunc_4, label %if_then_bb1, label %_f_qsort_bb5
_dBlock_bb:					 ;preds = %while_condition_bb1
	store i8 %zext_2, i8* %logic_and.alloc, align 1
	br label %_tBlock_bb
_sBlock_bb:					 ;preds = %while_condition_bb1
	%x.load = load %class_Edge*, %class_Edge** %x.alloc, align 8
	%gep1 = getelementptr inbounds %class_Edge, %class_Edge* %x.load, i32 0, i32 0
	%w.load = load i32, i32* %gep1, align 4
	%_array.load2 = load %class_Edge**, %class_Edge*** %e.alloc, align 8
	%j.load2 = load i32, i32* %j.alloc, align 4
	%gep2 = getelementptr inbounds %class_Edge*, %class_Edge** %_array.load2, i32 %j.load2
	%_array.load3 = load %class_Edge*, %class_Edge** %gep2, align 8
	%gep3 = getelementptr inbounds %class_Edge, %class_Edge* %_array.load3, i32 0, i32 0
	%w.load1 = load i32, i32* %gep3, align 4
	%sge = icmp sge i32 %w.load1, %w.load
	%zext_3 = zext i1 %sge to i8
	store i8 %zext_3, i8* %logic_and.alloc, align 1
	br label %_tBlock_bb
_tBlock_bb:					 ;preds = %_dBlock_bb, %_sBlock_bb
	%circuit.load = load i8, i8* %logic_and.alloc, align 1
	%trunc_3 = trunc i8 %circuit.load to i1
	br i1 %trunc_3, label %while_body_bb1, label %_f_qsort_bb4
if_then_bb1:					 ;preds = %_f_qsort_bb4
	%_array.load4 = load %class_Edge**, %class_Edge*** %e.alloc, align 8
	%j.load5 = load i32, i32* %j.alloc, align 4
	%gep4 = getelementptr inbounds %class_Edge*, %class_Edge** %_array.load4, i32 %j.load5
	%_array.load5 = load %class_Edge*, %class_Edge** %gep4, align 8
	%_array.load6 = load %class_Edge**, %class_Edge*** %e.alloc, align 8
	%i.load3 = load i32, i32* %i.alloc, align 4
	%gep5 = getelementptr inbounds %class_Edge*, %class_Edge** %_array.load6, i32 %i.load3
	store %class_Edge* %_array.load5, %class_Edge** %gep5, align 8
	%i.load4 = load i32, i32* %i.alloc, align 4
	%add1 = add i32 %i.load4, 1
	store i32 %add1, i32* %i.alloc, align 4
	br label %_f_qsort_bb5
_f_qsort_bb5:					 ;preds = %_f_qsort_bb4, %if_then_bb1
	br label %while_condition_bb2
while_condition_bb2:					 ;preds = %_f_qsort_bb5, %while_body_bb2
	%j.load6 = load i32, i32* %j.alloc, align 4
	%i.load5 = load i32, i32* %i.alloc, align 4
	%slt4 = icmp slt i32 %i.load5, %j.load6
	%zext_5 = zext i1 %slt4 to i8
	%trunc_5 = trunc i8 %zext_5 to i1
	br i1 %trunc_5, label %_sBlock_bb1, label %_dBlock_bb1
while_body_bb2:					 ;preds = %_tBlock_bb1
	%i.load7 = load i32, i32* %i.alloc, align 4
	%add2 = add i32 %i.load7, 1
	store i32 %add2, i32* %i.alloc, align 4
	br label %while_condition_bb2
_f_qsort_bb6:					 ;preds = %_tBlock_bb1
	%j.load7 = load i32, i32* %j.alloc, align 4
	%i.load8 = load i32, i32* %i.alloc, align 4
	%slt6 = icmp slt i32 %i.load8, %j.load7
	%zext_7 = zext i1 %slt6 to i8
	%trunc_7 = trunc i8 %zext_7 to i1
	br i1 %trunc_7, label %if_then_bb2, label %_f_qsort_bb7
_dBlock_bb1:					 ;preds = %while_condition_bb2
	store i8 %zext_5, i8* %logic_and.alloc1, align 1
	br label %_tBlock_bb1
_sBlock_bb1:					 ;preds = %while_condition_bb2
	%x.load1 = load %class_Edge*, %class_Edge** %x.alloc, align 8
	%gep6 = getelementptr inbounds %class_Edge, %class_Edge* %x.load1, i32 0, i32 0
	%w.load2 = load i32, i32* %gep6, align 4
	%_array.load7 = load %class_Edge**, %class_Edge*** %e.alloc, align 8
	%i.load6 = load i32, i32* %i.alloc, align 4
	%gep7 = getelementptr inbounds %class_Edge*, %class_Edge** %_array.load7, i32 %i.load6
	%_array.load8 = load %class_Edge*, %class_Edge** %gep7, align 8
	%gep8 = getelementptr inbounds %class_Edge, %class_Edge* %_array.load8, i32 0, i32 0
	%w.load3 = load i32, i32* %gep8, align 4
	%slt5 = icmp slt i32 %w.load3, %w.load2
	%zext_6 = zext i1 %slt5 to i8
	store i8 %zext_6, i8* %logic_and.alloc1, align 1
	br label %_tBlock_bb1
_tBlock_bb1:					 ;preds = %_dBlock_bb1, %_sBlock_bb1
	%circuit.load1 = load i8, i8* %logic_and.alloc1, align 1
	%trunc_6 = trunc i8 %circuit.load1 to i1
	br i1 %trunc_6, label %while_body_bb2, label %_f_qsort_bb6
if_then_bb2:					 ;preds = %_f_qsort_bb6
	%_array.load9 = load %class_Edge**, %class_Edge*** %e.alloc, align 8
	%i.load9 = load i32, i32* %i.alloc, align 4
	%gep9 = getelementptr inbounds %class_Edge*, %class_Edge** %_array.load9, i32 %i.load9
	%_array.load10 = load %class_Edge*, %class_Edge** %gep9, align 8
	%_array.load11 = load %class_Edge**, %class_Edge*** %e.alloc, align 8
	%j.load8 = load i32, i32* %j.alloc, align 4
	%gep10 = getelementptr inbounds %class_Edge*, %class_Edge** %_array.load11, i32 %j.load8
	store %class_Edge* %_array.load10, %class_Edge** %gep10, align 8
	%j.load9 = load i32, i32* %j.alloc, align 4
	%add3 = add i32 %j.load9, -1
	store i32 %add3, i32* %j.alloc, align 4
	br label %_f_qsort_bb7
_f_qsort_bb7:					 ;preds = %_f_qsort_bb6, %if_then_bb2
	br label %while_condition_bb
}
define void @_f_init()	{
_f_init_bb:
	%n.load = load i32, i32* @n_glo, align 4
	%add5 = add i32 %n.load, 1
	%mul = mul i32 %add5, 4
	%add6 = add i32 %mul, 4
	%_call_f__heap_malloc = call i8* @_f__heap_malloc(i32 %add6)
	%_call_f__heap_malloc_BC = bitcast i8* %_call_f__heap_malloc to i32*
	store i32 %add5, i32* %_call_f__heap_malloc_BC, align 4
	%gep12 = getelementptr inbounds i32, i32* %_call_f__heap_malloc_BC, i32 1
	%gep12_BC = bitcast i32* %gep12 to i32*
	store i32* %gep12_BC, i32** @fa_glo, align 8
	%n.load1 = load i32, i32* @n_glo, align 4
	%add7 = add i32 %n.load1, 1
	%mul1 = mul i32 %add7, 4
	%add8 = add i32 %mul1, 4
	%_call_f__heap_malloc1 = call i8* @_f__heap_malloc(i32 %add8)
	%_call_f__heap_malloc1_BC = bitcast i8* %_call_f__heap_malloc1 to i32*
	store i32 %add7, i32* %_call_f__heap_malloc1_BC, align 4
	%gep13 = getelementptr inbounds i32, i32* %_call_f__heap_malloc1_BC, i32 1
	%gep13_BC = bitcast i32* %gep13 to i32*
	store i32* %gep13_BC, i32** @rk_glo, align 8
	%i.alloc1 = alloca i32, align 4
	store i32 1, i32* %i.alloc1, align 4
	br label %for_condition_bb
_f_init_bb1:					 ;preds = %_f_init_bb2
	ret void
for_condition_bb:					 ;preds = %_f_init_bb, %for_iter_bb
	%n.load2 = load i32, i32* @n_glo, align 4
	%i.load13 = load i32, i32* %i.alloc1, align 4
	%sle = icmp sle i32 %i.load13, %n.load2
	%zext_8 = zext i1 %sle to i8
	%trunc_8 = trunc i8 %zext_8 to i1
	br i1 %trunc_8, label %for_body_bb, label %_f_init_bb2
for_iter_bb:					 ;preds = %for_body_bb
	%i.load17 = load i32, i32* %i.alloc1, align 4
	%add9 = add i32 %i.load17, 1
	store i32 %add9, i32* %i.alloc1, align 4
	br label %for_condition_bb
for_body_bb:					 ;preds = %for_condition_bb
	%i.load14 = load i32, i32* %i.alloc1, align 4
	%_array.load13 = load i32*, i32** @fa_glo, align 8
	%i.load15 = load i32, i32* %i.alloc1, align 4
	%gep14 = getelementptr inbounds i32, i32* %_array.load13, i32 %i.load15
	store i32 %i.load14, i32* %gep14, align 4
	%_array.load14 = load i32*, i32** @rk_glo, align 8
	%i.load16 = load i32, i32* %i.alloc1, align 4
	%gep15 = getelementptr inbounds i32, i32* %_array.load14, i32 %i.load16
	store i32 1, i32* %gep15, align 4
	br label %for_iter_bb
_f_init_bb2:					 ;preds = %for_condition_bb
	br label %_f_init_bb1
}
declare i8* @_f__heap_malloc(i32)
define i32 @main()	{
main_bb:
	%_return.alloc2 = alloca i32, align 4
	call void @_GLOBAL_()
	%_call_f_getInt = call i32 @_f_getInt()
	store i32 %_call_f_getInt, i32* @n_glo, align 4
	%_call_f_getInt1 = call i32 @_f_getInt()
	store i32 %_call_f_getInt1, i32* @m_glo, align 4
	%m.load = load i32, i32* @m_glo, align 4
	%mul2 = mul i32 %m.load, 8
	%add12 = add i32 %mul2, 4
	%_call_f__heap_malloc2 = call i8* @_f__heap_malloc(i32 %add12)
	%_call_f__heap_malloc2_BC = bitcast i8* %_call_f__heap_malloc2 to i32*
	store i32 %m.load, i32* %_call_f__heap_malloc2_BC, align 4
	%gep29 = getelementptr inbounds i32, i32* %_call_f__heap_malloc2_BC, i32 1
	%gep29_BC = bitcast i32* %gep29 to %class_Edge**
	store %class_Edge** %gep29_BC, %class_Edge*** @e_glo, align 8
	%i.alloc2 = alloca i32, align 4
	store i32 0, i32* %i.alloc2, align 4
	%ed.alloc = alloca %class_Edge*, align 8
	%j.alloc1 = alloca i32, align 4
	%ed.alloc1 = alloca %class_Edge*, align 8
	br label %for_condition_bb1
main_bb1:					 ;preds = %if_then_bb6, %main_bb6
	%_return.load2 = load i32, i32* %_return.alloc2, align 4
	ret i32 %_return.load2
for_condition_bb1:					 ;preds = %main_bb, %for_iter_bb1
	%m.load1 = load i32, i32* @m_glo, align 4
	%i.load18 = load i32, i32* %i.alloc2, align 4
	%slt7 = icmp slt i32 %i.load18, %m.load1
	%zext_12 = zext i1 %slt7 to i8
	%trunc_12 = trunc i8 %zext_12 to i1
	br i1 %trunc_12, label %for_body_bb1, label %main_bb2
for_iter_bb1:					 ;preds = %for_body_bb1
	%i.load20 = load i32, i32* %i.alloc2, align 4
	%add13 = add i32 %i.load20, 1
	store i32 %add13, i32* %i.alloc2, align 4
	br label %for_condition_bb1
for_body_bb1:					 ;preds = %for_condition_bb1
	%_call_f__heap_malloc3 = call i8* @_f__heap_malloc(i32 12)
	%_call_f__heap_malloc3_BC = bitcast i8* %_call_f__heap_malloc3 to %class_Edge*
	call void @_class_Edge_Edge(%class_Edge* %_call_f__heap_malloc3_BC)
	store %class_Edge* %_call_f__heap_malloc3_BC, %class_Edge** %ed.alloc, align 8
	%_call_f_getInt2 = call i32 @_f_getInt()
	%ed.load = load %class_Edge*, %class_Edge** %ed.alloc, align 8
	%gep30 = getelementptr inbounds %class_Edge, %class_Edge* %ed.load, i32 0, i32 1
	store i32 %_call_f_getInt2, i32* %gep30, align 4
	%_call_f_getInt3 = call i32 @_f_getInt()
	%ed.load1 = load %class_Edge*, %class_Edge** %ed.alloc, align 8
	%gep31 = getelementptr inbounds %class_Edge, %class_Edge* %ed.load1, i32 0, i32 2
	store i32 %_call_f_getInt3, i32* %gep31, align 4
	%_call_f_getInt4 = call i32 @_f_getInt()
	%ed.load2 = load %class_Edge*, %class_Edge** %ed.alloc, align 8
	%gep32 = getelementptr inbounds %class_Edge, %class_Edge* %ed.load2, i32 0, i32 0
	store i32 %_call_f_getInt4, i32* %gep32, align 4
	%ed.load3 = load %class_Edge*, %class_Edge** %ed.alloc, align 8
	%_array.load37 = load %class_Edge**, %class_Edge*** @e_glo, align 8
	%i.load19 = load i32, i32* %i.alloc2, align 4
	%gep33 = getelementptr inbounds %class_Edge*, %class_Edge** %_array.load37, i32 %i.load19
	store %class_Edge* %ed.load3, %class_Edge** %gep33, align 8
	br label %for_iter_bb1
main_bb2:					 ;preds = %for_condition_bb1
	%e.load2 = load %class_Edge**, %class_Edge*** @e_glo, align 8
	%m.load2 = load i32, i32* @m_glo, align 4
	%sub1 = sub i32 %m.load2, 1
	call void @_f_qsort(%class_Edge** %e.load2, i32 0, i32 %sub1)
	call void @_f_init()
	store i32 0, i32* %i.alloc2, align 4
	store i32 0, i32* %j.alloc1, align 4
	br label %while_condition_bb3
while_condition_bb3:					 ;preds = %main_bb2, %main_bb5
	%n.load3 = load i32, i32* @n_glo, align 4
	%sub2 = sub i32 %n.load3, 1
	%i.load21 = load i32, i32* %i.alloc2, align 4
	%slt8 = icmp slt i32 %i.load21, %sub2
	%zext_13 = zext i1 %slt8 to i8
	%trunc_13 = trunc i8 %zext_13 to i1
	br i1 %trunc_13, label %while_body_bb3, label %main_bb3
while_body_bb3:					 ;preds = %while_condition_bb3
	%m.load3 = load i32, i32* @m_glo, align 4
	%j.load10 = load i32, i32* %j.alloc1, align 4
	%sge1 = icmp sge i32 %j.load10, %m.load3
	%zext_14 = zext i1 %sge1 to i8
	%trunc_14 = trunc i8 %zext_14 to i1
	br i1 %trunc_14, label %if_then_bb6, label %main_bb4
main_bb3:					 ;preds = %while_condition_bb3
	%n.load4 = load i32, i32* @n_glo, align 4
	%_array.load40 = load i32*, i32** @rk_glo, align 8
	%_call_f_find3 = call i32 @_f_find(i32 1)
	%gep38 = getelementptr inbounds i32, i32* %_array.load40, i32 %_call_f_find3
	%_array.load41 = load i32, i32* %gep38, align 4
	%eq2 = icmp eq i32 %_array.load41, %n.load4
	%zext_15 = zext i1 %eq2 to i8
	%trunc_16 = trunc i8 %zext_15 to i1
	br i1 %trunc_16, label %if_then_bb8, label %if_else_bb1
if_then_bb6:					 ;preds = %while_body_bb3
	call void @_f_printInt(i32 -1)
	store i32 0, i32* %_return.alloc2, align 4
	br label %main_bb1
main_bb4:					 ;preds = %while_body_bb3, %if_then_bb6
	%_array.load38 = load %class_Edge**, %class_Edge*** @e_glo, align 8
	%j.load11 = load i32, i32* %j.alloc1, align 4
	%gep34 = getelementptr inbounds %class_Edge*, %class_Edge** %_array.load38, i32 %j.load11
	%_array.load39 = load %class_Edge*, %class_Edge** %gep34, align 8
	store %class_Edge* %_array.load39, %class_Edge** %ed.alloc1, align 8
	%j.load12 = load i32, i32* %j.alloc1, align 4
	%add14 = add i32 %j.load12, 1
	store i32 %add14, i32* %j.alloc1, align 4
	%ed.load4 = load %class_Edge*, %class_Edge** %ed.alloc1, align 8
	%gep35 = getelementptr inbounds %class_Edge, %class_Edge* %ed.load4, i32 0, i32 1
	%x.load16 = load i32, i32* %gep35, align 4
	%ed.load5 = load %class_Edge*, %class_Edge** %ed.alloc1, align 8
	%gep36 = getelementptr inbounds %class_Edge, %class_Edge* %ed.load5, i32 0, i32 2
	%y.load8 = load i32, i32* %gep36, align 4
	%_call_f_union = call i8 @_f_union(i32 %x.load16, i32 %y.load8)
	%trunc_15 = trunc i8 %_call_f_union to i1
	br i1 %trunc_15, label %if_then_bb7, label %main_bb5
if_then_bb7:					 ;preds = %main_bb4
	%i.load22 = load i32, i32* %i.alloc2, align 4
	%add15 = add i32 %i.load22, 1
	store i32 %add15, i32* %i.alloc2, align 4
	%ed.load6 = load %class_Edge*, %class_Edge** %ed.alloc1, align 8
	%gep37 = getelementptr inbounds %class_Edge, %class_Edge* %ed.load6, i32 0, i32 0
	%w.load4 = load i32, i32* %gep37, align 4
	%ans.load = load i32, i32* @ans_glo, align 4
	%add16 = add i32 %ans.load, %w.load4
	store i32 %add16, i32* @ans_glo, align 4
	br label %main_bb5
main_bb5:					 ;preds = %main_bb4, %if_then_bb7
	br label %while_condition_bb3
if_then_bb8:					 ;preds = %main_bb3
	%ans.load1 = load i32, i32* @ans_glo, align 4
	call void @_f_printInt(i32 %ans.load1)
	br label %main_bb6
main_bb6:					 ;preds = %if_else_bb1, %if_then_bb8
	store i32 0, i32* %_return.alloc2, align 4
	br label %main_bb1
if_else_bb1:					 ;preds = %main_bb3
	call void @_f_printInt(i32 -1)
	br label %main_bb6
}
define i8 @_f_union(i32 %_arg5, i32 %_arg6)	{
_f_union_bb:
	%_return.alloc1 = alloca i8, align 1
	%x.alloc2 = alloca i32, align 4
	store i32 %_arg5, i32* %x.alloc2, align 4
	%y.alloc = alloca i32, align 4
	store i32 %_arg6, i32* %y.alloc, align 4
	%x.load8 = load i32, i32* %x.alloc2, align 4
	%_call_f_find1 = call i32 @_f_find(i32 %x.load8)
	store i32 %_call_f_find1, i32* %x.alloc2, align 4
	%y.load = load i32, i32* %y.alloc, align 4
	%_call_f_find2 = call i32 @_f_find(i32 %y.load)
	store i32 %_call_f_find2, i32* %y.alloc, align 4
	%y.load1 = load i32, i32* %y.alloc, align 4
	%x.load9 = load i32, i32* %x.alloc2, align 4
	%eq1 = icmp eq i32 %x.load9, %y.load1
	%zext_10 = zext i1 %eq1 to i8
	%trunc_10 = trunc i8 %zext_10 to i1
	br i1 %trunc_10, label %if_then_bb4, label %_f_union_bb2
_f_union_bb1:					 ;preds = %if_then_bb4, %_f_union_bb3
	%_return.load1 = load i8, i8* %_return.alloc1, align 1
	ret i8 %_return.load1
if_then_bb4:					 ;preds = %_f_union_bb
	store i8 0, i8* %_return.alloc1, align 1
	br label %_f_union_bb1
_f_union_bb2:					 ;preds = %_f_union_bb, %if_then_bb4
	%_array.load21 = load i32*, i32** @rk_glo, align 8
	%y.load2 = load i32, i32* %y.alloc, align 4
	%gep19 = getelementptr inbounds i32, i32* %_array.load21, i32 %y.load2
	%_array.load22 = load i32, i32* %gep19, align 4
	%_array.load23 = load i32*, i32** @rk_glo, align 8
	%x.load10 = load i32, i32* %x.alloc2, align 4
	%gep20 = getelementptr inbounds i32, i32* %_array.load23, i32 %x.load10
	%_array.load24 = load i32, i32* %gep20, align 4
	%sgt = icmp sgt i32 %_array.load24, %_array.load22
	%zext_11 = zext i1 %sgt to i8
	%trunc_11 = trunc i8 %zext_11 to i1
	br i1 %trunc_11, label %if_then_bb5, label %if_else_bb
if_then_bb5:					 ;preds = %_f_union_bb2
	%x.load13 = load i32, i32* %x.alloc2, align 4
	%_array.load31 = load i32*, i32** @fa_glo, align 8
	%y.load6 = load i32, i32* %y.alloc, align 4
	%gep25 = getelementptr inbounds i32, i32* %_array.load31, i32 %y.load6
	store i32 %x.load13, i32* %gep25, align 4
	%_array.load32 = load i32*, i32** @rk_glo, align 8
	%y.load7 = load i32, i32* %y.alloc, align 4
	%gep26 = getelementptr inbounds i32, i32* %_array.load32, i32 %y.load7
	%_array.load33 = load i32, i32* %gep26, align 4
	%_array.load34 = load i32*, i32** @rk_glo, align 8
	%x.load14 = load i32, i32* %x.alloc2, align 4
	%gep27 = getelementptr inbounds i32, i32* %_array.load34, i32 %x.load14
	%_array.load35 = load i32, i32* %gep27, align 4
	%add11 = add i32 %_array.load35, %_array.load33
	%_array.load36 = load i32*, i32** @rk_glo, align 8
	%x.load15 = load i32, i32* %x.alloc2, align 4
	%gep28 = getelementptr inbounds i32, i32* %_array.load36, i32 %x.load15
	store i32 %add11, i32* %gep28, align 4
	br label %_f_union_bb3
_f_union_bb3:					 ;preds = %if_else_bb, %if_then_bb5
	store i8 1, i8* %_return.alloc1, align 1
	br label %_f_union_bb1
if_else_bb:					 ;preds = %_f_union_bb2
	%y.load3 = load i32, i32* %y.alloc, align 4
	%_array.load25 = load i32*, i32** @fa_glo, align 8
	%x.load11 = load i32, i32* %x.alloc2, align 4
	%gep21 = getelementptr inbounds i32, i32* %_array.load25, i32 %x.load11
	store i32 %y.load3, i32* %gep21, align 4
	%_array.load26 = load i32*, i32** @rk_glo, align 8
	%x.load12 = load i32, i32* %x.alloc2, align 4
	%gep22 = getelementptr inbounds i32, i32* %_array.load26, i32 %x.load12
	%_array.load27 = load i32, i32* %gep22, align 4
	%_array.load28 = load i32*, i32** @rk_glo, align 8
	%y.load4 = load i32, i32* %y.alloc, align 4
	%gep23 = getelementptr inbounds i32, i32* %_array.load28, i32 %y.load4
	%_array.load29 = load i32, i32* %gep23, align 4
	%add10 = add i32 %_array.load29, %_array.load27
	%_array.load30 = load i32*, i32** @rk_glo, align 8
	%y.load5 = load i32, i32* %y.alloc, align 4
	%gep24 = getelementptr inbounds i32, i32* %_array.load30, i32 %y.load5
	store i32 %add10, i32* %gep24, align 4
	br label %_f_union_bb3
}
declare i32 @_f_getInt()
define i32 @_f_find(i32 %_arg4)	{
_f_find_bb:
	%_return.alloc = alloca i32, align 4
	%x.alloc1 = alloca i32, align 4
	store i32 %_arg4, i32* %x.alloc1, align 4
	%_array.load15 = load i32*, i32** @fa_glo, align 8
	%x.load3 = load i32, i32* %x.alloc1, align 4
	%gep16 = getelementptr inbounds i32, i32* %_array.load15, i32 %x.load3
	%_array.load16 = load i32, i32* %gep16, align 4
	%x.load4 = load i32, i32* %x.alloc1, align 4
	%eq = icmp eq i32 %x.load4, %_array.load16
	%zext_9 = zext i1 %eq to i8
	%trunc_9 = trunc i8 %zext_9 to i1
	br i1 %trunc_9, label %if_then_bb3, label %_f_find_bb2
_f_find_bb1:					 ;preds = %if_then_bb3, %_f_find_bb2
	%_return.load = load i32, i32* %_return.alloc, align 4
	ret i32 %_return.load
if_then_bb3:					 ;preds = %_f_find_bb
	%x.load5 = load i32, i32* %x.alloc1, align 4
	store i32 %x.load5, i32* %_return.alloc, align 4
	br label %_f_find_bb1
_f_find_bb2:					 ;preds = %_f_find_bb, %if_then_bb3
	%_array.load17 = load i32*, i32** @fa_glo, align 8
	%x.load6 = load i32, i32* %x.alloc1, align 4
	%gep17 = getelementptr inbounds i32, i32* %_array.load17, i32 %x.load6
	%_array.load18 = load i32, i32* %gep17, align 4
	%_call_f_find = call i32 @_f_find(i32 %_array.load18)
	store i32 %_call_f_find, i32* %x.alloc1, align 4
	%_array.load19 = load i32*, i32** @fa_glo, align 8
	%x.load7 = load i32, i32* %x.alloc1, align 4
	%gep18 = getelementptr inbounds i32, i32* %_array.load19, i32 %x.load7
	%_array.load20 = load i32, i32* %gep18, align 4
	store i32 %_array.load20, i32* %_return.alloc, align 4
	br label %_f_find_bb1
}
declare void @_f_printInt(i32)
define void @_class_Edge_Edge(%class_Edge* %_arg)	{
_class_Edge_Edge_bb:
	ret void
}

