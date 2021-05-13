package main.grammar;

import main.visitor.Visitable;
import main.visitor.Visitor;

public class OrCond extends Condition implements Visitable {    // andCond, { orOp, andCond } ;
    private AndCond[] andConds;

    public OrCond(AndCond[] andConds){
        this.andConds = andConds;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public AndCond[] getAndConds() { return this.andConds; }
}
