package main.grammar;

import main.visitor.Visitable;
import main.visitor.Visitor;

public class PrintStatement extends Statement implements Visitable {    // "print", "(", printable, { ",", printable }, ")", ";" ;
    private Printable[] printables;

    public PrintStatement(Printable[] printables){
        this.printables = printables;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    @Override
    public Printable[] getPrintables() {
        return this.printables;
    }
}
