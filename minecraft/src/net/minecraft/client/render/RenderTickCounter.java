package net.minecraft.client.render;

import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public interface RenderTickCounter {
	RenderTickCounter ZERO = new RenderTickCounter.Constant(0.0F);
	RenderTickCounter ONE = new RenderTickCounter.Constant(1.0F);

	float getLastFrameDuration();

	float getTickDelta(boolean bl);

	float getLastDuration();

	@Environment(EnvType.CLIENT)
	public static class Constant implements RenderTickCounter {
		private final float value;

		Constant(float value) {
			this.value = value;
		}

		@Override
		public float getLastFrameDuration() {
			return this.value;
		}

		@Override
		public float getTickDelta(boolean bl) {
			return this.value;
		}

		@Override
		public float getLastDuration() {
			return this.value;
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Dynamic implements RenderTickCounter {
		private float lastFrameDuration;
		private float tickDelta;
		private float lastDuration;
		private float tickDeltaBeforePause;
		private long prevTimeMillis;
		private long timeMillis;
		private final float tickTime;
		private final FloatUnaryOperator targetMillisPerTick;
		private boolean paused;
		private boolean tickFrozen;

		public Dynamic(float tps, long timeMillis, FloatUnaryOperator targetMillisPerTick) {
			this.tickTime = 1000.0F / tps;
			this.timeMillis = this.prevTimeMillis = timeMillis;
			this.targetMillisPerTick = targetMillisPerTick;
		}

		public int beginRenderTick(long timeMillis, boolean tick) {
			this.setTimeMillis(timeMillis);
			return tick ? this.beginRenderTick(timeMillis) : 0;
		}

		private int beginRenderTick(long timeMillis) {
			this.lastFrameDuration = (float)(timeMillis - this.prevTimeMillis) / this.targetMillisPerTick.apply(this.tickTime);
			this.prevTimeMillis = timeMillis;
			this.tickDelta = this.tickDelta + this.lastFrameDuration;
			int i = (int)this.tickDelta;
			this.tickDelta -= (float)i;
			return i;
		}

		private void setTimeMillis(long timeMillis) {
			this.lastDuration = (float)(timeMillis - this.timeMillis) / this.tickTime;
			this.timeMillis = timeMillis;
		}

		public void tick(boolean paused) {
			if (paused) {
				this.tickPaused();
			} else {
				this.tickUnpaused();
			}
		}

		private void tickPaused() {
			if (!this.paused) {
				this.tickDeltaBeforePause = this.tickDelta;
			}

			this.paused = true;
		}

		private void tickUnpaused() {
			if (this.paused) {
				this.tickDelta = this.tickDeltaBeforePause;
			}

			this.paused = false;
		}

		public void setTickFrozen(boolean tickFrozen) {
			this.tickFrozen = tickFrozen;
		}

		@Override
		public float getLastFrameDuration() {
			return this.lastFrameDuration;
		}

		@Override
		public float getTickDelta(boolean bl) {
			if (!bl && this.tickFrozen) {
				return 1.0F;
			} else {
				return this.paused ? this.tickDeltaBeforePause : this.tickDelta;
			}
		}

		@Override
		public float getLastDuration() {
			return this.lastDuration > 7.0F ? 0.5F : this.lastDuration;
		}
	}
}
