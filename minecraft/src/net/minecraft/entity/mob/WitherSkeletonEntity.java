package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class WitherSkeletonEntity extends AbstractSkeletonEntity {
	public WitherSkeletonEntity(EntityType<? extends WitherSkeletonEntity> entityType, World world) {
		super(entityType, world);
		this.setPathNodeTypeWeight(PathNodeType.field_14, 8.0F);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15214;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15027;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15122;
	}

	@Override
	SoundEvent getStepSound() {
		return SoundEvents.field_14955;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		Entity entity = damageSource.getAttacker();
		if (entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.shouldDropHead()) {
				creeperEntity.onHeadDropped();
				this.dropItem(Items.WITHER_SKELETON_SKULL);
			}
		}
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		this.setEquippedStack(EquipmentSlot.field_6173, new ItemStack(Items.field_8528));
	}

	@Override
	protected void updateEnchantments(LocalDifficulty localDifficulty) {
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		EntityData entityData2 = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
		this.updateAttackType();
		return entityData2;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return 2.1F;
	}

	@Override
	public boolean tryAttack(Entity entity) {
		if (!super.tryAttack(entity)) {
			return false;
		} else {
			if (entity instanceof LivingEntity) {
				((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.field_5920, 200));
			}

			return true;
		}
	}

	@Override
	protected ProjectileEntity createArrowProjectile(ItemStack itemStack, float f) {
		ProjectileEntity projectileEntity = super.createArrowProjectile(itemStack, f);
		projectileEntity.setOnFireFor(100);
		return projectileEntity;
	}

	@Override
	public boolean isPotionEffective(StatusEffectInstance statusEffectInstance) {
		return statusEffectInstance.getEffectType() == StatusEffects.field_5920 ? false : super.isPotionEffective(statusEffectInstance);
	}
}
