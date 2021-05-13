package main.grammar;

import main.parser.operators.EqualOperator;
import main.visitor.Visitable;
import main.visitor.Visitor;

public class EqualCond extends Condition implements Visitable {     // relationCond, [ equalOp, relationCond ] ;
    private RelationCond relationCond1;
    private RelationCond relationCond2;
    private EqualOperator equalOp;

    public EqualCond(RelationCond relationCond){
        this.relationCond1 = relationCond;
        this.relationCond2 = null;
        this.equalOp = null;
    }

    public EqualCond(RelationCond relationCond1, EqualOperator equalOp, RelationCond relationCond2){
        this.relationCond1 = relationCond1;
        this.relationCond2 = relationCond2;
        this.equalOp = equalOp;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public RelationCond getRelationCond1() { return this.relationCond1; }
    public RelationCond getRelationCond2() { return this.relationCond2; }
    public EqualOperator getEqualOp() { return this.equalOp; }
}
