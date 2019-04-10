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
import net.minecraft.world.World;

public class AnimalMateGoal extends Goal {
	private static final TargetPredicate VALID_MATE_PREDICATE = new TargetPredicate()
		.setBaseMaxDistance(8.0)
		.includeInvulnerable()
		.includeTeammates()
		.includeHidden();
	protected final AnimalEntity owner;
	private final Class<? extends AnimalEntity> entityClass;
	protected final World world;
	protected AnimalEntity mate;
	private int timer;
	private final double chance;

	public AnimalMateGoal(AnimalEntity animalEntity, double d) {
		this(animalEntity, d, animalEntity.getClass());
	}

	public AnimalMateGoal(AnimalEntity animalEntity, double d, Class<? extends AnimalEntity> class_) {
		this.owner = animalEntity;
		this.world = animalEntity.world;
		this.entityClass = class_;
		this.chance = d;
		this.setControls(EnumSet.of(Goal.Control.field_18405, Goal.Control.field_18406));
	}

	@Override
	public boolean canStart() {
		if (!this.owner.isInLove()) {
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
		this.owner.getLookControl().lookAt(this.mate, 10.0F, (float)this.owner.getLookPitchSpeed());
		this.owner.getNavigation().startMovingTo(this.mate, this.chance);
		this.timer++;
		if (this.timer >= 60 && this.owner.squaredDistanceTo(this.mate) < 9.0) {
			this.breed();
		}
	}

	@Nullable
	private AnimalEntity findMate() {
		List<AnimalEntity> list = this.world.getTargets(this.entityClass, VALID_MATE_PREDICATE, this.owner, this.owner.getBoundingBox().expand(8.0));
		double d = Double.MAX_VALUE;
		AnimalEntity animalEntity = null;

		for (AnimalEntity animalEntity2 : list) {
			if (this.owner.canBreedWith(animalEntity2) && this.owner.squaredDistanceTo(animalEntity2) < d) {
				animalEntity = animalEntity2;
				d = this.owner.squaredDistanceTo(animalEntity2);
			}
		}

		return animalEntity;
	}

	protected void breed() {
		PassiveEntity passiveEntity = this.owner.createChild(this.mate);
		if (passiveEntity != null) {
			ServerPlayerEntity serverPlayerEntity = this.owner.getLovingPlayer();
			if (serverPlayerEntity == null && this.mate.getLovingPlayer() != null) {
				serverPlayerEntity = this.mate.getLovingPlayer();
			}

			if (serverPlayerEntity != null) {
				serverPlayerEntity.incrementStat(Stats.field_15410);
				Criterions.BRED_ANIMALS.handle(serverPlayerEntity, this.owner, this.mate, passiveEntity);
			}

			this.owner.setBreedingAge(6000);
			this.mate.setBreedingAge(6000);
			this.owner.resetLoveTicks();
			this.mate.resetLoveTicks();
			passiveEntity.setBreedingAge(-24000);
			passiveEntity.setPositionAndAngles(this.owner.x, this.owner.y, this.owner.z, 0.0F, 0.0F);
			this.world.spawnEntity(passiveEntity);
			this.world.sendEntityStatus(this.owner, (byte)18);
			if (this.world.getGameRules().getBoolean("doMobLoot")) {
				this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.owner.x, this.owner.y, this.owner.z, this.owner.getRand().nextInt(7) + 1));
			}
		}
	}
}
