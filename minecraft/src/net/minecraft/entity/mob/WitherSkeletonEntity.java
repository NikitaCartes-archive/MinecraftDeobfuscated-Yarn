package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class WitherSkeletonEntity extends AbstractSkeletonEntity {
	public WitherSkeletonEntity(EntityType<? extends WitherSkeletonEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
	}

	@Override
	protected void initGoals() {
		this.targetSelector.add(3, new FollowTargetGoal(this, AbstractPiglinEntity.class, true));
		super.initGoals();
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_WITHER_SKELETON_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WITHER_SKELETON_DEATH;
	}

	@Override
	SoundEvent getStepSound() {
		return SoundEvents.ENTITY_WITHER_SKELETON_STEP;
	}

	@Override
	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropEquipment(source, lootingMultiplier, allowDrops);
		Entity entity = source.getAttacker();
		if (entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.shouldDropHead()) {
				creeperEntity.onHeadDropped();
				this.dropItem(Items.WITHER_SKELETON_SKULL);
			}
		}
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
	}

	@Override
	protected void updateEnchantments(LocalDifficulty difficulty) {
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		EntityData entityData2 = super.initialize(world, difficulty, spawnReason, entityData, entityTag);
		this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.0);
		this.updateAttackType();
		return entityData2;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 2.1F;
	}

	@Override
	public boolean tryAttack(Entity target) {
		if (!super.tryAttack(target)) {
			return false;
		} else {
			if (target instanceof LivingEntity) {
				((LivingEntity)target).addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 200));
			}

			return true;
		}
	}

	@Override
	protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier) {
		PersistentProjectileEntity persistentProjectileEntity = super.createArrowProjectile(arrow, damageModifier);
		persistentProjectileEntity.setOnFireFor(100);
		return persistentProjectileEntity;
	}

	@Override
	public boolean canHaveStatusEffect(StatusEffectInstance effect) {
		return effect.getEffectType() == StatusEffects.WITHER ? false : super.canHaveStatusEffect(effect);
	}
}
