package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RenderTickCounter {
	public int ticksThisFrame;
	public float tickDelta;
	public float lastFrameDuration;
	private long prevTimeMillis;
	private final float tickTime;

	public RenderTickCounter(float tps, long timeMillis) {
		this.tickTime = 1000.0F / tps;
		this.prevTimeMillis = timeMillis;
	}

	public void beginRenderTick(long timeMillis) {
		this.lastFrameDuration = (float)(timeMillis - this.prevTimeMillis) / this.tickTime;
		this.prevTimeMillis = timeMillis;
		this.tickDelta = this.tickDelta + this.lastFrameDuration;
		this.ticksThisFrame = (int)this.tickDelta;
		this.tickDelta = this.tickDelta - (float)this.ticksThisFrame;
	}
}
