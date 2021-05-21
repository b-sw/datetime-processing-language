/*
 *	Name:		Block.java
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

public class Block extends Statement implements Visitable {        // "{", { statement }, "}" ;
    private Statement[] statements;

    public Block(Statement[] statements){
        this.statements = statements;
    }

    public Statement[] getStatements() { return this.statements; }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }
}
