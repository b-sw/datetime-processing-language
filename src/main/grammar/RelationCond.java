/*
 *	Name:		RelationCond.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.grammar;

import main.errors.Errors;
import main.grammar.operators.RelationOperator;
import main.visitor.Visitable;
import main.visitor.Visitor;

public class RelationCond extends Condition implements Visitable {  // primaryCond, [ relationOp, primaryCond ] ;
    private PrimaryCond primaryCond1;
    private PrimaryCond primaryCond2;
    private RelationOperator relationOp;

    public RelationCond(PrimaryCond primaryCond){
        this.primaryCond1 = primaryCond;
        this.primaryCond2 = null;
        this.relationOp = null;
    }

    public RelationCond(PrimaryCond primaryCond1, RelationOperator relationOp, PrimaryCond primaryCond2){
        this.primaryCond1 = primaryCond1;
        this.primaryCond2 = primaryCond2;
        this.relationOp = relationOp;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public PrimaryCond getPrimaryCond1() { return this.primaryCond1; }
    public PrimaryCond getPrimaryCond2() { return this.primaryCond2; }
    public RelationOperator getRelationOp(){ return this.relationOp; }
}