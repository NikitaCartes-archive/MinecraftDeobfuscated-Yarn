package net.minecraft.entity.mob;

import net.minecraft.class_3730;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LightType;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public abstract class HostileEntity extends MobEntityWithAi implements Monster {
	protected HostileEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.field_15251;
	}

	@Override
	public void updateMovement() {
		this.method_6119();
		this.method_16827();
		super.updateMovement();
	}

	protected void method_16827() {
		float f = this.method_5718();
		if (f > 0.5F) {
			this.field_6278 += 2;
		}
	}

	@Override
	public void update() {
		super.update();
		if (!this.world.isRemote && this.world.getDifficulty() == Difficulty.PEACEFUL) {
			this.invalidate();
		}
	}

	@Override
	protected SoundEvent getSoundSwim() {
		return SoundEvents.field_14630;
	}

	@Override
	protected SoundEvent getSoundSplash() {
		return SoundEvents.field_14836;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return this.isInvulnerableTo(damageSource) ? false : super.damage(damageSource, f);
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14994;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14899;
	}

	@Override
	protected SoundEvent getFallSound(int i) {
		return i > 4 ? SoundEvents.field_15157 : SoundEvents.field_14754;
	}

	@Override
	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		return 0.5F - viewableWorld.method_8610(blockPos);
	}

	protected boolean method_7075() {
		BlockPos blockPos = new BlockPos(this.x, this.getBoundingBox().minY, this.z);
		if (this.world.getLightLevel(LightType.field_9284, blockPos) > this.random.nextInt(32)) {
			return false;
		} else {
			int i = this.world.isThundering() ? this.world.method_8603(blockPos, 10) : this.world.method_8602(blockPos);
			return i <= this.random.nextInt(8);
		}
	}

	@Override
	public boolean method_5979(IWorld iWorld, class_3730 arg) {
		return iWorld.getDifficulty() != Difficulty.PEACEFUL && this.method_7075() && super.method_5979(iWorld, arg);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE);
	}

	@Override
	protected boolean canDropLootAndXp() {
		return true;
	}

	public boolean method_7076(PlayerEntity playerEntity) {
		return true;
	}
}
