/*
 *	Name:		Time.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.grammar;

public class Time {
    private String timeStr;

    public Time(String timeStr){
        this.timeStr = timeStr;
    }

    public String getTimeStr() {
        return this.timeStr;
    }
}
