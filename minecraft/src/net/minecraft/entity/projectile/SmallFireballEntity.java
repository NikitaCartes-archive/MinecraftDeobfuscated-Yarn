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
		if (!this.world.isClient) {
			Entity entity = entityHitResult.getEntity();
			if (!entity.isFireImmune()) {
				Entity entity2 = this.getOwner();
				int i = entity.getFireTicks();
				entity.setOnFireFor(5);
				boolean bl = entity.damage(DamageSource.fireball(this, entity2), 5.0F);
				if (!bl) {
					entity.setFireTicks(i);
				} else if (entity2 instanceof LivingEntity) {
					this.dealDamage((LivingEntity)entity2, entity);
				}
			}
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		if (!this.world.isClient) {
			Entity entity = this.getOwner();
			if (entity == null || !(entity instanceof MobEntity) || this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
				BlockPos blockPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
				if (this.world.isAir(blockPos)) {
					this.world.setBlockState(blockPos, AbstractFireBlock.getState(this.world, blockPos));
				}
			}
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.world.isClient) {
			this.remove();
		}
	}

	@Override
	public boolean collides() {
		return false;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}
}
