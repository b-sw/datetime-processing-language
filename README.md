# Interpreter for datetime processing

Compilation Techniques project @ Warsaw University of Technology. The aim of the project was to create an interpreter for simple C-based language with built-in date and time types. The implemented solution allows user to use a variety of features such as loops or conditions and perform basic operations on built-in types.

<!-- TABLE OF CONTENTS -->
## Table of Contents

* [Author](#author)
* [Requirements](#requirements)
* [Run guide](#run-guide)
* [Language features](#language-features)
* [Usage example](#usage-example)
* [Language details](#language-details)
* [Testing](#testing)
* [Grammar](#grammar)

## Author 
Bartosz Świtalski  

## Requirements
* JDK 15

## Run guide
```
/datetime-processing-language/src$ java main.Main <file_name>
```
**file_name** - file to interpret

### output
After program is run it writes the output to standard output.

## Language features
- `date` type
- `time` type
- `num` type
- arithmetic operators (`+`, `-`, `*`, `/`)
- logical operators (`&&`, `||`, `!`) 
- relational operators (`>`, `<`, `>=`, `<=`)
- parentheses
- `if-else-if` conditional statement
- `while` loop
- ability to define own methods
- built-in method `print()`

## Usage example
```c++
void check(date d1, date d2, num n1, num n2){
  if (d1 >= d2 && n1 > n2 || n1 < n2){
    print("priorities hierarchy ok");
  }
  else {
    print("oops.. something wrong");
  }
}
void main() {
  date d1 = 01.01.1999.11:11:12;
  date d2 = 01.01.1999.11.11.11;
  num n1 = 12.34;
  num n2 = 13;
  
  check(d1, d2, n1, n2);
}
// output: priorities hierarchy ok
```

## Language details
Language, technical details and more usage examples are to be found in `doc` directory in `manual` file.

## Testing
A good number of tests has been written for numerous parts of the project. Those include unit tests implemented with JUnit and additional functional tests that can be run using the included bash script.

### JUnit
JUnit tests in this project were written in order to test:
- source
- lexer
- parser
- scope
- interpreter

They can be found under `src/test` directory.

### Bash script for functional testing
In order to test whether interpreter works correctly I wrote a bash script that allows user to compare interpretable program output with desired(expected) output. In order to do so one must place two files in the `resources/` directory: 
- an interpretable file with an `.in` extension 
- file with desired(expected) output with an `.cmp` extension

After placing those files in the appropriate directory one should run the `test_output` bash script from `src/` directory. If any of the tests are unsuccesful the procedure will stop and exit with a proper message.

## Grammar
*EBNF notation*

| &nbsp; | &nbsp;  |
|---------------| :-----|
| program | = { functionDef } ; |
| functionDef | = signature, "(", parameters, ")", block ; |
| functionCall | = id, "(", arguments, ")" ; |
| signature | = type, id ; |
| parameters | = [ signature { ",", signature } ] ; |
| arguments| = [ expression { ”,”, expression } ];|
| block | = ”{”, { statement }, ”}” ; |
| statement | = block \| ifStatement \| whileStatement \| printStatement \| returnStatement \| initStatement \| assignStatement \| ( functionCall, ”;” ) ; |
| ifStatement | = ”if”, ”(”, orCondition, ”)”, statement,[ ”else”, statement ] ; |
| whileStatement | = ”while”, ”(”, ”orCondition”, ”)”, block ;|
| printStatement | = ”print”, ”(”, printable, { ”,”, printable }, ”)”, ”;” ;|
| returnStatement| = ”return”, expression, ”;” ;|
| initStatement | = signature, [ assignmentOp, expression ], ”;” ;|
| assignStatement | = id, assignmentOp, expression, ”;” ;|
| printable | = expression \| string ;|
| expression | = term, { addOp, term } ;|
| parenthExpr | = ”(”, expression, ”)” ;|
| term | = factor, { multOp, factor } ;|
| factor | = [”-”], ( number \| date \| time \| id \| parenthExpr \| functionCall) ;|
| orCondition | = andCond, { orOp, andCond } ;|
| andCond | = equalCond, { andOp, equalCond } ;|
| equalCond | = relationCond, [ equalOp, relationCond ] ;|
| relationCond | = primaryCond, [ relationOp, primaryCond ] ;|
| primaryCond | = [ negationOp ], ( parenthCond \| expression ) ;|
| parentCond | = ”(”, orCondition, ”)” ;|
| negationOp | = "!" ;|
| assignmentOp | = "=" ;|
| orOp | = "||" ;|
| andOp | = "&&" ;|
| equalOp | = "==" ;|
| relationOp | = ”>” \| ”<” \| ”>=” \| ”<=” ;|
| addOp | = ”+” \| ”-” ;|
| multOp | = ”*” \| ”/” ;|
| number | = ( digit,[ ”.”, digit, { digit } ] ) \| ( naturalDigit, { digit }, [ ”.”, digit, { digit } ] ) ;|
| date | = digit, digit, ”.”, digit, digit, ”.”, digit, [ digit, [ digit, [ digit ] ] ], ”.”, time ;|
| time | = digit, digit, ”:”, digit, digit, ”:”, digit, digit ;|
| id | = letter, { digit \| letter | ”_” } ;|
| letter | = ”a” \| ... \| ”z” \| ”A” \| ... \| ”Z” ;|
| digit | = ”0” \| ... \| ”9” ;|
| naturalDigit | = ”1” \| ... \| ”9” ;|
| type | = ”num” \| ”date” \| ”time” ;|
| string | = ”””, { ( anyChar - ””” ) \| ” ” }, ”””  ;|
| anyChar | = ? all visible characters ?|
