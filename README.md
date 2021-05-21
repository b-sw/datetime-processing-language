# Interpreter for datetime processing

Compilation Techniques project @ Warsaw University of Technology. The aim of the project was to create an interpreter for simple C-based language with built-in date and time types. The implemented solution allows user to use a variety of features such as loops or conditions and perform basic operations on built-in types.

<!-- TABLE OF CONTENTS -->
## Table of Contents

* [Author](#author)
* [Requirements](#requirements)
* [Run guide](#run-guide)
* [Language features](#language-features)
* [Language details](#language-details)

## Author 
Bartosz Świtalski  
bswitalski.main@gmail.com

## Requirements
* JDK 15

## Run guide
```
/datetime-processing-language/src$ java main.Main <file_name>
```
**file_name** - file to interpret

### output
After running the program it will write the output to standard output.

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

## Language details

