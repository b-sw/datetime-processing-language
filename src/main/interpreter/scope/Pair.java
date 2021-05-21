/*
 *	Name:		Pair.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.interpreter.scope;

public class Pair<T, U>{
    public final T t;
    public final U u;

    public Pair(T t, U u){
        this.t = t;
        this.u = u;
    }
}
