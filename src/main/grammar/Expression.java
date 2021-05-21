/*
 *	Name:		Expression.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.grammar;

import main.errors.Errors;
import main.grammar.operators.AddOperator;
import main.visitor.Visitable;
import main.visitor.Visitor;

public class Expression implements Visitable {   // term, { addOp, term } ;
    private Term[] terms;
    private AddOperator[] addOperators;

    public Expression(Term[] terms, AddOperator[] addOperators){
        this.terms = terms;
        this.addOperators = addOperators;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public Term[] getTerms() { return this.terms; }
    public AddOperator[] getAddOperators() { return this.addOperators; }
}
