package net.minecraft.entity;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class SaddledComponent {
	private static final int MIN_BOOST_TIME = 140;
	private static final int field_30061 = 700;
	private final DataTracker dataTracker;
	private final TrackedData<Integer> boostTime;
	private final TrackedData<Boolean> saddled;
	private boolean boosted;
	private int boostedTime;

	public SaddledComponent(DataTracker dataTracker, TrackedData<Integer> boostTime, TrackedData<Boolean> saddled) {
		this.dataTracker = dataTracker;
		this.boostTime = boostTime;
		this.saddled = saddled;
	}

	public void boost() {
		this.boosted = true;
		this.boostedTime = 0;
	}

	public boolean boost(Random random) {
		if (this.boosted) {
			return false;
		} else {
			this.boosted = true;
			this.boostedTime = 0;
			this.dataTracker.set(this.boostTime, random.nextInt(841) + 140);
			return true;
		}
	}

	public void tickBoost() {
		if (this.boosted && this.boostedTime++ > this.getBoostTime()) {
			this.boosted = false;
		}
	}

	public float getMovementSpeedMultiplier() {
		return this.boosted ? 1.0F + 1.15F * MathHelper.sin((float)this.boostedTime / (float)this.getBoostTime() * (float) Math.PI) : 1.0F;
	}

	private int getBoostTime() {
		return this.dataTracker.get(this.boostTime);
	}

	public void writeNbt(NbtCompound nbt) {
		nbt.putBoolean("Saddle", this.isSaddled());
	}

	public void readNbt(NbtCompound nbt) {
		this.setSaddled(nbt.getBoolean("Saddle"));
	}

	public void setSaddled(boolean saddled) {
		this.dataTracker.set(this.saddled, saddled);
	}

	public boolean isSaddled() {
		return this.dataTracker.get(this.saddled);
	}
}
