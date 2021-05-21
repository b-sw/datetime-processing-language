/*
 *	Name:		ParenthCond.java
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

public class ParenthCond extends Condition implements Visitable {   // "(", orCondition, ")" ;
    private OrCond condition;

    public ParenthCond(OrCond condition){
        this.condition = condition;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public OrCond getCondition() {
        return this.condition;
    }
}
