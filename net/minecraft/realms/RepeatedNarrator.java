/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;

@Environment(value=EnvType.CLIENT)
public class RepeatedNarrator {
    final Duration repeatDelay;
    private final float permitsPerSecond;
    final AtomicReference<Parameters> params;

    public RepeatedNarrator(Duration duration) {
        this.repeatDelay = duration;
        this.params = new AtomicReference();
        float f = (float)duration.toMillis() / 1000.0f;
        this.permitsPerSecond = 1.0f / f;
    }

    public void narrate(String string) {
        Parameters parameters2 = this.params.updateAndGet(parameters -> {
            if (parameters == null || !string.equals(parameters.message)) {
                return new Parameters(string, RateLimiter.create(this.permitsPerSecond));
            }
            return parameters;
        });
        if (parameters2.rateLimiter.tryAcquire(1)) {
            NarratorManager narratorManager = NarratorManager.INSTANCE;
            narratorManager.onChatMessage(MessageType.SYSTEM, new LiteralText(string));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Parameters {
        String message;
        RateLimiter rateLimiter;

        Parameters(String string, RateLimiter rateLimiter) {
            this.message = string;
            this.rateLimiter = rateLimiter;
        }
    }
}

