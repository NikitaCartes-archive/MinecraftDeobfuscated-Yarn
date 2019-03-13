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
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TNTMinecartEntity extends AbstractMinecartEntity {
	private int fuseTicks = -1;

	public TNTMinecartEntity(EntityType<? extends TNTMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public TNTMinecartEntity(World world, double d, double e, double f) {
		super(EntityType.TNT_MINECART, world, d, e, f);
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
	public void update() {
		super.update();
		if (this.fuseTicks > 0) {
			this.fuseTicks--;
			this.field_6002.method_8406(ParticleTypes.field_11251, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0);
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
		Entity entity = damageSource.method_5526();
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
			if (!damageSource.isExplosive() && this.field_6002.getGameRules().getBoolean("doEntityDrops")) {
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

			this.field_6002.createExplosion(this, this.x, this.y, this.z, (float)(4.0 + this.random.nextDouble() * 1.5 * e), Explosion.class_4179.field_18686);
			this.invalidate();
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
	public void method_5711(byte b) {
		if (b == 10) {
			this.prime();
		} else {
			super.method_5711(b);
		}
	}

	public void prime() {
		this.fuseTicks = 80;
		if (!this.field_6002.isClient) {
			this.field_6002.summonParticle(this, (byte)10);
			if (!this.isSilent()) {
				this.field_6002.method_8465(null, this.x, this.y, this.z, SoundEvents.field_15079, SoundCategory.field_15245, 1.0F, 1.0F);
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
		return !this.isPrimed() || !blockState.method_11602(BlockTags.field_15463) && !blockView.method_8320(blockPos.up()).method_11602(BlockTags.field_15463)
			? super.method_5774(explosion, blockView, blockPos, blockState, fluidState, f)
			: 0.0F;
	}

	@Override
	public boolean method_5853(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f) {
		return !this.isPrimed() || !blockState.method_11602(BlockTags.field_15463) && !blockView.method_8320(blockPos.up()).method_11602(BlockTags.field_15463)
			? super.method_5853(explosion, blockView, blockPos, blockState, f)
			: false;
	}

	@Override
	protected void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("TNTFuse", 99)) {
			this.fuseTicks = compoundTag.getInt("TNTFuse");
		}
	}

	@Override
	protected void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("TNTFuse", this.fuseTicks);
	}
}
