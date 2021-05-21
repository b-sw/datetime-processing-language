/*
 *	Name:		Lexer.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.lexer;

import main.errors.Errors;
import main.source.Source;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Lexer{
    private static final int TOKEN_MAX_LENGTH = 50;
    private static final int STRING_MAX_LENGTH = 1000;

    private final Source source;
    private Token token;

    public Lexer(Source source) throws IOException {
        this.source = source;
        this.source.moveCharOnePos();
    }

    public Lexer(String str) throws IOException {
        this.source = new Source(new BufferedInputStream(new ByteArrayInputStream(str.getBytes())));
        this.source.moveCharOnePos();
    }

    public void getNextToken() throws Errors.TokenError, IOException {
        this.skipWhites();
        this.buildToken();
    }

    private void buildToken() throws Errors.TokenError, IOException {
        if(this.isEof()){
            this.token = new Token(Token.Type.EOT, this.source.getLine(), this.source.getColumn());
        }
        else if(Character.isLetter(this.source.getChar())){
            this.buildKeywordOrIdentifier();
        }
        else if(Character.isDigit(this.source.getChar())){
            if(!this.tryBuildDate() && !this.tryBuildTime()){
                this.buildNumber();
            }
        }
        else if(this.source.getChar() == '"'){
            this.buildString();
        }
        else if(this.trySkipComment()){
            this.buildToken();
        }
        else if(this.tryBuildDoubleOperator());
        else if(this.tryBuildSingleOperator());
        else {
            throw new Errors.TokenInvalid(this.source.getLine(), this.source.getColumn());
        }

    }

    private void buildKeywordOrIdentifier() throws Errors.TokenError, IOException {
        String str = this.readKeywordOrIdentifier();
        this.token = new Token(Attributes.keywords.getOrDefault(str, Token.Type.IDENTIFIER), this.source.getLine(), this.source.getColumn(), str);
    }

    private String readKeywordOrIdentifier() throws Errors.TokenError, IOException {
        StringBuilder sb = new StringBuilder();
        while(Character.isLetter(this.source.getChar()) || Character.isDigit(this.source.getChar()) || (char)this.source.getChar() == '_'){
            sb.append((char)this.source.getChar());

            if(sb.length() > Lexer.TOKEN_MAX_LENGTH){
                throw new Errors.TokenTooLong(this.source.getLine(), this.source.getColumn());
            }

            this.source.moveCharOnePos();
        }
        return sb.toString();
    }

    private boolean tryBuildDate() throws IOException, Errors.TokenError {
        String date = this.readDate();
        if(date != null){
            this.token = new Token(Token.Type.DATE, this.source.getLine(), this.source.getColumn(), date);
            return true;
        }
        return false;
    }


    private String readDate() throws IOException, Errors.TokenError {
        StringBuilder sb = new StringBuilder();

        if(Character.isDigit(this.source.getChar())){
            sb.append((char)this.source.getChar());
            this.source.markStream(19);
            sb.append(this.getPartialDateOrTime('.'));
            sb.append(this.getPartialDateOrTime('.'));

            if(sb.length() != 7){
                this.source.resetStream();
                return null;
            }

            // check if year has more than single digit
            this.source.moveCharOnePos();
            while(Character.isDigit(this.source.getChar())){
                sb.append((char)this.source.getChar());
                this.source.moveCharOnePos();

                if(sb.length() > 10){
                    throw new Errors.DateInvalid(this.source.getLine(), this.source.getColumn());
                }
            }

            if((char)this.source.getChar() != '.'){
                throw new Errors.DateInvalid(this.source.getLine(), this.source.getColumn());
            }

            sb.append((char)this.source.getChar());
            this.source.moveCharOnePos();
            String time = this.readTime();
            sb.append(time);
        }
        return sb.toString();
    }

    private String getPartialDateOrTime(char separator) throws IOException {
        StringBuilder sb = new StringBuilder();

        this.source.moveCharOnePos();
        if(!Character.isDigit(this.source.getChar())){
            return null;
        }
        sb.append((char)this.source.getChar());

        this.source.moveCharOnePos();
        if((char)this.source.getChar() != separator){
            return null;
        }
        sb.append((char)this.source.getChar());

        this.source.moveCharOnePos();
        if(!Character.isDigit(this.source.getChar())){
            return null;
        }
        sb.append((char)this.source.getChar());

        return sb.toString();
    }

    private boolean tryBuildTime() throws IOException {
        String time = this.readTime();
        if(time != null){
            this.token = new Token(Token.Type.TIME, this.source.getLine(), this.source.getColumn(), time);
            return true;
        }
        return false;
    }

    private String readTime() throws IOException {
        StringBuilder sb = new StringBuilder();

        if(Character.isDigit(this.source.getChar())){
            sb.append((char)this.source.getChar());
            this.source.markStream(7);
            sb.append(this.getPartialDateOrTime(':'));
            sb.append(this.getPartialDateOrTime(':'));

            if(sb.length() != 7){
                this.source.resetStream();
                return null;
            }

            // check if time has seconds units digit
            this.source.moveCharOnePos();
            if(Character.isDigit(this.source.getChar())){
                sb.append((char)this.source.getChar());
                this.source.moveCharOnePos();
                return sb.toString();
            }
        }
        this.source.resetStream();
        return null;
    }

    private void buildNumber() throws Errors.TokenError, IOException {
        String number = this.readNumber();
        this.token = new Token(Token.Type.NUMERIC, this.source.getLine(), this.source.getColumn(), Double.parseDouble(number));
    }

    private String readNumber() throws Errors.TokenError, IOException {
        StringBuilder sb = new StringBuilder();

        if(Character.isDigit(this.source.getChar()) && (char)this.source.getChar() != '0'){
            sb = this.readDigits();
            if((char)this.source.getChar() == '.'){
                sb.append((char)this.source.getChar());
                this.source.moveCharOnePos();
                if(Character.isDigit(this.source.getChar())){
                    StringBuilder fractional = this.readDigits();
                    sb.append(fractional.toString());
                }
            }
        }
        else{ /* if '0..'*/
            sb.append((char)this.source.getChar());
            this.source.moveCharOnePos();
            if((char)this.source.getChar() == '.'){
                sb.append((char)this.source.getChar());
                this.source.moveCharOnePos();
                if(Character.isDigit(this.source.getChar())){
                    StringBuilder fractional = this.readDigits();
                    if(Double.parseDouble(fractional.toString()) == 0){
                        throw new Errors.NumberInvalid(this.source.getLine(), this.source.getColumn());
                    }
                    sb.append(fractional.toString());
                }
            }
        }

        return sb.toString();
    }

    private StringBuilder readDigits() throws Errors.TokenError, IOException {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append((char)this.source.getChar());

            if (sb.length() > Lexer.TOKEN_MAX_LENGTH) {
                throw new Errors.TokenTooLong(this.source.getLine(), this.source.getColumn());
            }
            this.source.moveCharOnePos();

        } while (Character.isDigit(this.source.getChar()));

        return sb;
    }

    private void buildString() throws Errors.TokenError, IOException {
        String str = this.readString();
        this.token = new Token(Token.Type.STRING, this.source.getLine(), this.source.getColumn(), str);
    }

    private String readString() throws Errors.StringTooLong, IOException, Errors.StringUnclosed {
        StringBuilder sb = new StringBuilder();
        sb.append((char)this.source.getChar());
        this.source.moveCharOnePos();

        while(this.source.getChar() != '"'){
            sb.append((char)this.source.getChar());
            if(sb.length() > Lexer.STRING_MAX_LENGTH){
                throw new Errors.StringTooLong(this.source.getLine(), this.source.getColumn());
            }
            this.source.moveCharOnePos();

            if(this.isEof()){
                throw new Errors.StringUnclosed(this.getLine(), this.getColumn());
            }
        }
        sb.append((char)this.source.getChar());
        this.source.moveCharOnePos();

        return sb.toString();
    }

    private boolean trySkipComment() throws IOException {
        if((char)this.source.getChar() == '/'){
            this.source.markStream(1);
            this.source.moveCharOnePos();
            if((char)this.source.getChar() == '/'){
                while((char)this.source.getChar() != '\n' && this.source.getChar() != -1){
                    this.source.moveCharOnePos();
                }
                this.skipWhites();
                return true;
            }
            this.source.resetStream();
        }
        return false;
    }

    private boolean tryBuildDoubleOperator() throws IOException {
        for(String key : Attributes.doubleOp.keySet()){
            if((char)this.source.getChar() == key.charAt(0)){
                this.source.markStream(1);
                this.source.moveCharOnePos();

                char secondChar = (char)this.source.getChar();
                if(secondChar == key.charAt(1)){
                    this.token = new Token(Attributes.doubleOp.get(key), this.source.getLine(), this.source.getColumn());
                    this.source.moveCharOnePos();
                    return true;
                }
                else{
                    this.source.resetStream();
                }
            }
        }
        return false;
    }

    private boolean tryBuildSingleOperator() throws IOException {
        if(Attributes.singleOp.containsKey((char)this.source.getChar())){
            this.token = new Token(Attributes.singleOp.get((char)this.source.getChar()), this.source.getLine(), this.source.getColumn());
            this.source.moveCharOnePos();
            return true;
        }
        return false;
    }

    private void skipWhites() throws IOException {
        while(Character.isWhitespace(this.source.getChar())){
            this.source.moveCharOnePos();
        }
    }

    private boolean isEof(){
        return this.source.getChar() == -1;
    }

    public Token getToken() { return this.token; }
    public int getLine()    { return this.source.getLine(); }
    public int getColumn()  { return this.source.getColumn(); }
}