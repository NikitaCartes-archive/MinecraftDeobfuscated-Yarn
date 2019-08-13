package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class AnimalMateGoal extends Goal {
	private static final TargetPredicate VALID_MATE_PREDICATE = new TargetPredicate()
		.setBaseMaxDistance(8.0)
		.includeInvulnerable()
		.includeTeammates()
		.includeHidden();
	protected final AnimalEntity animal;
	private final Class<? extends AnimalEntity> entityClass;
	protected final World world;
	protected AnimalEntity mate;
	private int timer;
	private final double chance;

	public AnimalMateGoal(AnimalEntity animalEntity, double d) {
		this(animalEntity, d, animalEntity.getClass());
	}

	public AnimalMateGoal(AnimalEntity animalEntity, double d, Class<? extends AnimalEntity> class_) {
		this.animal = animalEntity;
		this.world = animalEntity.world;
		this.entityClass = class_;
		this.chance = d;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
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
		return this.mate.isAlive() && this.mate.isInLove() && this.timer < 60;
	}

	@Override
	public void stop() {
		this.mate = null;
		this.timer = 0;
	}

	@Override
	public void tick() {
		this.animal.getLookControl().lookAt(this.mate, 10.0F, (float)this.animal.getLookPitchSpeed());
		this.animal.getNavigation().startMovingTo(this.mate, this.chance);
		this.timer++;
		if (this.timer >= 60 && this.animal.squaredDistanceTo(this.mate) < 9.0) {
			this.breed();
		}
	}

	@Nullable
	private AnimalEntity findMate() {
		List<AnimalEntity> list = this.world.getTargets(this.entityClass, VALID_MATE_PREDICATE, this.animal, this.animal.getBoundingBox().expand(8.0));
		double d = Double.MAX_VALUE;
		AnimalEntity animalEntity = null;

		for (AnimalEntity animalEntity2 : list) {
			if (this.animal.canBreedWith(animalEntity2) && this.animal.squaredDistanceTo(animalEntity2) < d) {
				animalEntity = animalEntity2;
				d = this.animal.squaredDistanceTo(animalEntity2);
			}
		}

		return animalEntity;
	}

	protected void breed() {
		PassiveEntity passiveEntity = this.animal.createChild(this.mate);
		if (passiveEntity != null) {
			ServerPlayerEntity serverPlayerEntity = this.animal.getLovingPlayer();
			if (serverPlayerEntity == null && this.mate.getLovingPlayer() != null) {
				serverPlayerEntity = this.mate.getLovingPlayer();
			}

			if (serverPlayerEntity != null) {
				serverPlayerEntity.incrementStat(Stats.field_15410);
				Criterions.BRED_ANIMALS.handle(serverPlayerEntity, this.animal, this.mate, passiveEntity);
			}

			this.animal.setBreedingAge(6000);
			this.mate.setBreedingAge(6000);
			this.animal.resetLoveTicks();
			this.mate.resetLoveTicks();
			passiveEntity.setBreedingAge(-24000);
			passiveEntity.setPositionAndAngles(this.animal.x, this.animal.y, this.animal.z, 0.0F, 0.0F);
			this.world.spawnEntity(passiveEntity);
			this.world.sendEntityStatus(this.animal, (byte)18);
			if (this.world.getGameRules().getBoolean(GameRules.field_19391)) {
				this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.animal.x, this.animal.y, this.animal.z, this.animal.getRand().nextInt(7) + 1));
			}
		}
	}
}
