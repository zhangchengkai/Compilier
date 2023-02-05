import AST.ASTBuilder;
import AST.RootNode;
import BackEnd.Infrastructure.ASMBuilder;
import BackEnd.RegAllocation.GraphColor;
import BackEnd.RegAllocation.EasyStack;
import BackEnd.Optimization.Mem2Reg;
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
//        String name = "try.mx";
//        InputStream input = new FileInputStream(name);
        InputStream input = System.in;
//        PrintStream os = System.out;
        PrintStream os = new PrintStream("output.s");
//        PrintStream os=new PrintStream("C:\\Users\\Z\\Desktop\\compilier\\compilier\\debug\\test.s");
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
//            os.println("---------------------------------------");
            IRBuilder irb = new IRBuilder(module, gScope);
            irb.visit(rt);
            irb.processGlobalInit();
//            os.println(module);
            Mem2Reg opt1 = new Mem2Reg(module);
//            System.out.println("visit OK here");
//            os.println(module);

            // ASM
            ASMBuilder asmB = new ASMBuilder();
            asmB.visit(module);
//            os.println(asmB.output.printASM());
//            EasyStack regAlloc = new EasyStack(asmB.output);
//            regAlloc.process();
//            os.println(regAlloc.ripe.printASM());
            GraphColor regAlloc = new GraphColor(asmB.output);
            os.println(regAlloc.ripe.printASM());


            os=new PrintStream("builtin.s");
            os.println(new PrintBuiltin().str);

        } catch (RuntimeException er) {
            System.err.println(er.getMessage());
            throw new RuntimeException();
        }
    }
}