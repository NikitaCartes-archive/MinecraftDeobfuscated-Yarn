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

	public NbtList toNbt() {
		NbtList nbtList = new NbtList();
		nbtList.add(NbtFloat.of(this.pitch));
		nbtList.add(NbtFloat.of(this.yaw));
		nbtList.add(NbtFloat.of(this.roll));
		return nbtList;
	}

	public boolean equals(Object o) {
		return !(o instanceof EulerAngle eulerAngle) ? false : this.pitch == eulerAngle.pitch && this.yaw == eulerAngle.yaw && this.roll == eulerAngle.roll;
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

	/**
	 * Returns the pitch that is wrapped to the interval {@code [-180, 180)}.
	 */
	public float getWrappedPitch() {
		return MathHelper.wrapDegrees(this.pitch);
	}

	/**
	 * Returns the yaw that is wrapped to the interval {@code [-180, 180)}.
	 */
	public float getWrappedYaw() {
		return MathHelper.wrapDegrees(this.yaw);
	}

	/**
	 * Returns the roll that is wrapped to the interval {@code [-180, 180)}.
	 */
	public float getWrappedRoll() {
		return MathHelper.wrapDegrees(this.roll);
	}
}
