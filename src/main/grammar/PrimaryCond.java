package main.grammar;

import main.visitor.Visitable;
import main.visitor.Visitor;

public class PrimaryCond extends Condition implements Visitable {   // [ negationOp ], ( parenthCond | expression ) ;
    private Boolean negationOp;
    private ParenthCond parenthCond;
    private Expression expression;

    public PrimaryCond(){
        this.negationOp = Boolean.FALSE;
        this.parenthCond = null;
        this.expression = null;
    }

    public PrimaryCond(Boolean negationOp){
        this.negationOp = negationOp;
        this.parenthCond = null;
        this.expression = null;
    }

    public PrimaryCond(ParenthCond parenthCond){
        this.negationOp = Boolean.FALSE;
        this.parenthCond = parenthCond;
        this.expression = null;
    }

    public PrimaryCond(Expression expression){
        this.negationOp = false;
        this.parenthCond = null;
        this.expression = expression;
    }

    public PrimaryCond(Boolean negationOp, ParenthCond parenthCond){
        this.negationOp = negationOp;
        this.parenthCond = parenthCond;
        this.expression = null;
    }

    public PrimaryCond(Boolean negationOp, Expression expression){
        this.negationOp = negationOp;
        this.parenthCond = null;
        this.expression = expression;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public Expression getExpression() {
        return this.expression; }

    public Boolean getNegationOp() {
        return this.negationOp;
    }

    public ParenthCond getParenthCond() {
        return this.parenthCond;
    }
}
