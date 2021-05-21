/*
 *	Name:		Term.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.grammar;

import main.errors.Errors;
import main.grammar.operators.MultOperator;
import main.visitor.Visitable;
import main.visitor.Visitor;

public class Term implements Visitable { // factor, { multOp, factor } ;
    private Factor[] factors;
    private MultOperator[] multOperators;

    public Term(Factor[] factors, MultOperator[] multOperators){
        this.factors = factors;
        this.multOperators = multOperators;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public Factor[] getFactors() { return this.factors; }
    public MultOperator[] getMultOperators() { return this.multOperators; }
}
