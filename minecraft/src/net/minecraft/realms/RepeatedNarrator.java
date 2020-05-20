package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
public class RepeatedNarrator {
	private final float permitsPerSecond;
	private final AtomicReference<RepeatedNarrator.Parameters> params = new AtomicReference();

	public RepeatedNarrator(Duration duration) {
		this.permitsPerSecond = 1000.0F / (float)duration.toMillis();
	}

	public void narrate(String message) {
		RepeatedNarrator.Parameters parameters = (RepeatedNarrator.Parameters)this.params
			.updateAndGet(
				parametersx -> parametersx != null && message.equals(parametersx.message)
						? parametersx
						: new RepeatedNarrator.Parameters(message, RateLimiter.create((double)this.permitsPerSecond))
			);
		if (parameters.rateLimiter.tryAcquire(1)) {
			NarratorManager.INSTANCE.onChatMessage(MessageType.SYSTEM, new LiteralText(message), Util.field_25140);
		}
	}

	@Environment(EnvType.CLIENT)
	static class Parameters {
		private final String message;
		private final RateLimiter rateLimiter;

		Parameters(String message, RateLimiter rateLimiter) {
			this.message = message;
			this.rateLimiter = rateLimiter;
		}
	}
}
