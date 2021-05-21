package interpreter;

import main.errors.Errors;
import main.interpreter.scope.Scope;
import main.interpreter.scope.Value;
import main.interpreter.scope.ValueType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScopeTest {

    @AfterAll
    static void cleanUp(){
        System.out.println("Scope: Execution of all JUNIT tests done.");
    }

    @Test
    void addNumVar() {
        Scope scope = new Scope("testScope");

        scope.addVariable("a", new Value(ValueType.NUM, 5.0));
        assertEquals(5, scope.getVariables().get("a").getDoubleValue());
    }

    @Test
    void addDateVar() {
        Scope scope = new Scope("testScope");

        scope.addVariable("d", new Value(ValueType.DATE, "01.01.2021.00:00:00"));
        assertEquals("01.01.2021.00:00:00", scope.getVariables().get("d").getStrValue());
    }

    @Test
    void addTimeVar() {
        Scope scope = new Scope("testScope");

        scope.addVariable("t", new Value(ValueType.TIME, "00:00:00"));
        assertEquals("00:00:00", scope.getVariables().get("t").getStrValue());
    }

    @Test
    void getNumVar() throws Errors.InterpreterError {
        Scope scope = new Scope("testScope");

        scope.addVariable("a", new Value(ValueType.NUM, 5.0));
        assertEquals(5.0, scope.getVariable("a").getDoubleValue());
    }

    @Test
    void getDateVar() throws Errors.InterpreterError {
        Scope scope = new Scope("testScope");

        scope.addVariable("d", new Value(ValueType.DATE, "01.01.2021.00:00:00"));
        assertEquals("01.01.2021.00:00:00", scope.getVariable("d").getStrValue());
    }

    @Test
    void getTimeVar() throws Errors.InterpreterError {
        Scope scope = new Scope("testScope");

        scope.addVariable("t", new Value(ValueType.TIME, "00:00:00"));
        assertEquals("00:00:00", scope.getVariable("t").getStrValue());
    }

    @Test
    void isDeclared() {
        Scope scope = new Scope("testScope");

        scope.addVariable("t", new Value(ValueType.TIME, "00:00:00"));
        assertTrue(scope.isDeclared("t"));
    }

    @Test
    void requireNotDeclared() throws Errors.InterpreterError {
        assertThrows(Errors.UndeclaredVariable.class, this::requireNotDeclaredAux);
    }

    private void requireNotDeclaredAux() throws Errors.InterpreterError {
        Scope scope = new Scope("testScope");

        scope.addVariable("a", new Value(ValueType.NUM, 5));
        scope.requireDeclaredVar("b");
    }
}