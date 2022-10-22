import AST.ASTBuilder;
import AST.RootNode;
import FrontEnd.BuiltInInitiator;
import FrontEnd.PreProcessor;
import FrontEnd.SemanticChecker;
import Utils.GlobalScope;
import Utils.MxErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import Parser.MxParser;
import Parser.MxLexer;
import java.io.*;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws Exception{
        String name = "try.mx";
//        InputStream input = new FileInputStream(name);
        InputStream input = System.in;
        PrintStream os = System.out;

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

        } catch (RuntimeException er) {
            System.err.println(er.getMessage());
            throw new RuntimeException();
        }
    }
}