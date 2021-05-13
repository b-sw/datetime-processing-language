/*
 *	Name:		TokenError.java
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

    public static class SyntaxError extends TokenError{
        public SyntaxError(int line, int column, ErrorMessages.ParserSyntaxError errorType){
            super("[" + line + ":" + column + "] Syntax error.");
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

    public static class DateInvalid extends TokenError{
        public DateInvalid(int line, int column) { super("[" + line + ":" + column + "] Date invalid."); }
    }

    public static class NumberInvalid extends TokenError{
        public NumberInvalid(int line, int column) { super("[" + line + ":" + column + "] Number invalid."); }
    }

}
