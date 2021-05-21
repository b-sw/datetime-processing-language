/*
 *	Name:		AssignStatement.java
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

public class AssignStatement extends Statement implements Visitable {   // id, assignmentOp, expression, ";" ;
    private String id;
    private Expression expression;

    public AssignStatement(String id, Expression expression){
        this.id = id;
        this.expression = expression;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public String getId() { return this.id; }
    public Expression getExpression() { return this.expression; }
}
