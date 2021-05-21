/*
 *	Name:		Statement.java
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

public class Statement implements Visitable {
    public Signature getSignature() { return null; }

    public Expression getExpression() { return null; }

    public Condition getOrCond() {
        return null;
    }

    public Block getIfBlock() {
        return null;
    }

    public Printable[] getPrintables() {
        return null;
    }

    public String getId() { return null; }

    public Statement getElseStatement() { return null; }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {}
}
