/*
 *	Name:		ParserTest.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package parser;

import main.errors.Errors;
import main.lexer.Lexer;
import main.grammar.*;
import main.parser.*;
import main.grammar.operators.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @AfterAll
    static void cleanUp(){
        System.out.println("Parser: Execution of all JUNIT tests done.");
    }

    @Test   // signature, "(", parameters, ")", block ;
    void parseFunctionDef() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                num sum(num a, num b) {
                    num output = a + b;
                    if(output > 0) {
                        print ("sum is greater than 0");
                    }
                    return output;
                }"""));

        FunctionDef functionDef = parser.parseFunctionDef();

        assertNotNull(functionDef);
        assertEquals(Type.NUM, functionDef.getSignature().getType());
        assertEquals("sum", functionDef.getSignature().getId());

        assertEquals(Type.NUM, functionDef.getParameters().getSignatures()[0].getType());
        assertEquals("a", functionDef.getParameters().getSignatures()[0].getId());

        assertEquals(Type.NUM, functionDef.getParameters().getSignatures()[1].getType());
        assertEquals("b", functionDef.getParameters().getSignatures()[1].getId());

        assertEquals("output", functionDef.getBlock().getStatements()[0].getSignature().getId());

        assertEquals("a", functionDef.getBlock().getStatements()[0].getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.ADD, functionDef.getBlock().getStatements()[0].getExpression().getAddOperators()[0]);

        assertEquals("b", functionDef.getBlock().getStatements()[0].getExpression().getTerms()[1].getFactors()[0].getId());

        assertTrue(functionDef.getBlock().getStatements()[1] instanceof IfStatement);
        assertEquals("output", functionDef.getBlock().getStatements()[1].getOrCond().getAndConds()[0]
                .getEqualConds()[0].getRelationCond1().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());

        assertEquals(RelationOperator.GREATER_THAN,functionDef.getBlock().getStatements()[1].getOrCond().
                getAndConds()[0].getEqualConds()[0].getRelationCond1().getRelationOp());

        assertEquals(0, functionDef.getBlock().getStatements()[1].getOrCond().getAndConds()[0].getEqualConds()[0]
        .getRelationCond1().getPrimaryCond2().getExpression().getTerms()[0].getFactors()[0].getNum());

        assertTrue(functionDef.getBlock().getStatements()[1].getIfBlock().getStatements()[0] instanceof PrintStatement);
        assertEquals("\"sum is greater than 0\"", functionDef.getBlock().getStatements()[1].getIfBlock().getStatements()[0].getPrintables()[0].getStr());

        assertTrue(functionDef.getBlock().getStatements()[2] instanceof ReturnStatement);
        assertEquals("output", functionDef.getBlock().getStatements()[2].getExpression().getTerms()[0].getFactors()[0].getId());
    }

    @Test   // { functionDef } ;
    void parseProgram() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                void main() {
                    num retVal = add(x, y);
                    print(result);
                }
                num sum(num a, num b) {return a + b;}"""));

        parser.parseProgram();
        assertNotNull(parser.getProgram());
        assertEquals(2, parser.getProgram().getFunctionDefs().length);

        assertEquals(Type.VOID, parser.getProgram().getFunctionDefs()[0].getSignature().getType());
        assertEquals("main", parser.getProgram().getFunctionDefs()[0].getSignature().getId());

        assertEquals(Type.NUM, parser.getProgram().getFunctionDefs()[1].getSignature().getType());
        assertEquals("sum", parser.getProgram().getFunctionDefs()[1].getSignature().getId());
    }

    @Test   // type, id ;
    void parseSignature() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("num variable"));
        Signature signature = parser.parseSignature();

        assertEquals(Type.NUM, signature.getType());
        assertEquals("variable", signature.getId());
    }

    @Test   // type, id ;
    void parseSignatureWithoutType() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("variable"));
        assertNull(parser.parseSignature());
    }

    @Test   // [ signature, { ",", signature } ] ;
    void parseParameters() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("num a, date b, time c"));
        Parameters parameters = parser.parseParameters();

        assertEquals(Type.NUM, parameters.getSignatures()[0].getType());
        assertEquals("a", parameters.getSignatures()[0].getId());
        assertEquals(Type.DATE, parameters.getSignatures()[1].getType());
        assertEquals("b", parameters.getSignatures()[1].getId());
        assertEquals(Type.TIME, parameters.getSignatures()[2].getType());
        assertEquals("c", parameters.getSignatures()[2].getId());
    }

    @Test   // [ signature, { ",", signature } ] ;
    void parseParametersIncorrect() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("num a, b"));

        assertThrows(Errors.SyntaxError.class, parser::parseParameters);
    }

    @Test   // [ signature, { ",", signature } ] ;
    void parseParametersEmpty() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer(" "));
        Parameters parameters = parser.parseParameters();

        assertEquals(0, parameters.getSignatures().length);
    }

    @Test   // signature, "(", parameters, ")", block;
    void parseFunctionDefEmptyParameters() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                void emptyTest() {
                    print("Hello world!");
                }
                """));
        FunctionDef functionDef = parser.parseFunctionDef();

        assertNotNull(functionDef);
        assertEquals(Type.VOID, functionDef.getSignature().getType());
        assertEquals("emptyTest",functionDef.getSignature().getId());
        assertEquals(0, functionDef.getParameters().getSignatures().length);
    }

    @Test   // [ expression { ",", expression } ] ;
    void parseArguments() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                a - b, c / d, e
                """));
        Arguments arguments = parser.parseArguments();

        assertNotNull(arguments);
        assertEquals("a", arguments.getExpressions()[0].getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.SUB, arguments.getExpressions()[0].getAddOperators()[0]);
        assertEquals("b", arguments.getExpressions()[0].getTerms()[1].getFactors()[0].getId());
        
        assertEquals("c", arguments.getExpressions()[1].getTerms()[0].getFactors()[0].getId());
        assertEquals(MultOperator.DIV, arguments.getExpressions()[1].getTerms()[0].getMultOperators()[0]);
        assertEquals("d", arguments.getExpressions()[1].getTerms()[0].getFactors()[1].getId());

        assertEquals("e", arguments.getExpressions()[2].getTerms()[0].getFactors()[0].getId());
    }

    @Test   // [ expression { ",", expression } ] ;
    void parseArguments2() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                a - b, c / d, calculate(e, f)
                """));
        Arguments arguments = parser.parseArguments();

        assertNotNull(arguments);
        assertEquals("a", arguments.getExpressions()[0].getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.SUB, arguments.getExpressions()[0].getAddOperators()[0]);
        assertEquals("b", arguments.getExpressions()[0].getTerms()[1].getFactors()[0].getId());

        assertEquals("c", arguments.getExpressions()[1].getTerms()[0].getFactors()[0].getId());
        assertEquals(MultOperator.DIV, arguments.getExpressions()[1].getTerms()[0].getMultOperators()[0]);
        assertEquals("d", arguments.getExpressions()[1].getTerms()[0].getFactors()[1].getId());

        assertNotNull(arguments.getExpressions()[2].getTerms()[0].getFactors()[0].getFunctionCall());
        assertEquals("calculate", arguments.getExpressions()[2].getTerms()[0].getFactors()[0].getFunctionCall().getId());
        assertEquals("e", arguments.getExpressions()[2].getTerms()[0].getFactors()[0].getFunctionCall().getArguments().getExpressions()[0]
                                    .getTerms()[0].getFactors()[0].getId());
        assertEquals("f", arguments.getExpressions()[2].getTerms()[0].getFactors()[0].getFunctionCall().getArguments().getExpressions()[1]
                .getTerms()[0].getFactors()[0].getId());
    }

    @Test   // [ expression { ",", expression } ] ;
    void parseEmptyArguments() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                """));
        Arguments arguments = parser.parseArguments();
        assertEquals(0, arguments.getExpressions().length);
    }

    @Test   // "{", { statement }, "}" ;
    void parseBlock() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                {
                    date1 = date2;
                    a = b + c;
                    return a;
                }
                """));
        Block block = parser.parseBlock();

        assertNotNull(block);
        assertEquals("date1", block.getStatements()[0].getId());
        assertEquals("date2", block.getStatements()[0].getExpression().getTerms()[0].getFactors()[0].getId());

        assertEquals("a", block.getStatements()[1].getId());
        assertTrue(block.getStatements()[1] instanceof AssignStatement);
        assertEquals("b", block.getStatements()[1].getExpression().getTerms()[0].getFactors()[0].getId());

        assertTrue(block.getStatements()[2] instanceof ReturnStatement);
        assertEquals("a", block.getStatements()[2].getExpression().getTerms()[0].getFactors()[0].getId());
    }

    @Test   // block | ifStatement | whileStatement | printStatement | returnStatement | initStatement | assignStatement | ( functionCall, ";" ) ;
    void parseStatement() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                if(time1 > time2) {
                    x_i = x_i + 1;
                }
                """));
        Statement statement = parser.parseStatement();
        assertNotNull(statement);
        assertTrue(statement instanceof IfStatement);
    }

    @Test   // block | ifStatement | whileStatement | printStatement | returnStatement | initStatement | assignStatement | ( functionCall, ";" ) ;
    void parseStatement1() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                while(x > 0) {
                    print(x);
                    x = x - 1;
                }
                """));
        Statement statement = parser.parseStatement();
        assertNotNull(statement);
        assertTrue(statement instanceof WhileStatement);
    }

    @Test   // block | ifStatement | whileStatement | printStatement | returnStatement | initStatement | assignStatement | ( functionCall, ";" ) ;
    void parseStatement2() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                return retVal;
                """));
        Statement statement = parser.parseStatement();
        assertNotNull(statement);
        assertTrue(statement instanceof ReturnStatement);
    }

    @Test   // block | ifStatement | whileStatement | printStatement | returnStatement | initStatement | assignStatement | ( functionCall, ";" ) ;
    void parseStatement3() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                num a = b / c;
                }
                """));
        Statement statement = parser.parseStatement();
        assertNotNull(statement);
        assertTrue(statement instanceof InitStatement);
    }

    @Test   // block | ifStatement | whileStatement | printStatement | returnStatement | initStatement | assignStatement | ( functionCall, ";" ) ;
    void parseStatement4() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                date1 = date2 + time;
                """));
        Statement statement = parser.parseStatement();
        assertNotNull(statement);
        assertTrue(statement instanceof AssignStatement);
    }

    @Test   // block | ifStatement | whileStatement | printStatement | returnStatement | initStatement | assignStatement | ( functionCall, ";" ) ;
    void parseStatement5() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                print("testing");
                }
                """));
        Statement statement = parser.parseStatement();
        assertNotNull(statement);
        assertTrue(statement instanceof PrintStatement);
    }

    @Test   // block | ifStatement | whileStatement | printStatement | returnStatement | initStatement | assignStatement | ( functionCall, ";" ) ;
    void parseStatement6() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                date var;
                """));
        Statement statement = parser.parseStatement();
        assertNotNull(statement);
        assertTrue(statement instanceof InitStatement);
    }

    @Test   // block | ifStatement | whileStatement | printStatement | returnStatement | initStatement | assignStatement | ( functionCall, ";" ) ;
    void parseStatement7() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                divide(a, b);
                """));
        Statement statement = parser.parseStatement();
        assertNotNull(statement);
        assertTrue(statement instanceof FunctionCall);
    }

    @Test   // block | ifStatement | whileStatement | printStatement | returnStatement | initStatement | assignStatement | ( functionCall, ";" ) ;
    void parseStatement8() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                return var
                """));
        assertThrows(Errors.SyntaxError.class, parser::parseStatement);
    }

    @Test // "if", "(", condition, ")", block, ["else", block] ;
    void parseIfStatement() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                if(var != 0){
                    var = var / 2;
                }
                """));
        IfStatement ifStatement = parser.parseIfStatement();

        assertNotNull(ifStatement);
        assertEquals("var", ifStatement.getOrCond().getAndConds()[0].getEqualConds()[0].getRelationCond1().getPrimaryCond1().getExpression()
                                    .getTerms()[0].getFactors()[0].getId());
        assertEquals(EqualOperator.NOT_EQUAL, ifStatement.getOrCond().getAndConds()[0].getEqualConds()[0].getEqualOp());
        assertEquals(0, ifStatement.getOrCond().getAndConds()[0].getEqualConds()[0].getRelationCond2().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getNum());

        assertEquals("var", ifStatement.getIfBlock().getStatements()[0].getId());
        assertTrue(ifStatement.getIfBlock().getStatements()[0] instanceof AssignStatement);
        assertEquals("var", ifStatement.getIfBlock().getStatements()[0].getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(MultOperator.DIV, ifStatement.getIfBlock().getStatements()[0].getExpression().getTerms()[0].getMultOperators()[0]);
        assertEquals(2, ifStatement.getIfBlock().getStatements()[0].getExpression().getTerms()[0].getFactors()[1].getNum());
    }

    @Test   // "if", "(", condition, ")", block, ["else", block] ;
    void parseElseIf() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                if(output > 0) {
                    print ("sum is greater than 0");
                }
                else if(output < 0) {
                    print ("sum is less than 0");
                }
                else print("sum is equal to 0");
                """));

        IfStatement ifStatement = parser.parseIfStatement();

        assertNotNull(ifStatement);

        assertEquals("output", ifStatement.getOrCond().getAndConds()[0].getEqualConds()[0].getRelationCond1().getPrimaryCond1()
                .getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(RelationOperator.GREATER_THAN, ifStatement.getOrCond().getAndConds()[0].getEqualConds()[0].getRelationCond1().getRelationOp());
        assertEquals(0, ifStatement.getOrCond().getAndConds()[0].getEqualConds()[0].getRelationCond1().getPrimaryCond2()
                .getExpression().getTerms()[0].getFactors()[0].getNum());

        assertTrue(ifStatement.getIfBlock().getStatements()[0] instanceof PrintStatement);
        assertEquals("\"sum is greater than 0\"", ifStatement.getIfBlock().getStatements()[0].getPrintables()[0].getStr());

        assertNotNull(ifStatement.getElseStatement());
        assertEquals("output", ifStatement.getElseStatement().getOrCond().getAndConds()[0].getEqualConds()[0].getRelationCond1().getPrimaryCond1()
                .getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(RelationOperator.LESS_THAN, ifStatement.getElseStatement().getOrCond().getAndConds()[0].getEqualConds()[0].getRelationCond1().getRelationOp());
        assertEquals(0, ifStatement.getElseStatement().getOrCond().getAndConds()[0].getEqualConds()[0].getRelationCond1().getPrimaryCond2()
                .getExpression().getTerms()[0].getFactors()[0].getNum());

        assertTrue(ifStatement.getElseStatement().getIfBlock().getStatements()[0] instanceof PrintStatement);
        assertEquals("\"sum is less than 0\"", ifStatement.getElseStatement().getIfBlock().getStatements()[0].getPrintables()[0].getStr());

        assertNotNull(ifStatement.getElseStatement().getElseStatement());
        assertTrue(ifStatement.getElseStatement().getElseStatement() instanceof PrintStatement);
        assertEquals("\"sum is equal to 0\"", ifStatement.getElseStatement().getElseStatement().getPrintables()[0].getStr());
    }

    @Test   // "if", "(", condition, ")", block, ["else", block] ;
    void parseIfStatementEmptyElse() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                if(a > b){
                    print("a");
                }
                else;
                """));
        assertThrows(Errors.SyntaxError.class, parser::parseStatement);
    }

    @Test   // "while", "(", "orCondition", ")", block ;
    void parseWhileStatement() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                while(a >= b){
                    b = b + 1;
                }
                """));
        WhileStatement whileStatement = parser.parseWhileStatement();

        assertNotNull(whileStatement);
        assertEquals("a", whileStatement.getOrCond().getAndConds()[0].getEqualConds()[0].getRelationCond1().getPrimaryCond1().getExpression()
                                    .getTerms()[0].getFactors()[0].getId());
        assertEquals(RelationOperator.GREATER_OR_EQUAL, whileStatement.getOrCond().getAndConds()[0].getEqualConds()[0].getRelationCond1().getRelationOp());
        assertEquals("b", whileStatement.getOrCond().getAndConds()[0].getEqualConds()[0].getRelationCond1().getPrimaryCond2().getExpression()
                .getTerms()[0].getFactors()[0].getId());

        assertTrue(whileStatement.getBlock().getStatements()[0] instanceof AssignStatement);
        assertEquals("b", whileStatement.getBlock().getStatements()[0].getId());
        assertEquals("b", whileStatement.getBlock().getStatements()[0].getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.ADD, whileStatement.getBlock().getStatements()[0].getExpression().getAddOperators()[0]);
        assertEquals(1, whileStatement.getBlock().getStatements()[0].getExpression().getTerms()[1].getFactors()[0].getNum());
    }

    @Test   // "return", expression, ";" ;
    void parseReturnStatement() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                return result;
                """));
        ReturnStatement returnStatement = parser.parseReturnStatement();

        assertNotNull(returnStatement);
        assertEquals("result", returnStatement.getExpression().getTerms()[0].getFactors()[0].getId());
    }

    @Test   // "return", expression, ";" ;
    void parseReturnStatement2() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                return a * b;
                """));
        ReturnStatement returnStatement = parser.parseReturnStatement();

        assertNotNull(returnStatement);
        assertEquals("a", returnStatement.getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(MultOperator.MUL, returnStatement.getExpression().getTerms()[0].getMultOperators()[0]);
        assertEquals("b", returnStatement.getExpression().getTerms()[0].getFactors()[1].getId());
    }

    @Test   // signature, [ assignmentOp, expression ], ";" ;
    void parseInitStatement() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                num x;
                """));
        InitStatement initStatement = parser.parseInitStatement();
        assertNotNull(initStatement);

        assertEquals(Type.NUM, initStatement.getSignature().getType());
        assertEquals("x", initStatement.getSignature().getId());
    }

    @Test   // signature, [ assignmentOp, expression ], ";" ;
    void parseInitStatement1() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                num x = y * z;
                """));
        InitStatement initStatement = parser.parseInitStatement();
        assertNotNull(initStatement);

        assertEquals(Type.NUM, initStatement.getSignature().getType());
        assertEquals("x", initStatement.getSignature().getId());
        assertEquals("y", initStatement.getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(MultOperator.MUL, initStatement.getExpression().getTerms()[0].getMultOperators()[0]);
        assertEquals("z", initStatement.getExpression().getTerms()[0].getFactors()[1].getId());
    }

    @Test   // signature, [ assignmentOp, expression ], ";" ;
    void parseInitStatement2() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                date d = 01.12.2020.00:00:00;
                """));
        InitStatement initStatement = parser.parseInitStatement();
        assertNotNull(initStatement);

        assertEquals(Type.DATE, initStatement.getSignature().getType());
        assertEquals("d", initStatement.getSignature().getId());
        assertEquals("01.12.2020.00:00:00", initStatement.getExpression().getTerms()[0].getFactors()[0].getDate().getDateStr());
    }

    @Test   // signature, [ assignmentOp, expression ], ";" ;
    void parseInitStatement3() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                time t = 11:11:11;
                """));
        InitStatement initStatement = parser.parseInitStatement();
        assertNotNull(initStatement);

        assertEquals(Type.TIME, initStatement.getSignature().getType());
        assertEquals("t", initStatement.getSignature().getId());
        assertEquals("11:11:11", initStatement.getExpression().getTerms()[0].getFactors()[0].getTime().getTimeStr());
    }

    @Test   // "print", "(", printable, { ",", printable }, ")", ";" ;
    void parsePrintStatement() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                print("result: ", a + b, ",");
                """));
        PrintStatement printStatement = parser.parsePrintStatement();
        assertNotNull(printStatement);

        assertEquals("\"result: \"", printStatement.getPrintables()[0].getStr());

        assertEquals("a", printStatement.getPrintables()[1].getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.ADD, printStatement.getPrintables()[1].getExpression().getAddOperators()[0]);
        assertEquals("b", printStatement.getPrintables()[1].getExpression().getTerms()[1].getFactors()[0].getId());

        assertEquals("\",\"", printStatement.getPrintables()[2].getStr());
    }

    @Test   // "print", "(", printable, { ",", printable }, ")", ";" ;
    void parsePrintStatement1() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                print(a + b, " is result.");
                """));
        PrintStatement printStatement = parser.parsePrintStatement();
        assertNotNull(printStatement);

        assertEquals("a", printStatement.getPrintables()[0].getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.ADD, printStatement.getPrintables()[0].getExpression().getAddOperators()[0]);
        assertEquals("b", printStatement.getPrintables()[0].getExpression().getTerms()[1].getFactors()[0].getId());

        assertEquals("\" is result.\"", printStatement.getPrintables()[1].getStr());
    }

    @Test   // "print", "(", printable, { ",", printable }, ")", ";" ;
    void parsePrintStatement2() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                print(a + b, " is", " result.", c);
                """));
        PrintStatement printStatement = parser.parsePrintStatement();
        assertNotNull(printStatement);

        assertEquals("a", printStatement.getPrintables()[0].getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.ADD, printStatement.getPrintables()[0].getExpression().getAddOperators()[0]);
        assertEquals("b", printStatement.getPrintables()[0].getExpression().getTerms()[1].getFactors()[0].getId());

        assertEquals("\" is\"", printStatement.getPrintables()[1].getStr());
        assertEquals("\" result.\"", printStatement.getPrintables()[2].getStr());

        assertEquals("c", printStatement.getPrintables()[3].getExpression().getTerms()[0].getFactors()[0].getId());
    }

    @Test   // "print", "(", printable, { ",", printable }, ")", ";" ;
    void parsePrintStatement3() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                print(a + b, " is ", c, " result.");
                """));
        PrintStatement printStatement = parser.parsePrintStatement();
        assertNotNull(printStatement);

        assertEquals("a", printStatement.getPrintables()[0].getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.ADD, printStatement.getPrintables()[0].getExpression().getAddOperators()[0]);
        assertEquals("b", printStatement.getPrintables()[0].getExpression().getTerms()[1].getFactors()[0].getId());

        assertEquals("\" is \"", printStatement.getPrintables()[1].getStr());

        assertEquals("c", printStatement.getPrintables()[2].getExpression().getTerms()[0].getFactors()[0].getId());

        assertEquals("\" result.\"", printStatement.getPrintables()[3].getStr());
    }

    @Test   // "print", "(", printable, { ",", printable }, ")", ";" ;
    void parsePrintStatementEmpty() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                print();
                """));
        assertThrows(Errors.SyntaxError.class, parser::parsePrintStatement);
    }

    @Test   // id, assignmentOp, expression, ";" ;
    void parseAssignStatement() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                a = b + c;
                """));
        AssignStatement assignStatement = (AssignStatement) parser.parseAssignStatementOrFunctionCall();
        assertNotNull(assignStatement);

        assertEquals("a", assignStatement.getId());
        assertEquals("b", assignStatement.getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.ADD, assignStatement.getExpression().getAddOperators()[0]);
        assertEquals("c", assignStatement.getExpression().getTerms()[1].getFactors()[0].getId());
    }

    @Test   // id, assignmentOp, expression, ";" ;
    void parseAssignStatement1() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                a = 01.01.2021.00:00:00;
                """));
        AssignStatement assignStatement = (AssignStatement) parser.parseAssignStatementOrFunctionCall();
        assertNotNull(assignStatement);

        assertEquals("a", assignStatement.getId());
        assertEquals("01.01.2021.00:00:00", assignStatement.getExpression().getTerms()[0].getFactors()[0].getDate().getDateStr());

        assertFalse(assignStatement.getExpression().getTerms()[0].getFactors()[0].getMinus());
        assertNull(assignStatement.getExpression().getTerms()[0].getFactors()[0].getTime());
        assertNull(assignStatement.getExpression().getTerms()[0].getFactors()[0].getParenthExpression());
        assertNull(assignStatement.getExpression().getTerms()[0].getFactors()[0].getId());
        assertNull(assignStatement.getExpression().getTerms()[0].getFactors()[0].getFunctionCall());
        assertNull(assignStatement.getExpression().getTerms()[0].getFactors()[0].getNum());
    }

    @Test   // id, "(", arguments, ")" ;
    void parseFunctionCall() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                multiply(a - b, c / d);
                """));
        FunctionCall functionCall = (FunctionCall) parser.parseAssignStatementOrFunctionCall();
        assertNotNull(functionCall);

        assertEquals("multiply", functionCall.getId());
        assertEquals("a", functionCall.getArguments().getExpressions()[0].getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.SUB, functionCall.getArguments().getExpressions()[0].getAddOperators()[0]);
        assertEquals("b", functionCall.getArguments().getExpressions()[0].getTerms()[1].getFactors()[0].getId());

        assertEquals("c", functionCall.getArguments().getExpressions()[1].getTerms()[0].getFactors()[0].getId());
        assertEquals(MultOperator.DIV, functionCall.getArguments().getExpressions()[1].getTerms()[0].getMultOperators()[0]);
        assertEquals("d", functionCall.getArguments().getExpressions()[1].getTerms()[0].getFactors()[1].getId());
    }

    @Test   // id, "(", arguments, ")" ;
    void parseFunctionCallEmptyArguments() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                subtract();
                """));
        FunctionCall functionCall = (FunctionCall) parser.parseAssignStatementOrFunctionCall();
        assertNotNull(functionCall);

        assertEquals("subtract", functionCall.getId());
        assertEquals(0, functionCall.getArguments().getExpressions().length);
    }

    @Test   // andCond, { orOp, andCond } ;
    void parseCondition() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                a == b && c != d || e <= f
                """));
        OrCond orCond = parser.parseCondition();
        assertNotNull(orCond);

        assertEquals("a", orCond.getAndConds()[0].getEqualConds()[0].getRelationCond1().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(EqualOperator.EQUAL, orCond.getAndConds()[0].getEqualConds()[0].getEqualOp());
        assertEquals("b", orCond.getAndConds()[0].getEqualConds()[0].getRelationCond2().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());

        assertEquals("c", orCond.getAndConds()[0].getEqualConds()[1].getRelationCond1().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(EqualOperator.NOT_EQUAL, orCond.getAndConds()[0].getEqualConds()[1].getEqualOp());
        assertEquals("d", orCond.getAndConds()[0].getEqualConds()[1].getRelationCond2().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());

        assertEquals("e", orCond.getAndConds()[1].getEqualConds()[0].getRelationCond1().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(RelationOperator.LESS_OR_EQUAL, orCond.getAndConds()[1].getEqualConds()[0].getRelationCond1().getRelationOp());
        assertEquals("f", orCond.getAndConds()[1].getEqualConds()[0].getRelationCond1().getPrimaryCond2().getExpression().getTerms()[0].getFactors()[0].getId());
    }

    @Test   // equalCond, { andOp, equalCond } ;
    void parseAndCond() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                a == b && c != d
                """));
        AndCond andCond = parser.parseAndCond();
        assertNotNull(andCond);

        assertEquals("a", andCond.getEqualConds()[0].getRelationCond1().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(EqualOperator.EQUAL, andCond.getEqualConds()[0].getEqualOp());
        assertEquals("b", andCond.getEqualConds()[0].getRelationCond2().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());

        assertEquals("c", andCond.getEqualConds()[1].getRelationCond1().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(EqualOperator.NOT_EQUAL, andCond.getEqualConds()[1].getEqualOp());
        assertEquals("d", andCond.getEqualConds()[1].getRelationCond2().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
    }

    @Test   // relationCond, [ equalOp, relationCond ] ;
    void parseEqualCond() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                c != d
                """));
        EqualCond equalCond = parser.parseEqualCond();
        assertNotNull(equalCond);

        assertEquals("c", equalCond.getRelationCond1().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(EqualOperator.NOT_EQUAL, equalCond.getEqualOp());
        assertEquals("d", equalCond.getRelationCond2().getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
    }

    @Test   // primaryCond, [ relationOp, primaryCond ] ;
    void parseRelationCond() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                a > b
                """));
        RelationCond relationCond = parser.parseRelationCond();
        assertNotNull(relationCond);

        assertEquals("a", relationCond.getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(RelationOperator.GREATER_THAN, relationCond.getRelationOp());
        assertEquals("b", relationCond.getPrimaryCond2().getExpression().getTerms()[0].getFactors()[0].getId());
    }

    @Test   // primaryCond, [ relationOp, primaryCond ] ;
    void parseRelationCond2() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                a > 0
                """));
        RelationCond relationCond = parser.parseRelationCond();
        assertNotNull(relationCond);

        assertEquals("a", relationCond.getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(RelationOperator.GREATER_THAN, relationCond.getRelationOp());
        assertEquals(0, relationCond.getPrimaryCond2().getExpression().getTerms()[0].getFactors()[0].getNum());
    }

    @Test   // [ negationOp ], ( parenthCond | expression ) ;
    void parsePrimaryCond() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                !a
                """));
        PrimaryCond primaryCond = parser.parsePrimaryCond();
        assertNotNull(primaryCond);

        assertEquals(true, primaryCond.getNegationOp());
        assertEquals("a", primaryCond.getExpression().getTerms()[0].getFactors()[0].getId());
    }

    @Test   // [ negationOp ], ( parenthCond | expression ) ;
    void parsePrimaryCond2() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                !(a > b)
                """));
        PrimaryCond primaryCond = parser.parsePrimaryCond();
        assertNotNull(primaryCond);

        assertEquals(true, primaryCond.getNegationOp());
        assertEquals("a", primaryCond.getParenthCond().getCondition().getAndConds()[0].getEqualConds()[0].getRelationCond1().getPrimaryCond1().getExpression()
                                    .getTerms()[0].getFactors()[0].getId());
        assertEquals("b", primaryCond.getParenthCond().getCondition().getAndConds()[0].getEqualConds()[0].getRelationCond1().getPrimaryCond2().getExpression()
                                    .getTerms()[0].getFactors()[0].getId());
    }

    @Test   // [ negationOp ], ( parenthCond | expression ) ;
    void parseDoubleParenthCond() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                (!(a > b))
                """));
        ParenthCond parenthCond = parser.parseParenthCond();
        assertNotNull(parenthCond);

        assertTrue(parenthCond.getCondition().getAndConds()[0].getEqualConds()[0].getRelationCond1().getPrimaryCond1().getNegationOp());

    }

    @Test   // "(", orCondition, ")" ;
    void parseParenthCond() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                (a == b && c != d || e <= f)
                """));
        ParenthCond parenthCond = parser.parseParenthCond();
        assertNotNull(parenthCond);

        assertEquals("a", parenthCond.getCondition().getAndConds()[0].getEqualConds()[0].getRelationCond1()
                .getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(EqualOperator.EQUAL, parenthCond.getCondition().getAndConds()[0].getEqualConds()[0].getEqualOp());
        assertEquals("b", parenthCond.getCondition().getAndConds()[0].getEqualConds()[0].getRelationCond2()
                .getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());

        assertEquals("c", parenthCond.getCondition().getAndConds()[0].getEqualConds()[1].getRelationCond1()
                .getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(EqualOperator.EQUAL, parenthCond.getCondition().getAndConds()[0].getEqualConds()[0].getEqualOp());
        assertEquals("d", parenthCond.getCondition().getAndConds()[0].getEqualConds()[1].getRelationCond2()
                .getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());

        assertEquals("e", parenthCond.getCondition().getAndConds()[1].getEqualConds()[0].getRelationCond1()
                .getPrimaryCond1().getExpression().getTerms()[0].getFactors()[0].getId());

        assertEquals(RelationOperator.LESS_OR_EQUAL, parenthCond.getCondition().getAndConds()[1].getEqualConds()[0].getRelationCond1().getRelationOp());
        assertEquals("f", parenthCond.getCondition().getAndConds()[1].getEqualConds()[0].getRelationCond1()
                .getPrimaryCond2().getExpression().getTerms()[0].getFactors()[0].getId());
    }

    @Test
    void parseConditionNegated() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                !(d < 01.01.2000.00:00:00)
                """));
        OrCond orCond = parser.parseCondition();
        assertNotNull(orCond);
    }


    @Test   // term, { addOp, term } ;
    void parseExpression() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                a * b + c
                """));
        Expression expression = parser.parseExpression();
        assertNotNull(expression);

        assertEquals("a", expression.getTerms()[0].getFactors()[0].getId());
        assertEquals(MultOperator.MUL, expression.getTerms()[0].getMultOperators()[0]);
        assertEquals("b", expression.getTerms()[0].getFactors()[1].getId());
        assertEquals(AddOperator.ADD, expression.getAddOperators()[0]);
        assertEquals("c", expression.getTerms()[1].getFactors()[0].getId());
    }

    @Test   // term, { addOp, term } ;
    void parseExpressionOnlyNumber() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                1
                """));
        Expression expression = parser.parseExpression();
        assertNotNull(expression);

        assertEquals(1, expression.getTerms()[0].getFactors()[0].getNum());
    }//
    @Test   // term, { addOp, term } ;
    void parseExpressionOnlyZero() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                0
                """));
        Expression expression = parser.parseExpression();
        assertNotNull(expression);

        assertEquals(0, expression.getTerms()[0].getFactors()[0].getNum());
    }

    @Test   // term, { addOp, term } ;
    void parseExpressionAdditiveOperators() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                a + b - c
                """));
        Expression expression = parser.parseExpression();
        assertNotNull(expression);

        assertEquals("a", expression.getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.ADD, expression.getAddOperators()[0]);
        assertEquals("b", expression.getTerms()[1].getFactors()[0].getId());
        assertEquals(AddOperator.SUB, expression.getAddOperators()[1]);
        assertEquals("c", expression.getTerms()[2].getFactors()[0].getId());
    }

    @Test   // "(", expression, ")" ;
    void parseParenthExpression() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                (a + b * c)
                """));
        ParenthExpression parenthExpression = parser.parseParenthExpression();
        assertNotNull(parenthExpression);

        assertEquals("a", parenthExpression.getExpression().getTerms()[0].getFactors()[0].getId());
        assertEquals(AddOperator.ADD, parenthExpression.getExpression().getAddOperators()[0]);
        assertEquals("b", parenthExpression.getExpression().getTerms()[1].getFactors()[0].getId());
        assertEquals(MultOperator.MUL, parenthExpression.getExpression().getTerms()[1].getMultOperators()[0]);
        assertEquals("c", parenthExpression.getExpression().getTerms()[1].getFactors()[1].getId());
    }

    @Test   // factor, { multOp, factor } ;
    void parseTerm() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                x * 10
                """));
        Term term = parser.parseTerm();
        assertNotNull(term);

        assertEquals("x", term.getFactors()[0].getId());
        assertEquals(MultOperator.MUL, term.getMultOperators()[0]);
        assertEquals(10, term.getFactors()[1].getNum());
    }

    @Test   // factor, { multOp, factor } ;
    void parseTermWithMultOperators() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                x * y / z
                """));
        Term term = parser.parseTerm();
        assertNotNull(term);

        assertEquals("x", term.getFactors()[0].getId());
        assertEquals(MultOperator.MUL, term.getMultOperators()[0]);
        assertEquals("y", term.getFactors()[1].getId());
        assertEquals(MultOperator.DIV, term.getMultOperators()[1]);
        assertEquals("z", term.getFactors()[2].getId());
    }

    @Test   // ["-"], ( number | date | time | id | parenthExpr | functionCall ) ;
    void parseFactor() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                -x
                """));
        Factor factor = parser.parseFactor();
        assertNotNull(factor);

        assertTrue(factor.getMinus());
        assertEquals("x", factor.getId());

        assertNull(factor.getNum());
        assertNull(factor.getFunctionCall());
        assertNull(factor.getParenthExpression());
        assertNull(factor.getTime());
        assertNull(factor.getDate());
    }

    @Test   // ["-"], ( number | date | time | id | parenthExpr | functionCall ) ;
    void parseFactor1() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                01.01.2021.15:15:15
                """));
        Factor factor = parser.parseFactor();
        assertNotNull(factor);

        assertEquals("01.01.2021.15:15:15", factor.getDate().getDateStr());

        assertFalse(factor.getMinus());
        assertNull(factor.getId());
        assertNull(factor.getNum());
        assertNull(factor.getFunctionCall());
        assertNull(factor.getParenthExpression());
        assertNull(factor.getTime());
    }

    @Test   // ["-"], ( number | date | time | id | parenthExpr | functionCall ) ;
    void parseFactor2() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                15:15:15
                """));
        Factor factor = parser.parseFactor();
        assertNotNull(factor);

        assertEquals("15:15:15", factor.getTime().getTimeStr());

        assertFalse(factor.getMinus());
        assertNull(factor.getId());
        assertNull(factor.getNum());
        assertNull(factor.getFunctionCall());
        assertNull(factor.getParenthExpression());
        assertNull(factor.getDate());
    }

    @Test   // ["-"], ( number | date | time | id | parenthExpr | functionCall ) ;
    void parseFactor3() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                getDate(d)
                """));
        Factor factor = parser.parseFactor();
        assertNotNull(factor);

        assertEquals("getDate", factor.getFunctionCall().getId());
        assertEquals("d", factor.getFunctionCall().getArguments().getExpressions()[0].getTerms()[0].getFactors()[0].getId());

        assertFalse(factor.getMinus());
        assertNull(factor.getId());
        assertNull(factor.getNum());
        assertNull(factor.getParenthExpression());
        assertNull(factor.getTime());
        assertNull(factor.getDate());
    }

    @Test   // ["-"], ( number | date | time | id | parenthExpr | functionCall ) ;
    void parseFactor4() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                (01.01.2021.00:00:01 - 00:00:01)
                """));
        Factor factor = parser.parseFactor();
        assertNotNull(factor);

        assertEquals("01.01.2021.00:00:01", factor.getParenthExpression().getExpression().getTerms()[0].getFactors()[0].getDate().getDateStr());
        assertNotNull(factor.getParenthExpression().getExpression().getTerms()[0].getFactors()[0].getDate());

        assertEquals(AddOperator.SUB, factor.getParenthExpression().getExpression().getAddOperators()[0]);

        assertEquals("00:00:01", factor.getParenthExpression().getExpression().getTerms()[1].getFactors()[0].getTime().getTimeStr());
        assertNotNull(factor.getParenthExpression().getExpression().getTerms()[1].getFactors()[0].getTime());

        assertFalse(factor.getMinus());
        assertNull(factor.getId());
        assertNull(factor.getNum());
        assertNull(factor.getFunctionCall());
        assertNull(factor.getTime());
        assertNull(factor.getDate());
    }

    @Test   // ["-"], ( number | date | time | id | parenthExpr | functionCall ) ;
    void parseFactor5() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                (01.01.2021.00:00:01 - 01.01.2021.00:00:00)
                """));
        Factor factor = parser.parseFactor();
        assertNotNull(factor);

        assertEquals("01.01.2021.00:00:01", factor.getParenthExpression().getExpression().getTerms()[0].getFactors()[0].getDate().getDateStr());
        assertNotNull(factor.getParenthExpression().getExpression().getTerms()[0].getFactors()[0].getDate());

        assertEquals(AddOperator.SUB, factor.getParenthExpression().getExpression().getAddOperators()[0]);

        assertEquals("01.01.2021.00:00:00", factor.getParenthExpression().getExpression().getTerms()[1].getFactors()[0].getDate().getDateStr());
        assertNotNull(factor.getParenthExpression().getExpression().getTerms()[1].getFactors()[0].getDate());

        assertFalse(factor.getMinus());
        assertNull(factor.getId());
        assertNull(factor.getNum());
        assertNull(factor.getFunctionCall());
        assertNull(factor.getTime());
        assertNull(factor.getDate());
    }

    @Test   // ["-"], ( number | date | time | id | parenthExpr | functionCall ) ;
    void parseFactor6() throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer("""
                (00:00:00 + 12:34:56)
                """));
        Factor factor = parser.parseFactor();
        assertNotNull(factor);

        assertEquals("00:00:00", factor.getParenthExpression().getExpression().getTerms()[0].getFactors()[0].getTime().getTimeStr());
        assertNotNull(factor.getParenthExpression().getExpression().getTerms()[0].getFactors()[0].getTime());

        assertEquals(AddOperator.ADD, factor.getParenthExpression().getExpression().getAddOperators()[0]);

        assertEquals("12:34:56", factor.getParenthExpression().getExpression().getTerms()[1].getFactors()[0].getTime().getTimeStr());
        assertNotNull(factor.getParenthExpression().getExpression().getTerms()[1].getFactors()[0].getTime());

        assertFalse(factor.getMinus());
        assertNull(factor.getId());
        assertNull(factor.getNum());
        assertNull(factor.getFunctionCall());
        assertNull(factor.getTime());
        assertNull(factor.getDate());
    }
}