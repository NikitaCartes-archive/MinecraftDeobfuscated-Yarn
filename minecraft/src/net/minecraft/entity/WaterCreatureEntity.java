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
	public boolean method_5957(ViewableWorld viewableWorld) {
		return viewableWorld.method_8606(this);
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 120;
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return true;
	}

	@Override
	protected int getCurrentExperience(PlayerEntity playerEntity) {
		return 1 + this.world.random.nextInt(3);
	}

	protected void method_6673(int i) {
		if (this.isValid() && !this.isInsideWaterOrBubbleColumn()) {
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
		this.method_6673(i);
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
