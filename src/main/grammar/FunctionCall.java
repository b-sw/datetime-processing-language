package main.grammar;

import main.visitor.Visitable;
import main.visitor.Visitor;

public class FunctionCall extends Statement implements Visitable { // id, "(", arguments, ")" ;
    private String id;
    private Arguments arguments;

    public FunctionCall(String id, Arguments arguments){
        this.id = id;
        this.arguments = arguments;
    }

    @Override
    public void accept(Visitor visitor) {
//        visitor.visit(this);
    }

    public String getId() {
        return this.id;
    }

    public Arguments getArguments() {
        return this.arguments;
    }
}
