package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class PufferfishEntity extends FishEntity {
	private static final TrackedData<Integer> PUFF_STATE = DataTracker.registerData(PufferfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
	int inflateTicks;
	int deflateTicks;
	private static final Predicate<LivingEntity> BLOW_UP_FILTER = entity -> {
		if (entity instanceof PlayerEntity playerEntity && playerEntity.isCreative()) {
			return false;
		}

		return !entity.getType().isIn(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH);
	};
	static final TargetPredicate BLOW_UP_TARGET_PREDICATE = TargetPredicate.createNonAttackable()
		.ignoreDistanceScalingFactor()
		.ignoreVisibility()
		.setPredicate(BLOW_UP_FILTER);
	public static final int NOT_PUFFED = 0;
	public static final int SEMI_PUFFED = 1;
	public static final int FULLY_PUFFED = 2;

	public PufferfishEntity(EntityType<? extends PufferfishEntity> entityType, World world) {
		super(entityType, world);
		this.calculateDimensions();
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(PUFF_STATE, 0);
	}

	public int getPuffState() {
		return this.dataTracker.get(PUFF_STATE);
	}

	public void setPuffState(int puffState) {
		this.dataTracker.set(PUFF_STATE, puffState);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (PUFF_STATE.equals(data)) {
			this.calculateDimensions();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("PuffState", this.getPuffState());
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setPuffState(Math.min(nbt.getInt("PuffState"), 2));
	}

	@Override
	public ItemStack getBucketItem() {
		return new ItemStack(Items.PUFFERFISH_BUCKET);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(1, new PufferfishEntity.InflateGoal(this));
	}

	@Override
	public void tick() {
		if (!this.getWorld().isClient && this.isAlive() && this.canMoveVoluntarily()) {
			if (this.inflateTicks > 0) {
				if (this.getPuffState() == 0) {
					this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP);
					this.setPuffState(SEMI_PUFFED);
				} else if (this.inflateTicks > 40 && this.getPuffState() == 1) {
					this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP);
					this.setPuffState(FULLY_PUFFED);
				}

				this.inflateTicks++;
			} else if (this.getPuffState() != 0) {
				if (this.deflateTicks > 60 && this.getPuffState() == 2) {
					this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT);
					this.setPuffState(SEMI_PUFFED);
				} else if (this.deflateTicks > 100 && this.getPuffState() == 1) {
					this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT);
					this.setPuffState(NOT_PUFFED);
				}

				this.deflateTicks++;
			}
		}

		super.tick();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.isAlive() && this.getPuffState() > 0) {
			for (MobEntity mobEntity : this.getWorld()
				.getEntitiesByClass(MobEntity.class, this.getBoundingBox().expand(0.3), entity -> BLOW_UP_TARGET_PREDICATE.test(this, entity))) {
				if (mobEntity.isAlive()) {
					this.sting(mobEntity);
				}
			}
		}
	}

	private void sting(MobEntity mob) {
		int i = this.getPuffState();
		if (mob.damage(this.getDamageSources().mobAttack(this), (float)(1 + i))) {
			mob.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * i, 0), this);
			this.playSound(SoundEvents.ENTITY_PUFFER_FISH_STING, 1.0F, 1.0F);
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		int i = this.getPuffState();
		if (player instanceof ServerPlayerEntity && i > 0 && player.damage(this.getDamageSources().mobAttack(this), (float)(1 + i))) {
			if (!this.isSilent()) {
				((ServerPlayerEntity)player)
					.networkHandler
					.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PUFFERFISH_STING, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
			}

			player.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 60 * i, 0), this);
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_PUFFER_FISH_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PUFFER_FISH_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_PUFFER_FISH_HURT;
	}

	@Override
	protected SoundEvent getFlopSound() {
		return SoundEvents.ENTITY_PUFFER_FISH_FLOP;
	}

	@Override
	public EntityDimensions getBaseDimensions(EntityPose pose) {
		return super.getBaseDimensions(pose).scaled(getScaleForPuffState(this.getPuffState()));
	}

	private static float getScaleForPuffState(int puffState) {
		switch (puffState) {
			case 0:
				return 0.5F;
			case 1:
				return 0.7F;
			default:
				return 1.0F;
		}
	}

	static class InflateGoal extends Goal {
		private final PufferfishEntity pufferfish;

		public InflateGoal(PufferfishEntity pufferfish) {
			this.pufferfish = pufferfish;
		}

		@Override
		public boolean canStart() {
			List<LivingEntity> list = this.pufferfish
				.getWorld()
				.getEntitiesByClass(
					LivingEntity.class,
					this.pufferfish.getBoundingBox().expand(2.0),
					livingEntity -> PufferfishEntity.BLOW_UP_TARGET_PREDICATE.test(this.pufferfish, livingEntity)
				);
			return !list.isEmpty();
		}

		@Override
		public void start() {
			this.pufferfish.inflateTicks = 1;
			this.pufferfish.deflateTicks = 0;
		}

		@Override
		public void stop() {
			this.pufferfish.inflateTicks = 0;
		}
	}
}
