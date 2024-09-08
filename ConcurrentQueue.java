import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public final class ConcurrentQueue<E> {
    private final AtomicReference<Node<E>> head;
    private final AtomicReference<Node<E>> tail;

    public ConcurrentQueue() {
        final Node<E> dummyNode = new Node<>();
        head = new AtomicReference<>(dummyNode);
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
        while (true) {
            final Node<E> previousHead = head.get();
            final Node<E> previousTail = tail.get();
            final Node<E> nextHead = previousHead.next.get();
            if (previousHead == previousTail) {
                if (nextHead == null) {
                    return empty();
                } else {
                    tail.compareAndSet(previousTail, nextHead);
                }
            }
            final E element = nextHead.value.get();
            if (element != null && nextHead.value.compareAndSet(element, null)) {
                updateHead(previousHead, nextHead);
                return of(element);
            } else {
                updateHead(previousHead, nextHead);
            }
        }
    }

    private void updateHead(final Node<E> previous, final Node<E> next) {
        if (head.compareAndSet(previous, next)) {
            previous.next.set(previous);
        }
    }

    private static final class Node<E> {
        private final AtomicReference<E> value;
        private final AtomicReference<Node<E>> next;

        public Node() {
            value = new AtomicReference<>();
            next = new AtomicReference<>();
        }

        public Node(final E value) {
            this.value = new AtomicReference<>(value);
            next = new AtomicReference<>();
        }
    }
}
