package main.grammar;

import main.visitor.Visitable;
import main.visitor.Visitor;

public class WhileStatement extends Statement implements Visitable {    // "while", "(", "orCondition", ")", block ;
    private Condition condition;
    private Block block;

    public WhileStatement(Condition condition, Block block){
        this.condition = condition;
        this.block = block;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    @Override
    public Condition getOrCond() {
        return this.condition;
    }

    public Block getBlock() {
        return this.block;
    }
}
