package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_4051;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;

public class AnimalMateGoal extends Goal {
	private static final class_4051 field_18086 = new class_4051().method_18418(8.0).method_18417().method_18421().method_18422();
	protected final AnimalEntity field_6404;
	private final Class<? extends AnimalEntity> field_6403;
	protected final World field_6405;
	protected AnimalEntity field_6406;
	private int timer;
	private final double chance;

	public AnimalMateGoal(AnimalEntity animalEntity, double d) {
		this(animalEntity, d, animalEntity.getClass());
	}

	public AnimalMateGoal(AnimalEntity animalEntity, double d, Class<? extends AnimalEntity> class_) {
		this.field_6404 = animalEntity;
		this.field_6405 = animalEntity.field_6002;
		this.field_6403 = class_;
		this.chance = d;
		this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
	}

	@Override
	public boolean canStart() {
		if (!this.field_6404.isInLove()) {
			return false;
		} else {
			this.field_6406 = this.method_6250();
			return this.field_6406 != null;
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6406.isValid() && this.field_6406.isInLove() && this.timer < 60;
	}

	@Override
	public void onRemove() {
		this.field_6406 = null;
		this.timer = 0;
	}

	@Override
	public void tick() {
		this.field_6404.method_5988().lookAt(this.field_6406, 10.0F, (float)this.field_6404.method_5978());
		this.field_6404.method_5942().startMovingTo(this.field_6406, this.chance);
		this.timer++;
		if (this.timer >= 60 && this.field_6404.squaredDistanceTo(this.field_6406) < 9.0) {
			this.method_6249();
		}
	}

	@Nullable
	private AnimalEntity method_6250() {
		List<AnimalEntity> list = this.field_6405.method_18466(this.field_6403, field_18086, this.field_6404, this.field_6404.method_5829().expand(8.0));
		double d = Double.MAX_VALUE;
		AnimalEntity animalEntity = null;

		for (AnimalEntity animalEntity2 : list) {
			if (this.field_6404.canBreedWith(animalEntity2) && this.field_6404.squaredDistanceTo(animalEntity2) < d) {
				animalEntity = animalEntity2;
				d = this.field_6404.squaredDistanceTo(animalEntity2);
			}
		}

		return animalEntity;
	}

	protected void method_6249() {
		PassiveEntity passiveEntity = this.field_6404.createChild(this.field_6406);
		if (passiveEntity != null) {
			ServerPlayerEntity serverPlayerEntity = this.field_6404.method_6478();
			if (serverPlayerEntity == null && this.field_6406.method_6478() != null) {
				serverPlayerEntity = this.field_6406.method_6478();
			}

			if (serverPlayerEntity != null) {
				serverPlayerEntity.method_7281(Stats.field_15410);
				Criterions.BRED_ANIMALS.method_855(serverPlayerEntity, this.field_6404, this.field_6406, passiveEntity);
			}

			this.field_6404.setBreedingAge(6000);
			this.field_6406.setBreedingAge(6000);
			this.field_6404.resetLoveTicks();
			this.field_6406.resetLoveTicks();
			passiveEntity.setBreedingAge(-24000);
			passiveEntity.setPositionAndAngles(this.field_6404.x, this.field_6404.y, this.field_6404.z, 0.0F, 0.0F);
			this.field_6405.spawnEntity(passiveEntity);
			Random random = this.field_6404.getRand();
			this.method_6251(random);
			if (this.field_6405.getGameRules().getBoolean("doMobLoot")) {
				this.field_6405.spawnEntity(new ExperienceOrbEntity(this.field_6405, this.field_6404.x, this.field_6404.y, this.field_6404.z, random.nextInt(7) + 1));
			}
		}
	}

	protected void method_6251(Random random) {
		for (int i = 0; i < 7; i++) {
			double d = random.nextGaussian() * 0.02;
			double e = random.nextGaussian() * 0.02;
			double f = random.nextGaussian() * 0.02;
			double g = random.nextDouble() * (double)this.field_6404.getWidth() * 2.0 - (double)this.field_6404.getWidth();
			double h = 0.5 + random.nextDouble() * (double)this.field_6404.getHeight();
			double j = random.nextDouble() * (double)this.field_6404.getWidth() * 2.0 - (double)this.field_6404.getWidth();
			this.field_6405.method_8406(ParticleTypes.field_11201, this.field_6404.x + g, this.field_6404.y + h, this.field_6404.z + j, d, e, f);
		}
	}
}
