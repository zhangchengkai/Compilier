package MiddleEnd.Infrastructure;

import AST.*;
import MiddleEnd.BaseClass.Value;
import MiddleEnd.IRBasicBlock;
import MiddleEnd.IRFunction;
import MiddleEnd.IRModule;
import MiddleEnd.Instruction.*;
import MiddleEnd.Operand.*;
import MiddleEnd.TypeSystem.*;
import Utils.GlobalScope;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

public class IRBuilder implements ASTVisitor {
    public IRModule targetModule;
    public GlobalScope gScope;
    public IRScope cScope;  // symbol table storing pointer
    public HashMap<String, IRType> typeTable;// int bool string classes...
    public HashMap<String, IRFunction> funcTable;//global functions & class functions (with this pointer
    public HashMap<String,StringConstant> stringTable; // avoid repeatedly global str
    public LinkedList<VarDefNode> globalInit;//global veriable initialize
    public IRBasicBlock curBlock;
    public IRFunction curFunction;
    public IRFunction EntryFunction;
    public StructType curClass;
    public enum Operator{add, sub, mul, sdiv, srem, shl, ashr, and, or, xor, logic_and, logic_or, eq, ne, sgt, sge, slt, sle, assign}
    // for loop nesting break & continue
    private final Stack<IRBasicBlock> loopContinue;
    private final Stack<IRBasicBlock> loopBreak;

    public IRBuilder(IRModule _module, GlobalScope _gScope){
        //先填充typetable，放入所有的type
        //插入global function
        //插入各class里的function，添加默认构造函数
        this.targetModule=_module;
        this.gScope=_gScope;
        this.cScope=new IRScope(null,IRScope.scopeType.Global);
        this.typeTable=new HashMap<>();
        this.funcTable=new HashMap<>();
        this.stringTable=new HashMap<>();
        this.globalInit=new LinkedList<>();
        this.curBlock=null;
        this.curFunction=null;
        this.curClass=null;
        loopContinue=new Stack<>();
        loopBreak=new Stack<>();
        //int bool string classes   void
        gScope.Class_Table.forEach((className,classScope)->{
            switch(className){
                case "int"->typeTable.put("int",new IntegerType(32));
                case "bool"->typeTable.put("bool",new BoolType());
                case "string"->typeTable.put("string",new PointerType(new IntegerType(8)));
                default ->{
                    StructType newClass=new StructType(className);
                    typeTable.put(className,new PointerType(newClass));
                    targetModule.addClassType(newClass);
                }
            }
        });
        typeTable.put("void",new VoidType());
        gScope.Functions_Table.forEach((funcName,funcNode)->{
            FunctionType funcType=new FunctionType(getType(funcNode.funcType));
            if(funcNode.parameterList!=null) funcNode.parameterList.forEach(tmp->{
                IRType argType=getType(tmp.varType);
                funcType.addParameters(argType,tmp.identifier);
            });
            IRFunction _func=new IRFunction("_f_"+funcName,funcType);
            if(funcNode.isBuiltin) _func.setBuiltin();
            funcTable.put(funcName,_func);
            targetModule.addFunction(_func);
        });
        //string classes
        gScope.Class_Table.forEach((className,classScope)->{
            switch(className){
                case "int","bool"-> {}
                default -> {
                    IRType pendingType=typeTable.get(className).dePointed();
                    if(!className.equals("string")) classScope.Variable_Table.forEach((identifier,tmpTy)->((StructType)pendingType).addMember(identifier,getType(tmpTy)));
                    classScope.Functions_Table.forEach((funcName,funcNode)->{
                        IRType returnTy=(funcNode==null)?new VoidType():getType(funcNode.funcType);
                        FunctionType funcType=new FunctionType(returnTy);
                        // add _this pointer to parameters
                        IRType argType=new PointerType(pendingType);
                        funcType.addParameters(argType,"_this");
                        if(funcNode.parameterList!=null){
                            for(VarDefNode tmp:funcNode.parameterList){
                                argType=getType(tmp.varType);
                                funcType.addParameters(argType,tmp.identifier);
                            }
                        }
                        IRFunction _func=new IRFunction("_class_"+className+"_"+funcName,funcType);
                        if(funcNode.isBuiltin) _func.setBuiltin();
                        funcTable.put(_func.name,_func);
                        targetModule.addFunction(_func);
                    });
                    //默认构造函数
                    if(!className.equals("string")&&funcTable.get("_class_"+className+"_"+className)==null){
                        FunctionType funcType=new FunctionType(new VoidType());
                        IRType argType=new PointerType(pendingType);
                        funcType.addParameters(argType,"_this");
                        IRFunction _func=new IRFunction("_class_"+className+"_"+className,funcType);
                        _func.addParameter(new Value("_arg",argType));
                        IRBasicBlock onlyBlock=new IRBasicBlock(_func.name,_func);
                        new Ret(new Value("BBTY",new VoidType()),onlyBlock);
                        funcTable.put(_func.name,_func);
                        targetModule.addFunction((_func));
                    }
                }
            }
        });
    }

    @Override
    public void visit(RootNode node) {
        node.elements.forEach(tmp->tmp.accept(this));
    }

    @Override
    public void visit(IntConstantExprNode node) {
        node.IRoperand = new IntConstant(node.value);
    }

    @Override
    public void visit(BoolConstantExprNode node) {
        node.IRoperand = new BoolConstant(node.value);
    }

    @Override
    public void visit(StringConstantExprNode node) {
        //对string常量先查个重
        StringConstant stringLiteral = stringTable.get(node.value);
        if(stringLiteral == null){
            stringLiteral = new StringConstant(node.value);
            targetModule.addString(stringLiteral);
            stringTable.put(node.value,stringLiteral);
        }
        node.IRoperand = stringLiteral;
    }

    @Override
    public void visit(NullConstantExprNode node) {
        node.IRoperand = new NullConstant();
    }

    @Override
    public void visit(VarDefNode node) {
        //注意区分数组类型
        //区分全局 or 局部变量
        IRType valueTy=getType(node.varType);
        Value value;
        if(cScope.parent==null){//全局变量
            value=new GlobalDef(node.identifier,valueTy);
            targetModule.addGlobalDef((GlobalDef) value);
        }else value=stackAlloc(node.identifier,valueTy);
        cScope.setVariable(node.identifier,value);
        node.IRoperand=value;
        if(node.initValue!=null){ // initialize
            if(cScope.parent!=null){ // 局部变量
                node.initValue.accept(this);
                Value initValue=node.initValue.IRoperand;
                if(initValue instanceof NullConstant) ((NullConstant) initValue).setType(valueTy);
                if(initValue instanceof StringConstant) initValue=getStringPtr(initValue);
                this.memoryStore(initValue,value);
            }else globalInit.add(node);
        }else{
            if(node.varType instanceof ArrayTypeNode){
                if(cScope.parent!=null) this.memoryStore(new NullConstant((PointerType) valueTy),value);
                else globalInit.add(node);
            }
        }
    }

    @Override
    public void visit(VarDefStmtNode node) {
        node.elements.forEach(tmp->tmp.accept(this));
    }

    @Override
    public void visit(IdentifierExprNode node) {
        // visit id Node means load it in; So function call should not travel in this node :)
        node.IRoperand = memoryLoad(node.identifier,getAddress(node),curBlock);
    }

    @Override
    public void visit(FuncCallExprNode node) {
        //调用全局函数 or 调用class内函数
        //class外调用class内 or class内调用class内
        //class function(*this,....){}
        //特判array.size()
        //固有class：string
        IRFunction func=null;
        Value thisPtr=null;
        if(node.Func instanceof IdentifierExprNode){
            String funcName=((IdentifierExprNode) node.Func).identifier;
            if(curClass!=null) func=funcTable.get("_"+curClass.name+"_"+funcName);
            if(func==null) func=funcTable.get(funcName);
            else{//class内调用，this不变
                thisPtr=cScope.fetchValue("_this");
                thisPtr=memoryLoad("_this",thisPtr,curBlock);
            }
        }else{
            ((ObjectMemberExprNode) node.Func).base.accept(this);
            thisPtr=((ObjectMemberExprNode) node.Func).base.IRoperand;
            if(((ObjectMemberExprNode) node.Func).base.exprType instanceof ArrayTypeNode){
                node.IRoperand=arraySize(thisPtr);//直接返回数组第-1项
                return;
            }
            String className;
            IRType classType=thisPtr.type.dePointed();
            if(classType instanceof StructType) className=((StructType) classType).name;
            else className="class_string";
            func=funcTable.get("_"+className+"_"+((ObjectMemberExprNode) node.Func).member);
        }
        if(node.AryList!=null){
            for(int i=0;i<node.AryList.size();++i){
                ASTNode tmp=node.AryList.get(i);
                tmp.accept(this);
                Value tmpArg=tmp.IRoperand;
                if(tmpArg instanceof StringConstant) tmpArg=getStringPtr(tmpArg);
                if(tmpArg instanceof NullConstant) ((NullConstant) tmpArg).setType(((FunctionType) func.type).parametersType.get(i));
                tmp.IRoperand=tmpArg;
            }
        }
        Call newOperand=new Call(func,curBlock);
        if(thisPtr!=null) newOperand.addArg(thisPtr);
        if(node.AryList!=null) node.AryList.forEach(tmp->newOperand.addArg(tmp.IRoperand));
        func.setUsed();
        node.IRoperand=newOperand;
    }

    @Override
    public void visit(ReturnStmtNode node) {
        //要把最后returnValue里的值存回到 return
        //todo
    }

    @Override
    public void visit(FuncDefNode node) {
        //区分全局的和class里的
        //自动建立 entry-Block和 exit-Block
        //add ret instruction to exit-Block
        //parameter转存到栈空间里
        //为非构造非void函数添加默认ret instruction
        // todo
    }

    @Override
    public void visit(ExprStmtNode node) {
        node.expr.accept(this);
    }

    @Override
    public void visit(BinaryExprNode node) {
        //逻辑&& || 短路求值
        // string 之间的比较需要调用函数
        // bool值直接转成i8
        // 注意 null
        //todo
    }

    @Override
    public void visit(MonoExprNode node) {
        //对于constant直接处理出结果，非constant生成指令
        //a++ a-- ++a --a 注意区分
        // todo
    }

    @Override
    public void visit(BlockStmtNode node) {
        cScope = new IRScope(cScope, IRScope.scopeType.Common);
        if(node.stmtList != null) node.stmtList.forEach(tmp->tmp.accept(this));
        cScope = cScope.upRoot();
    }

    @Override
    public void visit(IfStmtNode node) {
        //thenBlock / elseBlock -> termBlock
        // todo;
    }

    @Override
    public void visit(WhileStmtNode node) {
        // 3 blocks---condition loopBody termBlock
        // todo
    }

    @Override
    public void visit(ForStmtNode node) {
        //4 blocks----condition iter loopBody termBody
        //condition可为空
        // todo
    }

    @Override
    public void visit(BreakStmtNode Node) {
        new Branch(curBlock,loopBreak.peek());
    }

    @Override
    public void visit(ContinueStmtNode node) {
        new Branch(curBlock,loopContinue.peek());
    }

    @Override
    public void visit(NewExprNode node) {
        //new class 在堆空间申请空间，调用构造函数
        //new array 递归构造
        //new int[n][][]...
        //在堆空间申请(i32(n)，n个i8*(int[k][][]...的指针)
        //new int[n][m]
        //new int[n][]
        //for(int i=0;i<n;i++) [i]=new int[m];
    }

    @Override
    public void visit(ArrayAccessExprNode node) {
        Value address = getAddress(node);
        node.IRoperand = this.memoryLoad("_array",address,curBlock);
    }

    @Override
    public void visit(ClassDefNode node) {
        curClass = (StructType) typeTable.get(node.classIdentifier).dePointed();
        cScope = new IRScope(cScope, IRScope.scopeType.Class);
        curClass.indexTable.forEach((identifier,index)->cScope.setVariable(identifier,new IntConstant(index)));
        if(node.memberFunction != null) node.memberFunction.forEach(tmp->tmp.accept(this));
        curClass = null;
        cScope = cScope.upRoot();
    }

    @Override
    public void visit(ObjectMemberExprNode node) {
        node.base.accept(this);
        Value baseAddress = node.base.IRoperand;
        StructType baseType = (StructType) baseAddress.type.dePointed();
        Gep newOperand = new Gep(new PointerType(baseType.typeTable.get(node.member)),baseAddress,curBlock);
        newOperand.addIndex(new IntConstant(0)).addIndex(new IntConstant(baseType.indexTable.get(node.member)));
        node.IRoperand = memoryLoad(node.member,newOperand,curBlock);
    }

    @Override
    public void visit(ThisExprNode node) {
        Value ptr = cScope.fetchValue("_this");
        node.IRoperand = memoryLoad("_this",ptr,curBlock);
    }

    @Override
    public void visit(LambdaExprNode node) {

    }

    @Override
    public void visit(VoidTypeNode node) {

    }

    @Override
    public void visit(ClassTypeNode node) {

    }

    @Override
    public void visit(ArrayTypeNode node) {

    }


//////////////////////					auxiliary function					////////////////////////////




    private Alloc stackAlloc(String identifier, IRType _ty){
        return new Alloc(identifier,_ty,curFunction.entryBlock());
    }

    private Value heapAlloc(IRType targetType, Value byteSize){
        IRFunction malloc = funcTable.get("_malloc");
        Value returnValue = new Call(malloc,curBlock);
        ((Call)returnValue).addArg(byteSize);
        malloc.setUsed();
        if(!targetType.isEqual(returnValue.type)) returnValue = new Bitcast(returnValue,targetType,curBlock);
        return returnValue;
    }

    private Value memoryLoad(String identifier, Value address, IRBasicBlock _block){
        return new Load(identifier,address,_block);
    }

    private void memoryStore(Value value, Value address){
        new Store(value,address,curBlock);
    }

    private Value getAddress(ASTNode node){
        //全局/局部变量、成员变量、数组、
        // todo
    }

    private IRBuilder.Operator translateOp(BinaryExprNode.Op origin){
        switch (origin){
            case ADD -> {return Operator.add;}
            case SUB -> {return Operator.sub;}
            case MUL -> {return Operator.mul;}
            case DIV -> {return Operator.sdiv;}
            case MOD -> {return Operator.srem;}
            case SHL -> {return Operator.shl;}
            case SHR -> {return Operator.ashr;}
            case AND -> {return Operator.and;}
            case XOR -> {return Operator.xor;}
            case OR -> {return Operator.or;}
            case LAND -> {return Operator.logic_and;}
            case LOR -> {return Operator.logic_or;}
            case GT -> {return Operator.sgt;}
            case LT-> {return Operator.slt;}
            case GE -> {return Operator.sge;}
            case LE -> {return Operator.sle;}
            case EQ -> {return Operator.eq;}
            case NE -> {return Operator.ne;}
            case ASSIGN -> {return Operator.assign;}
            default -> throw new RuntimeException("[Debug] Unknown operator.");
        }
    }

    private IRConstant calculateConstant(IRBuilder.Operator op, IRConstant rs1, IRConstant rs2){
        assert rs1.type.isEqual(rs2.type);
        IRConstant returnValue;
        switch (op) {
            case add, sub, mul, sdiv, srem, shl, ashr, and, or, xor, logic_and, logic_or -> {
                if (rs1 instanceof IntConstant) {
                    int value1 = ((IntConstant) rs1).value;
                    int value2 = ((IntConstant) rs2).value;
                    int result;
                    switch (op) {
                        case add -> result = (value1 + value2);
                        case sub -> result = (value1 - value2);
                        case mul -> result = (value1 * value2);
                        case sdiv -> result = (value1 / value2);
                        case srem -> result = (value1 % value2);
                        case and -> result = (value1 & value2);
                        case or -> result = (value1 | value2);
                        case xor -> result = (value1 ^ value2);
                        case shl -> result = (value1 << value2);
                        case ashr -> result = (value1 >> value2);
                        default -> throw new RuntimeException("[Debug] Unknown Op.");
                    }
                    returnValue = new IntConstant(result);
                } else {
                    boolean value1 = ((BoolConstant) rs1).value;
                    boolean value2 = ((BoolConstant) rs2).value;
                    boolean result;
                    switch (op) {
                        case logic_and -> result = (value1 && value2);
                        case logic_or -> result = (value1 || value2);
                        default -> throw new RuntimeException("[Debug] Unknown Op.");
                    }
                    returnValue = new BoolConstant(result);
                }
            }
            case eq, ne, sgt, sge, slt, sle -> {
                boolean result;
                if(rs1 instanceof IntConstant){
                    int value1 = ((IntConstant) rs1).value;
                    int value2 = ((IntConstant) rs2).value;
                    switch (op) {
                        case eq -> result = (value1 == value2);
                        case ne -> result = (value1 != value2);
                        case sge -> result = (value1 >= value2);
                        case sgt -> result = (value1 > value2);
                        case sle -> result = (value1 <= value2);
                        case slt -> result = (value1 < value2);
                        default -> throw new RuntimeException("[Debug] Unknown Op.");
                    }
                }else{
                    boolean value1 = ((BoolConstant) rs1).value;
                    boolean value2 = ((BoolConstant) rs2).value;
                    switch (op) {
                        case eq -> result = (value1 == value2);
                        case ne -> result = (value1 != value2);
                        default -> throw new RuntimeException("[Debug] Unknown Op.");
                    }
                }
                returnValue = new BoolConstant(result);
            }
            default -> throw new RuntimeException("[Debug] Unknown op .");
        }
        return returnValue;
    }

    private Value shortCircuit(Operator op, BinaryExprNode node, Value tmpRs1){
        //短路求值
        // todo
    }

    public void processGlobalInit(){
        if(this.globalInit.size() == 0) return;
        FunctionType tempType = new FunctionType(new VoidType());
        IRFunction entryFunction = new IRFunction("_GLOBAL_",tempType);
        IRBasicBlock mainBody = new IRBasicBlock(entryFunction.name,entryFunction);
        this.globalInit.forEach(node->{
            IRType valueTy = getType(node.varType);
            IRFunction nowFunction = new IRFunction("_global_var_init",tempType);
            Value address = cScope.fetchValue(node.identifier);
            this.curFunction = nowFunction;
            this.curBlock = new IRBasicBlock(node.identifier,curFunction); // entry-Block
            IRBasicBlock tmpExit = new IRBasicBlock(node.identifier,curFunction); // exit-Block
            new Ret(new Value("Anonymous",new VoidType()),tmpExit);
            Value initValue;
            if(node.initValue == null) initValue = new NullConstant();
            else{
                node.initValue.accept(this);
                initValue = node.initValue.IRoperand;
            }
            if(initValue instanceof NullConstant) ((NullConstant) initValue).setType(valueTy);
            if (initValue instanceof StringConstant) initValue = getStringPtr(initValue);
            this.memoryStore(initValue,address);
            new Branch(curBlock,tmpExit);
            this.targetModule.addGlobalInit(curFunction);
            new Call(nowFunction,mainBody);
        });
        new Ret(new Value("Anonymous",new VoidType()),mainBody);
        this.targetModule.addGlobalInit(entryFunction);
    }

    private void pushStack(IRBasicBlock cBlock, IRBasicBlock bBlock){
        loopContinue.push(cBlock); loopBreak.push(bBlock);
    }

    private void popStack(){
        loopContinue.pop(); loopBreak.pop();
    }

    private Value recursiveNew(LinkedList<ExprNode> initList, IRType targetType){
        //new int[][][][][]...
        // todo
    }

    private Value arraySize(Value address){
        Value i32Pointer = new Bitcast(address,new PointerType(new IntegerType(32)),curBlock);
        Gep biasAddress = new Gep(new PointerType(new IntegerType(32)),i32Pointer,curBlock);
        biasAddress.addIndex(new IntConstant(-1));
        return memoryLoad("array_size",biasAddress,curBlock);
    }

    private Value getStringPtr(Value raw){
        //返回指向该字符串的指针
        Gep ptr = new Gep(new PointerType(new IntegerType(8)),raw,curBlock);
        ptr.addIndex(new IntConstant(0)).addIndex(new IntConstant(0));
        return ptr;
    }

    private Value callStringOperator(Operator op,Value str1, Value str2){
        Call returnValue;
        IRFunction calledFunction;
        switch(op){
            case add ->returnValue = new Call(calledFunction = funcTable.get("_str_splice"),curBlock);
            case eq -> returnValue = new Call(calledFunction = funcTable.get("_str_eq"),curBlock);
            case ne -> returnValue = new Call(calledFunction = funcTable.get("_str_ne"),curBlock);
            case slt -> returnValue = new Call(calledFunction = funcTable.get("_str_lt"),curBlock);
            case sle -> returnValue = new Call(calledFunction = funcTable.get("_str_le"),curBlock);
            case sgt -> returnValue = new Call(calledFunction = funcTable.get("_str_gt"),curBlock);
            case sge -> returnValue = new Call(calledFunction = funcTable.get("_str_ge"),curBlock);
            default -> throw new RuntimeException("[Debug] Unknown operator :(");
        }
        calledFunction.setUsed();
        returnValue.addArg(str1).addArg(str2);
        return returnValue;
    }

    private void addControl(IRBasicBlock cBlock, Value flag, IRBasicBlock tBlock, IRBasicBlock fBlock){
        //flag is i8 ,trunc it to i1 first
        Value _condition = new Trunc(flag,new IntegerType(1),cBlock);
        new Branch(cBlock,_condition,tBlock,fBlock);
    }

    private IRType getType(TypeNode rawTy){
        IRType returnTy = typeTable.get(rawTy.typeId);
        if(rawTy instanceof ArrayTypeNode) returnTy = new PointerType(returnTy,((ArrayTypeNode) rawTy).dimSize);
        return returnTy;
    }
}