package net.minecraft.entity.projectile;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SmallFireballEntity extends ExplosiveProjectileEntity {
	public SmallFireballEntity(World world) {
		super(EntityType.SMALL_FIREBALL, world, 0.3125F, 0.3125F);
	}

	public SmallFireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.SMALL_FIREBALL, livingEntity, d, e, f, world, 0.3125F, 0.3125F);
	}

	public SmallFireballEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.SMALL_FIREBALL, d, e, f, g, h, i, world, 0.3125F, 0.3125F);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		if (!this.world.isRemote) {
			if (hitResult.entity != null) {
				if (!hitResult.entity.isFireImmune()) {
					hitResult.entity.setOnFireFor(5);
					boolean bl = hitResult.entity.damage(DamageSource.explosiveProjectile(this, this.owner), 5.0F);
					if (bl) {
						this.method_5723(this.owner, hitResult.entity);
					}
				}
			} else {
				boolean bl = true;
				if (this.owner != null && this.owner instanceof MobEntity) {
					bl = this.world.getGameRules().getBoolean("mobGriefing");
				}

				if (bl) {
					BlockPos blockPos = hitResult.getBlockPos().method_10093(hitResult.field_1327);
					if (this.world.isAir(blockPos)) {
						this.world.setBlockState(blockPos, Blocks.field_10036.getDefaultState());
					}
				}
			}

			this.invalidate();
		}
	}

	@Override
	public boolean doesCollide() {
		return false;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return false;
	}
}
