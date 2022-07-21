package net.minecraft.client.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RepeatedNarrator {
	private final float permitsPerSecond;
	private final AtomicReference<RepeatedNarrator.Parameters> params = new AtomicReference();

	public RepeatedNarrator(Duration duration) {
		this.permitsPerSecond = 1000.0F / (float)duration.toMillis();
	}

	public void narrate(NarratorManager narratorManager, Text text) {
		RepeatedNarrator.Parameters parameters = (RepeatedNarrator.Parameters)this.params
			.updateAndGet(
				parametersx -> parametersx != null && text.equals(parametersx.message)
						? parametersx
						: new RepeatedNarrator.Parameters(text, RateLimiter.create((double)this.permitsPerSecond))
			);
		if (parameters.rateLimiter.tryAcquire(1)) {
			narratorManager.narrate(text);
		}
	}

	@Environment(EnvType.CLIENT)
	static class Parameters {
		final Text message;
		final RateLimiter rateLimiter;

		Parameters(Text text, RateLimiter rateLimiter) {
			this.message = text;
			this.rateLimiter = rateLimiter;
		}
	}
}
