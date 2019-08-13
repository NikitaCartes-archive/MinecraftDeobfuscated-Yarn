package net.minecraft.entity.projectile;

import net.minecraft.block.Blocks;
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

	public SmallFireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.field_6049, livingEntity, d, e, f, world);
	}

	public SmallFireballEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.field_6049, d, e, f, g, h, i, world);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		if (!this.world.isClient) {
			if (hitResult.getType() == HitResult.Type.field_1331) {
				Entity entity = ((EntityHitResult)hitResult).getEntity();
				if (!entity.isFireImmune()) {
					int i = entity.method_20802();
					entity.setOnFireFor(5);
					boolean bl = entity.damage(DamageSource.explosiveProjectile(this, this.owner), 5.0F);
					if (bl) {
						this.dealDamage(this.owner, entity);
					} else {
						entity.method_20803(i);
					}
				}
			} else if (this.owner == null || !(this.owner instanceof MobEntity) || this.world.getGameRules().getBoolean(GameRules.field_19388)) {
				BlockHitResult blockHitResult = (BlockHitResult)hitResult;
				BlockPos blockPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
				if (this.world.isAir(blockPos)) {
					this.world.setBlockState(blockPos, Blocks.field_10036.getDefaultState());
				}
			}

			this.remove();
		}
	}

	@Override
	public boolean collides() {
		return false;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return false;
	}
}
