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
	private static final TrackedData<Boolean> field_7654 = DataTracker.registerData(ExplodingWitherSkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public ExplodingWitherSkullEntity(EntityType<? extends ExplodingWitherSkullEntity> entityType, World world) {
		super(entityType, world);
	}

	public ExplodingWitherSkullEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.WITHER_SKULL, livingEntity, d, e, f, world);
	}

	@Environment(EnvType.CLIENT)
	public ExplodingWitherSkullEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.WITHER_SKULL, d, e, f, g, h, i, world);
	}

	@Override
	protected float method_7466() {
		return this.method_7503() ? 0.73F : super.method_7466();
	}

	@Override
	public boolean isOnFire() {
		return false;
	}

	@Override
	public float method_5774(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f) {
		return this.method_7503() && WitherEntity.method_6883(blockState) ? Math.min(0.8F, f) : f;
	}

	@Override
	protected void method_7469(HitResult hitResult) {
		if (!this.field_6002.isClient) {
			if (hitResult.getType() == HitResult.Type.ENTITY) {
				Entity entity = ((EntityHitResult)hitResult).getEntity();
				if (this.owner != null) {
					if (entity.damage(DamageSource.method_5511(this.owner), 8.0F)) {
						if (entity.isValid()) {
							this.method_5723(this.owner, entity);
						} else {
							this.owner.heal(5.0F);
						}
					}
				} else {
					entity.damage(DamageSource.MAGIC, 5.0F);
				}

				if (entity instanceof LivingEntity) {
					int i = 0;
					if (this.field_6002.getDifficulty() == Difficulty.NORMAL) {
						i = 10;
					} else if (this.field_6002.getDifficulty() == Difficulty.HARD) {
						i = 40;
					}

					if (i > 0) {
						((LivingEntity)entity).addPotionEffect(new StatusEffectInstance(StatusEffects.field_5920, 20 * i, 1));
					}
				}
			}

			Explosion.class_4179 lv = this.field_6002.getGameRules().getBoolean("mobGriefing") ? Explosion.class_4179.field_18687 : Explosion.class_4179.field_18685;
			this.field_6002.createExplosion(this, this.x, this.y, this.z, 1.0F, false, lv);
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
		this.field_6011.startTracking(field_7654, false);
	}

	public boolean method_7503() {
		return this.field_6011.get(field_7654);
	}

	public void setCharged(boolean bl) {
		this.field_6011.set(field_7654, bl);
	}

	@Override
	protected boolean method_7468() {
		return false;
	}
}
