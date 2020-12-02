/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import java.util.Iterator;
import java.util.List;
import net.minecraft.test.GameTest;
import net.minecraft.test.TimeMismatchException;
import net.minecraft.test.TimedTask;

public class TimedTaskRunner {
    private final GameTest test;
    private final List<TimedTask> tasks;
    private long tick;

    public void runSilently(long tick) {
        try {
            this.runTasks(tick);
        } catch (TimeMismatchException timeMismatchException) {
            // empty catch block
        }
    }

    public void runReported(long tick) {
        try {
            this.runTasks(tick);
        } catch (TimeMismatchException timeMismatchException) {
            this.test.fail(timeMismatchException);
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
            this.test.fail(new TimeMismatchException("Succeeded in invalid tick: expected " + (m + timedTask.duration) + ", but current tick is " + tick));
            break;
        }
    }
}

