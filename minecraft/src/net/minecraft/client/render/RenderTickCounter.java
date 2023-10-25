package net.minecraft.client.render;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RenderTickCounter {
	public float tickDelta;
	public float lastFrameDuration;
	private long prevTimeMillis;
	private final float tickTime;
	private final FloatUnaryOperator targetMillisPerTick;

	public RenderTickCounter(float tps, long timeMillis, FloatUnaryOperator targetMillisPerTick) {
		this.tickTime = 1000.0F / tps;
		this.prevTimeMillis = timeMillis;
		this.targetMillisPerTick = targetMillisPerTick;
	}

	public int beginRenderTick(long timeMillis) {
		this.lastFrameDuration = (float)(timeMillis - this.prevTimeMillis) / this.targetMillisPerTick.apply(this.tickTime);
		this.prevTimeMillis = timeMillis;
		this.tickDelta = this.tickDelta + this.lastFrameDuration;
		int i = (int)this.tickDelta;
		this.tickDelta -= (float)i;
		return i;
	}
}
