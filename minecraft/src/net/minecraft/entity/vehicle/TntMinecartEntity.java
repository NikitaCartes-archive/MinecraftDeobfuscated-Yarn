package net.minecraft.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

public class TntMinecartEntity extends AbstractMinecartEntity {
	private static final byte PRIME_TNT_STATUS = 10;
	private static final String EXPLOSION_POWER_NBT_KEY = "explosion_power";
	private static final float DEFAULT_EXPLOSION_POWER = 4.0F;
	private int fuseTicks = -1;
	private float explosionPower = 4.0F;

	public TntMinecartEntity(EntityType<? extends TntMinecartEntity> entityType, World world) {
		super(entityType, world);
	}

	public TntMinecartEntity(World world, double x, double y, double z) {
		super(EntityType.TNT_MINECART, world, x, y, z);
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
		double d = this.getVelocity().horizontalLengthSquared();
		super.tick();
		if (this.fuseTicks > 0) {
			this.fuseTicks--;
			this.getWorld().addParticle(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 0.0, 0.0, 0.0);
		} else if (this.fuseTicks == 0) {
			this.explode(this.getVelocity().horizontalLengthSquared());
		}

		if (this.horizontalCollision) {
			double e = this.getVelocity().horizontalLengthSquared();
			if (d >= 0.01F && e <= 0.01F) {
				this.explode(e);
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (source.getSource() instanceof PersistentProjectileEntity persistentProjectileEntity && persistentProjectileEntity.isOnFire()) {
			DamageSource damageSource = this.getDamageSources().explosion(this, source.getAttacker());
			this.explode(damageSource, persistentProjectileEntity.getVelocity().lengthSquared());
		}

		return super.damage(source, amount);
	}

	@Override
	public void killAndDropSelf(DamageSource source) {
		double d = this.getVelocity().horizontalLengthSquared();
		if (!shouldDetonate(source) && !(d >= 0.01F)) {
			this.killAndDropItem(this.asItem());
		} else {
			if (this.fuseTicks < 0) {
				this.prime();
				this.fuseTicks = this.random.nextInt(20) + this.random.nextInt(20);
			}
		}
	}

	@Override
	protected Item asItem() {
		return Items.TNT_MINECART;
	}

	protected void explode(double power) {
		this.explode(null, power);
	}

	protected void explode(@Nullable DamageSource damageSource, double power) {
		if (!this.getWorld().isClient) {
			double d = Math.sqrt(power);
			if (d > 5.0) {
				d = 5.0;
			}

			this.getWorld()
				.createExplosion(
					this, damageSource, null, this.getX(), this.getY(), this.getZ(), (float)(4.0 + this.random.nextDouble() * 1.5 * d), false, World.ExplosionSourceType.TNT
				);
			this.discard();
		}
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		if (fallDistance >= 3.0F) {
			float f = fallDistance / 10.0F;
			this.explode((double)(f * f));
		}

		return super.handleFallDamage(fallDistance, damageMultiplier, damageSource);
	}

	@Override
	public void onActivatorRail(int x, int y, int z, boolean powered) {
		if (powered && this.fuseTicks < 0) {
			this.prime();
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.SET_SHEEP_EAT_GRASS_TIMER_OR_PRIME_TNT_MINECART) {
			this.prime();
		} else {
			super.handleStatus(status);
		}
	}

	public void prime() {
		this.fuseTicks = 80;
		if (!this.getWorld().isClient) {
			this.getWorld().sendEntityStatus(this, EntityStatuses.SET_SHEEP_EAT_GRASS_TIMER_OR_PRIME_TNT_MINECART);
			if (!this.isSilent()) {
				this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
			}
		}
	}

	public int getFuseTicks() {
		return this.fuseTicks;
	}

	public boolean isPrimed() {
		return this.fuseTicks > -1;
	}

	@Override
	public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
		return !this.isPrimed() || !blockState.isIn(BlockTags.RAILS) && !world.getBlockState(pos.up()).isIn(BlockTags.RAILS)
			? super.getEffectiveExplosionResistance(explosion, world, pos, blockState, fluidState, max)
			: 0.0F;
	}

	@Override
	public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower) {
		return !this.isPrimed() || !state.isIn(BlockTags.RAILS) && !world.getBlockState(pos.up()).isIn(BlockTags.RAILS)
			? super.canExplosionDestroyBlock(explosion, world, pos, state, explosionPower)
			: false;
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("TNTFuse", NbtElement.NUMBER_TYPE)) {
			this.fuseTicks = nbt.getInt("TNTFuse");
		}

		if (nbt.contains("explosion_power", NbtElement.NUMBER_TYPE)) {
			this.explosionPower = MathHelper.clamp(nbt.getFloat("explosion_power"), 0.0F, 128.0F);
		}
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("TNTFuse", this.fuseTicks);
		if (this.explosionPower != 4.0F) {
			nbt.putFloat("explosion_power", this.explosionPower);
		}
	}

	@Override
	boolean shouldAlwaysKill(DamageSource source) {
		return shouldDetonate(source);
	}

	private static boolean shouldDetonate(DamageSource source) {
		return source.isIn(DamageTypeTags.IS_FIRE) || source.isIn(DamageTypeTags.IS_EXPLOSION);
	}
}
