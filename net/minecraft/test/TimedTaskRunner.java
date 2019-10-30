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

    public void runSilently(long l) {
        try {
            this.runTasks(l);
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public void runReported(long l) {
        try {
            this.runTasks(l);
        } catch (Exception exception) {
            this.test.fail(exception);
        }
    }

    private void runTasks(long l) {
        Iterator<TimedTask> iterator = this.tasks.iterator();
        while (iterator.hasNext()) {
            TimedTask timedTask = iterator.next();
            timedTask.task.run();
            iterator.remove();
            long m = l - this.tick;
            long n = this.tick;
            this.tick = l;
            if (timedTask.duration == null || timedTask.duration == m) continue;
            this.test.fail(new TimeMismatchException("Succeeded in invalid tick: expected " + (n + timedTask.duration) + ", but current tick is " + l));
            break;
        }
    }
}

