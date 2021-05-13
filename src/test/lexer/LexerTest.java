/*
 *	Name:		LexerTest.java
 *	Purpose:
 *
 *	@author:     Bartosz Świtalski
 *
 *	Warsaw University of Technology
 *	Faculty of Electronics and Information Technology
 */
package lexer;

import main.errors.Errors;
import main.lexer.Attributes;
import main.lexer.Lexer;
import main.lexer.Token;
import main.source.Source;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @AfterAll
    static void cleanUp(){
        System.out.println("Lexer: Execution of all JUNIT tests done.");
    }

    @Test
    void testEOF() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("");

        lexer.getNextToken();
        assertEquals(Token.Type.EOT, lexer.getToken().getType());
    }

    @Test
    void testIdentifier() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("identifier");

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("identifier", lexer.getToken().getStr());
    }

    @Test
    void testIdentifierUnderscore() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("id_entifier");

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("id_entifier", lexer.getToken().getStr());
    }

    @Test
    void testIdentifierWithUnary() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("!identifier");

        lexer.getNextToken();
        assertEquals(Token.Type.NOT, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("identifier", lexer.getToken().getStr());
    }

    @Test
    void testIdentifierWithUnary2() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("! identifier");

        lexer.getNextToken();
        assertEquals(Token.Type.NOT, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("identifier", lexer.getToken().getStr());
    }

    @Test
    void testIdentifierError() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("id_ent1f??er");

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("id_ent1f", lexer.getToken().getStr());
        assertThrows(Errors.TokenInvalid.class, lexer::getNextToken);
    }

    @Test
    void testTokenTooLong() throws IOException {
        char[] chars = new char[51];
        Arrays.fill(chars, 'a');
        Lexer lexer = new Lexer(new String(chars));
        assertThrows(Errors.TokenTooLong.class, lexer::getNextToken);
    }

    @Test
    void testInt() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("123");

        lexer.getNextToken();
        assertEquals(Token.Type.NUMERIC, lexer.getToken().getType());
        assertEquals(123, lexer.getToken().getValue());

        lexer.getNextToken();
        assertEquals(Token.Type.EOT, lexer.getToken().getType());
        assertEquals(0.0f, lexer.getToken().getValue());
        assertNull(lexer.getToken().getStr());
    }

    @Test
    void testFloat() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("1.23");

        lexer.getNextToken();
        assertEquals(Token.Type.NUMERIC, lexer.getToken().getType());
        assertEquals(1.23, lexer.getToken().getValue(), 1e-5);
    }

    @Test
    void testFloatRound() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("1.2300");

        lexer.getNextToken();
        assertEquals(Token.Type.NUMERIC, lexer.getToken().getType());
        assertEquals(1.23, lexer.getToken().getValue(), 1e-5);
    }

    @Test
    void testNumericAndChars() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("123abc");

        lexer.getNextToken();
        assertEquals(Token.Type.NUMERIC, lexer.getToken().getType());
        assertEquals(123, lexer.getToken().getValue());

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("abc", lexer.getToken().getStr());
    }

    @Test
    void testFractionWithZero() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("0.123");

        lexer.getNextToken();
        assertEquals(Token.Type.NUMERIC, lexer.getToken().getType());
        assertEquals(0.123, lexer.getToken().getValue(), 1e-5);
    }

    @Test
    void testFractionWithDoubleZero() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("0.0123");

        lexer.getNextToken();
        assertEquals(Token.Type.NUMERIC, lexer.getToken().getType());
        assertEquals(0.0123, lexer.getToken().getValue(), 1e-5);
    }

    @Test
    void testNegativeFloat() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("-12.34");

        lexer.getNextToken();
        assertEquals(Token.Type.MINUS, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.NUMERIC, lexer.getToken().getType());
        assertEquals(12.34, lexer.getToken().getValue(), 1e-5);
    }

    @Test
    void testZero() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("0");

        lexer.getNextToken();
        assertEquals(Token.Type.NUMERIC, lexer.getToken().getType());
        assertEquals(0, lexer.getToken().getValue());
    }

    @Test
    void testInvalidNumber() throws IOException {
        Lexer lexer = new Lexer("0.0");

        assertThrows(Errors.NumberInvalid.class, lexer::getNextToken);
    }

    @Test
    void testNumberWithManyDecimalPoints() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("1.2.3");

        lexer.getNextToken();
        assertEquals(Token.Type.NUMERIC, lexer.getToken().getType());
        assertEquals(1.2, lexer.getToken().getValue(), 1e-5);
    }

    @Test
    void testString() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("\"ab12\"");

        lexer.getNextToken();
        assertEquals(Token.Type.STRING, lexer.getToken().getType());
        assertEquals("\"ab12\"", lexer.getToken().getStr());
    }

    @Test
    void testStringTooLong() throws IOException {
        char[] chars = new char[1001];
        Arrays.fill(chars, 'a');
        String str = '"' + new String(chars) + '"';
        Lexer lexer = new Lexer(str);

        assertThrows(Errors.StringTooLong.class, lexer::getNextToken);
    }

    @Test
    void testStringUnclosed() throws IOException {
        char[] chars = new char[1001];
        String str = '"' + new String(chars);
        Lexer lexer = new Lexer(str);

        assertThrows(Errors.StringTooLong.class, lexer::getNextToken);
    }

    @Test
    void testDateFullYear() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("01.01.2021.12:34:56");

        lexer.getNextToken();
        assertEquals(Token.Type.DATE, lexer.getToken().getType());
        assertEquals("01.01.2021.12:34:56", lexer.getToken().getStr());
    }

    @Test
    void testDateInvalid() throws IOException {
        Lexer lexer = new Lexer("01.01.20211");

        assertThrows(Errors.DateInvalid.class, lexer::getNextToken);
    }

    @Test
    void testDateSingleYear() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("01.01.1.00:00:00");

        lexer.getNextToken();
        assertEquals(Token.Type.DATE, lexer.getToken().getType());
        assertEquals("01.01.1.00:00:00", lexer.getToken().getStr());
    }

    @Test
    void testTime() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("15:25:59");

        lexer.getNextToken();
        assertEquals(Token.Type.TIME, lexer.getToken().getType());
        assertEquals("15:25:59", lexer.getToken().getStr());
    }

    // ------------------ KEYWORDS ------------------
    @Test
    void testKeywordIf() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("if");

        lexer.getNextToken();
        assertEquals(Attributes.keywords.get("if"), lexer.getToken().getType());
    }

    @Test
    void testKeywordElse() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("else");

        lexer.getNextToken();
        assertEquals(Attributes.keywords.get("else"), lexer.getToken().getType());
    }

    @Test
    void testKeywordReturn() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("return");

        lexer.getNextToken();
        assertEquals(Attributes.keywords.get("return"), lexer.getToken().getType());
    }

    @Test
    void testKeywordWhile() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("while");

        lexer.getNextToken();
        assertEquals(Attributes.keywords.get("while"), lexer.getToken().getType());
    }

    @Test
    void testKeywordIfPrint() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("print");

        try {
            lexer.getNextToken();
        } catch (Errors.DateInvalid dateInvalid) {
            dateInvalid.printStackTrace();
        }
        assertEquals(Attributes.keywords.get("print"), lexer.getToken().getType());
    }

    @Test
    void testKeywordNum() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("num");

        lexer.getNextToken();
        assertEquals(Attributes.keywords.get("num"), lexer.getToken().getType());
    }

    @Test
    void testKeywordDate() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("date");

        lexer.getNextToken();
        assertEquals(Attributes.keywords.get("date"), lexer.getToken().getType());
    }

    @Test
    void testKeywordTime() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("time");

        lexer.getNextToken();
        assertEquals(Attributes.keywords.get("time"), lexer.getToken().getType());
    }

    // ------------------ SINGLE OPERATORS ------------------
    @Test
    void testOpLParen() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("(");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get('('), lexer.getToken().getType());
    }

    @Test
    void testOpRParen() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer(")");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get(')'), lexer.getToken().getType());
    }

    @Test
    void testOpLCParen() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("{");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get('{'), lexer.getToken().getType());
    }

    @Test
    void testOpRCParen() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("}");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get('}'), lexer.getToken().getType());
    }

    @Test
    void testOpPlus() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("+");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get('+'), lexer.getToken().getType());
    }

    @Test
    void testOpMinus() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("-");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get('-'), lexer.getToken().getType());
    }

    @Test
    void testOpMultiply() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("*");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get('*'), lexer.getToken().getType());
    }

    @Test
    void testOpDivide() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("/");

        try {
            lexer.getNextToken();
        } catch (Errors.DateInvalid dateInvalid) {
            dateInvalid.printStackTrace();
        }
        assertEquals(Attributes.singleOp.get('/'), lexer.getToken().getType());
    }

    @Test
    void testOpAssignment() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("=");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get('='), lexer.getToken().getType());
    }

    @Test
    void testOpGreaterThan() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer(">");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get('>'), lexer.getToken().getType());
    }

    @Test
    void testOpLessThan() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("<");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get('<'), lexer.getToken().getType());
    }

    @Test
    void testOpComma() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer(",");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get(','), lexer.getToken().getType());
    }

    @Test
    void testOpSemicolon() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer(";");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get(';'), lexer.getToken().getType());
    }

    @Test
    void testOpColon() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer(":");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get(':'), lexer.getToken().getType());
    }

    @Test
    void testOpDot() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer(".");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get('.'), lexer.getToken().getType());
    }

    @Test
    void testOpNot() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("!");

        lexer.getNextToken();
        assertEquals(Attributes.singleOp.get('!'), lexer.getToken().getType());
    }

    // ------------------ DOUBLE OPERATORS ------------------
    @Test
    void testOpAnd() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("&&");

        lexer.getNextToken();
        assertEquals(Attributes.doubleOp.get("&&"), lexer.getToken().getType());
    }

    @Test
    void testOpOr() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("||");

        lexer.getNextToken();
        assertEquals(Attributes.doubleOp.get("||"), lexer.getToken().getType());
    }

    @Test
    void testOpEqual() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("==");

        lexer.getNextToken();
        assertEquals(Attributes.doubleOp.get("=="), lexer.getToken().getType());
    }

    @Test
    void testOpNotEqual() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("!=");

        lexer.getNextToken();
        assertEquals(Attributes.doubleOp.get("!="), lexer.getToken().getType());
    }

    @Test
    void testOpGreaterOrEqual() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer(">=");

        lexer.getNextToken();
        assertEquals(Attributes.doubleOp.get(">="), lexer.getToken().getType());
    }

    @Test
    void testOpLessOrEqual() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("<=");

        lexer.getNextToken();
        assertEquals(Attributes.doubleOp.get("<="), lexer.getToken().getType());
    }

    // ------------------ COMPLEX ------------------
    @Test
    void testFakeComment() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("a + b / comment\nif");

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("a", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.PLUS, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("b", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.DIVIDE, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("comment", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.IF, lexer.getToken().getType());
    }

    @Test
    void testLineEnumeration() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("a b\nreturn\ndate");

        lexer.getNextToken();
        assertEquals(1, lexer.getLine());

        lexer.getNextToken();
        assertEquals(2, lexer.getLine());

        lexer.getNextToken();
        assertEquals(3, lexer.getLine());

        lexer.getNextToken();
        assertEquals(3, lexer.getLine());
    }

    @Test
    void testColumnEnumeration() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("a + b\nreturn");

        lexer.getNextToken();
        assertEquals("a", lexer.getToken().getStr());
        assertEquals(2, lexer.getColumn());

        lexer.getNextToken();
        assertEquals(4, lexer.getColumn());

        lexer.getNextToken();
        assertEquals(0, lexer.getColumn());

        lexer.getNextToken();
        assertEquals(7, lexer.getColumn());
    }

    @Test
    void testPrintString() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("print(\"test\")");

        lexer.getNextToken();
        assertEquals(Token.Type.PRINT, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.LPAREN, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.STRING, lexer.getToken().getType());
        assertEquals("\"test\"", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.RPAREN, lexer.getToken().getType());
    }

    @Test
    void testPrintVariables() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("print(\"test\", a, b)");

        lexer.getNextToken();
        assertEquals(Token.Type.PRINT, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.LPAREN, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.STRING, lexer.getToken().getType());
        assertEquals("\"test\"", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.COMMA, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("a", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.COMMA, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("b", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.RPAREN, lexer.getToken().getType());
    }

    @Test
    void testInvalidTokenAt() throws IOException {
        Lexer lexer = new Lexer("@ab");

        assertThrows(Errors.TokenInvalid.class, lexer::getNextToken);
    }

    @Test
    void testInvalidTokenDollar() throws IOException {
        Lexer lexer = new Lexer("$ab");

        assertThrows(Errors.TokenInvalid.class, lexer::getNextToken);
    }

    @Test
    void testInvalidTokenHash() throws IOException {
        Lexer lexer = new Lexer("#ab");

        assertThrows(Errors.TokenInvalid.class, lexer::getNextToken);
    }

    @Test
    void testFunctionCall() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("func(arg1, arg2);");

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("func", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.LPAREN, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("arg1", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.COMMA, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("arg2", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.RPAREN, lexer.getToken().getType());
    }

    @Test
    void testDateOperation() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("01.05.2021.12:34:56 - 01.04.2021.23:59:59");

        lexer.getNextToken();
        assertEquals(Token.Type.DATE, lexer.getToken().getType());
        assertEquals("01.05.2021.12:34:56", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.MINUS, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.DATE, lexer.getToken().getType());
        assertEquals("01.04.2021.23:59:59", lexer.getToken().getStr());
    }

    @Test
    void testTimeOperation() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("15:24:59 - 00:00:01");

        lexer.getNextToken();
        assertEquals(Token.Type.TIME, lexer.getToken().getType());
        assertEquals("15:24:59", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.MINUS, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.TIME, lexer.getToken().getType());
        assertEquals("00:00:01", lexer.getToken().getStr());
    }

    @Test
    void testDateTimeOperation() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("01.05.2021.00:00:00 + 00:00:01");

        lexer.getNextToken();
        assertEquals(Token.Type.DATE, lexer.getToken().getType());
        assertEquals("01.05.2021.00:00:00", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.PLUS, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.TIME, lexer.getToken().getType());
        assertEquals("00:00:01", lexer.getToken().getStr());
    }

    @Test
    void testDeclaration() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer("date d = 01.01.2021.00:00:00;");

        lexer.getNextToken();
        assertEquals(Token.Type.DATE, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());
        assertEquals("d", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.ASSIGNMENT, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.DATE, lexer.getToken().getType());
        assertEquals("01.01.2021.00:00:00", lexer.getToken().getStr());

        lexer.getNextToken();
        assertEquals(Token.Type.SEMICOLON, lexer.getToken().getType());
    }

    @Test
    void checkFileStream() throws IOException, Errors.TokenError {
        Lexer lexer = new Lexer(new Source("./resources/lexerTest/identifier.txt"));

        lexer.getNextToken();
        assertEquals(Token.Type.IF, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.LPAREN, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.IDENTIFIER, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.EQUAL, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.TIME, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.RPAREN, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.LCPAREN, lexer.getToken().getType());

        lexer.getNextToken();
        assertEquals(Token.Type.RCPAREN, lexer.getToken().getType());
    }

}