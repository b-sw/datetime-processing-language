/*
 *	Name:		Program.java
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

public class Program implements Visitable {      // { functionDef } ;
    private FunctionDef[] functionDefs;

    public Program(FunctionDef[] functionDefs){
        this.functionDefs = functionDefs;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    public FunctionDef[] getFunctionDefs() { return this.functionDefs; }
}
