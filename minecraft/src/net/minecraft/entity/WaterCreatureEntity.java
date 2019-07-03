package net.minecraft.entity;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class WaterCreatureEntity extends MobEntityWithAi {
	protected WaterCreatureEntity(EntityType<? extends WaterCreatureEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean canBreatheInWater() {
		return true;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.AQUATIC;
	}

	@Override
	public boolean canSpawn(ViewableWorld viewableWorld) {
		return viewableWorld.intersectsEntities(this);
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	protected int getCurrentExperience(PlayerEntity playerEntity) {
		return 1 + this.world.random.nextInt(3);
	}

	protected void tickBreath(int i) {
		if (this.isAlive() && !this.isInsideWaterOrBubbleColumn()) {
			this.setBreath(i - 1);
			if (this.getBreath() == -20) {
				this.setBreath(0);
				this.damage(DamageSource.DROWN, 2.0F);
			}
		} else {
			this.setBreath(300);
		}
	}

	@Override
	public void baseTick() {
		int i = this.getBreath();
		super.baseTick();
		this.tickBreath(i);
	}

	@Override
	public boolean canFly() {
		return false;
	}

	@Override
	public boolean canBeLeashedBy(PlayerEntity playerEntity) {
		return false;
	}
}
