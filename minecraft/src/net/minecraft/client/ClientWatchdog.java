package net.minecraft.client;

import java.io.File;
import java.time.Duration;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.dedicated.DedicatedServerWatchdog;
import net.minecraft.util.crash.CrashReport;

@Environment(EnvType.CLIENT)
public class ClientWatchdog {
	private static final Duration timeout = Duration.ofSeconds(15L);

	public static void shutdownClient(File runDir, long threadId) {
		Thread thread = new Thread(() -> {
			try {
				Thread.sleep(timeout);
			} catch (InterruptedException var4) {
				return;
			}

			CrashReport crashReport = DedicatedServerWatchdog.createCrashReport("Client shutdown", threadId);
			MinecraftClient.saveCrashReport(runDir, crashReport);
		});
		thread.setDaemon(true);
		thread.setName("Client shutdown watchdog");
		thread.start();
	}
}
