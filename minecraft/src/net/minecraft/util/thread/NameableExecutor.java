package net.minecraft.util.thread;

import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.SharedConstants;

public record NameableExecutor(ExecutorService service) implements Executor {
	public Executor named(String name) {
		if (SharedConstants.isDevelopment) {
			return runnable -> this.service.execute(() -> {
					Thread thread = Thread.currentThread();
					String string2 = thread.getName();
					thread.setName(name);

					try (Zone zone = TracyClient.beginZone(name, SharedConstants.isDevelopment)) {
						runnable.run();
					} finally {
						thread.setName(string2);
					}
				});
		} else {
			return (Executor)(TracyClient.isAvailable() ? runnable -> this.service.execute(() -> {
					try (Zone zone = TracyClient.beginZone(name, SharedConstants.isDevelopment)) {
						runnable.run();
					}
				}) : this.service);
		}
	}

	public void execute(Runnable runnable) {
		this.service.execute(wrapForTracy(runnable));
	}

	public void shutdown(long time, TimeUnit unit) {
		this.service.shutdown();

		boolean bl;
		try {
			bl = this.service.awaitTermination(time, unit);
		} catch (InterruptedException var6) {
			bl = false;
		}

		if (!bl) {
			this.service.shutdownNow();
		}
	}

	private static Runnable wrapForTracy(Runnable runnable) {
		return !TracyClient.isAvailable() ? runnable : () -> {
			try (Zone zone = TracyClient.beginZone("task", SharedConstants.isDevelopment)) {
				runnable.run();
			}
		};
	}
}
