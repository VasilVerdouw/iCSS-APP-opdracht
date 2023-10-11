package nl.han.ica.datastructures;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HANStackTest {

    @Test
    void testPushAndPop() {
        HANStack<Integer> stack = new HANStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);

        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    void testPeek() {
        HANStack<String> stack = new HANStack<>();
        stack.push("Hello");
        stack.push("World");

        assertEquals("World", stack.peek());
        assertEquals("World", stack.peek());
    }

    @Test
    void testPopEmptyStack() {
        HANStack<Double> stack = new HANStack<>();

        assertThrows(IllegalStateException.class, () -> {
            stack.pop();
        });
    }

    @Test
    void testPeekEmptyStack() {
        HANStack<Character> stack = new HANStack<>();

        assertNull(stack.peek());
    }
}