package net.minecraft.entity.ai.control;

import net.minecraft.util.math.MathHelper;

public interface Control {
	default float changeAngle(float start, float end, float maxChange) {
		float f = MathHelper.subtractAngles(start, end);
		float g = MathHelper.clamp(f, -maxChange, maxChange);
		return start + g;
	}
}
