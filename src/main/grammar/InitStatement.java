/*
 *	Name:		InitStatement.java
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

public class InitStatement extends Statement implements Visitable { // signature, [ assignmentOp, expression ], ";" ;
    private Signature signature;
    private Expression expression;

    public InitStatement(Signature signature, Expression expression){
        this.signature = signature;
        this.expression = expression;
    }

    public InitStatement(Signature signature){
        this.signature = signature;
        this.expression = null;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public Signature getSignature() { return this.signature; }
    public Expression getExpression() { return this.expression; }
}
