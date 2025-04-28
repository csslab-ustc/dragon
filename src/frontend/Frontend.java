package frontend;

import ast.Ast;
import cfg.Cfg;
import cfg.lab1.Translate;
import control.Control;
import frontend.minic.MiniCLexer;
import frontend.minic.MiniCParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import util.Error;

import java.nio.file.Paths;

public class Frontend {

    private Cfg.Program.T doit(String fileName) throws Exception {
        CharStream input = CharStreams.fromFileName(fileName);
        String currentWorkingDirectory = Paths.get("").toAbsolutePath().toString();
//        System.out.println("Current working directory: " + currentWorkingDirectory);
        MiniCLexer lexer = new MiniCLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniCParser parser = new MiniCParser(tokens);
        MiniCParser.ProgContext parseTree = parser.prog();

        AntlrTreePrinter printer = new AntlrTreePrinter();
//        printer.print(parseTree);

        SemanticChecker checker = new SemanticChecker();
        if (!checker.check(parseTree)) {
            throw new Exception("semantic check failed");
        }

        ASTTranslator astTranslator = new ASTTranslator();
        Ast.Program.T ast = astTranslator.translate(parseTree);

        Translate translate = new Translate();
        Cfg.Program.T cfg = translate.doitProgram(ast);
        return cfg;
    }

    private Cfg.Program.T buildCfg0(String fileName) {
        try{
            return doit(fileName);
        }catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    public Cfg.Program.T buildCfg(String fileName) {
       var trace = new Control.Trace<>("frontend.buildCfg",
               this::buildCfg0,
               fileName,
               System.out::print,
               Cfg.Program::pp);
       return trace.doit();
    }

}
