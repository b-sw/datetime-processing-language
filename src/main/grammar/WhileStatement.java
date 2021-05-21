/*
 *	Name:		WhileStatement.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.grammar;

import main.errors.Errors;
import main.visitor.Visitable;
import main.visitor.Visitor;

public class WhileStatement extends Statement implements Visitable {    // "while", "(", "orCondition", ")", block ;
    private OrCond condition;
    private Block block;

    public WhileStatement(OrCond condition, Block block){
        this.condition = condition;
        this.block = block;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    @Override
    public OrCond getOrCond() {
        return this.condition;
    }

    public Block getBlock() {
        return this.block;
    }
}
