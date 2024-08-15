package net.minecraft.server.dedicated;

import com.google.common.collect.Streams;
import com.mojang.logging.LogUtils;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import net.minecraft.Bootstrap;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ReportType;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;

public class DedicatedServerWatchdog implements Runnable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final long field_29664 = 10000L;
	private static final int field_29665 = 1;
	private final MinecraftDedicatedServer server;
	private final long maxTickTime;

	public DedicatedServerWatchdog(MinecraftDedicatedServer server) {
		this.server = server;
		this.maxTickTime = server.getMaxTickTime() * TimeHelper.MILLI_IN_NANOS;
	}

	public void run() {
		while (this.server.isRunning()) {
			long l = this.server.getTimeReference();
			long m = Util.getMeasuringTimeNano();
			long n = m - l;
			if (n > this.maxTickTime) {
				LOGGER.error(
					LogUtils.FATAL_MARKER,
					"A single server tick took {} seconds (should be max {})",
					String.format(Locale.ROOT, "%.2f", (float)n / (float)TimeHelper.SECOND_IN_NANOS),
					String.format(Locale.ROOT, "%.2f", this.server.getTickManager().getMillisPerTick() / (float)TimeHelper.SECOND_IN_MILLIS)
				);
				LOGGER.error(LogUtils.FATAL_MARKER, "Considering it to be crashed, server will forcibly shutdown.");
				CrashReport crashReport = createCrashReport("Watching Server", this.server.getThread().threadId());
				this.server.addSystemDetails(crashReport.getSystemDetailsSection());
				CrashReportSection crashReportSection = crashReport.addElement("Performance stats");
				crashReportSection.add(
					"Random tick rate", (CrashCallable<String>)(() -> this.server.getSaveProperties().getGameRules().get(GameRules.RANDOM_TICK_SPEED).toString())
				);
				crashReportSection.add(
					"Level stats",
					(CrashCallable<String>)(() -> (String)Streams.stream(this.server.getWorlds())
							.map(world -> world.getRegistryKey() + ": " + world.getDebugString())
							.collect(Collectors.joining(",\n")))
				);
				Bootstrap.println("Crash report:\n" + crashReport.asString(ReportType.MINECRAFT_CRASH_REPORT));
				Path path = this.server.getRunDirectory().resolve("crash-reports").resolve("crash-" + Util.getFormattedCurrentTime() + "-server.txt");
				if (crashReport.writeToFile(path, ReportType.MINECRAFT_CRASH_REPORT)) {
					LOGGER.error("This crash report has been saved to: {}", path.toAbsolutePath());
				} else {
					LOGGER.error("We were unable to save this crash report to disk.");
				}

				this.shutdown();
			}

			try {
				Thread.sleep((l + this.maxTickTime - m) / TimeHelper.MILLI_IN_NANOS);
			} catch (InterruptedException var10) {
			}
		}
	}

	public static CrashReport createCrashReport(String message, long threadId) {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
		StringBuilder stringBuilder = new StringBuilder();
		Error error = new Error("Watchdog");

		for (ThreadInfo threadInfo : threadInfos) {
			if (threadInfo.getThreadId() == threadId) {
				error.setStackTrace(threadInfo.getStackTrace());
			}

			stringBuilder.append(threadInfo);
			stringBuilder.append("\n");
		}

		CrashReport crashReport = new CrashReport(message, error);
		CrashReportSection crashReportSection = crashReport.addElement("Thread Dump");
		crashReportSection.add("Threads", stringBuilder);
		return crashReport;
	}

	private void shutdown() {
		try {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					Runtime.getRuntime().halt(1);
				}
			}, 10000L);
			System.exit(1);
		} catch (Throwable var2) {
			Runtime.getRuntime().halt(1);
		}
	}
}
