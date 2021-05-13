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

public class Block extends Statement{        // "{", { statement }, "}" ;
    private Statement[] statements;

    public Block(Statement[] statements){
        this.statements = statements;
    }

    public Statement[] getStatements() { return this.statements; }
}
