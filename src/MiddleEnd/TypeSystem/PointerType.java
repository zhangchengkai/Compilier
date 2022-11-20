package MiddleEnd.TypeSystem;

public class PointerType extends IRType{
    public IRType baseType;//指向的类型（高级指针指向低级指针
    public int dimSize;//几级指针

    public PointerType(IRType _base){
        if(_base instanceof PointerType){
            this.baseType = ((PointerType) _base).baseType;
            this.dimSize = ((PointerType) _base).dimSize + 1;
        }else{
            this.baseType = _base;
            this.dimSize = 1;
        }
    }

    public PointerType(IRType _base, int _dimSize){//定义数组的首地址
        if(_base instanceof PointerType){
            this.baseType = ((PointerType) _base).baseType;
            this.dimSize = ((PointerType) _base).dimSize + _dimSize;
        }else {
            this.baseType = _base;
            this.dimSize = _dimSize;
        }
    }

    @Override
    public int byteSize() {
        return 8;
    }

    @Override
    public String toString() {
        return this.baseType.toString() + "*".repeat(this.dimSize);
    }

    @Override
    public boolean isEqual(IRType other) {
        if(other instanceof PointerType) return this.baseType.isEqual(((PointerType)other).baseType)
                && this.dimSize == ((PointerType)other).dimSize;
        else return false;
    }
}