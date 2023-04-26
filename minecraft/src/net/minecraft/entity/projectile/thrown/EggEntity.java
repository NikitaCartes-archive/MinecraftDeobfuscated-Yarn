package net.minecraft.entity.projectile.thrown;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
			double d = 0.08;

			for (int i = 0; i < 8; i++) {
				this.getWorld()
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
		entityHitResult.getEntity().damage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getWorld().isClient) {
			if (this.random.nextInt(8) == 0) {
				int i = 1;
				if (this.random.nextInt(32) == 0) {
					i = 4;
				}

				for (int j = 0; j < i; j++) {
					ChickenEntity chickenEntity = EntityType.CHICKEN.create(this.getWorld());
					if (chickenEntity != null) {
						chickenEntity.setBreedingAge(-24000);
						chickenEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
						this.getWorld().spawnEntity(chickenEntity);
					}
				}
			}

			this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
			this.discard();
		}
	}

	@Override
	protected Item getDefaultItem() {
		return Items.EGG;
	}
}
