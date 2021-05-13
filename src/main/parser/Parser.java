/*
 *	Name:		SourceTest.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.parser;

import main.errors.ErrorMessages;
import main.errors.Errors;
import main.lexer.Lexer;
import main.lexer.Token;
import main.grammar.*;
import main.parser.operators.*;

import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    private final Lexer lexer;
    private Program program;

    public Parser(Lexer lexer) throws Errors.TokenError, IOException {
        this.lexer = lexer;
        this.lexer.getNextToken();
        this.program = null;
    }

    // { functionDef } ;
    public void parseProgram() throws IOException, Errors.TokenError {
        ArrayList<FunctionDef> functionDefs = new ArrayList<FunctionDef>();
        FunctionDef functionDef = this.parseFunctionDef();

        while(functionDef != null){
            functionDefs.add(functionDef);
            functionDef = this.parseFunctionDef();
        }

        this.program = new Program(functionDefs.toArray(new FunctionDef[0]));
    }

    // signature, "(", parameters, ")", block ;
    public FunctionDef parseFunctionDef() throws IOException, Errors.TokenError {
        Signature signature = this.parseSignature();
        if(signature == null){
            return null;
        }

        this.requireThenGetNext(Token.Type.LPAREN, ErrorMessages.ParserSyntaxError.FUNCTION_DEF);
        Parameters parameters = this.parseParameters();
        this.requireNotNull(parameters, ErrorMessages.ParserSyntaxError.FUNCTION_DEF);
        this.requireThenGetNext(Token.Type.RPAREN, ErrorMessages.ParserSyntaxError.FUNCTION_DEF);

        Block block = this.parseBlock();
        this.requireNotNull(block, ErrorMessages.ParserSyntaxError.FUNCTION_DEF);

        return new FunctionDef(signature, parameters, block);
    }

    // [ signature, {, ",", signature } ] ;
    public Parameters parseParameters() throws IOException, Errors.TokenError {
        ArrayList<Signature> signatures = new ArrayList<>();
        Signature signature = this.parseSignature();

        if(signature != null){
            signatures.add(signature);

            while(this.requireThenGetNext(Token.Type.COMMA)){
                signature = this.parseSignature();
                this.requireNotNull(signature, ErrorMessages.ParserSyntaxError.PARAMETERS);
                signatures.add(signature);
            }
        }
        return new Parameters(signatures.toArray(new Signature[0]));
    }

    // [ expression { ",", expression } ] ;
    public Arguments parseArguments() throws IOException, Errors.TokenError {
        ArrayList<Expression> expressions = new ArrayList<Expression>();
        Expression expression = this.parseExpression();

        if(expression != null){
            expressions.add(expression);

            while(this.requireThenGetNext(Token.Type.COMMA)){
                expression = this.parseExpression();

                this.requireNotNull(expression, ErrorMessages.ParserSyntaxError.ARGUMENTS);
                expressions.add(expression);
            }
        }
        return new Arguments(expressions.toArray(new Expression[0]));
    }

    // "{", { statement }, "}" ;
    public Block parseBlock() throws IOException, Errors.TokenError {
        if(!this.requireThenGetNext(Token.Type.LCPAREN)){
            return null;
        }
        else{
            ArrayList<Statement> statements = new ArrayList<Statement>();
            Statement statement = this.parseStatement();

            while(statement != null){
                statements.add(statement);
                statement = this.parseStatement();
            }

            this.requireThenGetNext(Token.Type.RCPAREN, ErrorMessages.ParserSyntaxError.BLOCK);
            return new Block(statements.toArray(new Statement[0]));
        }
    }

    // type, id ;
    public Signature parseSignature() throws IOException, Errors.TokenError {
        Type signatureType = Attributes.functionTypes.get(this.lexer.getToken().getType());

        if(signatureType == null){
            return null;
        }
        else{
            this.lexer.getNextToken();

            String id = this.lexer.getToken().getStr();
            this.requireThenGetNext(Token.Type.IDENTIFIER, ErrorMessages.ParserSyntaxError.SIGNATURE);
            return new Signature(signatureType, id);
        }
    }

    // block | ifStatement | whileStatement | printStatement | returnStatement | initStatement | assignStatement | ( functionCall, ";" ) ;
    public Statement parseStatement() throws IOException, Errors.TokenError {
        Statement statement = this.parseBlock();
        if(statement != null) { return statement; }

        statement = this.parseIfStatement();
        if(statement != null) { return statement; }

        statement = this.parseWhileStatement();
        if(statement != null) { return statement; }

        statement = this.parsePrintStatement();
        if(statement != null) { return statement; }

        statement = this.parseReturnStatement();
        if(statement != null) { return statement; }

        statement = this.parseInitStatement();
        if(statement != null) { return statement; }

        statement = this.parseAssignStatementOrFunctionCall();
        if(statement instanceof FunctionCall) {
            this.requireThenGetNext(Token.Type.SEMICOLON, ErrorMessages.ParserSyntaxError.STATEMENT);
            return statement;
        }
        else if(statement != null){
            return statement;
        }
        return null;
    }

    // "if", "(", condition, ")", block, ["else", block] ;
    public IfStatement parseIfStatement() throws IOException, Errors.TokenError {
        if(!this.requireThenGetNext(Token.Type.IF)){
            return null;
        }

        ParenthCond parenthCond = this.parseParenthCond();
        this.requireNotNull(parenthCond, ErrorMessages.ParserSyntaxError.IF_STATEMENT);

        Block block1 = this.parseBlock();
        this.requireNotNull(block1, ErrorMessages.ParserSyntaxError.IF_STATEMENT);

        Statement elseStatement = this.parseElseStatement();
        if(elseStatement == null){
            return new IfStatement(parenthCond.getCondition(), block1);
        }
        return new IfStatement(parenthCond.getCondition(), block1, elseStatement);
    }

    public Statement parseElseStatement() throws Errors.TokenError, IOException {
        if(!this.requireThenGetNext(Token.Type.ELSE)){
            return null;
        }
        Statement elseStatement = this.parseStatement();
        requireNotNull(elseStatement, ErrorMessages.ParserSyntaxError.ELSE_STATEMENT);
        return elseStatement;
    }

    // "while", "(", "orCondition", ")", block ;
    public WhileStatement parseWhileStatement() throws IOException, Errors.TokenError {
        if(!this.requireThenGetNext(Token.Type.WHILE)){
            return null;
        }
        ParenthCond parenthCond = this.parseParenthCond();
        this.requireNotNull(parenthCond, ErrorMessages.ParserSyntaxError.WHILE_STATEMENT);

        Block block = this.parseBlock();
        this.requireNotNull(block, ErrorMessages.ParserSyntaxError.WHILE_STATEMENT);

        return new WhileStatement(parenthCond.getCondition(), block);
    }

    // "return", expression, ";" ;
    public ReturnStatement parseReturnStatement() throws IOException, Errors.TokenError {
        if(!requireThenGetNext(Token.Type.RETURN)){
            return null;
        }
        Expression expression = this.parseExpression();
        this.requireNotNull(expression, ErrorMessages.ParserSyntaxError.RETURN_STATEMENT);
        this.requireThenGetNext(Token.Type.SEMICOLON, ErrorMessages.ParserSyntaxError.RETURN_STATEMENT);
        return new ReturnStatement(expression);
    }

    // signature, [ assignmentOp, expression ], ";" ;
    public InitStatement parseInitStatement() throws IOException, Errors.TokenError {
        Signature signature = this.parseSignature();

        if(signature == null){
            return null;
        }

        if(requireThenGetNext(Token.Type.ASSIGNMENT)){
            Expression expression = this.parseExpression();
            this.requireNotNull(expression, ErrorMessages.ParserSyntaxError.INIT_STATEMENT);
            this.requireThenGetNext(Token.Type.SEMICOLON);
            return new InitStatement(signature, expression);
        }

        this.requireThenGetNext(Token.Type.SEMICOLON, ErrorMessages.ParserSyntaxError.INIT_STATEMENT);
        return new InitStatement(signature);

    }

    // "print", "(", printable, { ",", printable }, ")", ";" ;
    public PrintStatement parsePrintStatement() throws IOException, Errors.TokenError {
        if(!this.requireThenGetNext(Token.Type.PRINT)){
            return null;
        }

        this.requireThenGetNext(Token.Type.LPAREN, ErrorMessages.ParserSyntaxError.PRINT_STATEMENT);

        ArrayList<Printable> printables = new ArrayList<>();
        Printable printable = this.parsePrintable();

        this.requireNotNull(printable, ErrorMessages.ParserSyntaxError.PRINT_STATEMENT);
        printables.add(printable);

        while(requireThenGetNext(Token.Type.COMMA)){
            printable = this.parsePrintable();
            this.requireNotNull(printable, ErrorMessages.ParserSyntaxError.PRINT_STATEMENT);
            printables.add(printable);
        }

        this.requireThenGetNext(Token.Type.RPAREN, ErrorMessages.ParserSyntaxError.PRINT_STATEMENT);
        this.requireThenGetNext(Token.Type.SEMICOLON, ErrorMessages.ParserSyntaxError.PRINT_STATEMENT);
        return new PrintStatement(printables.toArray(new Printable[0]));
    }

    // expression | string ;
    public Printable parsePrintable() throws IOException, Errors.TokenError {
        if (this.lexer.getToken().getType() == Token.Type.STRING) {
            Printable printable = new Printable(this.lexer.getToken().getStr());
            this.lexer.getNextToken();
            return printable;
        }
        return this.parsePrintableExpression();
    }


    public Printable parsePrintableExpression() throws IOException, Errors.TokenError {
        Expression expression = this.parseExpression();
        if(expression != null){
            return new Printable(expression);
        }
        return null;
    }

    // id, assignmentOp, expression, ";" ;
    public AssignStatement parseAssignStatement(String id) throws IOException, Errors.TokenError {
        if(!this.requireThenGetNext(Token.Type.ASSIGNMENT)){
            return null;
        }

        Expression expression = this.parseExpression();
        if(expression != null && this.requireThenGetNext(Token.Type.SEMICOLON)){
            return new AssignStatement(id, expression);
        }
        return null;
    }

    // id, "(", arguments, ")" ;
    public FunctionCall parseFunctionCall(String id) throws IOException, Errors.TokenError {
        if(!this.requireThenGetNext(Token.Type.LPAREN)){
            return null;
        }

        Arguments arguments = this.parseArguments();
        if(arguments != null && this.requireThenGetNext(Token.Type.RPAREN)){
            return new FunctionCall(id, arguments);
        }
        return null;
    }

    public Statement parseAssignStatementOrFunctionCall() throws IOException, Errors.TokenError {
        if(this.lexer.getToken().getType() == Token.Type.IDENTIFIER){
            String id = this.lexer.getToken().getStr();
            this.lexer.getNextToken();
            Statement statement = this.parseAssignStatement(id);

            if(statement != null){
                return statement;
            }

            statement = this.parseFunctionCall(id);
            if(statement != null){
                return statement;
            }
        }
        return null;
    }

    // andCond, { orOp, andCond } ;
    public OrCond parseCondition() throws IOException, Errors.TokenError {
        ArrayList<AndCond> andConds = new ArrayList<AndCond>();
        AndCond andCond = this.parseAndCond();

        if(andCond == null){
            return null;
        }

        andConds.add(andCond);
        while(this.requireThenGetNext(Token.Type.OR)){
            andCond = this.parseAndCond();
            this.requireNotNull(andCond, ErrorMessages.ParserSyntaxError.OR_COND);
            andConds.add(andCond);
        }
        return new OrCond(andConds.toArray(new AndCond[0]));
    }

    // equalCond, { andOp, equalCond } ;
    public AndCond parseAndCond() throws IOException, Errors.TokenError {
        ArrayList<EqualCond> equalConds = new ArrayList<EqualCond>();
        EqualCond equalCond = this.parseEqualCond();

        if(equalCond == null){
            return null;
        }
        equalConds.add(equalCond);

        while(this.requireThenGetNext(Token.Type.AND)){
            equalCond = this.parseEqualCond();

            requireNotNull(equalCond, ErrorMessages.ParserSyntaxError.AND_COND);
            equalConds.add(equalCond);
        }
        return new AndCond(equalConds.toArray(new EqualCond[0]));
    }

    // relationCond, [ equalOp, relationCond ] ;
    public EqualCond parseEqualCond() throws IOException, Errors.TokenError {
        RelationCond relationCond1 = this.parseRelationCond();

        if(relationCond1 == null){
            return null;
        }

        EqualOperator equalOperator = Attributes.equalOperators.get(this.lexer.getToken().getType());
        if(this.requireThenGetNext(Token.Type.EQUAL) || this.requireThenGetNext(Token.Type.NOT_EQUAL)){
            RelationCond relationCond2 = this.parseRelationCond();
            this.requireNotNull(relationCond2, ErrorMessages.ParserSyntaxError.EQUAL_COND);
            return new EqualCond(relationCond1, equalOperator, relationCond2);
        }
        return new EqualCond(relationCond1);
    }

    // primaryCond, [ relationOp, primaryCond ] ;
    public RelationCond parseRelationCond() throws IOException, Errors.TokenError {
        PrimaryCond primaryCond1 = this.parsePrimaryCond();

        if(primaryCond1 == null){
            return null;
        }

        if(Attributes.relationOperators.get(this.lexer.getToken().getType()) != null){
            RelationOperator relationOp = Attributes.relationOperators.get(this.lexer.getToken().getType());
            this.lexer.getNextToken();
            PrimaryCond primaryCond2 = this.parsePrimaryCond();

            this.requireNotNull(primaryCond2, ErrorMessages.ParserSyntaxError.RELATION_COND);
            return new RelationCond(primaryCond1, relationOp, primaryCond2);
        }

        return new RelationCond(primaryCond1);
    }

    // [ negationOp ], ( parenthCond | expression ) ;
    public PrimaryCond parsePrimaryCond() throws IOException, Errors.TokenError {
        boolean negationOp = this.requireThenGetNext(Token.Type.NOT);

        ParenthCond parenthCond = this.parseParenthCond();
        if(this.requireNotNullThenGetNext(parenthCond)){
            return new PrimaryCond(negationOp, parenthCond);
        }

        Expression expression = this.parseExpression();
        this.requireNotNull(expression, ErrorMessages.ParserSyntaxError.PRIMARY_COND);
        return new PrimaryCond(negationOp, expression);
    }

    // "(", orCondition, ")" ;
    public ParenthCond parseParenthCond() throws IOException, Errors.TokenError {
        if(!this.requireThenGetNext(Token.Type.LPAREN)){
            return null;
        }

        OrCond condition = this.parseCondition();
        this.requireNotNull(condition, ErrorMessages.ParserSyntaxError.PARENTH_COND);
        this.requireThenGetNext(Token.Type.RPAREN, ErrorMessages.ParserSyntaxError.PARENTH_COND);
        return new ParenthCond(condition);
    }

    // "(", expression, ")" ;
    public ParenthExpression parseParenthExpression() throws IOException, Errors.TokenError {
        if(this.requireThenGetNext(Token.Type.LPAREN)){
            Expression expression = this.parseExpression();

            if(expression != null){
                this.requireThenGetNext(Token.Type.RPAREN, ErrorMessages.ParserSyntaxError.PARENTH_EXPR);
                return new ParenthExpression(expression);
            }
        }
        return null;
    }

    // term, { addOp, term } ;
    public Expression parseExpression() throws IOException, Errors.TokenError {
        ArrayList<Term> terms = new ArrayList<Term>();
        ArrayList<AddOperator> addOperators = new ArrayList<AddOperator>();
        Term term = this.parseTerm();

        if(term == null){
            return null;
        }
        terms.add(term);

        while(this.lexer.getToken().getType() == Token.Type.PLUS || this.lexer.getToken().getType() == Token.Type.MINUS) {
            addOperators.add(Attributes.addOperators.get(this.lexer.getToken().getType()));
            this.lexer.getNextToken();
            term = this.parseTerm();

            requireNotNull(term, ErrorMessages.ParserSyntaxError.EXPRESSION);
            terms.add(term);
        }
        return new Expression(terms.toArray(new Term[0]), addOperators.toArray(new AddOperator[0]));
    }

    // factor, { multOp, factor } ;
    public Term parseTerm() throws IOException, Errors.TokenError {
        ArrayList<Factor> factors = new ArrayList<Factor>();
        ArrayList<MultOperator> multOperators = new ArrayList<MultOperator>();
        Factor factor = this.parseFactor();

        if(factor == null){
            return null;
        }
        factors.add(factor);

        while(this.lexer.getToken().getType() == Token.Type.MULTIPLY || this.lexer.getToken().getType() == Token.Type.DIVIDE){
                multOperators.add(Attributes.multOperators.get(this.lexer.getToken().getType()));
                this.lexer.getNextToken();
                factor = this.parseFactor();

                requireNotNull(factor, ErrorMessages.ParserSyntaxError.TERM);
                factors.add(factor);
            }
        return new Term(factors.toArray(new Factor[0]), multOperators.toArray(new MultOperator[0]));
    }

    // ["-"], ( number | date | time | id | parenthExpr | functionCall ) ;
    public Factor parseFactor() throws IOException, Errors.TokenError {
        boolean minus = false;
        String identifier;
        double number = 0;
        Date date = null;
        Time time = null;
        ParenthExpression parenthExpression = null;
        FunctionCall functionCall = null;

        if(this.lexer.getToken().getType() == Token.Type.MINUS){                // minus (comes with anything but date)
            minus = true;
            this.lexer.getNextToken();
        }

        if(this.lexer.getToken().getType() == Token.Type.NUMERIC){              // numeric
            number = this.lexer.getToken().getValue();
            this.lexer.getNextToken();

            return new Factor(minus, number);
        }
        else if(this.lexer.getToken().getType() == Token.Type.TIME){            // time
            time = new Time(this.lexer.getToken().getStr());
            this.lexer.getNextToken();

            return new Factor(minus, time);
        }
        else if(this.lexer.getToken().getType() == Token.Type.IDENTIFIER){      // id | functionCall
            identifier = this.lexer.getToken().getStr();
            this.lexer.getNextToken();

            functionCall = this.parseFunctionCall(identifier);
            if(functionCall != null){
                return new Factor(minus, functionCall);
            }
            else{
                return new Factor(minus, identifier);
            }
        }
        else if(!minus && this.lexer.getToken().getType() == Token.Type.DATE){  // date
            date = new Date(this.lexer.getToken().getStr());
            this.lexer.getNextToken();

            return new Factor(date);
        }
        else{          // parenthExpression
            parenthExpression = parseParenthExpression();

            if(parenthExpression != null){
                return new Factor(minus, parenthExpression);
            }
        }
        return null;
    }

    public void requireThenGetNext(Token.Type tokenType, ErrorMessages.ParserSyntaxError errorType) throws Errors.TokenError, IOException {
        if(this.lexer.getToken().getType() != tokenType){
            throwParserError(errorType);
        }
        this.lexer.getNextToken();
    }

    public boolean requireThenGetNext(Token.Type tokenType) throws Errors.TokenError, IOException {
        if(this.lexer.getToken().getType() != tokenType){
            return false;
        }
        this.lexer.getNextToken();
        return true;
    }

    public void requireNotNull(Object obj, ErrorMessages.ParserSyntaxError errorType) throws Errors.TokenError {
        if(obj == null){
            throwParserError(errorType);
        }
    }

    public boolean requireNotNullThenGetNext(Object obj) throws Errors.TokenError, IOException {
        if(obj == null){
            return false;
        }
        this.lexer.getNextToken();
        return true;
    }

    public void throwParserError(ErrorMessages.ParserSyntaxError errorType) throws Errors.TokenError {
        throw new Errors.SyntaxError(this.lexer.getLine(), this.lexer.getColumn(), errorType);
    }

    public Program getProgram(){ return this.program; }
}
