package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
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
	public WitherSkeletonEntity(World world) {
		super(EntityType.WITHER_SKELETON, world);
		this.setSize(0.7F, 2.4F);
		this.fireImmune = true;
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
	SoundEvent method_6998() {
		return SoundEvents.field_14955;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		Entity entity = damageSource.getAttacker();
		if (entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.method_7008()) {
				creeperEntity.method_7002();
				this.method_5706(Items.field_8791);
			}
		}
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8528));
	}

	@Override
	protected void method_5984(LocalDifficulty localDifficulty) {
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		EntityData entityData2 = super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
		this.method_6997();
		return entityData2;
	}

	@Override
	public float getEyeHeight() {
		return 2.1F;
	}

	@Override
	public boolean method_6121(Entity entity) {
		if (!super.method_6121(entity)) {
			return false;
		} else {
			if (entity instanceof LivingEntity) {
				((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.field_5920, 200));
			}

			return true;
		}
	}

	@Override
	protected ProjectileEntity method_6996(float f) {
		ProjectileEntity projectileEntity = super.method_6996(f);
		projectileEntity.setOnFireFor(100);
		return projectileEntity;
	}

	@Override
	public boolean isPotionEffective(StatusEffectInstance statusEffectInstance) {
		return statusEffectInstance.getEffectType() == StatusEffects.field_5920 ? false : super.isPotionEffective(statusEffectInstance);
	}
}
