package net.minecraft.entity.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3857;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ThrownEggEntity extends class_3857 {
	public ThrownEggEntity(EntityType<? extends ThrownEggEntity> entityType, World world) {
		super(entityType, world);
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
					.addParticle(
						new ItemStackParticleParameters(ParticleTypes.field_11218, this.getItem()),
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
		if (hitResult.getType() == HitResult.Type.ENTITY) {
			((EntityHitResult)hitResult).getEntity().damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.0F);
		}

		if (!this.world.isClient) {
			if (this.random.nextInt(8) == 0) {
				int i = 1;
				if (this.random.nextInt(32) == 0) {
					i = 4;
				}

				for (int j = 0; j < i; j++) {
					ChickenEntity chickenEntity = EntityType.CHICKEN.create(this.world);
					chickenEntity.setBreedingAge(-24000);
					chickenEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
					this.world.spawnEntity(chickenEntity);
				}
			}

			this.world.summonParticle(this, (byte)3);
			this.invalidate();
		}
	}

	@Override
	protected Item method_16942() {
		return Items.field_8803;
	}
}
