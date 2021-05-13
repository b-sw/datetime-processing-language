/*
 *	Name:		SourceTest.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.visitor;

import main.grammar.*;

public interface Visitor {
    public void visit(Program programme);
    public void visit(FunctionDef functionDef);
    public void visit(FunctionCall functionCall);
    public void visit(Block block);

    public void visit(IfStatement ifStatement);
    public void visit(WhileStatement whileStatement);
    public void visit(ReturnStatement returnStatement);
    public void visit(InitStatement initStatement);
    public void visit(AssignStatement assignStatement);
    public void visit(PrintStatement printStatement);

    public void visit(Expression expression);
    public void visit(Term term);
    public void visit(Factor factor);
    public void visit(ParenthExpression parenthExpression);

    public void visit(OrCond orCond);
    public void visit(AndCond andCond);
    public void visit(EqualCond equalCond);
    public void visit(RelationCond relationCond);
    public void visit(PrimaryCond primaryCond);
    public void visit(ParenthCond parenthCond);
}
