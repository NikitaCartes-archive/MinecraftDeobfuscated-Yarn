package net.minecraft.entity.projectile;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public abstract class AbstractWindChargeEntity extends ExplosiveProjectileEntity implements FlyingItemEntity {
	public AbstractWindChargeEntity(EntityType<? extends AbstractWindChargeEntity> entityType, World world) {
		super(entityType, world);
	}

	public AbstractWindChargeEntity(EntityType<? extends AbstractWindChargeEntity> type, World world, Entity owner, double x, double y, double z) {
		super(type, x, y, z, world);
		this.setOwner(owner);
	}

	AbstractWindChargeEntity(EntityType<? extends AbstractWindChargeEntity> entityType, double d, double e, double f, double g, double h, double i, World world) {
		super(entityType, d, e, f, g, h, i, world);
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
		return entity instanceof AbstractWindChargeEntity ? false : super.canHit(entity);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		if (!this.getWorld().isClient) {
			entityHitResult.getEntity()
				.damage(this.getDamageSources().windCharge(this, this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null), 1.0F);
			this.createExplosion();
		}
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
	protected RaycastContext.ShapeType getRaycastShapeType() {
		return RaycastContext.ShapeType.OUTLINE;
	}

	public static class WindChargeExplosionBehavior extends ExplosionBehavior {
		@Override
		public boolean shouldDamage(Explosion explosion, Entity entity) {
			return false;
		}

		@Override
		public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
			return blockState.isIn(BlockTags.BLOCKS_WIND_CHARGE_EXPLOSIONS) ? Optional.of(3600000.0F) : Optional.empty();
		}
	}
}
