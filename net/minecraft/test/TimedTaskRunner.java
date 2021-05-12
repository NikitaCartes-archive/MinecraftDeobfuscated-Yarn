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
    final GameTestState test;
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

    public TimedTaskRunner method_36077(int delay, Runnable task) {
        this.tasks.add(TimedTask.create(() -> {
            if (this.test.getTick() < this.tick + (long)delay) {
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

    public Trigger method_36083() {
        Trigger trigger = new Trigger();
        this.tasks.add(TimedTask.create(() -> trigger.trigger(this.test.getTick())));
        return trigger;
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

    public class Trigger {
        private static final long UNTRIGGERED_TICK = -1L;
        private long triggeredTick = -1L;

        void trigger(long tick) {
            if (this.triggeredTick != -1L) {
                throw new IllegalStateException("Condition already triggered at " + this.triggeredTick);
            }
            this.triggeredTick = tick;
        }

        public void checkTrigger() {
            long l = TimedTaskRunner.this.test.getTick();
            if (this.triggeredTick != l) {
                if (this.triggeredTick == -1L) {
                    throw new GameTestException("Condition not triggered (t=" + l + ")");
                }
                throw new GameTestException("Condition triggered at " + this.triggeredTick + ", (t=" + l + ")");
            }
        }
    }
}

