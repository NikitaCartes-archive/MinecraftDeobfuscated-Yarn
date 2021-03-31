/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.test.GameTestException;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TimedTask;

public class TimedTaskRunner {
    private final GameTestState test;
    private final List<TimedTask> tasks = Lists.newArrayList();
    private long tick;

    TimedTaskRunner(GameTestState gameTest) {
        this.test = gameTest;
        this.tick = gameTest.getTick();
    }

    public TimedTaskRunner createAndAdd(Runnable task) {
        this.tasks.add(TimedTask.create(task));
        return this;
    }

    public TimedTaskRunner createAndAdd(long duration, Runnable task) {
        this.tasks.add(TimedTask.create(duration, task));
        return this;
    }

    public TimedTaskRunner method_36076(int i) {
        return this.method_36077(i, () -> {});
    }

    public TimedTaskRunner method_36085(Runnable task) {
        this.tasks.add(TimedTask.create(() -> this.tryRun(task)));
        return this;
    }

    public TimedTaskRunner method_36077(int i, Runnable task) {
        this.tasks.add(TimedTask.create(() -> {
            if (this.test.getTick() < this.tick + (long)i) {
                throw new GameTestException("Waiting");
            }
            this.tryRun(task);
        }));
        return this;
    }

    public TimedTaskRunner method_36084(int i, Runnable task) {
        this.tasks.add(TimedTask.create(() -> {
            if (this.test.getTick() < this.tick + (long)i) {
                this.tryRun(task);
                throw new GameTestException("Waiting");
            }
        }));
        return this;
    }

    public void method_36075() {
        this.tasks.add(TimedTask.create(this.test::completeIfSuccessful));
    }

    public void method_36080(Supplier<Exception> supplier) {
        this.tasks.add(TimedTask.create(() -> this.test.fail((Throwable)supplier.get())));
    }

    public class_6304 method_36083() {
        class_6304 lv = new class_6304();
        this.tasks.add(TimedTask.create(() -> lv.method_36093(this.test.getTick())));
        return lv;
    }

    public void runSilently(long tick) {
        try {
            this.runTasks(tick);
        } catch (GameTestException gameTestException) {
            // empty catch block
        }
    }

    public void runReported(long tick) {
        try {
            this.runTasks(tick);
        } catch (GameTestException gameTestException) {
            this.test.fail(gameTestException);
        }
    }

    private void tryRun(Runnable task) {
        try {
            task.run();
        } catch (GameTestException gameTestException) {
            this.test.fail(gameTestException);
        }
    }

    private void runTasks(long tick) {
        Iterator<TimedTask> iterator = this.tasks.iterator();
        while (iterator.hasNext()) {
            TimedTask timedTask = iterator.next();
            timedTask.task.run();
            iterator.remove();
            long l = tick - this.tick;
            long m = this.tick;
            this.tick = tick;
            if (timedTask.duration == null || timedTask.duration == l) continue;
            this.test.fail(new GameTestException("Succeeded in invalid tick: expected " + (m + timedTask.duration) + ", but current tick is " + tick));
            break;
        }
    }

    public class class_6304 {
        private static final long field_33154 = -1L;
        private long field_33155 = -1L;

        void method_36093(long l) {
            if (this.field_33155 != -1L) {
                throw new IllegalStateException("Condition already triggered at " + this.field_33155);
            }
            this.field_33155 = l;
        }

        public void method_36092() {
            long l = TimedTaskRunner.this.test.getTick();
            if (this.field_33155 != l) {
                if (this.field_33155 == -1L) {
                    throw new GameTestException("Condition not triggered (t=" + l + ")");
                }
                throw new GameTestException("Condition triggered at " + this.field_33155 + ", (t=" + l + ")");
            }
        }
    }
}

