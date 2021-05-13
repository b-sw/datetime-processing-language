/*
 *	Name:		.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.grammar;

public class Parameters{    // [ signature, { ",", signature } ] ;
    private Signature[] signatures;

    public Parameters(Signature[] signatures){
        this.signatures = signatures;
    }

    public Signature[] getSignatures() { return this.signatures; }
}
