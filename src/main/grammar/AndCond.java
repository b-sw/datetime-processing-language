/*
 *	Name:		AndCond.java
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

public class AndCond extends Condition implements Visitable{    // equalCond, { andOp, equalCond } ;
    private EqualCond[] equalConds;

    public AndCond(EqualCond[] equalConds){
        this.equalConds = equalConds;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public EqualCond[] getEqualConds() { return this.equalConds; }
}
