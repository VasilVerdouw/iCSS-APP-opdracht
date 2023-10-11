package nl.han.ica.datastructures;

public class Node<T> {
    private Node<T> next;
    private T value = null;

    public Node(T value) {
        this.value = value;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Node<T> getNext() {
        return next;
    }

    public T getValue() {
        return value;
    }
}
