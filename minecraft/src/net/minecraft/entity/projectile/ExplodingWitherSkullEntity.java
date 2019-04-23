package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class ExplodingWitherSkullEntity extends ExplosiveProjectileEntity {
	private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(ExplodingWitherSkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public ExplodingWitherSkullEntity(EntityType<? extends ExplodingWitherSkullEntity> entityType, World world) {
		super(entityType, world);
	}

	public ExplodingWitherSkullEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.field_6130, livingEntity, d, e, f, world);
	}

	@Environment(EnvType.CLIENT)
	public ExplodingWitherSkullEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.field_6130, d, e, f, g, h, i, world);
	}

	@Override
	protected float getDrag() {
		return this.isCharged() ? 0.73F : super.getDrag();
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Override
	public float getEffectiveExplosionResistance(
		Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f
	) {
		return this.isCharged() && WitherEntity.canDestroy(blockState) ? Math.min(0.8F, f) : f;
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		if (!this.world.isClient) {
			if (hitResult.getType() == HitResult.Type.field_1331) {
				Entity entity = ((EntityHitResult)hitResult).getEntity();
				if (this.owner != null) {
					if (entity.damage(DamageSource.mob(this.owner), 8.0F)) {
						if (entity.isAlive()) {
							this.dealDamage(this.owner, entity);
						} else {
							this.owner.heal(5.0F);
						}
					}
				} else {
					entity.damage(DamageSource.MAGIC, 5.0F);
				}

				if (entity instanceof LivingEntity) {
					int i = 0;
					if (this.world.getDifficulty() == Difficulty.field_5802) {
						i = 10;
					} else if (this.world.getDifficulty() == Difficulty.field_5807) {
						i = 40;
					}

					if (i > 0) {
						((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.field_5920, 20 * i, 1));
					}
				}
			}

			Explosion.DestructionType destructionType = this.world.getGameRules().getBoolean("mobGriefing")
				? Explosion.DestructionType.field_18687
				: Explosion.DestructionType.field_18685;
			this.world.createExplosion(this, this.x, this.y, this.z, 1.0F, false, destructionType);
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

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(CHARGED, false);
	}

	public boolean isCharged() {
		return this.dataTracker.get(CHARGED);
	}

	public void setCharged(boolean bl) {
		this.dataTracker.set(CHARGED, bl);
	}

	@Override
	protected boolean isBurning() {
		return false;
	}
}
