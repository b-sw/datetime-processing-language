package main.grammar;

import main.visitor.Visitable;
import main.visitor.Visitor;

public class AndCond extends Condition implements Visitable{    // equalCond, { andOp, equalCond } ;
    private EqualCond[] equalConds;

    public AndCond(EqualCond[] equalConds){
        this.equalConds = equalConds;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public EqualCond[] getEqualConds() { return this.equalConds; }
}
