package PrintBuiltin;

public class PrintBuiltin {
    public String str="\t.text\n" +
            "\t.attribute\t4, 16\n" +
            "\t.attribute\t5, \"rv32i2p0_m2p0\"\n" +
            "\t.file\t\"builtin.c\"\n" +
            "\t.globl\t_f__heap_malloc\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f__heap_malloc,@function\n" +
            "_f__heap_malloc:\n" +
            "\ttail\tmalloc\n" +
            ".Lfunc_end0:\n" +
            "\t.size\t_f__heap_malloc, .Lfunc_end0-_f__heap_malloc\n" +
            "\n" +
            "\t.globl\t_f__string_merge\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f__string_merge,@function\n" +
            "_f__string_merge:\n" +
            "\taddi\tsp, sp, -16\n" +
            "\tsw\tra, 12(sp)\n" +
            "\tsw\ts0, 8(sp)\n" +
            "\tsw\ts1, 4(sp)\n" +
            "\tsw\ts2, 0(sp)\n" +
            "\tmv\ts0, a1\n" +
            "\tmv\ts1, a0\n" +
            "\tcall\tstrlen\n" +
            "\tmv\ts2, a0\n" +
            "\tmv\ta0, s0\n" +
            "\tcall\tstrlen\n" +
            "\tadd\ta0, s2, a0\n" +
            "\taddi\ta0, a0, 1\n" +
            "\tcall\tmalloc\n" +
            "\tmv\ts2, a0\n" +
            "\tmv\ta1, s1\n" +
            "\tcall\tstrcpy\n" +
            "\tmv\ta0, s2\n" +
            "\tmv\ta1, s0\n" +
            "\tlw\tra, 12(sp)\n" +
            "\tlw\ts0, 8(sp)\n" +
            "\tlw\ts1, 4(sp)\n" +
            "\tlw\ts2, 0(sp)\n" +
            "\taddi\tsp, sp, 16\n" +
            "\ttail\tstrcat\n" +
            ".Lfunc_end1:\n" +
            "\t.size\t_f__string_merge, .Lfunc_end1-_f__string_merge\n" +
            "\n" +
            "\t.globl\t_f__stringcmp_eq\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f__stringcmp_eq,@function\n" +
            "_f__stringcmp_eq:\n" +
            "\taddi\tsp, sp, -16\n" +
            "\tsw\tra, 12(sp)\n" +
            "\tcall\tstrcmp\n" +
            "\tseqz\ta0, a0\n" +
            "\tlw\tra, 12(sp)\n" +
            "\taddi\tsp, sp, 16\n" +
            "\tret\n" +
            ".Lfunc_end2:\n" +
            "\t.size\t_f__stringcmp_eq, .Lfunc_end2-_f__stringcmp_eq\n" +
            "\n" +
            "\t.globl\t_f__stringcmp_neq\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f__stringcmp_neq,@function\n" +
            "_f__stringcmp_neq:\n" +
            "\taddi\tsp, sp, -16\n" +
            "\tsw\tra, 12(sp)\n" +
            "\tcall\tstrcmp\n" +
            "\tsnez\ta0, a0\n" +
            "\tlw\tra, 12(sp)\n" +
            "\taddi\tsp, sp, 16\n" +
            "\tret\n" +
            ".Lfunc_end3:\n" +
            "\t.size\t_f__stringcmp_neq, .Lfunc_end3-_f__stringcmp_neq\n" +
            "\n" +
            "\t.globl\t_f__stringcmp_less\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f__stringcmp_less,@function\n" +
            "_f__stringcmp_less:\n" +
            "\taddi\tsp, sp, -16\n" +
            "\tsw\tra, 12(sp)\n" +
            "\tcall\tstrcmp\n" +
            "\tsrli\ta0, a0, 31\n" +
            "\tlw\tra, 12(sp)\n" +
            "\taddi\tsp, sp, 16\n" +
            "\tret\n" +
            ".Lfunc_end4:\n" +
            "\t.size\t_f__stringcmp_less, .Lfunc_end4-_f__stringcmp_less\n" +
            "\n" +
            "\t.globl\t_f__stringcmp_leq\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f__stringcmp_leq,@function\n" +
            "_f__stringcmp_leq:\n" +
            "\taddi\tsp, sp, -16\n" +
            "\tsw\tra, 12(sp)\n" +
            "\tcall\tstrcmp\n" +
            "\tslti\ta0, a0, 1\n" +
            "\tlw\tra, 12(sp)\n" +
            "\taddi\tsp, sp, 16\n" +
            "\tret\n" +
            ".Lfunc_end5:\n" +
            "\t.size\t_f__stringcmp_leq, .Lfunc_end5-_f__stringcmp_leq\n" +
            "\n" +
            "\t.globl\t_f__stringcmp_greater\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f__stringcmp_greater,@function\n" +
            "_f__stringcmp_greater:\n" +
            "\taddi\tsp, sp, -16\n" +
            "\tsw\tra, 12(sp)\n" +
            "\tcall\tstrcmp\n" +
            "\tsgtz\ta0, a0\n" +
            "\tlw\tra, 12(sp)\n" +
            "\taddi\tsp, sp, 16\n" +
            "\tret\n" +
            ".Lfunc_end6:\n" +
            "\t.size\t_f__stringcmp_greater, .Lfunc_end6-_f__stringcmp_greater\n" +
            "\n" +
            "\t.globl\t_f__stringcmp_geq\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f__stringcmp_geq,@function\n" +
            "_f__stringcmp_geq:\n" +
            "\taddi\tsp, sp, -16\n" +
            "\tsw\tra, 12(sp)\n" +
            "\tcall\tstrcmp\n" +
            "\tnot\ta0, a0\n" +
            "\tsrli\ta0, a0, 31\n" +
            "\tlw\tra, 12(sp)\n" +
            "\taddi\tsp, sp, 16\n" +
            "\tret\n" +
            ".Lfunc_end7:\n" +
            "\t.size\t_f__stringcmp_geq, .Lfunc_end7-_f__stringcmp_geq\n" +
            "\n" +
            "\t.globl\t_class_string_length\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_class_string_length,@function\n" +
            "_class_string_length:\n" +
            "\ttail\tstrlen\n" +
            ".Lfunc_end8:\n" +
            "\t.size\t_class_string_length, .Lfunc_end8-_class_string_length\n" +
            "\n" +
            "\t.globl\t_class_string_substring\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_class_string_substring,@function\n" +
            "_class_string_substring:\n" +
            "\taddi\tsp, sp, -32\n" +
            "\tsw\tra, 28(sp)\n" +
            "\tsw\ts0, 24(sp)\n" +
            "\tsw\ts1, 20(sp)\n" +
            "\tsw\ts2, 16(sp)\n" +
            "\tsw\ts3, 12(sp)\n" +
            "\tmv\ts0, a1\n" +
            "\tmv\ts1, a0\n" +
            "\tsub\ts2, a2, a1\n" +
            "\taddi\ta0, s2, 1\n" +
            "\tcall\tmalloc\n" +
            "\tmv\ts3, a0\n" +
            "\tadd\ta1, s1, s0\n" +
            "\tmv\ta2, s2\n" +
            "\tcall\tmemcpy\n" +
            "\tadd\ta0, s3, s2\n" +
            "\tsb\tzero, 0(a0)\n" +
            "\tmv\ta0, s3\n" +
            "\tlw\tra, 28(sp)\n" +
            "\tlw\ts0, 24(sp)\n" +
            "\tlw\ts1, 20(sp)\n" +
            "\tlw\ts2, 16(sp)\n" +
            "\tlw\ts3, 12(sp)\n" +
            "\taddi\tsp, sp, 32\n" +
            "\tret\n" +
            ".Lfunc_end9:\n" +
            "\t.size\t_class_string_substring, .Lfunc_end9-_class_string_substring\n" +
            "\n" +
            "\t.globl\t_class_string_parseInt\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_class_string_parseInt,@function\n" +
            "_class_string_parseInt:\n" +
            "\taddi\tsp, sp, -16\n" +
            "\tsw\tra, 12(sp)\n" +
            "\tlui\ta1, %hi(.L.str)\n" +
            "\taddi\ta1, a1, %lo(.L.str)\n" +
            "\taddi\ta2, sp, 8\n" +
            "\tcall\tsscanf\n" +
            "\tlw\ta0, 8(sp)\n" +
            "\tlw\tra, 12(sp)\n" +
            "\taddi\tsp, sp, 16\n" +
            "\tret\n" +
            ".Lfunc_end10:\n" +
            "\t.size\t_class_string_parseInt, .Lfunc_end10-_class_string_parseInt\n" +
            "\n" +
            "\t.globl\t_class_string_ord\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_class_string_ord,@function\n" +
            "_class_string_ord:\n" +
            "\tadd\ta0, a0, a1\n" +
            "\tlbu\ta0, 0(a0)\n" +
            "\tret\n" +
            ".Lfunc_end11:\n" +
            "\t.size\t_class_string_ord, .Lfunc_end11-_class_string_ord\n" +
            "\n" +
            "\t.globl\t_f_print\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f_print,@function\n" +
            "_f_print:\n" +
            "\tlui\ta1, %hi(.L.str.1)\n" +
            "\taddi\ta1, a1, %lo(.L.str.1)\n" +
            "\tmv\ta2, a0\n" +
            "\tmv\ta0, a1\n" +
            "\tmv\ta1, a2\n" +
            "\ttail\tprintf\n" +
            ".Lfunc_end12:\n" +
            "\t.size\t_f_print, .Lfunc_end12-_f_print\n" +
            "\n" +
            "\t.globl\t_f_println\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f_println,@function\n" +
            "_f_println:\n" +
            "\ttail\tputs\n" +
            ".Lfunc_end13:\n" +
            "\t.size\t_f_println, .Lfunc_end13-_f_println\n" +
            "\n" +
            "\t.globl\t_f_printInt\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f_printInt,@function\n" +
            "_f_printInt:\n" +
            "\tlui\ta1, %hi(.L.str)\n" +
            "\taddi\ta1, a1, %lo(.L.str)\n" +
            "\tmv\ta2, a0\n" +
            "\tmv\ta0, a1\n" +
            "\tmv\ta1, a2\n" +
            "\ttail\tprintf\n" +
            ".Lfunc_end14:\n" +
            "\t.size\t_f_printInt, .Lfunc_end14-_f_printInt\n" +
            "\n" +
            "\t.globl\t_f_printlnInt\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f_printlnInt,@function\n" +
            "_f_printlnInt:\n" +
            "\tlui\ta1, %hi(.L.str.3)\n" +
            "\taddi\ta1, a1, %lo(.L.str.3)\n" +
            "\tmv\ta2, a0\n" +
            "\tmv\ta0, a1\n" +
            "\tmv\ta1, a2\n" +
            "\ttail\tprintf\n" +
            ".Lfunc_end15:\n" +
            "\t.size\t_f_printlnInt, .Lfunc_end15-_f_printlnInt\n" +
            "\n" +
            "\t.globl\t_f_getString\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f_getString,@function\n" +
            "_f_getString:\n" +
            "\taddi\tsp, sp, -16\n" +
            "\tsw\tra, 12(sp)\n" +
            "\tsw\ts0, 8(sp)\n" +
            "\tli\ta0, 256\n" +
            "\tcall\tmalloc\n" +
            "\tmv\ts0, a0\n" +
            "\tlui\ta0, %hi(.L.str.1)\n" +
            "\taddi\ta0, a0, %lo(.L.str.1)\n" +
            "\tmv\ta1, s0\n" +
            "\tcall\tscanf\n" +
            "\tmv\ta0, s0\n" +
            "\tlw\tra, 12(sp)\n" +
            "\tlw\ts0, 8(sp)\n" +
            "\taddi\tsp, sp, 16\n" +
            "\tret\n" +
            ".Lfunc_end16:\n" +
            "\t.size\t_f_getString, .Lfunc_end16-_f_getString\n" +
            "\n" +
            "\t.globl\t_f_getInt\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f_getInt,@function\n" +
            "_f_getInt:\n" +
            "\taddi\tsp, sp, -16\n" +
            "\tsw\tra, 12(sp)\n" +
            "\tlui\ta0, %hi(.L.str)\n" +
            "\taddi\ta0, a0, %lo(.L.str)\n" +
            "\taddi\ta1, sp, 8\n" +
            "\tcall\tscanf\n" +
            "\tlw\ta0, 8(sp)\n" +
            "\tlw\tra, 12(sp)\n" +
            "\taddi\tsp, sp, 16\n" +
            "\tret\n" +
            ".Lfunc_end17:\n" +
            "\t.size\t_f_getInt, .Lfunc_end17-_f_getInt\n" +
            "\n" +
            "\t.globl\t_f_toString\n" +
            "\t.p2align\t2\n" +
            "\t.type\t_f_toString,@function\n" +
            "_f_toString:\n" +
            "\taddi\tsp, sp, -16\n" +
            "\tsw\tra, 12(sp)\n" +
            "\tsw\ts0, 8(sp)\n" +
            "\tsw\ts1, 4(sp)\n" +
            "\tmv\ts0, a0\n" +
            "\tli\ta0, 12\n" +
            "\tcall\tmalloc\n" +
            "\tmv\ts1, a0\n" +
            "\tlui\ta0, %hi(.L.str)\n" +
            "\taddi\ta1, a0, %lo(.L.str)\n" +
            "\tmv\ta0, s1\n" +
            "\tmv\ta2, s0\n" +
            "\tcall\tsprintf\n" +
            "\tmv\ta0, s1\n" +
            "\tlw\tra, 12(sp)\n" +
            "\tlw\ts0, 8(sp)\n" +
            "\tlw\ts1, 4(sp)\n" +
            "\taddi\tsp, sp, 16\n" +
            "\tret\n" +
            ".Lfunc_end18:\n" +
            "\t.size\t_f_toString, .Lfunc_end18-_f_toString\n" +
            "\n" +
            "\t.type\tSTR_BUF_SIZE,@object\n" +
            "\t.section\t.rodata,\"a\",@progbits\n" +
            "\t.globl\tSTR_BUF_SIZE\n" +
            "\t.p2align\t2\n" +
            "STR_BUF_SIZE:\n" +
            "\t.word\t256\n" +
            "\t.size\tSTR_BUF_SIZE, 4\n" +
            "\n" +
            "\t.type\tINT_BUF_SIZE,@object\n" +
            "\t.globl\tINT_BUF_SIZE\n" +
            "\t.p2align\t2\n" +
            "INT_BUF_SIZE:\n" +
            "\t.word\t20\n" +
            "\t.size\tINT_BUF_SIZE, 4\n" +
            "\n" +
            "\t.type\t.L.str,@object\n" +
            "\t.section\t.rodata.str1.1,\"aMS\",@progbits,1\n" +
            ".L.str:\n" +
            "\t.asciz\t\"%d\"\n" +
            "\t.size\t.L.str, 3\n" +
            "\n" +
            "\t.type\t.L.str.1,@object\n" +
            ".L.str.1:\n" +
            "\t.asciz\t\"%s\"\n" +
            "\t.size\t.L.str.1, 3\n" +
            "\n" +
            "\t.type\t.L.str.3,@object\n" +
            ".L.str.3:\n" +
            "\t.asciz\t\"%d\\n\"\n" +
            "\t.size\t.L.str.3, 4\n" +
            "\n" +
            "\t.ident\t\"Ubuntu clang version 14.0.0-1ubuntu1\"\n" +
            "\t.section\t\".note.GNU-stack\",\"\",@progbits\n" +
            "\t.addrsig\n";
}