import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public final class ConcurrentQueue<E> {
    private volatile Node<E> head;
    private volatile Node<E> tail;

    public ConcurrentQueue() {
        final Node<E> dummyNode = new Node<>();
        head = dummyNode;
        tail = dummyNode;
    }

    public void enqueue(final E element) {
        final Node<E> newNode = new Node<>(element);
        while (true) {
            if (tail.next.compareAndSet(null, newNode)) {
                break;
            }
        }
        tail = newNode;
    }

    public Optional<E> dequeue() {
        throw new UnsupportedOperationException();
    }

    private static final class Node<E> {
        private volatile E value;
        private final AtomicReference<Node<E>> next;

        public Node() {
            next = new AtomicReference<>();
        }

        public Node(final E value) {
            this.value = value;
            next = new AtomicReference<>();
        }
    }
}
