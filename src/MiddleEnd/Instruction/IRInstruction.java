package MiddleEnd.Instruction;

import MiddleEnd.BaseClass.User;
import MiddleEnd.IRBasicBlock;
import MiddleEnd.TypeSystem.IRType;


public abstract class IRInstruction extends User{
    public IRBasicBlock parentBlock;

    public IRInstruction(String _name, IRType _type, IRBasicBlock _block) {
        super(_name, _type);
        this.parentBlock = _block;
        if(_block != null) _block.addInstruction(this);
    }

}