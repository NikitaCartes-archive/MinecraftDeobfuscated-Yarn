package net.minecraft.entity.passive;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GlowSquidEntity extends SquidEntity {
	private static final TrackedData<Integer> DARK_TICKS_REMAINING = DataTracker.registerData(GlowSquidEntity.class, TrackedDataHandlerRegistry.INTEGER);

	public GlowSquidEntity(EntityType<? extends GlowSquidEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected ParticleEffect getInkParticle() {
		return ParticleTypes.GLOW_SQUID_INK;
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(DARK_TICKS_REMAINING, 0);
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return EntityType.GLOW_SQUID.create(world, SpawnReason.BREEDING);
	}

	@Override
	protected SoundEvent getSquirtSound() {
		return SoundEvents.ENTITY_GLOW_SQUID_SQUIRT;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_GLOW_SQUID_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_GLOW_SQUID_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_GLOW_SQUID_DEATH;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("DarkTicksRemaining", this.getDarkTicksRemaining());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setDarkTicksRemaining(nbt.getInt("DarkTicksRemaining"));
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		int i = this.getDarkTicksRemaining();
		if (i > 0) {
			this.setDarkTicksRemaining(i - 1);
		}

		this.getWorld().addParticle(ParticleTypes.GLOW, this.getParticleX(0.6), this.getRandomBodyY(), this.getParticleZ(0.6), 0.0, 0.0, 0.0);
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		boolean bl = super.damage(world, source, amount);
		if (bl) {
			this.setDarkTicksRemaining(100);
		}

		return bl;
	}

	private void setDarkTicksRemaining(int ticks) {
		this.dataTracker.set(DARK_TICKS_REMAINING, ticks);
	}

	public int getDarkTicksRemaining() {
		return this.dataTracker.get(DARK_TICKS_REMAINING);
	}

	public static boolean canSpawn(EntityType<? extends LivingEntity> type, ServerWorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
		return pos.getY() <= world.getSeaLevel() - 33 && world.getBaseLightLevel(pos, 0) == 0 && world.getBlockState(pos).isOf(Blocks.WATER);
	}
}
