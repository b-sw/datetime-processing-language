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

import main.lexer.Token;

import java.util.HashMap;

public class ErrorMessages {

    public static final HashMap<ParserSyntaxError, String> strings = new HashMap<>();

    static {
        strings.put(ParserSyntaxError.SYNTAX_ERROR, " Syntax error.");
        strings.put(ParserSyntaxError.FUNCTION_DEF, " Function definition syntax error.");
        strings.put(ParserSyntaxError.PARAMETERS, " Parameters syntax error.");
        strings.put(ParserSyntaxError.ARGUMENTS, " Arguments syntax error.");
        strings.put(ParserSyntaxError.BLOCK, " Block syntax error.");
        strings.put(ParserSyntaxError.SIGNATURE, " Signature syntax error.");
        strings.put(ParserSyntaxError.STATEMENT, " Statement syntax error.");
        strings.put(ParserSyntaxError.IF_STATEMENT, " If statement syntax error.");
        strings.put(ParserSyntaxError.WHILE_STATEMENT, " While statement syntax error.");
        strings.put(ParserSyntaxError.ELSE_STATEMENT, " Else statement syntax error.");
        strings.put(ParserSyntaxError.RETURN_STATEMENT, " Return statement syntax error.");
        strings.put(ParserSyntaxError.INIT_STATEMENT, " Init statement syntax error.");
        strings.put(ParserSyntaxError.PRINT_STATEMENT, " Print statement syntax error.");
        strings.put(ParserSyntaxError.ASSIGN_STATEMENT, " Assign statement syntax error.");
        strings.put(ParserSyntaxError.OR_COND, " OrCond syntax error.");
        strings.put(ParserSyntaxError.AND_COND, " AndCond syntax error.");
        strings.put(ParserSyntaxError.EQUAL_COND, " EqualCond syntax error.");
        strings.put(ParserSyntaxError.RELATION_COND, " RelationCond syntax error.");
        strings.put(ParserSyntaxError.PRIMARY_COND, " PrimaryCond syntax error.");
        strings.put(ParserSyntaxError.PARENTH_COND, " ParenthCond syntax error.");
        strings.put(ParserSyntaxError.PARENTH_EXPR, " ParenthExpression syntax error.");
        strings.put(ParserSyntaxError.EXPRESSION, " Expression syntax error.");
        strings.put(ParserSyntaxError.TERM, " Term syntax error.");

    }

    public enum ParserSyntaxError{
        SYNTAX_ERROR,
        FUNCTION_DEF,
        PARAMETERS,
        ARGUMENTS,
        BLOCK,
        SIGNATURE,
        STATEMENT,
        IF_STATEMENT,
        WHILE_STATEMENT,
        ELSE_STATEMENT,
        RETURN_STATEMENT,
        INIT_STATEMENT,
        PRINT_STATEMENT,
        ASSIGN_STATEMENT,
        OR_COND,
        AND_COND,
        EQUAL_COND,
        RELATION_COND,
        PRIMARY_COND,
        PARENTH_COND,
        PARENTH_EXPR,
        EXPRESSION,
        TERM
    }
}
