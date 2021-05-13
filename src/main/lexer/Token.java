/*
 *	Name:		Token.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.lexer;

public class Token{
    private Type type;
    private int line;
    private int column;
    private double value;
    private String str;

    public Token(Type type, int line, int column){
        this.type = type;
        this.line = line;
        this.column = column;
    }
    public Token(Type type, int line, int column, double value){
        this.type = type;
        this.line = line;
        this.column = column;
        this.value = value;
    }

    public Token(Type type, int line, int column, String str){
        this.type = type;
        this.line = line;
        this.column = column;
        this.str = str;
    }

    public enum Type{
        EOT,
        IDENTIFIER,
        NUMERIC,
        VOID,
        STRING,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        LPAREN,
        RPAREN,
        LCPAREN,
        RCPAREN,
        SEMICOLON,
        COLON,
        DOT,
        COMMA,
        QUOTATION_MARK,
        NOT,
        IF,
        ELSE,
        WHILE,
        RETURN,
        LESS_THAN,
        GREATER_THAN,
        LESS_OR_EQUAL,
        GREATER_OR_EQUAL,
        ASSIGNMENT,
        EQUAL,
        NOT_EQUAL,
        AND,
        OR,
        PRINT,
        DATE,
        TIME
    }

    public Type getType()    { return this.type; }
    public int getLine()     { return this.line; }
    public int getColumn()   { return this.column; }
    public double getValue() { return this.value; }
    public String getStr()   { return this.str; }
}