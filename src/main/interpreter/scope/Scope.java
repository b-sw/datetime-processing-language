/*
 *	Name:		Scope.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.interpreter.scope;

import main.errors.Errors;
import main.grammar.FunctionDef;

import java.util.HashMap;

public class Scope {
    private String name;
    private Scope parentScope;
    private HashMap<String, Value> variables;
    private HashMap<String, FunctionDef> functionDefs;

    private Value lastResult;
    private Value returnResult;

    public Scope(String name){
        this.name = name;
        this.variables = new HashMap<>();
        this.functionDefs = new HashMap<>();

        this.parentScope = null;
    }

    public Scope(String name, Scope parentScope){
        this.name = name;
        this.parentScope = parentScope;
        this.variables = new HashMap<>();
        this.functionDefs = new HashMap<>();
    }

    public void addVariable(String name, Value value){
        this.variables.put(name, value);
    }

    public Value getVariable(String name) throws Errors.InterpreterError {
        requireDeclaredVar(name);
        return this.variables.get(name);
    }

    public void addFunction(String name, FunctionDef functionDef){
        this.functionDefs.put(name, functionDef);
    }

    public FunctionDef getFunctionDef(String name) throws Errors.InterpreterError {
        requireDeclaredFun(name);
        return this.functionDefs.get(name);
    }

    public boolean isDeclared(String name){
        return this.variables.containsKey(name);
    }

    public void requireDeclaredVar(String name) throws Errors.InterpreterError {
        if(!variables.containsKey(name)){
            throw new Errors.UndeclaredVariable(name);
        }
    }

    public void requireUndeclaredVar(String name) throws Errors.InterpreterError {
        if(variables.containsKey(name)){
            throw new Errors.OverwriteError(name);
        }
    }

    public void requireDeclaredFun(String name) throws Errors.InterpreterError {
        if(!functionDefs.containsKey(name)){
            throw new Errors.UndeclaredFunction(name);
        }
    }

    public HashMap<String, Value> getVariables(){ return this.variables; }
    public HashMap<String, FunctionDef> getFunctionDefs(){ return this.functionDefs; }
    public Scope getParentScope() { return this.parentScope; }
    public String getName() { return this.name; }
    public void setLastResult(Value lastResult) { this.lastResult = lastResult; }
    public void setReturnResult(Value returnResult) { this.returnResult = returnResult; }
    public Value getLastResult() { return this.lastResult; }
    public Value getReturnResult() { return this.returnResult; }
}
