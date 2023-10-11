package nl.han.ica.datastructures;

public class HANQueue<T> implements IHANQueue<T> {
    private Node<T> tail = null;

    @Override
    public void clear() {
        tail = null;
    }

    @Override
    public boolean isEmpty() {
        return tail == null;
    }

    // Alternatively this loop could be done at dequeue time. This would make
    // enqueue O(1) and dequeue O(n).
    // Currently enqueue is O(n) and dequeue is O(1).
    @Override
    public void enqueue(T value) {
        if (tail == null) {
            tail = new Node<T>(value);
            return;
        }

        Node<T> head = tail;
        while (head.getNext() != null) {
            head = head.getNext();
        }
        head.setNext(new Node<T>(value));
    }

    @Override
    public T dequeue() {
        Node<T> previousTail = tail;
        tail = tail.getNext();
        return previousTail.getValue();
    }

    @Override
    public T peek() {
        return tail.getValue();
    }

    @Override
    public int getSize() {
        int size = 0;
        Node<T> head = tail;
        while (head != null) {
            size++;
            head = head.getNext();
        }
        return size;
    }

}
