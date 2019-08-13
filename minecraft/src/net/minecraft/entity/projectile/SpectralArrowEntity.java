package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class SpectralArrowEntity extends ProjectileEntity {
	private int duration = 200;

	public SpectralArrowEntity(EntityType<? extends SpectralArrowEntity> entityType, World world) {
		super(entityType, world);
	}

	public SpectralArrowEntity(World world, LivingEntity livingEntity) {
		super(EntityType.field_6135, livingEntity, world);
	}

	public SpectralArrowEntity(World world, double d, double e, double f) {
		super(EntityType.field_6135, d, e, f, world);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient && !this.inGround) {
			this.world.addParticle(ParticleTypes.field_11213, this.x, this.y, this.z, 0.0, 0.0, 0.0);
		}
	}

	@Override
	protected ItemStack asItemStack() {
		return new ItemStack(Items.field_8236);
	}

	@Override
	protected void onHit(LivingEntity livingEntity) {
		super.onHit(livingEntity);
		StatusEffectInstance statusEffectInstance = new StatusEffectInstance(StatusEffects.field_5912, this.duration, 0);
		livingEntity.addPotionEffect(statusEffectInstance);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("Duration")) {
			this.duration = compoundTag.getInt("Duration");
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Duration", this.duration);
	}
}
