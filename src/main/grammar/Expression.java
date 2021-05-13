package main.grammar;

import main.parser.operators.AddOperator;
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
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public Term[] getTerms() { return this.terms; }
    public AddOperator[] getAddOperators() { return this.addOperators; }
}
