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
		super(EntityType.field_6053, world, d, e, f);
	}

	@Override
	public AbstractMinecartEntity.Type getMinecartType() {
		return AbstractMinecartEntity.Type.field_7675;
	}

	@Override
	public BlockState method_7517() {
		return Blocks.field_10375.method_9564();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.fuseTicks > 0) {
			this.fuseTicks--;
			this.field_6002.addParticle(ParticleTypes.field_11251, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
		} else if (this.fuseTicks == 0) {
			this.explode(method_17996(this.method_18798()));
		}

		if (this.horizontalCollision) {
			double d = method_17996(this.method_18798());
			if (d >= 0.01F) {
				this.explode(d);
			}
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		Entity entity = damageSource.getSource();
		if (entity instanceof ProjectileEntity) {
			ProjectileEntity projectileEntity = (ProjectileEntity)entity;
			if (projectileEntity.isOnFire()) {
				this.explode(projectileEntity.method_18798().lengthSquared());
			}
		}

		return super.damage(damageSource, f);
	}

	@Override
	public void dropItems(DamageSource damageSource) {
		double d = method_17996(this.method_18798());
		if (!damageSource.isFire() && !damageSource.isExplosive() && !(d >= 0.01F)) {
			super.dropItems(damageSource);
			if (!damageSource.isExplosive() && this.field_6002.getGameRules().getBoolean(GameRules.field_19393)) {
				this.method_5706(Blocks.field_10375);
			}
		} else {
			if (this.fuseTicks < 0) {
				this.prime();
				this.fuseTicks = this.random.nextInt(20) + this.random.nextInt(20);
			}
		}
	}

	protected void explode(double d) {
		if (!this.field_6002.isClient) {
			double e = Math.sqrt(d);
			if (e > 5.0) {
				e = 5.0;
			}

			this.field_6002.createExplosion(this, this.x, this.y, this.z, (float)(4.0 + this.random.nextDouble() * 1.5 * e), Explosion.DestructionType.field_18686);
			this.remove();
		}
	}

	@Override
	public void handleFallDamage(float f, float g) {
		if (f >= 3.0F) {
			float h = f / 10.0F;
			this.explode((double)(h * h));
		}

		super.handleFallDamage(f, g);
	}

	@Override
	public void onActivatorRail(int i, int j, int k, boolean bl) {
		if (bl && this.fuseTicks < 0) {
			this.prime();
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void handleStatus(byte b) {
		if (b == 10) {
			this.prime();
		} else {
			super.handleStatus(b);
		}
	}

	public void prime() {
		this.fuseTicks = 80;
		if (!this.field_6002.isClient) {
			this.field_6002.sendEntityStatus(this, (byte)10);
			if (!this.isSilent()) {
				this.field_6002.playSound(null, this.x, this.y, this.z, SoundEvents.field_15079, SoundCategory.field_15245, 1.0F, 1.0F);
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
	public float method_5774(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f) {
		return !this.isPrimed() || !blockState.matches(BlockTags.field_15463) && !blockView.method_8320(blockPos.up()).matches(BlockTags.field_15463)
			? super.method_5774(explosion, blockView, blockPos, blockState, fluidState, f)
			: 0.0F;
	}

	@Override
	public boolean method_5853(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f) {
		return !this.isPrimed() || !blockState.matches(BlockTags.field_15463) && !blockView.method_8320(blockPos.up()).matches(BlockTags.field_15463)
			? super.method_5853(explosion, blockView, blockPos, blockState, f)
			: false;
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("TNTFuse", 99)) {
			this.fuseTicks = compoundTag.getInt("TNTFuse");
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("TNTFuse", this.fuseTicks);
	}
}
