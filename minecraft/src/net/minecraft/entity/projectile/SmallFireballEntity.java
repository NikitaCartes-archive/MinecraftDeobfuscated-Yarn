package net.minecraft.entity.projectile;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class SmallFireballEntity extends AbstractFireballEntity {
	public SmallFireballEntity(EntityType<? extends SmallFireballEntity> entityType, World world) {
		super(entityType, world);
	}

	public SmallFireballEntity(World world, LivingEntity owner, double velocityX, double velocityY, double velocityZ) {
		super(EntityType.SMALL_FIREBALL, owner, velocityX, velocityY, velocityZ, world);
	}

	public SmallFireballEntity(World world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
		super(EntityType.SMALL_FIREBALL, x, y, z, velocityX, velocityY, velocityZ, world);
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		if (!this.getWorld().isClient) {
			Entity entity = entityHitResult.getEntity();
			Entity entity2 = this.getOwner();
			int i = entity.getFireTicks();
			entity.setOnFireFor(5);
			if (!entity.damage(this.getDamageSources().fireball(this, entity2), 5.0F)) {
				entity.setFireTicks(i);
			} else if (entity2 instanceof LivingEntity) {
				this.applyDamageEffects((LivingEntity)entity2, entity);
			}
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		if (!this.getWorld().isClient) {
			Entity entity = this.getOwner();
			if (!(entity instanceof MobEntity) || this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
				BlockPos blockPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
				if (this.getWorld().isAir(blockPos)) {
					this.getWorld().setBlockState(blockPos, AbstractFireBlock.getState(this.getWorld(), blockPos));
				}
			}
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
	public boolean canHit() {
		return false;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}
}
