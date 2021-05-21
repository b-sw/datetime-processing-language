package interpreter;

import main.errors.Errors;
import main.grammar.*;
import main.interpreter.Interpreter;
import main.interpreter.scope.Value;
import main.interpreter.scope.ValueType;
import main.lexer.Lexer;
import main.parser.Parser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InterpreterTest {

    @AfterAll
    static void cleanUp(){
        System.out.println("Scope: Execution of all JUNIT tests done.");
    }

    private Interpreter initInterpreter(String str) throws IOException, Errors.TokenError {
        Parser parser = new Parser(new Lexer(str));
        return new Interpreter(parser);
    }

    @ParameterizedTest
    @CsvSource({"2,<,3",
                "3,>,2",
                "2,>=,2",
                "2,<=,2"})
    void visitRelationCondWithNegation(double num1, String relationOp, double num2) throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("a " + relationOp + " b");
        RelationCond relationCond = interpreter.getParser().parseRelationCond();

        this.addNumVar(interpreter, "a", num1);
        this.addNumVar(interpreter, "b", num2);
        interpreter.visit(relationCond);

        assertTrue(interpreter.getScopeManager().getLastResult().getBoolValue());
    }

    @Test
    void visitRelationCondComplex() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("""
                a < b * c
                """);
        RelationCond relationCond = interpreter.getParser().parseRelationCond();

        this.addNumVar(interpreter, "a", 1);
        this.addNumVar(interpreter, "b", 2);
        this.addNumVar(interpreter, "c", 3);
        interpreter.visit(relationCond);

        assertTrue(interpreter.getScopeManager().getLastResult().getBoolValue());
    }

    @ParameterizedTest
    @CsvSource({"2,!=,3",
                "3,==,3"})
    void visitEqualCond(double num1, String relationOp, double num2) throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("a " + relationOp + " b");
        EqualCond equalCond = interpreter.getParser().parseEqualCond();

        this.addNumVar(interpreter, "a", num1);
        this.addNumVar(interpreter, "b", num2);
        interpreter.visit(equalCond);

        assertTrue(interpreter.getScopeManager().getLastResult().getBoolValue());
    }

    @ParameterizedTest
    @CsvSource({"5,==",
                "6,!=",})
    void visitEqualCondWithNumComparison(double num, String relationOp) throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("a " + relationOp + " 5");
        EqualCond equalCond = interpreter.getParser().parseEqualCond();

        this.addNumVar(interpreter, "a", num);
        interpreter.visit(equalCond);

        assertTrue(interpreter.getScopeManager().getLastResult().getBoolValue());
    }

    @ParameterizedTest
    @CsvSource({"2,>,1,6,!=,5",
                "2,<,3,5,==,5",
                "2,<=,2,6,!=,5"})
    void visitAndCond(double num1, String relationOp1, double num2, double num3, String relationOp2, double num4) throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("a " + relationOp1 + " b && c " + relationOp2 + " d");
        AndCond andCond = interpreter.getParser().parseAndCond();

        this.addNumVar(interpreter, "a", num1);
        this.addNumVar(interpreter, "b", num2);
        this.addNumVar(interpreter, "c", num3);
        this.addNumVar(interpreter, "d", num4);

        interpreter.visit(andCond);

        assertTrue(interpreter.getScopeManager().getLastResult().getBoolValue());
    }

    @ParameterizedTest
    @CsvSource({"2,>,1,&&,5,==,5",
                "3,<,2,||,5,!=,4"})
    void visitOrCondTrue(double num1, String relationOp1, double num2, String relationOp2, double num3, String relationOp3, double num4) throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("a " + relationOp1 + " b " + relationOp2 + " c " + relationOp3 + " d");
        OrCond orCond = interpreter.getParser().parseCondition();

        this.addNumVar(interpreter, "a", num1);
        this.addNumVar(interpreter, "b", num2);
        this.addNumVar(interpreter, "c", num3);
        this.addNumVar(interpreter, "d", num4);

        interpreter.visit(orCond);

        assertTrue(interpreter.getScopeManager().getLastResult().getBoolValue());
    }

    @ParameterizedTest
    @CsvSource({"2,>,1,&&,5,!=,5",
                "3,>=,4,||,5,==,4"})
    void visitOrCondFalse(double num1, String relationOp1, double num2, String relationOp2, double num3, String relationOp3, double num4) throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("a " + relationOp1 + " b " + relationOp2 + " c " + relationOp3 + " d");
        OrCond orCond = interpreter.getParser().parseCondition();

        this.addNumVar(interpreter, "a", num1);
        this.addNumVar(interpreter, "b", num2);
        this.addNumVar(interpreter, "c", num3);
        this.addNumVar(interpreter, "d", num4);

        interpreter.visit(orCond);

        assertFalse(interpreter.getScopeManager().getLastResult().getBoolValue());
    }

    @ParameterizedTest
    @CsvSource({"2,>,1,&&,2,>,3,||,5,!=,4",
                "2,>,1,&&,3,>,2,||,5,==,4",})
    void visitOrCondComplexTrue(double n1, String ro1, double n2, String ro2, double n3, String ro3, double n4, String ro4, double n5, String ro5, double n6) throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("a" + ro1 + "b" + ro2 + "c" + ro3 + "d" + ro4 + "e" + ro5 + "f");
        OrCond orCond = interpreter.getParser().parseCondition();

        this.addNumVar(interpreter, "a", n1);
        this.addNumVar(interpreter, "b", n2);
        this.addNumVar(interpreter, "c", n3);
        this.addNumVar(interpreter, "d", n4);
        this.addNumVar(interpreter, "e", n5);
        this.addNumVar(interpreter, "f", n6);

        interpreter.visit(orCond);

        assertTrue(interpreter.getScopeManager().getLastResult().getBoolValue());
    }

    @ParameterizedTest
    @CsvSource({"2,>,1,&&,2,>,3,||,5,!=,5",
            "2,<=,1,&&,3,>,2,||,5,==,4",})
    void visitOrCondComplexFalse(double n1, String ro1, double n2, String ro2, double n3, String ro3, double n4, String ro4, double n5, String ro5, double n6) throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("a" + ro1 + "b" + ro2 + "c" + ro3 + "d" + ro4 + "e" + ro5 + "f");
        OrCond orCond = interpreter.getParser().parseCondition();

        this.addNumVar(interpreter, "a", n1);
        this.addNumVar(interpreter, "b", n2);
        this.addNumVar(interpreter, "c", n3);
        this.addNumVar(interpreter, "d", n4);
        this.addNumVar(interpreter, "e", n5);
        this.addNumVar(interpreter, "f", n6);

        interpreter.visit(orCond);

        assertFalse(interpreter.getScopeManager().getLastResult().getBoolValue());
    }

    @ParameterizedTest
    @CsvSource({"2,>,1,&&,2,>,3,||,5,!=,4",
            "2,<=,3,&&,3,>,2,||,5,==,4",})
    void visitOrCondComplexParenthesisTrue(double n1, String ro1, double n2, String ro2, double n3, String ro3, double n4, String ro4, double n5, String ro5, double n6) throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("a" + ro1 + "b" + ro2 + "(c" + ro3 + "d" + ro4 + "e" + ro5 + "f)");
        OrCond orCond = interpreter.getParser().parseCondition();

        this.addNumVar(interpreter, "a", n1);
        this.addNumVar(interpreter, "b", n2);
        this.addNumVar(interpreter, "c", n3);
        this.addNumVar(interpreter, "d", n4);
        this.addNumVar(interpreter, "e", n5);
        this.addNumVar(interpreter, "f", n6);

        interpreter.visit(orCond);

        assertTrue(interpreter.getScopeManager().getLastResult().getBoolValue());
    }

    @ParameterizedTest
    @CsvSource({"2,>,1,&&,2,>,3,||,5,!=,5",
            "2,<=,3,&&,3,>,4,||,5,==,4",})
    void visitOrCondComplexParenthesisFalse(double n1, String ro1, double n2, String ro2, double n3, String ro3, double n4, String ro4, double n5, String ro5, double n6) throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("a" + ro1 + "b" + ro2 + "(c" + ro3 + "d" + ro4 + "e" + ro5 + "f)");
        OrCond orCond = interpreter.getParser().parseCondition();

        this.addNumVar(interpreter, "a", n1);
        this.addNumVar(interpreter, "b", n2);
        this.addNumVar(interpreter, "c", n3);
        this.addNumVar(interpreter, "d", n4);
        this.addNumVar(interpreter, "e", n5);
        this.addNumVar(interpreter, "f", n6);

        interpreter.visit(orCond);

        assertFalse(interpreter.getScopeManager().getLastResult().getBoolValue());
    }

    @Test
    void visitTerm() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("""
                a * b/ c
                """);
        Term term = interpreter.getParser().parseTerm();

        this.addNumVar(interpreter, "a", 3);
        this.addNumVar(interpreter, "b", 4);
        this.addNumVar(interpreter, "c", 2);

        interpreter.visit(term);

        assertEquals(ValueType.NUM, interpreter.getScopeManager().getLastResult().getType());
        assertEquals(6, interpreter.getScopeManager().getLastResult().getDoubleValue());
    }

    @Test
    void visitTermError() throws IOException, Errors.TokenError {
        Interpreter interpreter = this.initInterpreter("""
                a * b/ c
                """);
        Term term = interpreter.getParser().parseTerm();

        this.addNumVar(interpreter, "a", 3);
        this.addNumVar(interpreter, "b", 4);
        this.addNumVar(interpreter, "c", 0);

        assertThrows(Errors.DivisionZero.class, () -> interpreter.visit(term));
    }

    @ParameterizedTest
    @CsvSource({"1,+,2,-,3,0",
                "1,+,2,*,3,7"})
    void visitExpression(double n1, String op1, double n2, String op2, double n3, double result) throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("a" + op1 + "b" + op2 + "c");
        Expression expression = interpreter.getParser().parseExpression();

        this.addNumVar(interpreter, "a", n1);
        this.addNumVar(interpreter, "b", n2);
        this.addNumVar(interpreter, "c", n3);

        interpreter.visit(expression);

        assertEquals(ValueType.NUM, interpreter.getScopeManager().getLastResult().getType());
        assertEquals(result, interpreter.getScopeManager().getLastResult().getDoubleValue());
    }

    @Test
    void visitExpressionParenthesis() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("(a + b) * c");
        Expression expression = interpreter.getParser().parseExpression();

        this.addNumVar(interpreter, "a", 2);
        this.addNumVar(interpreter, "b", 2);
        this.addNumVar(interpreter, "c", 2);

        interpreter.visit(expression);

        assertEquals(ValueType.NUM, interpreter.getScopeManager().getLastResult().getType());
        assertEquals(8, interpreter.getScopeManager().getLastResult().getDoubleValue());
    }

    @Test
    void visitPrintStatement() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("print(\"result: \", 2 + 2);");
        PrintStatement printStatement = interpreter.getParser().parsePrintStatement();
        interpreter.visit(printStatement);

        assertEquals("result: 4.0", interpreter.getScopeManager().getLastResult().getStrValue());
    }

    @Test
    void visitPrintStatementVars() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("print(\"result: \", 2 + 2, \" is correct. \", a - b);");
        PrintStatement printStatement = interpreter.getParser().parsePrintStatement();

        this.addNumVar(interpreter, "a", 2);
        this.addNumVar(interpreter, "b", 1);

        interpreter.visit(printStatement);

        assertEquals("result: 4.0 is correct. 1.0", interpreter.getScopeManager().getLastResult().getStrValue());
    }

    @Test
    void visitInitNum() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("num a = -3.5;");
        InitStatement initStatement = interpreter.getParser().parseInitStatement();
        interpreter.visit(initStatement);

        Value var = interpreter.getScopeManager().getVariable("a");
        assertEquals(ValueType.NUM, var.getType());
        assertTrue(interpreter.getScopeManager().getLocalScope().isDeclared("a"));
        assertEquals(-3.5, var.getDoubleValue());
    }

    @Test
    void visitInitDate() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("date d = 01.01.2021.00:01:59;");
        InitStatement initStatement = interpreter.getParser().parseInitStatement();
        interpreter.visit(initStatement);

        Value var = interpreter.getScopeManager().getVariable("d");
        assertEquals(ValueType.DATE, var.getType());
        assertTrue(interpreter.getScopeManager().getLocalScope().isDeclared("d"));
        assertEquals("01.01.2021.00:01:59", var.getStrValue());
    }

    @Test
    void visitInitTime() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("time t = 00:01:59;");
        InitStatement initStatement = interpreter.getParser().parseInitStatement();
        interpreter.visit(initStatement);

        Value var = interpreter.getScopeManager().getVariable("t");
        assertEquals(ValueType.TIME, var.getType());
        assertTrue(interpreter.getScopeManager().getLocalScope().isDeclared("t"));
        assertEquals("00:01:59", var.getStrValue());
    }

    @Test
    void visitInitTimeNegative() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("time t = -00:01:59;");
        InitStatement initStatement = interpreter.getParser().parseInitStatement();

        interpreter.visit(initStatement);

        Value var = interpreter.getScopeManager().getVariable("t");
        assertEquals(ValueType.TIME, var.getType());
        assertTrue(interpreter.getScopeManager().getLocalScope().isDeclared("t"));
        assertEquals("00:01:59", var.getStrValue());
        assertTrue(var.getBoolValue());
    }

    @Test
    void visitInitError() throws IOException, Errors.TokenError {
        Interpreter interpreter = this.initInterpreter("void v = 1;");
        InitStatement initStatement = interpreter.getParser().parseInitStatement();

        assertThrows(Errors.InvalidVariableType.class, () -> interpreter.visit(initStatement));
    }


    @Test
    void visitInitOverwrite() throws IOException, Errors.TokenError {
        Interpreter interpreter = this.initInterpreter("num a = 5;");
        this.addNumVar(interpreter, "a", 5);

        InitStatement initStatement = interpreter.getParser().parseInitStatement();
        assertThrows(Errors.OverwriteError.class, () -> interpreter.visit(initStatement));
    }

    @Test
    void visitInitAssignVar() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("num a = -b;");
        this.addNumVar(interpreter, "b", 5);

        InitStatement initStatement = interpreter.getParser().parseInitStatement();
        interpreter.visit(initStatement);

        assertTrue(interpreter.getScopeManager().getLocalScope().isDeclared("a"));
        assertTrue(interpreter.getScopeManager().getLocalScope().isDeclared("b"));

        Value varA = interpreter.getScopeManager().getVariable("a");
        Value varB = interpreter.getScopeManager().getVariable("b");

        assertEquals(ValueType.NUM, varA.getType());
        assertEquals(ValueType.NUM, varB.getType());

        assertEquals(-5, varA.getDoubleValue());
        assertEquals(5, varB.getDoubleValue());
    }

    @Test
    void visitInitExpression() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = this.initInterpreter("num result = - (a + b);");
        this.addNumVar(interpreter, "a", 5);
        this.addNumVar(interpreter, "b", 1);

        InitStatement initStatement = interpreter.getParser().parseInitStatement();
        interpreter.visit(initStatement);

        Value var = interpreter.getScopeManager().getLocalScope().getVariable("result");
        assertEquals(ValueType.NUM, var.getType());
        assertTrue(interpreter.getScopeManager().getLocalScope().isDeclared("result"));
        assertEquals(-6, var.getDoubleValue());
    }

    @Test
    void visitAssignDeclared() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("d = 01.01.2021.00:00:00;");
        interpreter.getScopeManager().getLocalScope().addVariable("d", new Value(ValueType.DATE, "01.01.2021.00:00:01"));

        assertTrue(interpreter.getScopeManager().getLocalScope().isDeclared("d"));
        Value var = interpreter.getScopeManager().getLocalScope().getVariable("d");
        assertEquals(ValueType.DATE, var.getType());
        assertEquals("01.01.2021.00:00:01", var.getStrValue());

        AssignStatement assignStatement = (AssignStatement) interpreter.getParser().parseAssignStatementOrFunctionCall();
        assertNotNull(assignStatement);
        interpreter.visit(assignStatement);

        var = interpreter.getScopeManager().getLocalScope().getVariable("d");
        assertEquals("01.01.2021.00:00:00", var.getStrValue());
    }

    @Test
    void visitAssignNotDeclared() throws IOException, Errors.TokenError {
        Interpreter interpreter = initInterpreter("b = 5;");
        AssignStatement assignStatement = (AssignStatement) interpreter.getParser().parseAssignStatementOrFunctionCall();

        assertThrows(Errors.UndeclaredVariable.class, () -> interpreter.visit(assignStatement));
    }

    @Test
    void visitAssignDeclaredWrongType() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("b = 5;");
        interpreter.getScopeManager().addVar("b", new Value(ValueType.TIME, "00:00:00;"));

        assertEquals(ValueType.TIME, interpreter.getScopeManager().getLocalScope().getVariable("b").getType());

        AssignStatement assignStatement = (AssignStatement) interpreter.getParser().parseAssignStatementOrFunctionCall();

        assertThrows(Errors.IncompatibleTypesError.class, () -> interpreter.visit(assignStatement));
    }

    @Test
    void visitAssignUndeclaredError() throws IOException, Errors.TokenError {
        Interpreter interpreter = initInterpreter("b = 5;");
        AssignStatement assignStatement = (AssignStatement) interpreter.getParser().parseAssignStatementOrFunctionCall();

        assertThrows(Errors.UndeclaredVariable.class, () -> interpreter.visit(assignStatement));
    }

    @Test
    void visitIfStatement() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("""
                if(a > b){
                    a = a - 1;
                }
                """);
        this.addNumVar(interpreter, "a", 10);
        this.addNumVar(interpreter, "b", 9);
        IfStatement ifStatement = interpreter.getParser().parseIfStatement();

        interpreter.visit(ifStatement);
        assertEquals(9, interpreter.getScopeManager().getLocalScope().getVariable("a").getDoubleValue());
    }

    @Test
    void visitWhileStatement() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("""
                while(a < 3){
                    b = b + 1;
                    a = a + 1;
                }
                """);
        this.addNumVar(interpreter, "a", 1);
        this.addNumVar(interpreter, "b", 0);

        WhileStatement whileStatement = interpreter.getParser().parseWhileStatement();
        interpreter.visit(whileStatement);

        assertEquals(3, interpreter.getScopeManager().getLocalScope().getVariable("a").getDoubleValue());
        assertEquals(2, interpreter.getScopeManager().getLocalScope().getVariable("b").getDoubleValue());
    }

    @Test
    void visitWhileStatementNegativeCondition() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("""
                while(-a > 3){
                    print(-a);
                    b = b + 1;
                    a = a + 1;
                }
                """);
        this.addNumVar(interpreter, "a", -5);
        this.addNumVar(interpreter, "b", 0);

        WhileStatement whileStatement = interpreter.getParser().parseWhileStatement();
        interpreter.visit(whileStatement);

        assertEquals(-3, interpreter.getScopeManager().getLocalScope().getVariable("a").getDoubleValue());
        assertEquals(2, interpreter.getScopeManager().getLocalScope().getVariable("b").getDoubleValue());
    }

    @Test
    void visitFunctionDef() throws IOException, Errors.TokenError {
        Interpreter interpreter = initInterpreter("""
                void calculate(num a, num b){
                    num result = a + b;
                    print(result);
                }
                """);
        FunctionDef functionDef = interpreter.getParser().parseFunctionDef();
        interpreter.visit(functionDef);

        assertNotNull(interpreter.getScopeManager().getGlobalScope().getFunctionDefs().get("calculate"));
        assertEquals(2, interpreter.getScopeManager().getGlobalScope().getFunctionDefs().get("calculate").getParameters().getSignatures().length);
        assertEquals(2, interpreter.getScopeManager().getGlobalScope().getFunctionDefs().get("calculate").getBlock().getStatements().length);
    }

    @Test
    void visitReturnExpression() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("""
                return a + b;
                """);
        this.addNumVar(interpreter, "a", 1);
        this.addNumVar(interpreter, "b", 2);

        ReturnStatement returnStatement = interpreter.getParser().parseReturnStatement();
        interpreter.visit(returnStatement);

        assertEquals(ValueType.NUM, interpreter.getScopeManager().getLastResult().getType());
        assertEquals(3, interpreter.getScopeManager().getLastResult().getDoubleValue());
    }

    @Test
    void visitReturnVar() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("""
                return result;
                """);
        this.addNumVar(interpreter, "result", 3);
        ReturnStatement returnStatement = interpreter.getParser().parseReturnStatement();
        interpreter.visit(returnStatement);

        assertEquals(ValueType.NUM, interpreter.getScopeManager().getLastResult().getType());
        assertEquals(3, interpreter.getScopeManager().getLastResult().getDoubleValue());
    }

    @Test
    void visitReturnVarNegated() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("""
                return -result;
                """);

        this.addNumVar(interpreter, "result", 3);
        ReturnStatement returnStatement = interpreter.getParser().parseReturnStatement();
        interpreter.visit(returnStatement);

        assertEquals(3, interpreter.getScopeManager().getLocalScope().getVariable("result").getDoubleValue());

        assertEquals(ValueType.NUM, interpreter.getScopeManager().getLastResult().getType());
        assertEquals(-3, interpreter.getScopeManager().getLastResult().getDoubleValue());
    }

    @Test
    void visitProgramExample1() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("""
                num func(num a){
                    if(a < 10){
                        return a;
                    }
                    else{
                        return 10;
                    }
                }
                void main(){
                    num result = func(10.01);
                    print("result: ", result);
                }
                """);
        interpreter.interpret();

        assertNotNull(interpreter.getScopeManager().getGlobalScope().getFunctionDef("func"));
        assertNotNull(interpreter.getScopeManager().getGlobalScope().getFunctionDef("main"));
        assertNotNull(interpreter.getScopeManager().getLocalScope().getVariables().get("result"));

        Value result = interpreter.getScopeManager().getLocalScope().getVariable("result");
        assertEquals(ValueType.NUM, result.getType());
        assertEquals(10, result.getDoubleValue());
    }

    @Test
    void visitProgramExample2() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("""
                void func(date d){
                    if( !(d < 01.01.2000.00:00:00) ) {
                        print("not 1999");
                    }
                }
                
                void main() {
                    date d = 01.01.2021.15:25:59;
                    func(d);
                }
                """);
        interpreter.interpret();

        assertNotNull(interpreter.getScopeManager().getGlobalScope().getFunctionDef("func"));
        assertNotNull(interpreter.getScopeManager().getGlobalScope().getFunctionDef("main"));

        assertTrue(interpreter.getScopeManager().getLocalScope().isDeclared("d"));
        assertEquals(ValueType.DATE, interpreter.getScopeManager().getLocalScope().getVariable("d").getType());
        assertEquals("01.01.2021.15:25:59", interpreter.getScopeManager().getLocalScope().getVariable("d").getStrValue());
    }

    @Test
    void visitProgramExample3() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("""
                // function
                date change(date d, time t){
                    num i = 0;
                    while(i < 5) {
                        d = d + t;
                        i = i + 1;
                    }
                    return d;
                }
                // main
                void main() {
                    date now = 01.01.2021.23:59:59;
                    time add = 24:00:00;
                    
                    date later = change(now, add);
                    print("after change: ", later);
                }
                """);
        interpreter.interpret();

    }

    @Test
    void visitProgramExample4() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("""
                time dif(date d1, date d2){
                    if (d1 >= d2) {
                        print("d1>=d2");
                        return d1 - d2;
                    }
                    else {
                        print("d1<d2");
                        return d2 - d1;
                    }
                }
                void main() {
                    time ref = 00:00:01;
                    date d1 = 01.01.1999.00:00:01;      // komentarz
                    date d2 = 01.01.1999.00:00:00;
                    date d3 = 01.01.2021.00:00:00;
                    
                    if(d3 > d1 && dif(d1, d2) == ref) {
                        print("everything is ok");
                    }
                }
                """);
        interpreter.interpret();
    }

    @Test
    void visitProgramExample5() throws IOException, Errors.TokenError, Errors.InterpreterError {
        Interpreter interpreter = initInterpreter("""
                void check(date d1, date d2, num n1, num n2) {
                    if (d1 >= d2 && n1 > n2 || n1 < n2){
                        print("priorities hierarchy ok");
                    }
                    else{
                        print("oops.. something wrong");
                    }
                }
                void main() {
                    date d1 = 01.01.1999.11:11:11;
                    date d2 = 01.01.1999.11:11:11;
                    num n1 = 12.34;
                    num n2 = 5;
                    
                    check(d1, d2, n1, n2);
                }
                """);
        interpreter.interpret();
    }

    @Test
    void visitProgramExample6() throws IOException, Errors.TokenError {
        Interpreter interpreter = initInterpreter("""
                void doSomething(){
                    time t = 00:00:01;
                }
                void main(){
                    num n = 1;
                    
                    while(n < 5){
                        n = n + 1;
                    }
                    doSomething();
                    print(t);
                }
                """);
        assertThrows(Errors.UndeclaredVariable.class, interpreter::interpret);
//        interpreter.interpret();
    }

    private void addNumVar(Interpreter interpreter, String name, double value){
        interpreter.getScopeManager().getLocalScope().addVariable(name, new Value(ValueType.NUM, value));
    }
}