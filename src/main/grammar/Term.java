package main.grammar;

import main.parser.operators.MultOperator;
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
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public Factor[] getFactors() { return this.factors; }
    public MultOperator[] getMultOperators() { return this.multOperators; }
}
