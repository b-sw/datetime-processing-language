package main.grammar;

public class Arguments {    // [ expression { ",", expression } ] ;
    private Expression[] expressions;

    public Arguments(Expression[] expressions){
        this.expressions = expressions;
    }

    public Expression[] getExpressions() { return this.expressions; }
}
