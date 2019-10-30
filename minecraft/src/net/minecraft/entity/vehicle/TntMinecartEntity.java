package net.minecraft.entity.vehicle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntMinecartEntity extends AbstractMinecartEntity {
	private int fuseTicks = -1;

	public TntMinecartEntity(EntityType<? extends TntMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public TntMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.TNT_MINECART, world, d, e, f);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.TNT;
	}

	@Override
	public BlockState getDefaultContainedBlock() {
		return Blocks.TNT.getDefaultState();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.fuseTicks > 0) {
			this.fuseTicks--;
			this.world.addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
		} else if (this.fuseTicks == 0) {
			this.explode(squaredHorizontalLength(this.getVelocity()));
		}

		if (this.horizontalCollision) {
			double d = squaredHorizontalLength(this.getVelocity());
			if (d >= 0.01F) {
				this.explode(d);
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		Entity entity = source.getSource();
		if (entity instanceof ProjectileEntity) {
			ProjectileEntity projectileEntity = (ProjectileEntity)entity;
			if (projectileEntity.isOnFire()) {
				this.explode(projectileEntity.getVelocity().lengthSquared());
			}
		}

		return super.damage(source, amount);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		double d = squaredHorizontalLength(this.getVelocity());
		if (!damageSource.isFire() && !damageSource.isExplosive() && !(d >= 0.01F)) {
			super.dropItems(damageSource);
			if (!damageSource.isExplosive() && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
				this.dropItem(Blocks.TNT);
			}
		} else {
			if (this.fuseTicks < 0) {
				this.prime();
				this.fuseTicks = this.random.nextInt(20) + this.random.nextInt(20);
			}
		}
	}

	protected void explode(double d) {
		if (!this.world.isClient) {
			double e = Math.sqrt(d);
			if (e > 5.0) {
				e = 5.0;
			}

			this.world.createExplosion(this, this.getX(), this.getY(), this.getZ(), (float)(4.0 + this.random.nextDouble() * 1.5 * e), Explosion.DestructionType.BREAK);
			this.remove();
		}
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
		if (fallDistance >= 3.0F) {
			float f = fallDistance / 10.0F;
			this.explode((double)(f * f));
		}

		return super.handleFallDamage(fallDistance, damageMultiplier);
	}

	@Override
	public void onActivatorRail(int x, int y, int z, boolean powered) {
		if (powered && this.fuseTicks < 0) {
			this.prime();
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte status) {
		if (status == 10) {
			this.prime();
		} else {
			super.handleStatus(status);
		}
	}

	public void prime() {
		this.fuseTicks = 80;
		if (!this.world.isClient) {
			this.world.sendEntityStatus(this, (byte)10);
			if (!this.isSilent()) {
				this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public int getFuseTicks() {
		return this.fuseTicks;
	}

	public boolean isPrimed() {
		return this.fuseTicks > -1;
	}

	@Override
	public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
		return !this.isPrimed() || !blockState.matches(BlockTags.RAILS) && !world.getBlockState(pos.up()).matches(BlockTags.RAILS)
			? super.getEffectiveExplosionResistance(explosion, world, pos, blockState, fluidState, max)
			: 0.0F;
	}

	@Override
	public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower) {
		return !this.isPrimed() || !state.matches(BlockTags.RAILS) && !world.getBlockState(pos.up()).matches(BlockTags.RAILS)
			? super.canExplosionDestroyBlock(explosion, world, pos, state, explosionPower)
			: false;
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("TNTFuse", 99)) {
			this.fuseTicks = tag.getInt("TNTFuse");
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("TNTFuse", this.fuseTicks);
	}
}
