package net.minecraft.entity.ai.goal;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class AnimalMateGoal extends Goal {
	protected final AnimalEntity owner;
	private final Class<? extends AnimalEntity> field_6403;
	protected World world;
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
		this.setControlBits(3);
	}

	@Override
	public boolean canStart() {
		if (!this.owner.method_6479()) {
			return false;
		} else {
			this.mate = this.findMate();
			return this.mate != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.mate.isValid() && this.mate.method_6479() && this.timer < 60;
	}

	@Override
	public void onRemove() {
		this.mate = null;
		this.timer = 0;
	}

	@Override
	public void tick() {
		this.owner.getLookControl().lookAt(this.mate, 10.0F, (float)this.owner.method_5978());
		this.owner.getNavigation().method_6335(this.mate, this.chance);
		this.timer++;
		if (this.timer >= 60 && this.owner.squaredDistanceTo(this.mate) < 9.0) {
			this.method_6249();
		}
	}

	@Nullable
	private AnimalEntity findMate() {
		List<AnimalEntity> list = this.world.getVisibleEntities(this.field_6403, this.owner.getBoundingBox().expand(8.0));
		double d = Double.MAX_VALUE;
		AnimalEntity animalEntity = null;

		for (AnimalEntity animalEntity2 : list) {
			if (this.owner.method_6474(animalEntity2) && this.owner.squaredDistanceTo(animalEntity2) < d) {
				animalEntity = animalEntity2;
				d = this.owner.squaredDistanceTo(animalEntity2);
			}
		}

		return animalEntity;
	}

	protected void method_6249() {
		PassiveEntity passiveEntity = this.owner.createChild(this.mate);
		if (passiveEntity != null) {
			ServerPlayerEntity serverPlayerEntity = this.owner.method_6478();
			if (serverPlayerEntity == null && this.mate.method_6478() != null) {
				serverPlayerEntity = this.mate.method_6478();
			}

			if (serverPlayerEntity != null) {
				serverPlayerEntity.method_7281(Stats.field_15410);
				CriterionCriterions.BRED_ANIMALS.handle(serverPlayerEntity, this.owner, this.mate, passiveEntity);
			}

			this.owner.setBreedingAge(6000);
			this.mate.setBreedingAge(6000);
			this.owner.method_6477();
			this.mate.method_6477();
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
			double g = random.nextDouble() * (double)this.owner.width * 2.0 - (double)this.owner.width;
			double h = 0.5 + random.nextDouble() * (double)this.owner.height;
			double j = random.nextDouble() * (double)this.owner.width * 2.0 - (double)this.owner.width;
			this.world.method_8406(ParticleTypes.field_11201, this.owner.x + g, this.owner.y + h, this.owner.z + j, d, e, f);
		}
	}
}
