package MiddleEnd.BaseClass;

import MiddleEnd.Infrastructure.IRVisitor;
import MiddleEnd.TypeSystem.IRType;
import java.util.ArrayList;


public abstract class User extends Value{
    public ArrayList<Value> operands;//values used

    public User(String _name, IRType _type) {
        super(_name, _type);
        this.operands = new ArrayList<>();
    }

    public void addOperand(Value _operand){
        _operand.addUser(this);
        this.operands.add(_operand);
    }

    public Value getOperand(int index){
        return operands.get(index);
    }

    public abstract void accept(IRVisitor visitor);
}