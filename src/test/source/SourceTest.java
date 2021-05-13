/*
 *	Name:		SourceTest.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package source;

import main.source.Source;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class SourceTest {
    private Source source;

    @BeforeEach
    void setUp() throws IOException {
        this.source = new Source("./resources/sourceTest/SourceTest.txt");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Source: Execution of JUNIT test file done");
    }

    @Test
    void getLine() {
        int line = this.source.getLine();
        assertEquals(1, line);
    }

    @Test
    void getColumn() {
        int column = this.source.getColumn();
        assertEquals(0, column);
    }

    @Test
    void getBytePosition() {
        int bytePosition = this.source.getBytePosition();
        assertEquals(1, bytePosition);
    }

    @Test
    void getChar() {
        char character = (char)this.source.getChar();
        assertEquals('\0', character);
    }

    @Test
    void moveCharOnePosFileStream() throws IOException {
        this.source.moveCharOnePos();
        assertEquals('t', this.source.getChar());
        this.source.moveCharOnePos();
        assertEquals(-1, this.source.getChar());
    }

    @Test
    void moveCharOnePosStringStream() throws IOException {
        String str = "test";

        this.source = new Source(new BufferedInputStream(new ByteArrayInputStream(str.getBytes())));
        this.source.moveCharOnePos();
        assertEquals('t', this.source.getChar());

    }
}