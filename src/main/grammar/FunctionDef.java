/*
 *	Name:		FunctionDef.java
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

public class FunctionDef implements Visitable {      // signature, "(", parameters, ")", block;
    private Signature signature;
    private Parameters parameters;
    private Block block;

    public FunctionDef(Signature signature, Parameters parameters, Block block){
        this.signature = signature;
        this.parameters = parameters;
        this.block = block;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public Signature getSignature() {
        return this.signature;
    }

    public Parameters getParameters() {
        return this.parameters;
    }

    public Block getBlock() {
        return this.block;
    }
}
