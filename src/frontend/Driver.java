package frontend;

import ast.Ast;
import cfg.Cfg;
import cfg.lab1.Translate;
import frontend.minic.MiniCLexer;
import frontend.minic.MiniCParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;


public class Driver {
    private final String filePath;

    public Driver(String filePath) {
        this.filePath = filePath;
    }

    public Cfg.Program.T getControlFlowGraph() throws Exception {
        CharStream input = CharStreams.fromFileName(filePath);
        MiniCLexer lexer = new MiniCLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniCParser parser = new MiniCParser(tokens);
        MiniCParser.ProgContext parseTree = parser.prog();

        SemanticChecker checker = new SemanticChecker();
        if (!checker.check(parseTree)) {
            throw new Exception("semantic check failed");
        }

        ASTTranslator astTranslator = new ASTTranslator();
        Ast.Program.T ast = astTranslator.translate(parseTree);

        Translate translate = new Translate();
        return translate.doitProgram(ast);
    }
}
