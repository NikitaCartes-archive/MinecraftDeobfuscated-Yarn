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

	public SpectralArrowEntity(World world) {
		super(EntityType.SPECTRAL_ARROW, world);
	}

	public SpectralArrowEntity(World world, LivingEntity livingEntity) {
		super(EntityType.SPECTRAL_ARROW, livingEntity, world);
	}

	public SpectralArrowEntity(World world, double d, double e, double f) {
		super(EntityType.SPECTRAL_ARROW, d, e, f, world);
	}

	@Override
	public void update() {
		super.update();
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
