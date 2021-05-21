/*
 *	Name:		ReturnStatement.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.grammar;

import main.errors.Errors;
import main.visitor.Visitable;
import main.visitor.Visitor;

public class ReturnStatement extends Statement implements Visitable {   // "return", expression, ";" ;
    private Expression expression;

    public ReturnStatement(Expression expression){
        this.expression = expression;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public Expression getExpression() { return this.expression; }
}
