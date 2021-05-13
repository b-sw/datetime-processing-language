package main.grammar;

import main.visitor.Visitable;
import main.visitor.Visitor;

public class AssignStatement extends Statement implements Visitable {   // id, assignmentOp, expression, ";" ;
    private String id;
    private Expression expression;

    public AssignStatement(String id, Expression expression){
        this.id = id;
        this.expression = expression;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public String getId() { return this.id; }
    public Expression getExpression() { return this.expression; }
}
