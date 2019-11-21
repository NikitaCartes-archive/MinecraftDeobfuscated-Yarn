/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.thread;

import com.google.common.collect.Queues;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.jetbrains.annotations.Nullable;

public interface TaskQueue<T, F> {
    @Nullable
    public F poll();

    public boolean add(T var1);

    public boolean isEmpty();

    public static final class Prioritized
    implements TaskQueue<PrioritizedTask, Runnable> {
        private final List<Queue<Runnable>> queues;

        public Prioritized(int i2) {
            this.queues = IntStream.range(0, i2).mapToObj(i -> Queues.newConcurrentLinkedQueue()).collect(Collectors.toList());
        }

        @Override
        @Nullable
        public Runnable poll() {
            for (Queue<Runnable> queue : this.queues) {
                Runnable runnable = queue.poll();
                if (runnable == null) continue;
                return runnable;
            }
            return null;
        }

        @Override
        public boolean add(PrioritizedTask prioritizedTask) {
            int i = prioritizedTask.getPriority();
            this.queues.get(i).add(prioritizedTask);
            return true;
        }

        @Override
        public boolean isEmpty() {
            return this.queues.stream().allMatch(Collection::isEmpty);
        }

        @Override
        @Nullable
        public /* synthetic */ Object poll() {
            return this.poll();
        }
    }

    public static final class PrioritizedTask
    implements Runnable {
        private final int priority;
        private final Runnable runnable;

        public PrioritizedTask(int i, Runnable runnable) {
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

    public static final class Simple<T>
    implements TaskQueue<T, T> {
        private final Queue<T> queue;

        public Simple(Queue<T> queue) {
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

