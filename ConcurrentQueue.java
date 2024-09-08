import java.util.Optional;

public final class ConcurrentQueue<E> {
    private volatile Node<E> head;
    private volatile Node<E> tail;

    public ConcurrentQueue() {
        final Node<E> dummyNode = new Node<>();
        head = dummyNode;
        tail = dummyNode;
    }

    public void enqueue(final E element) {
        throw new UnsupportedOperationException();
    }

    public Optional<E> dequeue() {
        throw new UnsupportedOperationException();
    }

    private static final class Node<E> {
        private volatile E value;
        private volatile Node<E> next;

        public Node() {

        }

        public Node(final E value) {
            this.value = value;
        }
    }
}
