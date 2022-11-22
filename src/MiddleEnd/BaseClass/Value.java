package MiddleEnd.BaseClass;

import BackEnd.Operand.Operand;
import MiddleEnd.Infrastructure.IRVisitor;
import MiddleEnd.TypeSystem.IRType;
import java.util.ArrayList;
import java.util.HashMap;

public class Value{
    public Operand ASMOperand;
    public String name;
    public IRType type;
    public ArrayList<User> useList;// record users
    public static HashMap<String, Integer> renamingMachine = new HashMap<>();

    public Value(String _name,IRType _type){
        this.ASMOperand = null;
        this.name = renaming(_name);
        this.type = _type;
        this.useList = new ArrayList<>();
    }

    public String renaming(String _name){
        if(_name.equals("_f_main")) return "main";
        Integer count = renamingMachine.get(_name);
        if(count == null) count = 0;
        else count++;
        renamingMachine.put(_name,count);
        return (count == 0) ? _name : _name + count;
    }

    public void addUser(User _user){
        this.useList.add(_user);
    }

    public String getName(){
        return "%" + this.name;
    }

    public String getTypeName(){
        return type.toString() + " " + this.getName();
    }

    public String toString(){
        throw new RuntimeException("[Debug] Why use base value's toString");
    }

}