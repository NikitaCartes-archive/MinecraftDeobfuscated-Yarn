/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.Nullable;

public interface Mailbox<T, F> {
    @Nullable
    public F poll();

    public boolean add(T var1);

    public boolean isEmpty();

    public static final class PrioritizedQueueMailbox
    implements Mailbox<PrioritizedMessage, Runnable> {
        private final List<Queue<Runnable>> queues;

        public PrioritizedQueueMailbox(int i2) {
            this.queues = IntStream.range(0, i2).mapToObj(i -> Queues.newConcurrentLinkedQueue()).collect(Collectors.toList());
        }

        @Nullable
        public Runnable method_17346() {
            for (Queue<Runnable> queue : this.queues) {
                Runnable runnable = queue.poll();
                if (runnable == null) continue;
                return runnable;
            }
            return null;
        }

        public boolean method_16913(PrioritizedMessage prioritizedMessage) {
            int i = prioritizedMessage.getPriority();
            this.queues.get(i).add(prioritizedMessage);
            return true;
        }

        @Override
        public boolean isEmpty() {
            return this.queues.stream().allMatch(Collection::isEmpty);
        }

        @Override
        @Nullable
        public /* synthetic */ Object poll() {
            return this.method_17346();
        }
    }

    public static final class PrioritizedMessage
    implements Runnable {
        private final int priority;
        private final Runnable runnable;

        public PrioritizedMessage(int i, Runnable runnable) {
            this.priority = i;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            this.runnable.run();
        }

        public int getPriority() {
            return this.priority;
        }
    }

    public static final class QueueMailbox<T>
    implements Mailbox<T, T> {
        private final Queue<T> queue;

        public QueueMailbox(Queue<T> queue) {
            this.queue = queue;
        }

        @Override
        @Nullable
        public T poll() {
            return this.queue.poll();
        }

        @Override
        public boolean add(T object) {
            return this.queue.add(object);
        }

        @Override
        public boolean isEmpty() {
            return this.queue.isEmpty();
        }
    }
}

