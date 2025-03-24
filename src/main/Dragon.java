package main;

import ast.Ast;
import cfg.lab1.Translate;
import cfg.Cfg;
import frontend.ASTTranslator;
import frontend.AntlrTreePrinter;
import frontend.SemanticChecker;
import org.antlr.v4.runtime.*;
import frontend.minic.MiniCLexer;
import frontend.minic.MiniCParser;

import java.nio.file.Paths;

public class Dragon {
    public static void main(String[] args) throws Exception {
        CharStream input = CharStreams.fromFileName(args[0]);
        String currentWorkingDirectory = Paths.get("").toAbsolutePath().toString();
        System.out.println("Current working directory: " + currentWorkingDirectory);
        MiniCLexer lexer = new MiniCLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniCParser parser = new MiniCParser(tokens);
        MiniCParser.ProgContext parseTree = parser.prog();

        AntlrTreePrinter printer = new AntlrTreePrinter();
        printer.print(parseTree);

        SemanticChecker checker = new SemanticChecker();
        if (!checker.check(parseTree)) {
            throw new Exception("semantic check failed");
        }

        ASTTranslator astTranslator = new ASTTranslator();
        Ast.Program.T ast = astTranslator.translate(parseTree);

        Translate translate = new Translate();
        Cfg.Program.T cfg = translate.doitProgram(ast);

        Cfg.Program.pp(cfg);
    }

}
