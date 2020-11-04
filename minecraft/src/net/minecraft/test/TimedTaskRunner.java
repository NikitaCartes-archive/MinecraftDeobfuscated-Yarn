package net.minecraft.test;

import java.util.Iterator;
import java.util.List;

public class TimedTaskRunner {
	private final GameTest test;
	private final List<TimedTask> tasks;
	private long tick;

	public void runSilently(long tick) {
		try {
			this.runTasks(tick);
		} catch (Exception var4) {
		}
	}

	public void runReported(long tick) {
		try {
			this.runTasks(tick);
		} catch (Exception var4) {
			this.test.fail(var4);
		}
	}

	private void runTasks(long tick) {
		Iterator<TimedTask> iterator = this.tasks.iterator();

		while (iterator.hasNext()) {
			TimedTask timedTask = (TimedTask)iterator.next();
			timedTask.task.run();
			iterator.remove();
			long l = tick - this.tick;
			long m = this.tick;
			this.tick = tick;
			if (timedTask.duration != null && timedTask.duration != l) {
				this.test.fail(new TimeMismatchException("Succeeded in invalid tick: expected " + (m + timedTask.duration) + ", but current tick is " + tick));
				break;
			}
		}
	}
}
