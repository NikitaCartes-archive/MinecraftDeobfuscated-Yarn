package net.minecraft.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class WindChargeEntity extends ExplosiveProjectileEntity implements FlyingItemEntity {
	public WindChargeEntity(EntityType<? extends WindChargeEntity> entityType, World world) {
		super(entityType, world);
	}

	public WindChargeEntity(EntityType<? extends WindChargeEntity> type, BreezeEntity breeze, World world) {
		super(type, breeze.getX(), breeze.getChargeY(), breeze.getZ(), world);
		this.setOwner(breeze);
	}

	@Override
	public boolean collidesWith(Entity other) {
		return other instanceof WindChargeEntity ? false : super.collidesWith(other);
	}

	@Override
	protected boolean canHit(Entity entity) {
		return entity instanceof WindChargeEntity ? false : super.canHit(entity);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		if (!this.getWorld().isClient) {
			entityHitResult.getEntity()
				.damage(this.getDamageSources().mobProjectile(this, this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null), 1.0F);
			this.createExplosion();
		}
	}

	private void createExplosion() {
		this.getWorld()
			.createExplosion(
				this,
				null,
				null,
				this.getX(),
				this.getY(),
				this.getZ(),
				(float)(3.0 + this.random.nextDouble()),
				false,
				World.ExplosionSourceType.BLOW,
				ParticleTypes.GUST,
				ParticleTypes.GUST_EMITTER,
				SoundEvents.ENTITY_GENERIC_WIND_BURST
			);
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		this.createExplosion();
		this.discard();
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getWorld().isClient) {
			this.discard();
		}
	}

	@Override
	protected boolean isBurning() {
		return false;
	}

	@Override
	public ItemStack getStack() {
		return ItemStack.EMPTY;
	}

	@Override
	protected float getDrag() {
		return 1.0F;
	}

	@Nullable
	@Override
	protected ParticleEffect getParticleType() {
		return null;
	}

	@Override
	protected RaycastContext.ShapeType getRaycastShapeType() {
		return RaycastContext.ShapeType.OUTLINE;
	}
}
