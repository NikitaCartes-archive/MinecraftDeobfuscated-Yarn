package net.minecraft.util.math;

import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;

public class EulerAngle {
	protected final float pitch;
	protected final float yaw;
	protected final float roll;

	public EulerAngle(float pitch, float yaw, float roll) {
		this.pitch = !Float.isInfinite(pitch) && !Float.isNaN(pitch) ? pitch % 360.0F : 0.0F;
		this.yaw = !Float.isInfinite(yaw) && !Float.isNaN(yaw) ? yaw % 360.0F : 0.0F;
		this.roll = !Float.isInfinite(roll) && !Float.isNaN(roll) ? roll % 360.0F : 0.0F;
	}

	public EulerAngle(NbtList serialized) {
		this(serialized.getFloat(0), serialized.getFloat(1), serialized.getFloat(2));
	}

	public NbtList serialize() {
		NbtList nbtList = new NbtList();
		nbtList.add(NbtFloat.of(this.pitch));
		nbtList.add(NbtFloat.of(this.yaw));
		nbtList.add(NbtFloat.of(this.roll));
		return nbtList;
	}

	public boolean equals(Object o) {
		if (!(o instanceof EulerAngle)) {
			return false;
		} else {
			EulerAngle eulerAngle = (EulerAngle)o;
			return this.pitch == eulerAngle.pitch && this.yaw == eulerAngle.yaw && this.roll == eulerAngle.roll;
		}
	}

	public float getPitch() {
		return this.pitch;
	}

	public float getYaw() {
		return this.yaw;
	}

	public float getRoll() {
		return this.roll;
	}

	public float method_35845() {
		return MathHelper.wrapDegrees(this.pitch);
	}

	public float method_35846() {
		return MathHelper.wrapDegrees(this.yaw);
	}

	public float method_35847() {
		return MathHelper.wrapDegrees(this.roll);
	}
}
