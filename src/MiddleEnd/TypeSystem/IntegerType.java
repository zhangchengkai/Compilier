package MiddleEnd.TypeSystem;

public class IntegerType extends IRType{
    public int width;//i1 i8 i16 i32 i64

    public IntegerType(int _width){
        this.width = _width;
    }

    @Override
    public int byteSize() {
        return this.width / 8;
    }

    @Override
    public String toString() {
        return "i" + this.width;
    }

    @Override
    public boolean isEqual(IRType other) {
        if(other instanceof IntegerType) return ((IntegerType) other).width == this.width;
        else return false;
    }
}