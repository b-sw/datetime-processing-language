/*
 *	Name:		OrCond.java
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

public class OrCond extends Condition implements Visitable {    // andCond, { orOp, andCond } ;
    private AndCond[] andConds;

    public OrCond(AndCond[] andConds){
        this.andConds = andConds;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public AndCond[] getAndConds() { return this.andConds; }
}
