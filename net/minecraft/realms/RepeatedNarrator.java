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
    final AtomicReference<class_4283> params;

    public RepeatedNarrator(Duration duration) {
        this.repeatDelay = duration;
        this.params = new AtomicReference();
        float f = (float)duration.toMillis() / 1000.0f;
        this.permitsPerSecond = 1.0f / f;
    }

    public void narrate(String string) {
        class_4283 lv = this.params.updateAndGet(arg -> {
            if (arg == null || !string.equals(arg.field_19210)) {
                return new class_4283(string, RateLimiter.create(this.permitsPerSecond));
            }
            return arg;
        });
        if (lv.field_19211.tryAcquire(1)) {
            NarratorManager narratorManager = NarratorManager.INSTANCE;
            narratorManager.onChatMessage(MessageType.SYSTEM, new LiteralText(string));
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class class_4283 {
        String field_19210;
        RateLimiter field_19211;

        class_4283(String string, RateLimiter rateLimiter) {
            this.field_19210 = string;
            this.field_19211 = rateLimiter;
        }
    }
}

