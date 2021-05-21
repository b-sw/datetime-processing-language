/*
 *	Name:		Utils.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.interpreter;

import main.grammar.FunctionDef;
import main.interpreter.scope.Value;

import java.util.Arrays;

public class Utils {
    public static void checkReturnType(FunctionDef functionDef, Value returnResult){}
    public static void checkArguments(FunctionDef functionDef, Value[] arguments){}
    public static int[] getTime(String str){
        String[] times = str.split(":");
        return Arrays.stream(times).mapToInt(Integer::parseInt).toArray();
    }
    public static void checkNumberOfArguments(FunctionDef functionDef, Value[] arguments){}
    public static void checkTypesOfArguments(FunctionDef functionDef, Value[] arguments){}
}
