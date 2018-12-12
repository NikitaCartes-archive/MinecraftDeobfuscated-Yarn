package net.minecraft.entity.projectile;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class ExplodingWitherSkullEntity extends ExplosiveProjectileEntity {
	private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(ExplodingWitherSkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public ExplodingWitherSkullEntity(World world) {
		super(EntityType.WITHER_SKULL, world, 0.3125F, 0.3125F);
	}

	public ExplodingWitherSkullEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.WITHER_SKULL, livingEntity, d, e, f, world, 0.3125F, 0.3125F);
	}

	@Environment(EnvType.CLIENT)
	public ExplodingWitherSkullEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.WITHER_SKULL, d, e, f, g, h, i, world, 0.3125F, 0.3125F);
	}

	@Override
	protected float method_7466() {
		return this.isCharged() ? 0.73F : super.method_7466();
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Override
	public float getEffectiveExplosionResistance(
		Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f
	) {
		return this.isCharged() && EntityWither.canDestroy(blockState.getBlock()) ? Math.min(0.8F, f) : f;
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		if (!this.world.isClient) {
			if (hitResult.entity != null) {
				if (this.owner != null) {
					if (hitResult.entity.damage(DamageSource.mob(this.owner), 8.0F)) {
						if (hitResult.entity.isValid()) {
							this.method_5723(this.owner, hitResult.entity);
						} else {
							this.owner.heal(5.0F);
						}
					}
				} else {
					hitResult.entity.damage(DamageSource.MAGIC, 5.0F);
				}

				if (hitResult.entity instanceof LivingEntity) {
					int i = 0;
					if (this.world.getDifficulty() == Difficulty.NORMAL) {
						i = 10;
					} else if (this.world.getDifficulty() == Difficulty.HARD) {
						i = 40;
					}

					if (i > 0) {
						((LivingEntity)hitResult.entity).addPotionEffect(new StatusEffectInstance(StatusEffects.field_5920, 20 * i, 1));
					}
				}
			}

			this.world.createExplosion(this, this.x, this.y, this.z, 1.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
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
	protected boolean method_7468() {
		return false;
	}
}
