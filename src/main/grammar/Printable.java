package main.grammar;

public class Printable {
    private String str;
    private Expression expression;

    public Printable(String str){
        this.str = str;
        this.expression = null;
    }

    public Printable(Expression expression){
        this.expression = expression;
        this.str = null;
    }

    public String getStr() { return this.str; }
    public Expression getExpression() { return this.expression; }
}
