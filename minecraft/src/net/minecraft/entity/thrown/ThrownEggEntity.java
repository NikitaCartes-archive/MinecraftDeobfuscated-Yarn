package net.minecraft.entity.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticle;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.HitResult;
import net.minecraft.world.World;

public class ThrownEggEntity extends ThrownEntity {
	public ThrownEggEntity(World world) {
		super(EntityType.EGG, world);
	}

	public ThrownEggEntity(World world, LivingEntity livingEntity) {
		super(EntityType.EGG, livingEntity, world);
	}

	public ThrownEggEntity(World world, double d, double e, double f) {
		super(EntityType.EGG, d, e, f, world);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5711(byte b) {
		if (b == 3) {
			double d = 0.08;

			for (int i = 0; i < 8; i++) {
				this.world
					.method_8406(
						new ItemStackParticle(ParticleTypes.field_11218, new ItemStack(Items.field_8803)),
						this.x,
						this.y,
						this.z,
						((double)this.random.nextFloat() - 0.5) * 0.08,
						((double)this.random.nextFloat() - 0.5) * 0.08,
						((double)this.random.nextFloat() - 0.5) * 0.08
					);
			}
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		if (hitResult.entity != null) {
			hitResult.entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.0F);
		}

		if (!this.world.isRemote) {
			if (this.random.nextInt(8) == 0) {
				int i = 1;
				if (this.random.nextInt(32) == 0) {
					i = 4;
				}

				for (int j = 0; j < i; j++) {
					ChickenEntity chickenEntity = new ChickenEntity(this.world);
					chickenEntity.setBreedingAge(-24000);
					chickenEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
					this.world.spawnEntity(chickenEntity);
				}
			}

			this.world.method_8421(this, (byte)3);
			this.invalidate();
		}
	}
}
