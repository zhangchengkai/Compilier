package BackEnd.Instruction;

import BackEnd.Infrastructure.ASMBlock;
import BackEnd.Operand.Operand;
import BackEnd.Operand.Register;
import BackEnd.Operand.VirtualRegister;

/*
    About GEP(Pointer Calculation) :
    1. Constant: We use rs1 + imm
    2. Variable: We use rs1(by Add) + 0
 */
public class LoadInstr extends Instruction{
    // 0 rd ; 1 rs1(contains offset)
    //	lw		t1,	8(sp)
    public LoadInstr(ASMBlock _curBlock, String _op) {
        super(_curBlock, _op); // lb lh lw lbu lhu
    }

    @Override
    public void rewriteUse(String origin, VirtualRegister born) {
        if(rs1.getName().equals(origin)){
            rs1 = born;
            use.remove(origin);
            use.add(born.getName());
        }
    }

    @Override
    public void rewriteDef(String origin, VirtualRegister born) {
        if(rd.getName().equals(origin)){
            rd = born;
            def.remove(origin);
            def.add(born.getName());
        }
    }

    @Override
    public Instruction addOperand(Operand... args) {
        assert args.length == 2;
        rd = args[0]; rs1 = args[1]; rs2 = null;
        this.def.add(rd.getName());
        this.use.add(rs1.getName());
        return this;
    }

    @Override
    public String printASM() {
        return String.format("%s\t%s, %s(%s)", op, rd.getName(), ((Register)rs1).offset, rs1.getName());
    }
}