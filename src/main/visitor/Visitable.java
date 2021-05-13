/*
 *	Name:		SourceTest.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.visitor;

import main.visitor.Visitor;

public interface Visitable {
    public void accept(Visitor visitor);
}
