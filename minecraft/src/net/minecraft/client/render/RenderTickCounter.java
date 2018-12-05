package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RenderTickCounter {
	public int ticksThisFrame;
	public float tickDelta;
	public float field_1969;
	private long prevTimeMillis;
	private final float timeScale;

	public RenderTickCounter(float f, long l) {
		this.timeScale = 1000.0F / f;
		this.prevTimeMillis = l;
	}

	public void method_1658(long l) {
		this.field_1969 = (float)(l - this.prevTimeMillis) / this.timeScale;
		this.prevTimeMillis = l;
		this.tickDelta = this.tickDelta + this.field_1969;
		this.ticksThisFrame = (int)this.tickDelta;
		this.tickDelta = this.tickDelta - (float)this.ticksThisFrame;
	}
}
