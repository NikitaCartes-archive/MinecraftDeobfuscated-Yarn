/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

@Environment(value=EnvType.CLIENT)
public class RepeatedNarrator {
    private final float permitsPerSecond;
    private final AtomicReference<Parameters> params = new AtomicReference();

    public RepeatedNarrator(Duration duration) {
        this.permitsPerSecond = 1000.0f / (float)duration.toMillis();
    }

    public void narrate(String message) {
        Parameters parameters2 = this.params.updateAndGet(parameters -> {
            if (parameters == null || !message.equals(((Parameters)parameters).message)) {
                return new Parameters(message, RateLimiter.create(this.permitsPerSecond));
            }
            return parameters;
        });
        if (parameters2.rateLimiter.tryAcquire(1)) {
            NarratorManager.INSTANCE.onChatMessage(MessageType.SYSTEM, new LiteralText(message), Util.NIL_UUID);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Parameters {
        private final String message;
        private final RateLimiter rateLimiter;

        Parameters(String message, RateLimiter rateLimiter) {
            this.message = message;
            this.rateLimiter = rateLimiter;
        }
    }
}

