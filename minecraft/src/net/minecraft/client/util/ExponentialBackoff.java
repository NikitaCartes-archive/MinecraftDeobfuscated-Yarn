package net.minecraft.client.util;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;

/**
 * An implementation of an exponential backoff algorithm with the upper limit
 * on delay. The implementation does not require a specific time unit (such as ticks,
 * seconds, etc); the only requirement is that {@link #run} method is called
 * periodically.
 */
@Environment(EnvType.CLIENT)
public class ExponentialBackoff implements Runnable {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ExponentialBackoff.Runner runner;
	private final int maxSkippableRuns;
	private int runsToSkip;
	private int skippedRuns;

	public ExponentialBackoff(ExponentialBackoff.Runner runner, int maxSkippableRuns) {
		this.runner = runner;
		this.maxSkippableRuns = maxSkippableRuns;
	}

	public void run() {
		if (this.runsToSkip > this.skippedRuns) {
			this.skippedRuns++;
		} else {
			this.skippedRuns = 0;

			try {
				this.runner.run();
				this.runsToSkip = 0;
			} catch (Exception var2) {
				if (this.runsToSkip == 0) {
					this.runsToSkip = 1;
				} else {
					this.runsToSkip = Math.min(2 * this.runsToSkip, this.maxSkippableRuns);
				}

				LOGGER.info("Skipping next {}", this.runsToSkip);
			}
		}
	}

	/**
	 * A runner for the exponential backoff. This can raise any exceptions, and
	 * such exceptions are caught and treated as failure for the purpose of
	 * exponential backoff.
	 */
	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface Runner {
		void run() throws Exception;
	}
}
