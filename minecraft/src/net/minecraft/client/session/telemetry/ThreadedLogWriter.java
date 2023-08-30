package net.minecraft.client.session.telemetry;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.logging.LogWriter;
import net.minecraft.util.thread.TaskExecutor;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class ThreadedLogWriter implements AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final LogWriter<SentTelemetryEvent> writer;
	private final TaskExecutor<Runnable> executor;

	public ThreadedLogWriter(FileChannel channel, Executor executor) {
		this.writer = new LogWriter<>(SentTelemetryEvent.CODEC, channel);
		this.executor = TaskExecutor.create(executor, "telemetry-event-log");
	}

	public TelemetryLogger getLogger() {
		return event -> this.executor.send(() -> {
				try {
					this.writer.write(event);
				} catch (IOException var3) {
					LOGGER.error("Failed to write telemetry event to log", (Throwable)var3);
				}
			});
	}

	public void close() {
		this.executor.send(() -> IOUtils.closeQuietly(this.writer));
		this.executor.close();
	}
}
