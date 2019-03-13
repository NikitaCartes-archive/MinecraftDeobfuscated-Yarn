package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
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
		this.fireImmune = true;
		this.method_5941(PathNodeType.field_14, 8.0F);
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_15214;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15027;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15122;
	}

	@Override
	SoundEvent method_6998() {
		return SoundEvents.field_14955;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		Entity entity = damageSource.method_5529();
		if (entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.method_7008()) {
				creeperEntity.method_7002();
				this.method_5706(Items.WITHER_SKELETON_SKULL);
			}
		}
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		this.method_5673(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8528));
	}

	@Override
	protected void method_5984(LocalDifficulty localDifficulty) {
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		EntityData entityData2 = super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		this.method_5996(EntityAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
		this.method_6997();
		return entityData2;
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return 2.1F;
	}

	@Override
	public boolean attack(Entity entity) {
		if (!super.attack(entity)) {
			return false;
		} else {
			if (entity instanceof LivingEntity) {
				((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.field_5920, 200));
			}

			return true;
		}
	}

	@Override
	protected ProjectileEntity method_6996(ItemStack itemStack, float f) {
		ProjectileEntity projectileEntity = super.method_6996(itemStack, f);
		projectileEntity.setOnFireFor(100);
		return projectileEntity;
	}

	@Override
	public boolean isPotionEffective(StatusEffectInstance statusEffectInstance) {
		return statusEffectInstance.getEffectType() == StatusEffects.field_5920 ? false : super.isPotionEffective(statusEffectInstance);
	}
}
