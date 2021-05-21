/*
 *	Name:		Attributes.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.grammar.operators;

import main.lexer.Token;

import java.util.HashMap;

public class Attributes{
    public static final HashMap<Token.Type, AddOperator> addOperators = new HashMap<>();
    public static final HashMap<Token.Type, Type> functionTypes = new HashMap<>();
    public static final HashMap<Token.Type, MultOperator> multOperators = new HashMap<>();
    public static final HashMap<Token.Type, RelationOperator> relationOperators = new HashMap<>();
    public static final HashMap<Token.Type, EqualOperator> equalOperators = new HashMap<>();

    static{
        addOperators.put(Token.Type.PLUS, AddOperator.ADD);
        addOperators.put(Token.Type.MINUS, AddOperator.SUB);
    }

    static{
        functionTypes.put(Token.Type.VOID, Type.VOID);
        functionTypes.put(Token.Type.NUMERIC, Type.NUM);
        functionTypes.put(Token.Type.DATE, Type.DATE);
        functionTypes.put(Token.Type.TIME, Type.TIME);
    }

    static{
        multOperators.put(Token.Type.MULTIPLY, MultOperator.MUL);
        multOperators.put(Token.Type.DIVIDE, MultOperator.DIV);
    }

    static{
        relationOperators.put(Token.Type.GREATER_THAN, RelationOperator.GREATER_THAN);
        relationOperators.put(Token.Type.LESS_THAN, RelationOperator.LESS_THAN);
        relationOperators.put(Token.Type.GREATER_OR_EQUAL, RelationOperator.GREATER_OR_EQUAL);
        relationOperators.put(Token.Type.LESS_OR_EQUAL, RelationOperator.LESS_OR_EQUAL);
    }

    static{
        equalOperators.put(Token.Type.EQUAL, EqualOperator.EQUAL);
        equalOperators.put(Token.Type.NOT_EQUAL, EqualOperator.NOT_EQUAL);
    }
}
