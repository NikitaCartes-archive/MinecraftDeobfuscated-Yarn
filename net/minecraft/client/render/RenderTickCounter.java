/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class RenderTickCounter {
    public int ticksThisFrame;
    public float tickDelta;
    public float lastFrameDuration;
    private long prevTimeMillis;
    private final float tickTime;

    public RenderTickCounter(float f, long l) {
        this.tickTime = 1000.0f / f;
        this.prevTimeMillis = l;
    }

    public void beginRenderTick(long l) {
        this.lastFrameDuration = (float)(l - this.prevTimeMillis) / this.tickTime;
        this.prevTimeMillis = l;
        this.tickDelta += this.lastFrameDuration;
        this.ticksThisFrame = (int)this.tickDelta;
        this.tickDelta -= (float)this.ticksThisFrame;
    }
}

