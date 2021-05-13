package main.grammar;

import main.visitor.Visitable;
import main.visitor.Visitor;

public class ParenthCond extends Condition implements Visitable {   // "(", orCondition, ")" ;
    private OrCond condition;

    public ParenthCond(OrCond condition){
        this.condition = condition;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public OrCond getCondition() {
        return this.condition;
    }
}
