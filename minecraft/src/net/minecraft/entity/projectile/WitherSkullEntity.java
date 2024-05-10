package net.minecraft.entity.projectile;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class WitherSkullEntity extends ExplosiveProjectileEntity {
	private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(WitherSkullEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

	public WitherSkullEntity(EntityType<? extends WitherSkullEntity> entityType, World world) {
		super(entityType, world);
	}

	public WitherSkullEntity(World world, LivingEntity owner, Vec3d velocity) {
		super(EntityType.WITHER_SKULL, owner, velocity, world);
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
	public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
		return this.isCharged() && WitherEntity.canDestroy(blockState) ? Math.min(0.8F, max) : max;
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			Entity var8 = entityHitResult.getEntity();
			boolean bl;
			if (this.getOwner() instanceof LivingEntity livingEntity) {
				DamageSource damageSource = this.getDamageSources().witherSkull(this, livingEntity);
				bl = var8.damage(damageSource, 8.0F);
				if (bl) {
					if (var8.isAlive()) {
						EnchantmentHelper.onTargetDamaged(serverWorld, var8, damageSource);
					} else {
						livingEntity.heal(5.0F);
					}
				}
			} else {
				bl = var8.damage(this.getDamageSources().magic(), 5.0F);
			}

			if (bl && var8 instanceof LivingEntity livingEntityx) {
				int i = 0;
				if (this.getWorld().getDifficulty() == Difficulty.NORMAL) {
					i = 10;
				} else if (this.getWorld().getDifficulty() == Difficulty.HARD) {
					i = 40;
				}

				if (i > 0) {
					livingEntityx.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 20 * i, 1), this.getEffectCause());
				}
			}
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getWorld().isClient) {
			this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 1.0F, false, World.ExplosionSourceType.MOB);
			this.discard();
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return false;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		builder.add(CHARGED, false);
	}

	public boolean isCharged() {
		return this.dataTracker.get(CHARGED);
	}

	public void setCharged(boolean charged) {
		this.dataTracker.set(CHARGED, charged);
	}

	@Override
	protected boolean isBurning() {
		return false;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("dangerous", this.isCharged());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setCharged(nbt.getBoolean("dangerous"));
	}
}
