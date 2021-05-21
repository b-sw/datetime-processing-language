/*
 *	Name:		Value.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.interpreter.scope;

import main.errors.Errors;
import main.interpreter.Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Value {
    private ValueType type;
    private String strValue;
    private double doubleValue;
    private boolean boolValue;

    public Value(ValueType type, String strValue){
        this.type = type;
        this.strValue = strValue;

        this.boolValue = false;
    }

    public Value(ValueType type, boolean minus, String strValue){
        this.type = type;
        this.strValue = strValue;
        this.boolValue = minus;
    }

    public Value(ValueType type, double doubleValue){
        this.type = type;
        this.doubleValue = doubleValue;

        this.boolValue = false;
        this.strValue = null;
    }

    public Value(ValueType type, boolean boolValue){
        this.type = type;
        this.boolValue = boolValue;

        this.strValue = null;
    }

    public Value(ValueType type){
        this.type = type;

        this.strValue = null;
    }

    public boolean greaterThan(Value secondValue) throws Errors.InterpreterError {
        if(this.getType() != secondValue.getType()){
            throw new Errors.InvalidTypesCompare(this.type.name(), secondValue.getType().name());
        }

        switch(this.type){
            case NUM:
                return this.getDoubleValue() > secondValue.getDoubleValue();
            case DATE:
                DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.uuuu.HH:mm:ss");
                LocalDateTime date1 = LocalDateTime.parse(this.strValue, f);
                LocalDateTime date2 = LocalDateTime.parse(secondValue.getStrValue(), f);
                return date1.isAfter(date2);
            case TIME:
                int[] time1 = Utils.getTime(this.strValue);
                int[] time2 = Utils.getTime(secondValue.getStrValue());

                if(time1[0] < time2[0]){
                    return false;
                }
                if(time1[0] == time2[0] && time1[1] < time2[1]){
                    return false;
                }
                if(time1[0] == time2[0] && time1[1] == time2[1] && time1[2] < time2[2]){
                    return false;
                }
                return true;
        }
        throw new Errors.InvalidTypeCompare(this.type.name());
    }

    public boolean lessThan(Value secondValue) throws Errors.InterpreterError {
        switch(this.type){
            case NUM:
                return this.getDoubleValue() < secondValue.getDoubleValue();
            case DATE:
                DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.uuuu.HH:mm:ss");
                LocalDateTime date1 = LocalDateTime.parse(this.strValue, f);
                LocalDateTime date2 = LocalDateTime.parse(secondValue.getStrValue(), f);
                return date1.isBefore(date2);
            case TIME:
                int[] time1 = Utils.getTime(this.strValue);
                int[] time2 = Utils.getTime(secondValue.getStrValue());

                if(time1[0] > time2[0]){
                    return false;
                }
                if(time1[0] == time2[0] && time1[1] > time2[1]){
                    return false;
                }
                if(time1[0] == time2[0] && time1[1] == time2[1] && time1[2] > time2[2]){
                    return false;
                }
                return true;
        }
        throw new Errors.InvalidTypeCompare(this.type.name());
    }

    public boolean equals(Value secondValue) throws Errors.InterpreterError {
        switch(this.type){
            case NUM:
                return this.getDoubleValue() == secondValue.getDoubleValue();
            case DATE:
                DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.uuuu.HH:mm:ss");
                LocalDateTime date1 = LocalDateTime.parse(this.strValue, f);
                LocalDateTime date2 = LocalDateTime.parse(secondValue.getStrValue(), f);
                return date1.isEqual(date2);
            case TIME:
                int[] time1 = Utils.getTime(this.strValue);
                int[] time2 = Utils.getTime(secondValue.getStrValue());

                return time1[0] == time2[0] &&
                        time1[1] == time2[1] &&
                        time1[2] == time2[2];
        }
        throw new Errors.InvalidTypeCompare(this.type.name());
    }

    public ValueType getType() { return this.type; }
    public String getStrValue() { return this.strValue; }
    public double getDoubleValue() { return this.doubleValue; }
    public boolean getBoolValue() { return this.boolValue; }
    public void setBoolValue(boolean boolValue) { this.boolValue = boolValue; }
}
