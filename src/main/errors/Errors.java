/*
 *	Name:		Errors.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.errors;

public final class Errors {
    private Errors() {}

    public static class TokenError extends Exception{
        public TokenError(String message){
            super((message));
        }
    }

    public static class InterpreterError extends Exception{
        public InterpreterError(String message) { super((message)); }
    }

    public static class SyntaxError extends TokenError{
        public SyntaxError(int line, int column, ErrorMessages.ParserSyntaxError errorType){
            super("[" + line + ":" + column + "]" + ErrorMessages.strings.get(errorType));
        }
    }

    public static class TokenInvalid extends TokenError{
        public TokenInvalid(int line, int column){
            super("[" + line + ":" + column + "] Invalid token.");
        }
    }

    public static class TokenTooLong extends TokenError{
        public TokenTooLong(int line, int column){
            super("[" + line + ":" + column + "] Token too long.");
        }
    }

    public static class StringTooLong extends TokenError{
        public StringTooLong(int line, int column){
            super("[" + line + ":" + column + "] String too long.");
        }
    }

    public static class StringUnclosed extends TokenError{
        public StringUnclosed(int line, int column){
            super("[" + line + ":" + column + "] String unclosed.");
        }
    }

    public static class DateInvalid extends TokenError{
        public DateInvalid(int line, int column) { super("[" + line + ":" + column + "] Date invalid."); }
    }

    public static class NumberInvalid extends TokenError{
        public NumberInvalid(int line, int column) { super("[" + line + ":" + column + "] Number invalid."); }
    }

    public static class UndeclaredVariable extends InterpreterError{
        public UndeclaredVariable(String name){
            super("Variable " + name + " was not declared.");
        }
    }

    public static class UndeclaredFunction extends InterpreterError{
        public UndeclaredFunction(String name){
            super("Function " + name + " was not declared.");
        }
    }

    public static class OverwriteError extends InterpreterError{
        public OverwriteError(String name){
            super("Variable " + name + " is already declared.");
        }
    }

    public static class IncompatibleTypesError extends InterpreterError{
        public IncompatibleTypesError(String name){
            super("Attempt to change variable " + name + " type.");
        }
    }

    public static class MainNotDeclaredError extends InterpreterError{
        public MainNotDeclaredError(){
            super("Main not declared.");
        }
    }

    public static class NoParentContextError extends InterpreterError{
        public NoParentContextError(String name){ super("No parent context for context: " + name + "."); }
    }

    public static class InvalidVariableType extends InterpreterError{
        public InvalidVariableType(String name){ super(name + " is an invalid variable type."); }
    }

    public static class InvalidTypeCompare extends InterpreterError{
        public InvalidTypeCompare(String type){ super(type + " cannot be compared."); }
    }

    public static class InvalidTypesCompare extends InterpreterError{
        public InvalidTypesCompare(String type1, String type2){ super(type1 + " cannot be compared to " + type2); }
    }

    public static class IllicitOperation extends InterpreterError{
        public IllicitOperation(){ super("Illicit operation."); }
    }

    public static class DivisionZero extends InterpreterError{
        public DivisionZero(){ super("Division by zero."); }
    }

    public static class TooBigTimeDuration extends InterpreterError{
        public TooBigTimeDuration(){ super("Time duration is bigger than 99:99:99."); }
    }
}
