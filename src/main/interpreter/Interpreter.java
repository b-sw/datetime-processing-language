/*
 *	Name:		Interpreter.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.interpreter;
import main.errors.Errors;
import main.grammar.*;
import main.grammar.operators.AddOperator;
import main.grammar.operators.MultOperator;
import main.grammar.operators.Type;
import main.interpreter.scope.*;
import main.parser.Parser;
import main.visitor.Visitor;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Interpreter implements Visitor{
    private final Parser parser;
    private final ScopeManager scopeManager;

    public Interpreter(Parser parser){
        this.parser = parser;
        this.scopeManager = new ScopeManager();
    }

    public void interpret() throws Errors.TokenError, IOException, Errors.InterpreterError {
        this.parser.parseProgram();
        this.parser.getProgram().accept(this);
    }

    @Override
    public void visit(Program program) throws Errors.InterpreterError {
        boolean isMain = false;

        for(FunctionDef functionDef : program.getFunctionDefs()){
            functionDef.accept(this);

            if(functionDef.getSignature().getId().equals("main")){
                isMain = true;
            }
        }

        if(!isMain){
            throw new Errors.MainNotDeclaredError();
        }

        for(FunctionDef functionDef : program.getFunctionDefs()){
            if(functionDef.getSignature().getId().equals("main")){
                functionDef.getBlock().accept(this);
            }
        }
    }

    @Override
    public void visit(FunctionDef functionDef) {
        this.scopeManager.addFunction(functionDef.getSignature().getId(), functionDef);
    }

    @Override
    public void visit(FunctionCall functionCall) throws Errors.InterpreterError {
        String functionName = functionCall.getId();
        FunctionDef functionDef = this.scopeManager.getFunction(functionName);
        ArrayList<Value> arguments = new ArrayList<>();

        for(Expression expression : functionCall.getArguments().getExpressions()){
            expression.accept(this);
            arguments.add(this.scopeManager.getLastResult());
        }
        this.executeFunction(functionDef, arguments.toArray(new Value[0]));
    }

    @Override
    public void visit(Block block) throws Errors.InterpreterError {
        for(Statement statement : block.getStatements()){
            statement.accept(this);

            if(this.scopeManager.getReturnResult() != null){
                return;
            }
        }
    }

    @Override
    public void visit(IfStatement ifStatement) throws Errors.InterpreterError {
        ifStatement.getOrCond().accept(this);
        if(this.scopeManager.getLastResult().getBoolValue()){
            ifStatement.getIfBlock().accept(this);
        }
        else if(ifStatement.getElseStatement() != null){
            ifStatement.getElseStatement().accept(this);
        }
    }

    @Override
    public void visit(WhileStatement whileStatement) throws Errors.InterpreterError {
        whileStatement.getOrCond().accept(this);

        while(this.scopeManager.getLastResult().getBoolValue()){
            whileStatement.getBlock().accept(this);
            whileStatement.getOrCond().accept(this);
        }
    }

    @Override
    public void visit(ReturnStatement returnStatement) throws Errors.InterpreterError {
        returnStatement.getExpression().accept(this);
        this.scopeManager.setLastResultAsReturnResult();
    }

    @Override
    public void visit(InitStatement initStatement) throws Errors.InterpreterError {
        String name = initStatement.getSignature().getId();
        requireVarType(initStatement.getSignature().getType());

        if(initStatement.getSignature().getType() == Type.NUM){
            addVarInitStatement(initStatement, ValueType.NUM, name);
        }
        else if(initStatement.getSignature().getType() == Type.DATE){
            addVarInitStatement(initStatement, ValueType.DATE, name);
        }
        else if(initStatement.getSignature().getType() == Type.TIME){
            addVarInitStatement(initStatement, ValueType.TIME, name);
        }
    }

    @Override
    public void visit(AssignStatement assignStatement) throws Errors.InterpreterError {
        String name = assignStatement.getId();
        assignStatement.getExpression().accept(this);
        this.scopeManager.updateVar(name, this.scopeManager.getLastResult());
    }

    @Override
    public void visit(PrintStatement printStatement) throws Errors.InterpreterError {  // "print", "(", printable, { ",", printable }, ")", ";" ;
        System.out.println(this.mergePrintables(printStatement));
    }

    @Override
    public void visit(Expression expression) throws Errors.InterpreterError {  // term, { addOp, term } ;
        expression.getTerms()[0].accept(this);
        Value result = this.scopeManager.getLastResult();

        ArrayList<Pair<AddOperator, Term>> operations = this.getOperations(expression.getAddOperators(), expression.getTerms());

        for(Pair<AddOperator, Term> operation : operations){
            operation.u.accept(this);

            result = this.updateResult(result, operation.t);
        }
        this.scopeManager.setLastResult(result);
    }

    @Override
    public void visit(Term term) throws Errors.InterpreterError {  // factor, { multOp, factor } ;
        term.getFactors()[0].accept(this);
        Value result = this.scopeManager.getLastResult();

        ArrayList<Pair<MultOperator, Factor>> operations = this.getOperations(term.getMultOperators(), term.getFactors());

        for(Pair<MultOperator, Factor> operation : operations){
            operation.u.accept(this);

            result = this.updateResult(result, operation.t);
        }
        this.scopeManager.setLastResult(result);
    }

    @Override
    public void visit(Factor factor) throws Errors.InterpreterError {  // ["-"], ( number | date | time | id | parenthExpr | functionCall ) ;
        this.tryFactorNum(factor);
        this.tryFactorDate(factor);
        this.tryFactorTime(factor);
        this.tryFactorId(factor);
        this.tryFactorParenthExpression(factor);
        this.tryFactorFunctionCall(factor);
    }

    @Override
    public void visit(ParenthExpression parenthExpression) throws Errors.InterpreterError { // "(", expression, ")" ;
        parenthExpression.getExpression().accept(this);
    }

    @Override
    public void visit(OrCond orCond) throws Errors.InterpreterError {   // andCond, { orOp, andCond } ;
        for(AndCond andCond : orCond.getAndConds()){
            andCond.accept(this);
            if(this.scopeManager.getLastResult().getBoolValue()){
                return;
            }
        }
    }

    @Override
    public void visit(AndCond andCond) throws Errors.InterpreterError { // equalCond, { andOp, equalCond } ;
        Value result = new Value(ValueType.BOOL, true);

        for(EqualCond equalCond : andCond.getEqualConds()){
            equalCond.accept(this);
            if(!this.scopeManager.getLastResult().getBoolValue()){
                result = new Value(ValueType.BOOL, false);
            }
        }
        this.scopeManager.setLastResult(result);
    }

    @Override
    public void visit(EqualCond equalCond) throws Errors.InterpreterError { // relationCond, [ equalOp, relationCond ] ;
        equalCond.getRelationCond1().accept(this);

        Pair<Value, Boolean> resultAndNegationOp = this.getNegationAndResult();
        Value result = resultAndNegationOp.t;
        boolean negationOp = resultAndNegationOp.u;

        if(equalCond.getEqualOp() != null){
            result = getResultFromEqualRelation(equalCond);
            if(negationOp){
                result = new Value(ValueType.BOOL, !result.getBoolValue());
            }
        }
        this.scopeManager.setLastResult(result);
    }

    private Value getResultFromEqualRelation(EqualCond equalCond) throws Errors.InterpreterError {
//        equalCond.getRelationCond1().accept(this);
        Value value1 = this.scopeManager.getLastResult();

        equalCond.getRelationCond2().accept(this);
        Value value2 = this.scopeManager.getLastResult();

        return this.compareResultsEqualRelation(equalCond, value1, value2);
    }

    @Override
    public void visit(RelationCond relationCond) throws Errors.InterpreterError {   // primaryCond, [ relationOp, primaryCond ] ;
        relationCond.getPrimaryCond1().accept(this);

        Pair<Value, Boolean> resultAndNegationOp = this.getNegationAndResult();
        Value result = resultAndNegationOp.t;
        boolean negationOp = resultAndNegationOp.u;

        if (relationCond.getRelationOp() != null) {
            result = getResultFromRelation(relationCond);
        }
        // add negationOp

        this.scopeManager.setLastResult(result);
        if(negationOp){
            this.scopeManager.setLastResult(new Value(ValueType.BOOL, !this.scopeManager.getLastResult().getBoolValue()));
        }
    }

    @Override
    public void visit(PrimaryCond primaryCond) throws Errors.InterpreterError {    // [ negationOp ], ( parenthCond | expression ) ;
        boolean result = false;

        if(primaryCond.getParenthCond() != null){
            primaryCond.getParenthCond().accept(this);

            if(this.scopeManager.getLastResult().getBoolValue()){
                result = true;
            }
            if(primaryCond.getNegationOp()){
                result = !result;
            }

            this.scopeManager.setLastResult(new Value(ValueType.BOOL, result));
        }
        else if(primaryCond.getExpression() != null){
            primaryCond.getExpression().accept(this);
        }
    }

    @Override
    public void visit(ParenthCond parenthCond) throws Errors.InterpreterError { // "(", orCondition, ")" ;
        parenthCond.getCondition().accept(this);
    }

    private <T, U> ArrayList<Pair<T, U>> getOperations(T[] operators, U[] expressions){
        ArrayList<Pair<T, U>> operations = new ArrayList<>();
        for(int i = 0; i < operators.length; ++i){
            operations.add(new Pair<>(operators[i], expressions[i + 1]));
        }

        return operations;
    }

    private Value updateResult(Value result, MultOperator operator) throws Errors.InterpreterError {
        switch (operator) {
            case MUL -> {
                if (this.scopeManager.getLastResult().getType() != ValueType.NUM) {
                    throw new Errors.IllicitOperation();
                }
                if(result.getType() != ValueType.NUM){
                    throw new Errors.IllicitOperation();
                }
                result = new Value(ValueType.NUM, result.getDoubleValue() * this.scopeManager.getLastResult().getDoubleValue());
            }
            case DIV -> {
                if(this.scopeManager.getLastResult().getDoubleValue() == 0){
                    throw new Errors.DivisionZero();
                }
                if (this.scopeManager.getLastResult().getType() != ValueType.NUM) {
                    throw new Errors.IllicitOperation();
                }
                if(result.getType() != ValueType.NUM){
                    throw new Errors.IllicitOperation();
                }
                result = new Value(ValueType.NUM, result.getDoubleValue() / this.scopeManager.getLastResult().getDoubleValue());
            }
        }

        return result;
    }
    
    private Value updateResult(Value result, AddOperator operator) throws Errors.InterpreterError {
        switch(result.getType()){
            case DATE -> result = this.updateIfResultDate(result, operator);
            case TIME -> result = this.updateIfResultTime(result, operator);
            case NUM -> result = this.updateIfLastResultNum(result, operator);
        }

        return result;
    }
    
    private Value updateIfResultDate(Value result, AddOperator operator) throws Errors.InterpreterError {
        if(this.scopeManager.getLastResult().getType() == ValueType.NUM){
            throw new Errors.InterpreterError("Illicit operator for date.");
        }
    
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu.HH:mm:ss");
    
        switch(operator){
            case ADD -> {   // DATE + TIME
                return handleAddTime(result, dateFormatter);
            }
            case SUB -> {   // DATE - DATE or DATE - TIME
                return handleSubDateTime(result, dateFormatter);
            }
        }
        throw new Errors.InterpreterError("Illicit operator for date.");
    }
    
    private Value handleSubDateTime(Value result, DateTimeFormatter d) throws Errors.InterpreterError {
        boolean minus = this.scopeManager.getLastResult().getBoolValue();
        if(minus){
            return updateResult(result, AddOperator.ADD);
        }

        LocalDateTime date = LocalDateTime.parse(result.getStrValue(), d);

        if(this.scopeManager.getLastResult().getType() == ValueType.DATE){ // DATE - DATE
            LocalDateTime date2 = LocalDateTime.parse(this.scopeManager.getLastResult().getStrValue(), d);

            Duration duration = Duration.between(date2, date);

            return new Value(ValueType.TIME, this.getTimeStringFromDuration(duration.getSeconds()));
        }
        else{ // DATE - TIME
            int[] time = Utils.getTime(this.scopeManager.getLastResult().getStrValue());
            date = date.minusHours(time[0]).minusMinutes(time[1]).minusSeconds(time[2]);

            return new Value(ValueType.DATE, date.format(d));
        }
    }
    
    private Value updateIfResultTime(Value result, AddOperator operator) throws Errors.InterpreterError {
        if(this.scopeManager.getLastResult().getType() != ValueType.TIME){
            throw new Errors.InterpreterError("Illicit operator for time.");
        }
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.uuuu.HH:mm:ss");
    
        switch(operator){
            case ADD -> {
                return handleAddTime(result, dateFormatter);
            }
            case SUB -> {
                return handleSubTime(result);
            }
        }
        throw new Errors.InterpreterError("Illicit operator for time.");
    }
    
    private Value handleAddTime(Value result, DateTimeFormatter d) throws Errors.InterpreterError {
        boolean minus = this.scopeManager.getLastResult().getBoolValue();
        if(minus){
            return updateResult(result, AddOperator.SUB);
        }
    
        if(result.getType() == ValueType.DATE){ // DATE + TIME
    
            LocalDateTime dt = LocalDateTime.parse(result.getStrValue(), d);
            int[] time = Utils.getTime(this.scopeManager.getLastResult().getStrValue());
    
            dt = dt.plusHours(time[0]).plusMinutes(time[1]).plusSeconds(time[2]);
    
            return new Value(ValueType.DATE, dt.format(d));
        }
        else{ // TIME + TIME

            Pair<Long, Long> times = this.getTimeFromResultAndLastResult(result);
            long duration = times.t + times.u;

            return new Value(ValueType.TIME, this.getTimeStringFromDuration(duration));
        }
    }

    private Value handleSubTime(Value result) throws Errors.InterpreterError {
        boolean minus = this.scopeManager.getLastResult().getBoolValue();
        if(minus){
            return updateResult(result, AddOperator.ADD);
        }

        Pair<Long, Long> times = this.getTimeFromResultAndLastResult(result);
        long duration = times.t - times.u;

        return new Value(ValueType.TIME, this.getTimeStringFromDuration(duration));
    }

    private Pair<Long, Long> getTimeFromResultAndLastResult(Value result){
        int[] time1 = Utils.getTime(result.getStrValue());
        int[] time2 = Utils.getTime(this.scopeManager.getLastResult().getStrValue());

        long time1seconds = time1[0] * 3600L + time1[1] * 60L + time1[2];
        long time2seconds = time2[0] * 3600L + time2[1] * 60L + time2[2];

        return new Pair<>(time1seconds, time2seconds);
    }

    private Value updateIfLastResultNum(Value result, AddOperator operator) throws Errors.InterpreterError{
        if(this.scopeManager.getLastResult().getType() != ValueType.NUM){
            throw new Errors.IllicitOperation();
        }

        switch(operator){
            case ADD -> {
                return new Value(ValueType.NUM, result.getDoubleValue() + this.scopeManager.getLastResult().getDoubleValue());
            }
            case SUB -> {
                return new Value(ValueType.NUM, result.getDoubleValue() - this.scopeManager.getLastResult().getDoubleValue());
            }
        }
        throw new Errors.InterpreterError("Illicit operator for num.");
    }

    private StringBuilder mergePrintables(PrintStatement printStatement) throws Errors.InterpreterError {
        StringBuilder printString = new StringBuilder();

        for(Printable printable : printStatement.getPrintables()){
            if(printable.getStr() != null){
                printable = new Printable(printable.getStr().replace("\"", ""));
                printString.append(printable.getStr());
            }
            else if(printable.getExpression() != null){
                printable.getExpression().accept(this);
                switch (this.scopeManager.getLastResult().getType()) {
                    case NUM -> printString.append(this.scopeManager.getLastResult().getDoubleValue());
                    case DATE, TIME -> printString.append(this.scopeManager.getLastResult().getStrValue());
                }
            }
        }
        this.scopeManager.setLastResult(new Value(ValueType.STRING, printString.toString()));

        return printString;
    }

    private void addVarInitStatement(InitStatement initStatement, ValueType type, String name) throws Errors.InterpreterError {
        if(initStatement.getExpression() == null){
            this.scopeManager.addVar(name, new Value(type));
        }
        else{
            initStatement.getExpression().accept(this);
            requireValueOfType(this.scopeManager.getLastResult(), type);

            this.scopeManager.addVar(name, this.scopeManager.getLastResult());
        }
    }

    private Pair<Value, Boolean> getNegationAndResult(){
        Value result = new Value(ValueType.BOOL, false);
        boolean negationOp = false;

        switch (this.scopeManager.getLastResult().getType()) {
            case BOOL -> result = new Value(ValueType.BOOL, this.scopeManager.getLastResult().getBoolValue());
            case NUM -> result = new Value(ValueType.NUM, this.scopeManager.getLastResult().getDoubleValue());
            case DATE -> {
                negationOp = this.scopeManager.getLastResult().getBoolValue();
                result = new Value(ValueType.DATE, this.scopeManager.getLastResult().getStrValue());
            }
            case TIME -> {
                negationOp = this.scopeManager.getLastResult().getBoolValue();
                result = new Value(ValueType.TIME, this.scopeManager.getLastResult().getStrValue());
            }
        }

        return new Pair<>(result, negationOp);
    }

    private Value getResultFromRelation(RelationCond relationCond) throws Errors.InterpreterError {
        relationCond.getPrimaryCond1().accept(this);
        Value value1 = this.scopeManager.getLastResult();

        relationCond.getPrimaryCond2().accept(this);
        Value value2 = this.scopeManager.getLastResult();

        return this.compareResultsRelation(relationCond, value1, value2);
    }

    private Value compareResultsEqualRelation(EqualCond equalCond, Value value1, Value value2) throws Errors.InterpreterError {
        Value result = new Value(ValueType.BOOL, false);

        switch(equalCond.getEqualOp()){
            case EQUAL:
                if(value1.equals(value2)){
                    result = new Value(ValueType.BOOL, true);
                }
                break;
            case NOT_EQUAL:
                if(!value1.equals(value2)){
                    result = new Value(ValueType.BOOL, true);
                }
                break;
        }

        return result;
    }

    private Value compareResultsRelation(RelationCond relationCond, Value value1, Value value2) throws Errors.InterpreterError {
        Value result = new Value(ValueType.BOOL, false);

        switch(relationCond.getRelationOp()){
            case GREATER_THAN:
                if(value1.greaterThan(value2)){
                    result = new Value(ValueType.BOOL, true);
                }
                break;
            case LESS_THAN:
                if(value1.lessThan(value2)){
                    result = new Value(ValueType.BOOL, true);
                }
                break;
            case LESS_OR_EQUAL:
                if(!value1.greaterThan(value2)){
                    result = new Value(ValueType.BOOL, true);
                }
                break;
            case GREATER_OR_EQUAL:
                if(!value1.lessThan(value2)){
                    result = new Value(ValueType.BOOL, true);
                }
                break;
        }

        return result;
    }

    private void tryFactorDate(Factor factor){
        if(factor.getDate() == null){
            return;
        }

        this.scopeManager.setLastResult(new Value(ValueType.DATE, factor.getDate().getDateStr()));
    }

    private void tryFactorTime(Factor factor){
        if(factor.getTime() == null){
            return;
        }

        this.scopeManager.setLastResult(new Value(ValueType.TIME, factor.getMinus(), factor.getTime().getTimeStr()));
    }

    private void tryFactorId(Factor factor) throws Errors.InterpreterError {
        if(factor.getId() == null){
            return;
        }

        Value variable = this.scopeManager.getVariable(factor.getId());
        if(variable.getType() == ValueType.NUM){
            if(factor.getMinus()){
                this.scopeManager.setLastResult(new Value(ValueType.NUM, -variable.getDoubleValue()));
            }
            else{
                this.scopeManager.setLastResult(new Value(ValueType.NUM, variable.getDoubleValue()));
            }
        }
        else{ // if TIME or DATE
            this.scopeManager.setLastResult(variable);
            this.scopeManager.setLastResultMinus(factor.getMinus());
        }
    }

    private void tryFactorParenthExpression(Factor factor) throws Errors.InterpreterError {
        if(factor.getParenthExpression() == null){
            return;
        }

        factor.getParenthExpression().accept(this);
        this.setLastResultInverseIfTrue(factor.getMinus());
    }

    private void tryFactorFunctionCall(Factor factor) throws Errors.InterpreterError {
        if(factor.getFunctionCall() == null){
            return;
        }

        factor.getFunctionCall().accept(this);
        this.setLastResultInverseIfTrue(factor.getMinus());
    }

    private void setLastResultInverseIfTrue(boolean minus){
        if(minus){
            if(this.scopeManager.getLastResult().getType() == ValueType.NUM){
                this.scopeManager.setLastResult(new Value(ValueType.NUM, -this.scopeManager.getLastResult().getDoubleValue()));
            }
            else{
                this.scopeManager.setLastResultMinus(!this.scopeManager.getLastResult().getBoolValue());
            }
        }
    }

    private void tryFactorNum(Factor factor){
        if(factor.getNum() == null){
            return;
        }

        if(factor.getMinus()){
            this.scopeManager.setLastResult(new Value(ValueType.NUM, -factor.getNum()));
        }
        else{
            this.scopeManager.setLastResult(new Value(ValueType.NUM, factor.getNum()));
        }
    }

    private void executeFunction(FunctionDef functionDef, Value[] arguments) throws Errors.InterpreterError {
        Utils.checkArguments(functionDef, arguments);
        this.scopeManager.createNewScopeAndSwitch(functionDef);
        this.addArgumentsToFunctionScope(functionDef, arguments);

        functionDef.getBlock().accept(this);
        Utils.checkReturnType(functionDef, this.scopeManager.getReturnResult());
        this.scopeManager.switchToParentScope();
    }

    private void addArgumentsToFunctionScope(FunctionDef functionDef, Value[] arguments) throws Errors.InterpreterError {
        int iter = 0;
        for(Signature parameterSignature : functionDef.getParameters().getSignatures()){
            switch (arguments[iter].getType()) {
                case NUM -> this.scopeManager.addVar(parameterSignature.getId(), new Value(ValueType.NUM, arguments[iter].getDoubleValue()));
                case DATE -> this.scopeManager.addVar(parameterSignature.getId(), new Value(ValueType.DATE, arguments[iter].getStrValue()));
                case TIME -> this.scopeManager.addVar(parameterSignature.getId(), new Value(ValueType.TIME, arguments[iter].getStrValue()));
                default -> throw new Errors.InterpreterError("Error addArgumentsToFunctionScope");
            }
            iter = iter + 1;
        }
    }

    private void requireVarType(Type type) throws Errors.InvalidVariableType {
        if(type != Type.NUM && type != Type.DATE && type != Type.TIME){
            throw new Errors.InvalidVariableType(type.name());
        }
    }

    private void requireValueOfType(Value value, ValueType type) throws Errors.InterpreterError {
        if(value.getType() != type){
            throw new Errors.InvalidVariableType(value.getType().name());
        }
    }

    private String getTimeStringFromDuration(long duration) throws Errors.TooBigTimeDuration {
        int hours = (int) (duration / 3600);
        int minutes = (int) ((duration - hours * 3600L) / 60);
        int seconds = (int) (duration - hours * 3600L - minutes * 60);

        if(hours > 99){
            throw new Errors.TooBigTimeDuration();
        }
        return String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    public Parser getParser() { return this.parser; }
    public ScopeManager getScopeManager() { return this.scopeManager; }
}
