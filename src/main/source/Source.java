/*
 *	Name:		Source.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package main.source;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

public class Source{
    private InputStream stream;
    private int line;
    private int column;
    private int bytePosition;
    private int _char;
    private int markChar;
    private int markLine;
    private int markColumn;

    public Source(String fileName) throws IOException{
        this.stream = new BufferedInputStream(new FileInputStream(fileName));

        this.line = 1;
        this.column = 0;
        this.bytePosition = 1;
        this._char = '\0';
    }

    public Source(BufferedInputStream stream){
        this.stream = stream;

        this.line = 1;
        this.column = 0;
        this.bytePosition = 1;
        this._char = '\0';
    }

    public Source(){
        this.line = 1;
        this.column = 0;
        this.bytePosition = 0;
        this._char = '\0';
    }

    public void moveCharOnePos() throws IOException {
        this._char = this.stream.read();

        this.bytePosition += 1;

        if (this._char == '\n'){
            this.line += 1;
            this.column = 0;
        }
        else{
            this.column += 1;
        }
    }

    public void markStream(int readLimit){
        this.markChar = this._char;
        this.markLine = this.line;
        this.markColumn = this.column;
        this.stream.mark(readLimit);
    }

    public void resetStream() throws IOException {
        this.stream.reset();
        this._char = this.markChar;
        this.line = this.markLine;
        this.column = this.markColumn;
    }

    public InputStream getStream()          { return this.stream; }
    public int getLine()                    { return this.line; }
    public int getColumn()                  { return this.column; }
    public int getBytePosition()            { return this.bytePosition; }
    public int getChar()                    { return this._char; }

}
