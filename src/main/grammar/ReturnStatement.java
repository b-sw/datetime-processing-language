package main.grammar;

import main.visitor.Visitable;
import main.visitor.Visitor;

public class ReturnStatement extends Statement implements Visitable {   // "return", expression, ";" ;
    private Expression expression;

    public ReturnStatement(Expression expression){
        this.expression = expression;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public Expression getExpression() { return this.expression; }
}
