package net.minecraft.entity.ai.goal;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.passive.AnimalEntity;

public class FollowParentGoal extends Goal {
	public static final int HORIZONTAL_CHECK_RANGE = 8;
	public static final int VERTICAL_CHECK_RANGE = 4;
	public static final int MIN_DISTANCE = 3;
	private final AnimalEntity animal;
	@Nullable
	private AnimalEntity parent;
	private final double speed;
	private int delay;

	public FollowParentGoal(AnimalEntity animal, double speed) {
		this.animal = animal;
		this.speed = speed;
	}

	@Override
	public boolean canStart() {
		if (this.animal.getBreedingAge() >= 0) {
			return false;
		} else {
			List<? extends AnimalEntity> list = this.animal.world.getNonSpectatingEntities(this.animal.getClass(), this.animal.getBoundingBox().expand(8.0, 4.0, 8.0));
			AnimalEntity animalEntity = null;
			double d = Double.MAX_VALUE;

			for (AnimalEntity animalEntity2 : list) {
				if (animalEntity2.getBreedingAge() >= 0) {
					double e = this.animal.squaredDistanceTo(animalEntity2);
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
		if (this.animal.getBreedingAge() >= 0) {
			return false;
		} else if (!this.parent.isAlive()) {
			return false;
		} else {
			double d = this.animal.squaredDistanceTo(this.parent);
			return !(d < 9.0) && !(d > 256.0);
		}
	}

	@Override
	public void start() {
		this.delay = 0;
	}

	@Override
	public void stop() {
		this.parent = null;
	}

	@Override
	public void tick() {
		if (--this.delay <= 0) {
			this.delay = this.getTickCount(10);
			this.animal.getNavigation().startMovingTo(this.parent, this.speed);
		}
	}
}
