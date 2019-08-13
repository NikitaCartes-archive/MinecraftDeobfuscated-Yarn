package net.minecraft.entity.mob;

import java.util.Random;
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

	public static boolean method_20677(EntityType<HuskEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return method_20680(entityType, iWorld, spawnType, blockPos, random) && (spawnType == SpawnType.field_16469 || iWorld.isSkyVisible(blockPos));
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
		this.convertTo(EntityType.field_6051);
		this.world.playLevelEvent(null, 1041, new BlockPos(this), 0);
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}
}
