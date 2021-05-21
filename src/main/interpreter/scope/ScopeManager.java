/*
 *	Name:		ScopeManager.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.interpreter.scope;

import main.errors.Errors;
import main.grammar.Block;
import main.grammar.FunctionDef;

public class ScopeManager {
    private Scope globalScope;
    private Scope localScope;

    public ScopeManager(){
        this.globalScope = new Scope("global");
        this.localScope = new Scope("main");
    }

    public void addVar(String name, Value value) throws Errors.InterpreterError {
        this.localScope.requireUndeclaredVar(name);
        this.localScope.addVariable(name, value);
    }

    public void updateVar(String name, Value value) throws Errors.InterpreterError {
        this.localScope.requireDeclaredVar(name);
        if(this.localScope.getVariable(name).getType() != value.getType()){
            throw new Errors.IncompatibleTypesError(name);
        }
        this.localScope.addVariable(name, value);
    }

    public void addFunction(String name, FunctionDef functionDef){
        this.globalScope.addFunction(name, functionDef);
    }

    public FunctionDef getFunction(String name) throws Errors.InterpreterError {
        return this.globalScope.getFunctionDef(name);
    }

    public void createNewScopeAndSwitch(FunctionDef functionDef){
        this.localScope = new Scope(functionDef.getSignature().getId(), this.localScope);
    }

    public void switchToParentScope() throws Errors.NoParentContextError {
        if(this.localScope.getParentScope() == null){
            throw new Errors.NoParentContextError(this.localScope.getName());
        }

        Value lastResult = this.localScope.getReturnResult();
        this.localScope = this.localScope.getParentScope();
        this.localScope.setLastResult(lastResult);
    }

    public Value getVariable(String name) throws Errors.InterpreterError {
        return this.localScope.getVariable(name);
    }

    public void setLastResultAsReturnResult(){
        this.localScope.setReturnResult(this.localScope.getLastResult());
    }

    public Scope getGlobalScope() { return this.globalScope; }
    public Scope getLocalScope() { return this.localScope; }

    public Value getLastResult() { return this.localScope.getLastResult(); }
    public Value getReturnResult() { return this.localScope.getReturnResult(); }
    public void setLastResult(Value lastResult) { this.localScope.setLastResult(lastResult); }
    public void setLastResultMinus(boolean minus) { this.localScope.getLastResult().setBoolValue(minus);}
}
