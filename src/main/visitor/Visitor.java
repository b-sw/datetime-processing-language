/*
 *	Name:		Visitor.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.visitor;

import main.errors.Errors;
import main.grammar.*;

public interface Visitor {
    public void visit(Program programme) throws Errors.InterpreterError;
    public void visit(FunctionDef functionDef);
    public void visit(FunctionCall functionCall) throws Errors.InterpreterError;
    public void visit(Block block) throws Errors.InterpreterError;

    public void visit(IfStatement ifStatement) throws Errors.InterpreterError;
    public void visit(WhileStatement whileStatement) throws Errors.InterpreterError;
    public void visit(ReturnStatement returnStatement) throws Errors.InterpreterError;
    public void visit(InitStatement initStatement) throws Errors.InterpreterError;
    public void visit(AssignStatement assignStatement) throws Errors.InterpreterError;
    public void visit(PrintStatement printStatement) throws Errors.InterpreterError;

    public void visit(Expression expression) throws Errors.InterpreterError;
    public void visit(Term term) throws Errors.InterpreterError;
    public void visit(Factor factor) throws Errors.InterpreterError;
    public void visit(ParenthExpression parenthExpression) throws Errors.InterpreterError;

    public void visit(OrCond orCond) throws Errors.InterpreterError;
    public void visit(AndCond andCond) throws Errors.InterpreterError;
    public void visit(EqualCond equalCond) throws Errors.InterpreterError;
    public void visit(RelationCond relationCond) throws Errors.InterpreterError;
    public void visit(PrimaryCond primaryCond) throws Errors.InterpreterError;
    public void visit(ParenthCond parenthCond) throws Errors.InterpreterError;
}
