package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.TextComponent;

@Environment(EnvType.CLIENT)
class RepeatedNarrator {
	final Duration repeatDelay;
	private final float permitsPerSecond;
	final AtomicReference<RepeatedNarrator.class_4283> params;

	public RepeatedNarrator(Duration duration) {
		this.repeatDelay = duration;
		this.params = new AtomicReference();
		float f = (float)duration.toMillis() / 1000.0F;
		this.permitsPerSecond = 1.0F / f;
	}

	public void narrate(String string) {
		RepeatedNarrator.class_4283 lv = (RepeatedNarrator.class_4283)this.params
			.updateAndGet(
				arg -> arg != null && string.equals(arg.field_19210) ? arg : new RepeatedNarrator.class_4283(string, RateLimiter.create((double)this.permitsPerSecond))
			);
		if (lv.field_19211.tryAcquire(1)) {
			NarratorManager narratorManager = NarratorManager.INSTANCE;
			narratorManager.onChatMessage(ChatMessageType.field_11735, new TextComponent(string));
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_4283 {
		String field_19210;
		RateLimiter field_19211;

		class_4283(String string, RateLimiter rateLimiter) {
			this.field_19210 = string;
			this.field_19211 = rateLimiter;
		}
	}
}
