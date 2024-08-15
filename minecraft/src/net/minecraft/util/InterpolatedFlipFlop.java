package net.minecraft.util;

import net.minecraft.util.math.MathHelper;

public class InterpolatedFlipFlop {
	/**
	 * How many frames it takes to switch from fully off to fully on.
	 */
	private final int frames;
	private final InterpolatedFlipFlop.SmoothingFunction smoothingFunction;
	private int current;
	private int previous;

	public InterpolatedFlipFlop(int frames, InterpolatedFlipFlop.SmoothingFunction smoothingFunction) {
		this.frames = frames;
		this.smoothingFunction = smoothingFunction;
	}

	public InterpolatedFlipFlop(int frames) {
		this(frames, tickDelta -> tickDelta);
	}

	public void tick(boolean active) {
		this.previous = this.current;
		if (active) {
			if (this.current < this.frames) {
				this.current++;
			}
		} else if (this.current > 0) {
			this.current--;
		}
	}

	public float getValue(float tickDelta) {
		float f = MathHelper.lerp(tickDelta, (float)this.previous, (float)this.current) / (float)this.frames;
		return this.smoothingFunction.apply(f);
	}

	public interface SmoothingFunction {
		float apply(float tickDelta);
	}
}
