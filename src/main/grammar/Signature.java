/*
 *	Name:		Signature.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.grammar;

import main.grammar.operators.Type;

public class Signature{     // type, id ;
    private Type type;
    private String id;

    public Signature(Type type, String id){
        this.type = type;
        this.id = id;
    }

    public Type getType(){ return this.type; }
    public String getId(){ return this.id; }
}
