/*
 *	Name:		ParenthExpression.java
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

public class ParenthExpression implements Visitable {    // "(", expression, ")" ;
    private Expression expression;

    public ParenthExpression(Expression expression){
        this.expression = expression;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public Expression getExpression() {
        return this.expression;
    }
}
