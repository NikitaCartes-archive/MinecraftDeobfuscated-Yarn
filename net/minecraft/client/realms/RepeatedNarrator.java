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
import net.minecraft.text.Text;

@Environment(value=EnvType.CLIENT)
public class RepeatedNarrator {
    private final float permitsPerSecond;
    private final AtomicReference<Parameters> params = new AtomicReference();

    public RepeatedNarrator(Duration duration) {
        this.permitsPerSecond = 1000.0f / (float)duration.toMillis();
    }

    public void narrate(Text text) {
        Parameters parameters2 = this.params.updateAndGet(parameters -> {
            if (parameters == null || !text.equals(parameters.message)) {
                return new Parameters(text, RateLimiter.create(this.permitsPerSecond));
            }
            return parameters;
        });
        if (parameters2.rateLimiter.tryAcquire(1)) {
            NarratorManager.INSTANCE.narrate(text);
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Parameters {
        final Text message;
        final RateLimiter rateLimiter;

        Parameters(Text text, RateLimiter rateLimiter) {
            this.message = text;
            this.rateLimiter = rateLimiter;
        }
    }
}

