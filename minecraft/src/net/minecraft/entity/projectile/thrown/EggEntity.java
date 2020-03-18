package net.minecraft.entity.projectile.thrown;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class EggEntity extends ThrownItemEntity {
	public EggEntity(EntityType<? extends EggEntity> entityType, World world) {
		super(entityType, world);
	}

	public EggEntity(World world, LivingEntity owner) {
		super(EntityType.EGG, owner, world);
	}

	public EggEntity(World world, double x, double y, double z) {
		super(EntityType.EGG, x, y, z, world);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 3) {
			double d = 0.08;

			for (int i = 0; i < 8; i++) {
				this.world
					.addParticle(
						new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()),
						this.getX(),
						this.getY(),
						this.getZ(),
						((double)this.random.nextFloat() - 0.5) * 0.08,
						((double)this.random.nextFloat() - 0.5) * 0.08,
						((double)this.random.nextFloat() - 0.5) * 0.08
					);
			}
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		entityHitResult.getEntity().damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.0F);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.world.isClient) {
			if (this.random.nextInt(8) == 0) {
				int i = 1;
				if (this.random.nextInt(32) == 0) {
					i = 4;
				}

				for (int j = 0; j < i; j++) {
					ChickenEntity chickenEntity = EntityType.CHICKEN.create(this.world);
					chickenEntity.setBreedingAge(-24000);
					chickenEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, 0.0F);
					this.world.spawnEntity(chickenEntity);
				}
			}

			this.world.sendEntityStatus(this, (byte)3);
			this.remove();
		}
	}

	@Override
	protected Item getDefaultItem() {
		return Items.EGG;
	}
}
