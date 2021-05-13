package main.grammar;

import main.visitor.Visitable;
import main.visitor.Visitor;

public class ParenthExpression implements Visitable {    // "(", expression, ")" ;
    private Expression expression;

    public ParenthExpression(Expression expression){
        this.expression = expression;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public Expression getExpression() {
        return this.expression;
    }
}
