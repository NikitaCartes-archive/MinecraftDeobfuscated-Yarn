package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;

@Environment(EnvType.CLIENT)
public class RepeatedNarrator {
	final Duration repeatDelay;
	private final float permitsPerSecond;
	final AtomicReference<RepeatedNarrator.Parameters> params;

	public RepeatedNarrator(Duration duration) {
		this.repeatDelay = duration;
		this.params = new AtomicReference();
		float f = (float)duration.toMillis() / 1000.0F;
		this.permitsPerSecond = 1.0F / f;
	}

	public void narrate(String string) {
		RepeatedNarrator.Parameters parameters = (RepeatedNarrator.Parameters)this.params
			.updateAndGet(
				parametersx -> parametersx != null && string.equals(parametersx.message)
						? parametersx
						: new RepeatedNarrator.Parameters(string, RateLimiter.create((double)this.permitsPerSecond))
			);
		if (parameters.rateLimiter.tryAcquire(1)) {
			NarratorManager narratorManager = NarratorManager.INSTANCE;
			narratorManager.onChatMessage(MessageType.SYSTEM, new LiteralText(string));
		}
	}

	@Environment(EnvType.CLIENT)
	static class Parameters {
		String message;
		RateLimiter rateLimiter;

		Parameters(String string, RateLimiter rateLimiter) {
			this.message = string;
			this.rateLimiter = rateLimiter;
		}
	}
}
