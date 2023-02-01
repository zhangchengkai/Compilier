import AST.ASTBuilder;
import AST.RootNode;
import BackEnd.Infrastructure.ASMBuilder;
import BackEnd.RegAllocation.GraphColor;
import BackEnd.RegAllocation.EasyStack;
import Optimization.Mem2Reg;
import FrontEnd.BuiltInInitiator;
import FrontEnd.PreProcessor;
import FrontEnd.SemanticChecker;
import MiddleEnd.IRModule;
import MiddleEnd.Infrastructure.IRBuilder;
import Utils.GlobalScope;
import Utils.MxErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import PrintBuiltin.PrintBuiltin;
import Parser.MxParser;
import Parser.MxLexer;
import java.io.*;
import java.util.Objects;

public class Compiler {
    public static void main(String[] args) throws Exception{
//			CharStream input = CharStreams.fromFileName("E:\\Compiler-Design\\src\\test.mx");//新建一个CharStream读取数据
//        CharStream input = CharStreams.fromStream(System.in); // 从stdin读取数据
//			PrintStream ps=new PrintStream("E:\\Compiler-Design\\debug\\test.ll");
//        PrintStream ps=System.out;
//			System.out.println(input);
//			System.out.println("-----------");

//        String name = "try.mx";
//        InputStream input = new FileInputStream(name);
        InputStream input = System.in;
        PrintStream os = System.out;
//        os.println("wssb\n");

        try {
            MxLexer lexer = new MxLexer(CharStreams.fromStream(input));
            lexer.removeErrorListeners();
            lexer.addErrorListener(new MxErrorListener());
            MxParser parser = new MxParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(new MxErrorListener());
            //从program（root of the CST）开始解析,
            ParseTree parseTreeRoot = parser.program();

            ASTBuilder test = new ASTBuilder();
            RootNode rt = (RootNode) test.visit(parseTreeRoot);

            GlobalScope gScope = new GlobalScope(null);
            BuiltInInitiator initialer = new BuiltInInitiator();
            gScope = initialer.init(gScope);
            PreProcessor preprocess = new PreProcessor(gScope);
            preprocess.visit(rt);

            SemanticChecker semanticCheck = new SemanticChecker(gScope);
            semanticCheck.visit(rt);

            IRModule module = new IRModule();
            IRBuilder irb = new IRBuilder(module, gScope);
            irb.visit(rt);
            irb.processGlobalInit();
//            System.out.println("visit OK here");
//            os.println(module);

            // ASM
            ASMBuilder asmB = new ASMBuilder();
            asmB.visit(module);
//            os.println(asmB.output.printASM());
            EasyStack regAlloc = new EasyStack(asmB.output);
            regAlloc.process();
            os.println(regAlloc.ripe.printASM());
//            GraphColor regAlloc = new GraphColor(asmB.output);
//            os.println(regAlloc.ripe.printASM());


            os=new PrintStream("builtin.s");
            os.println(new PrintBuiltin().str);

        } catch (RuntimeException er) {
            System.err.println(er.getMessage());
            throw new RuntimeException();
        }
    }
}