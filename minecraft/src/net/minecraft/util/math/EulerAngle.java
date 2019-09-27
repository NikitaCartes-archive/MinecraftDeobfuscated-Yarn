package net.minecraft.util.math;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;

public class EulerAngle {
	protected final float pitch;
	protected final float yaw;
	protected final float roll;

	public EulerAngle(float f, float g, float h) {
		this.pitch = !Float.isInfinite(f) && !Float.isNaN(f) ? f % 360.0F : 0.0F;
		this.yaw = !Float.isInfinite(g) && !Float.isNaN(g) ? g % 360.0F : 0.0F;
		this.roll = !Float.isInfinite(h) && !Float.isNaN(h) ? h % 360.0F : 0.0F;
	}

	public EulerAngle(ListTag listTag) {
		this(listTag.getFloat(0), listTag.getFloat(1), listTag.getFloat(2));
	}

	public ListTag serialize() {
		ListTag listTag = new ListTag();
		listTag.add(FloatTag.of(this.pitch));
		listTag.add(FloatTag.of(this.yaw));
		listTag.add(FloatTag.of(this.roll));
		return listTag;
	}

	public boolean equals(Object object) {
		if (!(object instanceof EulerAngle)) {
			return false;
		} else {
			EulerAngle eulerAngle = (EulerAngle)object;
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
}
