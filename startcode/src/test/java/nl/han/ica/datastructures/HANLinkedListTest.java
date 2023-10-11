package nl.han.ica.datastructures;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class HANLinkedListTest {
    @Test
    void testAddFirst() {
        HANLinkedList<Integer> list = new HANLinkedList<>();
        list.addFirst(1);
        assertEquals(1, list.getFirst());
        list.addFirst(2);
        assertEquals(2, list.getFirst());
        list.addFirst(3);
        assertEquals(3, list.getFirst());
    }

    @Test
    void testClear() {
        HANLinkedList<Integer> list = new HANLinkedList<>();
        list.addFirst(1);
        list.addFirst(2);
        list.addFirst(3);
        list.clear();
        assertEquals(0, list.getSize());
        assertNull(list.getFirst());
    }

    @Test
    void testDelete() {
        HANLinkedList<Integer> list = new HANLinkedList<>();
        list.addFirst(1);
        list.addFirst(2);
        list.addFirst(3);
        list.delete(2);
        assertEquals(2, list.getSize());
        assertEquals(3, list.getFirst());
        assertEquals(2, list.get(1));
    }

    @Test
    void testGet() {
        HANLinkedList<Integer> list = new HANLinkedList<>();
        list.addFirst(1);
        list.addFirst(2);
        list.addFirst(3);
        assertEquals(3, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(1, list.get(2));
    }

    @Test
    void testGetFirst() {
        HANLinkedList<Integer> list = new HANLinkedList<>();
        list.addFirst(1);
        list.addFirst(2);
        list.addFirst(3);
        assertEquals(3, list.getFirst());
    }

    @Test
    void testGetSize() {
        HANLinkedList<Integer> list = new HANLinkedList<>();
        assertEquals(0, list.getSize());
        list.addFirst(1);
        assertEquals(1, list.getSize());
        list.addFirst(2);
        assertEquals(2, list.getSize());
        list.addFirst(3);
        assertEquals(3, list.getSize());
    }

    @Test
    void testInsert() {
        HANLinkedList<Integer> list = new HANLinkedList<>();
        list.addFirst(1);
        list.addFirst(2);
        list.addFirst(3);
        list.insert(1, 4);
        assertEquals(4, list.getSize());
        assertEquals(3, list.get(0));
        assertEquals(4, list.get(1));
        assertEquals(2, list.get(2));
        assertEquals(1, list.get(3));
    }

    @Test
    void testRemoveFirst() {
        HANLinkedList<Integer> list = new HANLinkedList<>();
        list.addFirst(1);
        list.addFirst(2);
        list.addFirst(3);
        list.removeFirst();
        assertEquals(2, list.getSize());
        assertEquals(2, list.getFirst());
    }
}
