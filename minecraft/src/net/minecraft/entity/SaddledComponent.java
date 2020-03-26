package net.minecraft.entity;

import java.util.Random;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.CompoundTag;

public class SaddledComponent {
	private final DataTracker dataTracker;
	private final TrackedData<Integer> boostTime;
	private final TrackedData<Boolean> saddled;
	public boolean boosted;
	public int field_23216;
	public int currentBoostTime;

	public SaddledComponent(DataTracker dataTracker, TrackedData<Integer> boostTime, TrackedData<Boolean> saddled) {
		this.dataTracker = dataTracker;
		this.boostTime = boostTime;
		this.saddled = saddled;
	}

	public void boost() {
		this.boosted = true;
		this.field_23216 = 0;
		this.currentBoostTime = this.dataTracker.get(this.boostTime);
	}

	public boolean boost(Random random) {
		if (this.boosted) {
			return false;
		} else {
			this.boosted = true;
			this.field_23216 = 0;
			this.currentBoostTime = random.nextInt(841) + 140;
			this.dataTracker.set(this.boostTime, this.currentBoostTime);
			return true;
		}
	}

	public void toTag(CompoundTag tag) {
		tag.putBoolean("Saddle", this.isSaddled());
	}

	public void fromTag(CompoundTag tag) {
		this.setSaddled(tag.getBoolean("Saddle"));
	}

	public void setSaddled(boolean saddled) {
		this.dataTracker.set(this.saddled, saddled);
	}

	public boolean isSaddled() {
		return this.dataTracker.get(this.saddled);
	}
}
