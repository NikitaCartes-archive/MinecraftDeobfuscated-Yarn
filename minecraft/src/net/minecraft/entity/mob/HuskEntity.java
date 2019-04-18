package net.minecraft.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class HuskEntity extends ZombieEntity {
	public HuskEntity(EntityType<? extends HuskEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return super.canSpawn(iWorld, spawnType) && (spawnType == SpawnType.field_16469 || iWorld.isSkyVisible(new BlockPos(this)));
	}

	@Override
	protected boolean burnsInDaylight() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14680;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15196;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14892;
	}

	@Override
	protected SoundEvent getStepSound() {
		return SoundEvents.field_15046;
	}

	@Override
	public boolean tryAttack(Entity entity) {
		boolean bl = super.tryAttack(entity);
		if (bl && this.getMainHandStack().isEmpty() && entity instanceof LivingEntity) {
			float f = this.world.getLocalDifficulty(new BlockPos(this)).getLocalDifficulty();
			((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.field_5903, 140 * (int)f));
		}

		return bl;
	}

	@Override
	protected boolean canConvertInWater() {
		return true;
	}

	@Override
	protected void convertInWater() {
		this.convertTo(EntityType.ZOMBIE);
		this.world.playLevelEvent(null, 1041, new BlockPos((int)this.x, (int)this.y, (int)this.z), 0);
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}
}
