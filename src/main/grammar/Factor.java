package main.grammar;

import main.visitor.Visitable;
import main.visitor.Visitor;

public class Factor implements Visitable {   // ["-"], ( number | date | time | id | parenthExpr | functionCall ) ;
    private boolean minus;
    private double num;
    private Date date;
    private Time time;
    private String id;
    private ParenthExpression parenthExpression;
    private FunctionCall functionCall;

    public Factor(double num){
        this.num = num;

        this.minus = false;
        this.date = null;
        this.time = null;
        this.id = null;
        this.parenthExpression = null;
        this.functionCall = null;
    }

    public Factor(boolean minus, double num){
        this.minus = minus;
        this.num = num;

        this.date = null;
        this.time = null;
        this.id = null;
        this.parenthExpression = null;
        this.functionCall = null;
    }

    public Factor(Date date){
        this.date = date;

        this.minus = false;
        this.time = null;
        this.id = null;
        this.parenthExpression = null;
        this.functionCall = null;
    }

    public Factor(Time time){
        this.time = time;

        this.minus = false;
        this.date = null;
        this.id = null;
        this.parenthExpression = null;
        this.functionCall = null;
    }

    public Factor(boolean minus, Time time){
        this.minus = minus;
        this.time = time;

        this.date = null;
        this.id = null;
        this.parenthExpression = null;
        this.functionCall = null;
    }

    public Factor(String id){
        this.id = id;

        this.minus = false;
        this.date = null;
        this.time = null;
        this.parenthExpression = null;
        this.functionCall = null;
    }

    public Factor(boolean minus, String id){
        this.id = id;
        this.minus = minus;

        this.date = null;
        this.time = null;
        this.parenthExpression = null;
        this.functionCall = null;
    }

    public Factor(ParenthExpression parenthExpression){
        this.parenthExpression = parenthExpression;

        this.minus = false;
        this.date = null;
        this.time = null;
        this.id = null;
        this.functionCall = null;
    }

    public Factor(boolean minus, ParenthExpression parenthExpression){
        this.parenthExpression = parenthExpression;
        this.minus = minus;

        this.date = null;
        this.time = null;
        this.id = null;
        this.functionCall = null;
    }

    public Factor(FunctionCall functionCall){
        this.functionCall = functionCall;

        this.minus = false;
        this.date = null;
        this.time = null;
        this.id = null;
        this.parenthExpression = null;
    }

    public Factor(boolean minus, FunctionCall functionCall){
        this.functionCall = functionCall;
        this.minus = minus;

        this.date = null;
        this.time = null;
        this.id = null;
        this.parenthExpression = null;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public String getId() {
        return this.id;
    }

    public double getNum() {
        return this.num;
    }

    public boolean getMinus() {
        return this.minus;
    }

    public Date getDate() {
        return this.date;
    }

    public Time getTime() {
        return this.time;
    }

    public ParenthExpression getParenthExpression() {
        return this.parenthExpression;
    }

    public FunctionCall getFunctionCall() {
        return this.functionCall;
    }
}
