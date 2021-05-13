/*
 *	Name:		Attributes.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.lexer;

import java.util.HashMap;

public class Attributes{
    public static final HashMap<String, Token.Type> keywords = new HashMap<>();
    public static final HashMap<Character, Token.Type> singleOp = new HashMap<>();
    public static final HashMap<String, Token.Type> doubleOp = new HashMap<>();

    static{
        keywords.put("if", Token.Type.IF);
        keywords.put("else", Token.Type.ELSE);
        keywords.put("while", Token.Type.WHILE);
        keywords.put("return", Token.Type.RETURN);
        keywords.put("num", Token.Type.NUMERIC);
        keywords.put("date", Token.Type.DATE);
        keywords.put("time", Token.Type.TIME);
        keywords.put("void", Token.Type.VOID);
        keywords.put("print", Token.Type.PRINT);
    }

    static{
        singleOp.put('(', Token.Type.LPAREN);
        singleOp.put(')', Token.Type.RPAREN);
        singleOp.put('{', Token.Type.LCPAREN);
        singleOp.put('}', Token.Type.RCPAREN);
        singleOp.put('+', Token.Type.PLUS);
        singleOp.put('-', Token.Type.MINUS);
        singleOp.put('*', Token.Type.MULTIPLY);
        singleOp.put('/', Token.Type.DIVIDE);
        singleOp.put('=', Token.Type.ASSIGNMENT);
        singleOp.put('>', Token.Type.GREATER_THAN);
        singleOp.put('<', Token.Type.LESS_THAN);
        singleOp.put(',', Token.Type.COMMA);
        singleOp.put(';', Token.Type.SEMICOLON);
        singleOp.put(':', Token.Type.COLON);
        singleOp.put('.', Token.Type.DOT);
        singleOp.put('!', Token.Type.NOT);
    }

    static{
        doubleOp.put("&&", Token.Type.AND);
        doubleOp.put("||", Token.Type.OR);
        doubleOp.put("==", Token.Type.EQUAL);
        doubleOp.put("!=", Token.Type.NOT_EQUAL);
        doubleOp.put(">=", Token.Type.GREATER_OR_EQUAL);
        doubleOp.put("<=", Token.Type.LESS_OR_EQUAL);
    }
}
