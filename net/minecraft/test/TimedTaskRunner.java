/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import java.util.Iterator;
import java.util.List;
import net.minecraft.test.GameTestException;
import net.minecraft.test.GameTestState;
import net.minecraft.test.TimedTask;

public class TimedTaskRunner {
    private final GameTestState test;
    private final List<TimedTask> tasks;
    private long tick;

    public void runSilently(long tick) {
        try {
            this.runTasks(tick);
        } catch (Exception exception) {
            // empty catch block
        }
    }

    public void runReported(long tick) {
        try {
            this.runTasks(tick);
        } catch (Exception exception) {
            this.test.fail(exception);
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
}

