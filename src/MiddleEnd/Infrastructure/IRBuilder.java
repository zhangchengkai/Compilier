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
//            System.out.println("func!");
            FunctionType funcType=new FunctionType(getType(funcNode.funcType));
            if(funcNode.parameterList!=null) funcNode.parameterList.forEach(tmp->{
//                System.out.println("tmp!");
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
                    if(!className.equals("string")){
//                        System.out.println("???");
                        classScope.Variable_Table.forEach((identifier,tmpTy)->((StructType)pendingType).addMember(identifier,getType(tmpTy)));
//                        System.out.println("!!!");
                    }
                    classScope.Functions_Table.forEach((funcName,funcNode)->{
//                        System.out.println("func!");
//                        faaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaak!
                        IRType returnTy=(funcNode.funcType==null)?new VoidType():getType(funcNode.funcType);
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
//        System.out.println("variable def here");
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
//        System.out.println("variable def end");
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
        if(node.returnVal!=null){
            Value returnValue;
            node.returnVal.accept(this);
            returnValue=node.returnVal.IRoperand;
            if(returnValue instanceof StringConstant) returnValue=getStringPtr(returnValue);
            if(returnValue instanceof NullConstant) ((NullConstant)returnValue).setType(((FunctionType)curFunction.type).returnType);
            this.memoryStore(returnValue,curFunction.returnAddress);
        }
        new Branch(curBlock,curFunction.exitBlock());
        cScope.setInvalid();
    }

    @Override
    public void visit(FuncDefNode node) {
        //区分全局的和class里的
        //自动建立 entry-Block和 exit-Block
        //add ret instruction to exit-Block
        //为非构造非void函数添加默认ret instruction
        curFunction=(curClass==null)?funcTable.get(node.identifier):funcTable.get("_"+curClass.name+"_"+node.identifier);
        FunctionType funcType=(FunctionType) curFunction.type;
        cScope=new IRScope(cScope,IRScope.scopeType.Func);
        IRBasicBlock tmpEntry=new IRBasicBlock(curFunction.name,curFunction);
        IRBasicBlock tmpExit=new IRBasicBlock(curFunction.name,curFunction);

        Value tmpReturnValue;
        if(!funcType.toString().equals("void")){
            curFunction.returnAddress=stackAlloc("_return",funcType.returnType);
            tmpReturnValue=memoryLoad("_return",curFunction.returnAddress,tmpExit);
        }else tmpReturnValue=new Value("BBTY",new VoidType());
        new Ret(tmpReturnValue,tmpExit);
        curBlock=curFunction.entryBlock();
        //remember to call global_initialze at main!!!!!
        if(node.identifier.equals("main")){
//            System.out.println("!"+node.identifier+"!");
            EntryFunction = new IRFunction("_GLOBAL_", new FunctionType(new VoidType()));
            new Call(EntryFunction, tmpEntry);
        }
        //parameter转存到栈空间里
        for(int i=0;i<funcType.parametersName.size();++i){
            Value tmpArg=new Value("_arg",funcType.parametersType.get(i));
            curFunction.addParameter(tmpArg);
            Alloc realArg=this.stackAlloc(funcType.parametersName.get(i),tmpArg.type);
            this.memoryStore(tmpArg,realArg);
            cScope.setVariable(funcType.parametersName.get(i),realArg);
        }
        if(node.funcBody.stmtList!=null) node.funcBody.stmtList.forEach(stmt->stmt.accept(this));
        if(curBlock.terminator==null){
            //not constructor or void func
            if(node.funcType!=null&&(!(funcType.toString().equals("void")))){
                new Store(new IntConstant(0),curFunction.returnAddress,curBlock);
            }
            new Branch(curBlock,curFunction.exitBlock());
        }
        curBlock=null;
        cScope=cScope.upRoot();
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
        Operator op=translateOp((node.operator));
        Value newOperand=null;
        if(op==Operator.logic_and || op==Operator.logic_or){
            node.LOperand.accept(this);
            Value tmpRs1=node.LOperand.IRoperand;
            switch(op){
                case logic_and -> {
                    if(tmpRs1 instanceof BoolConstant){
                        if(!((BoolConstant) tmpRs1).value) newOperand=tmpRs1;
                        else{
                            node.ROperand.accept(this);
                            newOperand=node.ROperand.IRoperand;
                        }
                    }else newOperand=shortCircuit(op,node,tmpRs1);
                }
                case logic_or -> {
                    if(tmpRs1 instanceof BoolConstant){
                        if(((BoolConstant) tmpRs1).value) newOperand = tmpRs1;
                        else{
                            node.ROperand.accept(this);
                            newOperand = node.ROperand.IRoperand;
                        }
                    }else newOperand = shortCircuit(op,node,tmpRs1);
                }
            }
        }else{
            node.ROperand.accept(this);
            Value tmpRs2=node.ROperand.IRoperand;
            if(op!=Operator.assign){
                node.LOperand.accept(this);
                Value tmpRs1=node.LOperand.IRoperand;
                if(tmpRs1 instanceof StringConstant) tmpRs1=getStringPtr(tmpRs1);
                if(tmpRs2 instanceof StringConstant) tmpRs2=getStringPtr(tmpRs2);
                if(tmpRs1 instanceof IRConstant && tmpRs2 instanceof IRConstant){
                    newOperand=calculateConstant(op,(IRConstant) tmpRs1,(IRConstant) tmpRs2);
                }else{
                    //string a is a i8 pointer
                    if(tmpRs1.type.isEqual(new PointerType(new IntegerType(8)))){
                        newOperand=callStringOperator(op,tmpRs1,tmpRs2);
                    }else{
                        switch(op){
                            case add, sub, mul, sdiv, srem, shl, ashr, and, or, xor -> newOperand=new Binary(op,tmpRs1,tmpRs2,curBlock);
                            case eq, ne, sgt, sge, slt, sle -> {
                                //null can be compared with types
                                if(tmpRs2 instanceof NullConstant) ((NullConstant) tmpRs2).setType(tmpRs1.type);
                                newOperand=new Icmp(op,tmpRs1,tmpRs2,curBlock);
                                newOperand=new Zext(newOperand,new BoolType(),curBlock);
                            }
                        }
                    }
                }
            }else{
                Value _address=getAddress(node.LOperand);
                if(tmpRs2 instanceof NullConstant) ((NullConstant) tmpRs2).setType(_address.type.dePointed());
                if(tmpRs2 instanceof StringConstant) tmpRs2=getStringPtr(tmpRs2);
                newOperand=tmpRs2;
                this.memoryStore(newOperand,_address);
            }
        }
        node.IRoperand=newOperand;
    }

    @Override
    public void visit(MonoExprNode node) {
        //对于constant直接处理出结果，非constant生成指令
        //a++ a-- ++a --a 注意区分
        node.operand.accept(this);
        Value originValue=node.operand.IRoperand;
        Value newOperand=originValue;
        switch(node.operator){
            case LNOT,BITNOT,POS,NEG->{
                if(originValue instanceof IRConstant){
                    switch(node.operator){
                        case LNOT -> newOperand=new BoolConstant(!((BoolConstant)originValue).value);
                        case BITNOT -> newOperand=new IntConstant(~((IntConstant)originValue).value);
                        case NEG -> newOperand=new IntConstant(-((IntConstant)originValue).value);
                    }
                }else{
                    switch (node.operator){
                        case LNOT -> newOperand=new Binary(Operator.xor,originValue,new BoolConstant(true),curBlock);
                        case BITNOT -> newOperand=new Binary(Operator.xor,originValue,new IntConstant(-1),curBlock);
                        case NEG -> newOperand=new Binary(Operator.sub,new IntConstant(0),originValue,curBlock);
                    }
                }
            }
            case PREINC,PREDEC,AFTDEC,AFTINC -> {
                Value address=getAddress(node.operand);
                Value newValue=null;
                switch(node.operator){
                    case PREINC -> newValue=newOperand=new Binary(Operator.add,originValue,new IntConstant(1),curBlock);
                    case PREDEC -> newValue=newOperand=new Binary(Operator.add,originValue,new IntConstant(-1),curBlock);
                    case AFTINC -> newValue=new Binary(Operator.add,originValue,new IntConstant(1),curBlock);
                    case AFTDEC -> newValue=new Binary(Operator.add,originValue,new IntConstant(-1),curBlock);
                }
                this.memoryStore(newValue,address);
            }
        }
        node.IRoperand=newOperand;
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
        node.IRoperand=null;
        cScope=new IRScope(cScope,IRScope.scopeType.Flow);
        IRBasicBlock thenBlock=new IRBasicBlock("if_then",curFunction);
        IRBasicBlock termBlock=new IRBasicBlock(curFunction.name,curFunction);
        node.condition.accept(this);
        if(node.elseCode!=null){
            IRBasicBlock elseBlock=new IRBasicBlock("if_else",curFunction);
            addControl(curBlock,node.condition.IRoperand,thenBlock,elseBlock);
            curBlock=elseBlock;
            node.elseCode.accept(this);
            new Branch(curBlock,termBlock);
        }else addControl(curBlock,node.condition.IRoperand,thenBlock,termBlock);
        curBlock=thenBlock;
        node.thenCode.accept(this);
        new Branch(curBlock,termBlock);
        curBlock=termBlock;
        cScope=cScope.upRoot();
    }

    @Override
    public void visit(WhileStmtNode node) {
        // 3 blocks---condition loopBody termBlock
        node.IRoperand=null;
        cScope=new IRScope(cScope,IRScope.scopeType.Flow);
        IRBasicBlock condition=new IRBasicBlock("while_condition",curFunction);
        IRBasicBlock loopbody=new IRBasicBlock("while_body",curFunction);
        IRBasicBlock termBlock=new IRBasicBlock(curFunction.name,curFunction);
        this.pushStack(condition,termBlock);
        new Branch(curBlock,condition);
        curBlock=condition;
        node.condition.accept(this);
        addControl(curBlock,node.condition.IRoperand,loopbody,termBlock);
        curBlock=loopbody;
        node.loopBody.accept(this);
        new Branch(curBlock,condition);
//        System.out.println(curBlock.toString());
        curBlock=termBlock;
        this.popStack();
        cScope=cScope.upRoot();
    }

    @Override
    public void visit(ForStmtNode node) {
        //4 blocks----condition iter loopBody termBody
        //condition可为空
        node.IRoperand=null;
        cScope=new IRScope(cScope,IRScope.scopeType.Flow);
        if(node.init!=null) node.init.accept(this);
        IRBasicBlock condition=new IRBasicBlock("for_condition",curFunction);
        IRBasicBlock iter=new IRBasicBlock("for_iter",curFunction);
        IRBasicBlock loopBody=new IRBasicBlock("for_body",curFunction);
        IRBasicBlock termBody=new IRBasicBlock(curFunction.name,curFunction);
        this.pushStack(iter,termBody);
        new Branch(curBlock,condition);
        curBlock=condition;
        if(node.condition!=null){
            node.condition.accept(this);
            addControl(curBlock,node.condition.IRoperand,loopBody,termBody);
        }else{
            //for(int i=0;;++i) no condition
            new Branch(curBlock,loopBody);
        }
        curBlock=loopBody;
        if(node.loopBody!=null) node.loopBody.accept(this);
        new Branch(curBlock,iter);
        curBlock=iter;
        if(node.iteration!=null) node.iteration.accept(this);
        new Branch(curBlock,condition);
        curBlock=termBody;
        this.popStack();
        cScope=cScope.upRoot();
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
        //new int[n][m] =>
        //new int[n][]
        //for(int i=0;i<n;i++) [i]=new int[m];
        Value newOperand;
        if(node.isArray()){
            LinkedList<ExprNode> initList=new LinkedList<>(node.SizeList);
//            System.out.println("new a array here");
            newOperand=recursiveNew(initList,new PointerType(getType(node.newType),node.DimSize));
//            System.out.println("new array end");
        }else{
            String className=node.newType.typeId;
            StructType classType=(StructType) typeTable.get(className).dePointed();
            newOperand=heapAlloc(new PointerType(classType),new IntConstant(classType.byteSize()));
            Call constructor=new Call(funcTable.get("_"+classType.name+"_"+className),curBlock);
            constructor.addArg(newOperand);
        }
        node.IRoperand=newOperand;
    }

    @Override
    public void visit(ArrayAccessExprNode node) {
        Value address=getAddress(node);
        node.IRoperand=this.memoryLoad("_array",address,curBlock);
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
//        System.out.println("sdifjsidjfsdf");
        node.base.accept(this);
        Value baseAddress = node.base.IRoperand;
        StructType baseType = (StructType) baseAddress.type.dePointed();
        Gep newOperand = new Gep(new PointerType(baseType.typeTable.get(node.member)),baseAddress,curBlock);
        newOperand.addIndex(new IntConstant(0)).addIndex(new IntConstant(baseType.indexTable.get(node.member)));
//        System.out.println(node.member);
        node.IRoperand = memoryLoad(node.member,newOperand,curBlock);
    }

    @Override
    public void visit(ThisExprNode node) {
        Value ptr = cScope.fetchValue("_this");
        node.IRoperand = memoryLoad("_this",ptr,curBlock);
    }

    @Override
    public void visit(LambdaExprNode node) {}

    @Override
    public void visit(VoidTypeNode node) {}

    @Override
    public void visit(ClassTypeNode node) {}

    @Override
    public void visit(ArrayTypeNode node) {}


//////////////////////					auxiliary function					////////////////////////////

    private Alloc stackAlloc(String identifier, IRType _ty){
        return new Alloc(identifier,_ty,curFunction.entryBlock());
    }

    private Value heapAlloc(IRType targetType, Value byteSize){
        IRFunction malloc = funcTable.get("_heap_malloc");
//        System.out.println("1111111111111111111");
        Value returnValue = new Call(malloc,curBlock);
//        System.out.println("2222222222222222");
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
        if(node instanceof IdentifierExprNode){
            String identifier=((IdentifierExprNode) node).identifier;
            Value returnValue=cScope.fetchValue(identifier);
            // in class
            if(cScope.isClass(identifier)){
                Value ptr=cScope.fetchValue("_this");
                ptr=memoryLoad("_this",ptr,curBlock);
                returnValue=new Gep(new PointerType(curClass.typeTable.get(identifier)),ptr,curBlock);
                ((Gep) returnValue).addIndex(new IntConstant(0)).addIndex(new IntConstant(curClass.indexTable.get(identifier)));
            }
            return returnValue;
        }else if(node instanceof ObjectMemberExprNode){
            ((ObjectMemberExprNode) node).base.accept(this);
            Value baseAddress=((ObjectMemberExprNode) node).base.IRoperand;
            StructType baseType=(StructType) baseAddress.type.dePointed();
            Gep returnValue=new Gep(new PointerType(baseType.typeTable.get(((ObjectMemberExprNode) node).member)),baseAddress,curBlock);
            returnValue.addIndex(new IntConstant(0)).addIndex(new IntConstant(baseType.indexTable.get(((ObjectMemberExprNode) node).member)));
            return returnValue;
        }else if(node instanceof ArrayAccessExprNode){
            Value ptrAddress=getAddress(((ArrayAccessExprNode) node).array);
            Value address=memoryLoad("_array",ptrAddress,curBlock);
            ((ArrayAccessExprNode) node).index.accept(this);
            Gep biasAddress=new Gep(address.type,address,curBlock);
            biasAddress.addIndex(((ArrayAccessExprNode) node).index.IRoperand);
            return biasAddress;
        }else{
//            System.out.println("some MonoExprNode here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1");
            return getAddress(((MonoExprNode) node).operand);
        }
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
            default -> throw new RuntimeException("[Debug] Unknown Op.");
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
            default -> throw new RuntimeException("[Debug] Unknown Op.");
        }
        return returnValue;
    }

    private Value shortCircuit(Operator op, BinaryExprNode node, Value tmpRs1){
        //短路求值
        Value tmpAddress=stackAlloc(op.toString(),new BoolType());
        IRBasicBlock dBlock=new IRBasicBlock("_dBlock",curFunction);
        IRBasicBlock sBlock=new IRBasicBlock("_sBlock",curFunction);
        IRBasicBlock tBlock=new IRBasicBlock("_tBlock",curFunction);
        switch(op){
            case logic_and -> addControl(curBlock,tmpRs1,sBlock,dBlock);
            case logic_or -> addControl(curBlock,tmpRs1,dBlock,sBlock);
        }
        curBlock=dBlock;
        memoryStore(tmpRs1,tmpAddress);
        new Branch(curBlock,tBlock);
        curBlock=sBlock;
        node.ROperand.accept(this);
        Value tmpRs2=node.ROperand.IRoperand;
        memoryStore(tmpRs2,tmpAddress);
        new Branch(curBlock,tBlock);
        curBlock=tBlock;
        return memoryLoad("circuit",tmpAddress,curBlock);
    }

    public void processGlobalInit(){
        FunctionType tempType = new FunctionType(new VoidType());
        IRBasicBlock mainBody = new IRBasicBlock(EntryFunction.name,EntryFunction);
        this.globalInit.forEach(node->{
            IRType valueTy = getType(node.varType);
            IRFunction nowFunction = new IRFunction("_global_var_init",tempType);
            Value address = cScope.fetchValue(node.identifier);
            this.curFunction = nowFunction;
            this.curBlock = new IRBasicBlock(node.identifier,curFunction); // entry-Block
            IRBasicBlock tmpExit = new IRBasicBlock(node.identifier,curFunction); // exit-Block
            new Ret(new Value("BBTY",new VoidType()),tmpExit);
            Value initValue;
            if(node.initValue == null) initValue = new NullConstant();
            else{
//                System.out.println("newType begin");
                node.initValue.accept(this);
                initValue = node.initValue.IRoperand;
//                System.out.println("-------------");
            }
            if(initValue instanceof NullConstant) ((NullConstant) initValue).setType(valueTy);
            if (initValue instanceof StringConstant) initValue = getStringPtr(initValue);
            this.memoryStore(initValue,address);
            new Branch(curBlock,tmpExit);
            this.targetModule.addGlobalInit(curFunction);
            new Call(nowFunction,mainBody);
        });
        new Ret(new Value("BBTY",new VoidType()),mainBody);
        this.targetModule.addGlobalInit(EntryFunction);
    }

    private void pushStack(IRBasicBlock cBlock, IRBasicBlock bBlock){
        loopContinue.push(cBlock); loopBreak.push(bBlock);
    }

    private void popStack(){
        loopContinue.pop(); loopBreak.pop();
    }

    private Value recursiveNew(LinkedList<ExprNode> initList, IRType targetType){
        //new int[][][][][]...
        // to be continued
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

    private Value callStringOperator(MiddleEnd.Infrastructure.IRBuilder.Operator op, Value str1, Value str2){
        assert str1.type.isEqual(new PointerType(new IntegerType(8))) && str2.type.isEqual(new PointerType(new IntegerType(8)));
        Call returnValue;
        IRFunction calledFunction;
        switch(op){
            case add ->returnValue = new Call(calledFunction = funcTable.get("_string_merge"),curBlock);
            case eq -> returnValue = new Call(calledFunction = funcTable.get("_stringcmp_eq"),curBlock);
            case ne -> returnValue = new Call(calledFunction = funcTable.get("_stringcmp_neq"),curBlock);
            case slt -> returnValue = new Call(calledFunction = funcTable.get("_stringcmp_less"),curBlock);
            case sle -> returnValue = new Call(calledFunction = funcTable.get("_stringcmp_leq"),curBlock);
            case sgt -> returnValue = new Call(calledFunction = funcTable.get("_stringcmp_greater"),curBlock);
            case sge -> returnValue = new Call(calledFunction = funcTable.get("_stringcmp_geq"),curBlock);
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
//        System.out.println(rawTy.toString());
        IRType returnTy = typeTable.get(rawTy.typeId);
//        System.out.println("222222222222222222222");
        if(rawTy instanceof ArrayTypeNode) returnTy = new PointerType(returnTy,((ArrayTypeNode) rawTy).dimSize);
        return returnTy;
    }
}