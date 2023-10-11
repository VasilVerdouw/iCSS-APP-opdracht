package nl.han.ica.datastructures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class HANQueueTest {
    @Test
    public void testEnqueue() {
        HANQueue<Integer> queue = new HANQueue<>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assertEquals(1, (int) queue.peek());
        assertEquals(3, queue.getSize());
    }

    @Test
    public void testDequeue() {
        HANQueue<Integer> queue = new HANQueue<>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assertEquals(1, (int) queue.dequeue());
        assertEquals(2, (int) queue.peek());
        assertEquals(2, queue.getSize());
    }

    @Test
    public void testClear() {
        HANQueue<Integer> queue = new HANQueue<>();
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        queue.clear();
        assertTrue(queue.isEmpty());
    }

    @Test
    public void testIsEmpty() {
        HANQueue<Integer> queue = new HANQueue<>();
        assertTrue(queue.isEmpty());
        queue.enqueue(1);
        assertFalse(queue.isEmpty());
    }

    @Test
    public void testGetSize() {
        HANQueue<Integer> queue = new HANQueue<>();
        assertEquals(0, queue.getSize());
        queue.enqueue(1);
        queue.enqueue(2);
        queue.enqueue(3);
        assertEquals(3, queue.getSize());
    }
}