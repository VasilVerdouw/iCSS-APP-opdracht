package nl.han.ica.datastructures;

import java.util.Iterator;

public class HANLinkedList<T> implements IHANLinkedList<T> {

    private Node<T> first = null;

    @Override
    public void addFirst(T value) {
        Node<T> newNode = new Node<T>(value);
        newNode.setNext(first);
        first = newNode;
    }

    @Override
    public void clear() {
        first = null;
    }

    @Override
    public void insert(int index, T value) {
        if (index == 0) {
            addFirst(value);
            return;
        }

        if (index > getSize() || index < 0) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds");
        }

        // Find the node before the index
        Node<T> node = first;
        for (int i = 0; i < index - 1; i++) {
            node = node.getNext();
        }

        // Insert the new node
        Node<T> newNode = new Node<T>(value);
        newNode.setNext(node.getNext());
        node.setNext(newNode);
    }

    @Override
    public void delete(int pos) {
        if (pos == 0) {
            removeFirst();
            return;
        }

        if (pos >= getSize() || pos < 0) {
            throw new IndexOutOfBoundsException("Index " + pos + " is out of bounds");
        }

        // Find the node before the index
        Node<T> node = first;
        for (int i = 0; i < pos - 1; i++) {
            node = node.getNext();
        }

        // Delete the node by skipping it in the chain
        node.setNext(node.getNext().getNext());
    }

    @Override
    public T get(int pos) {
        if (pos >= getSize() || pos < 0) {
            throw new IndexOutOfBoundsException("Index " + pos + " is out of bounds");
        }

        // Find the node before the index
        Iterator<T> iterator = new LinkedListIterator<T>(first);
        for (int i = 0; i < pos; i++) {
            iterator.next();
        }

        return iterator.next();
    }

    @Override
    public void removeFirst() {
        first = first.getNext();
    }

    @Override
    public T getFirst() {
        if (first == null)
            return null;
        return first.getValue();
    }

    // Alternative would be to keep a size variable and update it on every add and
    // delete.
    @Override
    public int getSize() {
        if (first == null)
            return 0;

        int size = 0;
        Iterator<T> iterator = new LinkedListIterator<T>(first);

        while (iterator.hasNext()) {
            size++;
            iterator.next();
        }
        return size;
    }
}
