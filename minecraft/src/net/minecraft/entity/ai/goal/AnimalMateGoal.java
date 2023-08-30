package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class AnimalMateGoal extends Goal {
	private static final TargetPredicate VALID_MATE_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(8.0).ignoreVisibility();
	protected final AnimalEntity animal;
	private final Class<? extends AnimalEntity> entityClass;
	protected final World world;
	@Nullable
	protected AnimalEntity mate;
	private int timer;
	private final double speed;

	public AnimalMateGoal(AnimalEntity animal, double speed) {
		this(animal, speed, animal.getClass());
	}

	public AnimalMateGoal(AnimalEntity animal, double speed, Class<? extends AnimalEntity> entityClass) {
		this.animal = animal;
		this.world = animal.getWorld();
		this.entityClass = entityClass;
		this.speed = speed;
		this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
	}

	@Override
	public boolean canStart() {
		if (!this.animal.isInLove()) {
			return false;
		} else {
			this.mate = this.findMate();
			return this.mate != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.mate.isAlive() && this.mate.isInLove() && this.timer < 60 && !this.mate.isPanicking();
	}

	@Override
	public void stop() {
		this.mate = null;
		this.timer = 0;
	}

	@Override
	public void tick() {
		this.animal.getLookControl().lookAt(this.mate, 10.0F, (float)this.animal.getMaxLookPitchChange());
		this.animal.getNavigation().startMovingTo(this.mate, this.speed);
		this.timer++;
		if (this.timer >= this.getTickCount(60) && this.animal.squaredDistanceTo(this.mate) < 9.0) {
			this.breed();
		}
	}

	@Nullable
	private AnimalEntity findMate() {
		List<? extends AnimalEntity> list = this.world.getTargets(this.entityClass, VALID_MATE_PREDICATE, this.animal, this.animal.getBoundingBox().expand(8.0));
		double d = Double.MAX_VALUE;
		AnimalEntity animalEntity = null;

		for (AnimalEntity animalEntity2 : list) {
			if (this.animal.canBreedWith(animalEntity2) && !animalEntity2.isPanicking() && this.animal.squaredDistanceTo(animalEntity2) < d) {
				animalEntity = animalEntity2;
				d = this.animal.squaredDistanceTo(animalEntity2);
			}
		}

		return animalEntity;
	}

	protected void breed() {
		this.animal.breed((ServerWorld)this.world, this.mate);
	}
}
