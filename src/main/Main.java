package main;

import main.errors.Errors;
import main.interpreter.Interpreter;
import main.lexer.Lexer;
import main.parser.Parser;
import main.source.Source;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, Errors.TokenError, Errors.InterpreterError {
        if(args.length != 1){
            System.out.println("Invalid number of input args.");
        }
        else{
            Source source = new Source("./resources/interpreterTest/" + args[0]);
            Lexer lexer = new Lexer(source);
            Parser parser = new Parser(lexer);
            Interpreter interpreter = new Interpreter(parser);
            interpreter.interpret();
        }
    }
}
