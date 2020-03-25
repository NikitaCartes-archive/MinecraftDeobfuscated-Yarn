package net.minecraft;

import java.util.Random;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.CompoundTag;

public class class_4980 {
	private final DataTracker field_23218;
	private final TrackedData<Integer> field_23219;
	private final TrackedData<Boolean> field_23220;
	public boolean field_23215;
	public int field_23216;
	public int field_23217;

	public class_4980(DataTracker dataTracker, TrackedData<Integer> trackedData, TrackedData<Boolean> trackedData2) {
		this.field_23218 = dataTracker;
		this.field_23219 = trackedData;
		this.field_23220 = trackedData2;
	}

	public void method_26307() {
		this.field_23215 = true;
		this.field_23216 = 0;
		this.field_23217 = this.field_23218.get(this.field_23219);
	}

	public boolean method_26308(Random random) {
		if (this.field_23215) {
			return false;
		} else {
			this.field_23215 = true;
			this.field_23216 = 0;
			this.field_23217 = random.nextInt(841) + 140;
			this.field_23218.set(this.field_23219, this.field_23217);
			return true;
		}
	}

	public void method_26309(CompoundTag compoundTag) {
		compoundTag.putBoolean("Saddle", this.method_26311());
	}

	public void method_26312(CompoundTag compoundTag) {
		this.method_26310(compoundTag.getBoolean("Saddle"));
	}

	public void method_26310(boolean bl) {
		this.field_23218.set(this.field_23220, bl);
	}

	public boolean method_26311() {
		return this.field_23218.get(this.field_23220);
	}
}
