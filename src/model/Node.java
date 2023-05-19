package model;

public class Node<T extends Task> {
    public T data;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T data, Node<T> next) {
        this.next = next;
        this.prev = prev;
        this.data = data;
    }
}
