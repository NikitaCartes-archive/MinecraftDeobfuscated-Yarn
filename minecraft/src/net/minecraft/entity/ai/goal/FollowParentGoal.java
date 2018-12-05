package net.minecraft.entity.ai.goal;

import java.util.List;
import net.minecraft.entity.passive.AnimalEntity;

public class FollowParentGoal extends Goal {
	private final AnimalEntity owner;
	private AnimalEntity parent;
	private final double field_6453;
	private int field_6454;

	public FollowParentGoal(AnimalEntity animalEntity, double d) {
		this.owner = animalEntity;
		this.field_6453 = d;
	}

	@Override
	public boolean canStart() {
		if (this.owner.getBreedingAge() >= 0) {
			return false;
		} else {
			List<AnimalEntity> list = this.owner.world.getVisibleEntities(this.owner.getClass(), this.owner.getBoundingBox().expand(8.0, 4.0, 8.0));
			AnimalEntity animalEntity = null;
			double d = Double.MAX_VALUE;

			for (AnimalEntity animalEntity2 : list) {
				if (animalEntity2.getBreedingAge() >= 0) {
					double e = this.owner.squaredDistanceTo(animalEntity2);
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
				this.parent = animalEntity;
				return true;
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.owner.getBreedingAge() >= 0) {
			return false;
		} else if (!this.parent.isValid()) {
			return false;
		} else {
			double d = this.owner.squaredDistanceTo(this.parent);
			return !(d < 9.0) && !(d > 256.0);
		}
	}

	@Override
	public void start() {
		this.field_6454 = 0;
	}

	@Override
	public void onRemove() {
		this.parent = null;
	}

	@Override
	public void tick() {
		if (--this.field_6454 <= 0) {
			this.field_6454 = 10;
			this.owner.getNavigation().method_6335(this.parent, this.field_6453);
		}
	}
}
