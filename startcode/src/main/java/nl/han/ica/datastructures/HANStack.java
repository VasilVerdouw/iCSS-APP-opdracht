package nl.han.ica.datastructures;

public class HANStack<T> implements IHANStack<T> {
    private Node<T> head;

    @Override
    public void push(T value) {
        Node<T> newNode = new Node<T>(value);
        newNode.setNext(head);
        head = newNode;
    }

    @Override
    public T pop() {
        if (head == null) {
            throw new IllegalStateException("Stack is empty");
        }

        Node<T> previousHead = head;
        head = head.getNext();
        return (T) previousHead.getValue();
    }

    @Override
    public T peek() {
        if (head == null) {
            return null;
        } else {
            return (T) head.getValue();
        }
    }
}
