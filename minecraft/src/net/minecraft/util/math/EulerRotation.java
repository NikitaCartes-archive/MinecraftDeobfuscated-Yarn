package net.minecraft.util.math;

import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;

public class EulerRotation {
	protected final float x;
	protected final float y;
	protected final float z;

	public EulerRotation(float f, float g, float h) {
		this.x = !Float.isInfinite(f) && !Float.isNaN(f) ? f % 360.0F : 0.0F;
		this.y = !Float.isInfinite(g) && !Float.isNaN(g) ? g % 360.0F : 0.0F;
		this.z = !Float.isInfinite(h) && !Float.isNaN(h) ? h % 360.0F : 0.0F;
	}

	public EulerRotation(ListTag listTag) {
		this(listTag.getFloat(0), listTag.getFloat(1), listTag.getFloat(2));
	}

	public ListTag serialize() {
		ListTag listTag = new ListTag();
		listTag.add(new FloatTag(this.x));
		listTag.add(new FloatTag(this.y));
		listTag.add(new FloatTag(this.z));
		return listTag;
	}

	public boolean equals(Object object) {
		if (!(object instanceof EulerRotation)) {
			return false;
		} else {
			EulerRotation eulerRotation = (EulerRotation)object;
			return this.x == eulerRotation.x && this.y == eulerRotation.y && this.z == eulerRotation.z;
		}
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}
}
