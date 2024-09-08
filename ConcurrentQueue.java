import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class ConcurrentQueue<E> {
    private volatile Node<E> head;
    private final AtomicReference<Node<E>> tail;

    public ConcurrentQueue() {
        final Node<E> dummyNode = new Node<>();
        head = dummyNode;
        tail = new AtomicReference<>(dummyNode);
    }

    public void enqueue(final E element) {
        final Node<E> newNode = new Node<>(requireNonNull(element));
        while (true) {
            final Node<E> previousTail = tail.get();
            if (previousTail.next.compareAndSet(null, newNode)) {
                tail.compareAndSet(previousTail, newNode);
                return;
            } else {
                tail.compareAndSet(previousTail, previousTail.next.get());
            }
        }
    }

    public Optional<E> dequeue() {
        final Node<E> previousHead = head;
        final Node<E> nextHead = previousHead.next.get();
        if (previousHead == tail.get()) {
            return empty();
        }
        final E element = nextHead.value;
        nextHead.value = null;
        head = nextHead;
        previousHead.next.set(previousHead);
        return of(element);
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
