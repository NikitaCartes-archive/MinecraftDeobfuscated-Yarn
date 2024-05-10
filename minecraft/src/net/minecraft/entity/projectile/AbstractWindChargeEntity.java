package net.minecraft.entity.projectile;

import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.ExplosionBehavior;

public abstract class AbstractWindChargeEntity extends ExplosiveProjectileEntity implements FlyingItemEntity {
	public static final ExplosionBehavior EXPLOSION_BEHAVIOR = new AdvancedExplosionBehavior(
		true, false, Optional.empty(), Registries.BLOCK.getEntryList(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS).map(Function.identity())
	);

	public AbstractWindChargeEntity(EntityType<? extends AbstractWindChargeEntity> entityType, World world) {
		super(entityType, world);
		this.field_51893 = 0.0;
	}

	public AbstractWindChargeEntity(EntityType<? extends AbstractWindChargeEntity> type, World world, Entity owner, double x, double y, double z) {
		super(type, x, y, z, world);
		this.setOwner(owner);
		this.field_51893 = 0.0;
	}

	AbstractWindChargeEntity(EntityType<? extends AbstractWindChargeEntity> entityType, double d, double e, double f, Vec3d vec3d, World world) {
		super(entityType, d, e, f, vec3d, world);
		this.field_51893 = 0.0;
	}

	@Override
	protected Box calculateBoundingBox() {
		float f = this.getType().getDimensions().width() / 2.0F;
		float g = this.getType().getDimensions().height();
		float h = 0.15F;
		return new Box(
			this.getPos().x - (double)f,
			this.getPos().y - 0.15F,
			this.getPos().z - (double)f,
			this.getPos().x + (double)f,
			this.getPos().y - 0.15F + (double)g,
			this.getPos().z + (double)f
		);
	}

	@Override
	public boolean collidesWith(Entity other) {
		return other instanceof AbstractWindChargeEntity ? false : super.collidesWith(other);
	}

	@Override
	protected boolean canHit(Entity entity) {
		if (entity instanceof AbstractWindChargeEntity) {
			return false;
		} else {
			return entity.getType() == EntityType.END_CRYSTAL ? false : super.canHit(entity);
		}
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		if (!this.getWorld().isClient) {
			LivingEntity livingEntity2 = this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null;
			Entity entity = (Entity)entityHitResult.getEntity().getPassengerNearestTo(entityHitResult.getPos()).orElse(entityHitResult.getEntity());
			if (livingEntity2 != null) {
				livingEntity2.onAttacking(entity);
			}

			entity.damage(this.getDamageSources().windCharge(this, livingEntity2), 1.0F);
			this.createExplosion();
		}
	}

	@Override
	public void addVelocity(double deltaX, double deltaY, double deltaZ) {
	}

	protected abstract void createExplosion();

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		if (!this.getWorld().isClient) {
			this.createExplosion();
			this.discard();
		}
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

	@Override
	protected float getDragInWater() {
		return this.getDrag();
	}

	@Nullable
	@Override
	protected ParticleEffect getParticleType() {
		return null;
	}

	@Override
	public void tick() {
		if (!this.getWorld().isClient && this.getBlockY() > this.getWorld().getTopY() + 30) {
			this.createExplosion();
			this.discard();
		} else {
			super.tick();
		}
	}
}
