package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class AnimalMateGoal extends Goal {
	private static final TargetPredicate field_18086 = new TargetPredicate().setBaseMaxDistance(8.0).includeInvulnerable().includeTeammates().includeHidden();
	protected final AnimalEntity owner;
	private final Class<? extends AnimalEntity> field_6403;
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
		this.field_6403 = class_;
		this.chance = d;
		this.setControlBits(EnumSet.of(Goal.ControlBit.field_18405, Goal.ControlBit.field_18406));
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
		return this.mate.isValid() && this.mate.isInLove() && this.timer < 60;
	}

	@Override
	public void onRemove() {
		this.mate = null;
		this.timer = 0;
	}

	@Override
	public void tick() {
		this.owner.getLookControl().lookAt(this.mate, 10.0F, (float)this.owner.method_5978());
		this.owner.getNavigation().startMovingTo(this.mate, this.chance);
		this.timer++;
		if (this.timer >= 60 && this.owner.squaredDistanceTo(this.mate) < 9.0) {
			this.method_6249();
		}
	}

	@Nullable
	private AnimalEntity findMate() {
		List<AnimalEntity> list = this.world.method_18466(this.field_6403, field_18086, this.owner, this.owner.getBoundingBox().expand(8.0));
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

	protected void method_6249() {
		PassiveEntity passiveEntity = this.owner.createChild(this.mate);
		if (passiveEntity != null) {
			ServerPlayerEntity serverPlayerEntity = this.owner.getLovingPlayer();
			if (serverPlayerEntity == null && this.mate.getLovingPlayer() != null) {
				serverPlayerEntity = this.mate.getLovingPlayer();
			}

			if (serverPlayerEntity != null) {
				serverPlayerEntity.increaseStat(Stats.field_15410);
				Criterions.BRED_ANIMALS.handle(serverPlayerEntity, this.owner, this.mate, passiveEntity);
			}

			this.owner.setBreedingAge(6000);
			this.mate.setBreedingAge(6000);
			this.owner.resetLoveTicks();
			this.mate.resetLoveTicks();
			passiveEntity.setBreedingAge(-24000);
			passiveEntity.setPositionAndAngles(this.owner.x, this.owner.y, this.owner.z, 0.0F, 0.0F);
			this.world.spawnEntity(passiveEntity);
			Random random = this.owner.getRand();
			this.method_6251(random);
			if (this.world.getGameRules().getBoolean("doMobLoot")) {
				this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.owner.x, this.owner.y, this.owner.z, random.nextInt(7) + 1));
			}
		}
	}

	protected void method_6251(Random random) {
		for (int i = 0; i < 7; i++) {
			double d = random.nextGaussian() * 0.02;
			double e = random.nextGaussian() * 0.02;
			double f = random.nextGaussian() * 0.02;
			double g = random.nextDouble() * (double)this.owner.getWidth() * 2.0 - (double)this.owner.getWidth();
			double h = 0.5 + random.nextDouble() * (double)this.owner.getHeight();
			double j = random.nextDouble() * (double)this.owner.getWidth() * 2.0 - (double)this.owner.getWidth();
			this.world.addParticle(ParticleTypes.field_11201, this.owner.x + g, this.owner.y + h, this.owner.z + j, d, e, f);
		}
	}
}
