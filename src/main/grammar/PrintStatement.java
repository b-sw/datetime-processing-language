/*
 *	Name:		PrintStatement.java
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

public class PrintStatement extends Statement implements Visitable {    // "print", "(", printable, { ",", printable }, ")", ";" ;
    private Printable[] printables;

    public PrintStatement(Printable[] printables){
        this.printables = printables;
    }

    @Override
    public void accept(Visitor visitor) throws Errors.InterpreterError {
        visitor.visit(this);
    }

    @Override
    public Printable[] getPrintables() {
        return this.printables;
    }
}
