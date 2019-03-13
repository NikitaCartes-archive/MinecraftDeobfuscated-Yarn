package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.passive.AnimalEntity;

public class FollowParentGoal extends Goal {
	private final AnimalEntity field_6455;
	private AnimalEntity field_6452;
	private final double field_6453;
	private int field_6454;

	public FollowParentGoal(AnimalEntity animalEntity, double d) {
		this.field_6455 = animalEntity;
		this.field_6453 = d;
	}

	@Override
	public boolean canStart() {
		if (this.field_6455.getBreedingAge() >= 0) {
			return false;
		} else {
			List<AnimalEntity> list = this.field_6455.field_6002.method_18467(this.field_6455.getClass(), this.field_6455.method_5829().expand(8.0, 4.0, 8.0));
			AnimalEntity animalEntity = null;
			double d = Double.MAX_VALUE;

			for (AnimalEntity animalEntity2 : list) {
				if (animalEntity2.getBreedingAge() >= 0) {
					double e = this.field_6455.squaredDistanceTo(animalEntity2);
					if (!(e > d)) {
						d = e;
						animalEntity = animalEntity2;
					}
				}
			}

			if (animalEntity == null) {
				return false;
			} else if (d < 9.0) {
				return false;
			} else {
				this.field_6452 = animalEntity;
				return true;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.field_6455.getBreedingAge() >= 0) {
			return false;
		} else if (!this.field_6452.isValid()) {
			return false;
		} else {
			double d = this.field_6455.squaredDistanceTo(this.field_6452);
			return !(d < 9.0) && !(d > 256.0);
		}
	}

	@Override
	public void start() {
		this.field_6454 = 0;
	}

	@Override
	public void onRemove() {
		this.field_6452 = null;
	}

	@Override
	public void tick() {
		if (--this.field_6454 <= 0) {
			this.field_6454 = 10;
			this.field_6455.method_5942().startMovingTo(this.field_6452, this.field_6453);
		}
	}
}
