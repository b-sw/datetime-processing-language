/*
 *	Name:		Date.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.grammar;

public class Date {
    private String dateStr;

    public Date(String dateStr){
        this.dateStr = dateStr;
    }

    public String getDateStr() {
        return this.dateStr;
    }
}
