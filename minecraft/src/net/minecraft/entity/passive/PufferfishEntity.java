package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.network.packet.GameStateChangeS2CPacket;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class PufferfishEntity extends FishEntity {
	private static final TrackedData<Integer> PUFF_STATE = DataTracker.registerData(PufferfishEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private int field_6833;
	private int field_6832;
	private static final Predicate<LivingEntity> field_6834 = livingEntity -> {
		if (livingEntity == null) {
			return false;
		} else {
			return !(livingEntity instanceof PlayerEntity) || !livingEntity.isSpectator() && !((PlayerEntity)livingEntity).isCreative()
				? livingEntity.getGroup() != EntityGroup.AQUATIC
				: false;
		}
	};

	public PufferfishEntity(EntityType<? extends PufferfishEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(PUFF_STATE, 0);
	}

	public int getPuffState() {
		return this.dataTracker.get(PUFF_STATE);
	}

	public void setPuffState(int i) {
		this.dataTracker.set(PUFF_STATE, i);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (PUFF_STATE.equals(trackedData)) {
			this.calculateDimensions();
		}

		super.onTrackedDataSet(trackedData);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("PuffState", this.getPuffState());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setPuffState(compoundTag.getInt("PuffState"));
	}

	@Override
	protected ItemStack getFishBucketItem() {
		return new ItemStack(Items.field_8108);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(1, new PufferfishEntity.class_1455(this));
	}

	@Override
	public void tick() {
		if (!this.world.isClient && this.isAlive() && this.canMoveVoluntarily()) {
			if (this.field_6833 > 0) {
				if (this.getPuffState() == 0) {
					this.playSound(SoundEvents.field_15235, this.getSoundVolume(), this.getSoundPitch());
					this.setPuffState(1);
				} else if (this.field_6833 > 40 && this.getPuffState() == 1) {
					this.playSound(SoundEvents.field_15235, this.getSoundVolume(), this.getSoundPitch());
					this.setPuffState(2);
				}

				this.field_6833++;
			} else if (this.getPuffState() != 0) {
				if (this.field_6832 > 60 && this.getPuffState() == 2) {
					this.playSound(SoundEvents.field_15133, this.getSoundVolume(), this.getSoundPitch());
					this.setPuffState(1);
				} else if (this.field_6832 > 100 && this.getPuffState() == 1) {
					this.playSound(SoundEvents.field_15133, this.getSoundVolume(), this.getSoundPitch());
					this.setPuffState(0);
				}

				this.field_6832++;
			}
		}

		super.tick();
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.isAlive() && this.getPuffState() > 0) {
			for (MobEntity mobEntity : this.world.getEntities(MobEntity.class, this.getBoundingBox().expand(0.3), field_6834)) {
				if (mobEntity.isAlive()) {
					this.sting(mobEntity);
				}
			}
		}
	}

	private void sting(MobEntity mobEntity) {
		int i = this.getPuffState();
		if (mobEntity.damage(DamageSource.mob(this), (float)(1 + i))) {
			mobEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5899, 60 * i, 0));
			this.playSound(SoundEvents.field_14848, 1.0F, 1.0F);
		}
	}

	@Override
	public void onPlayerCollision(PlayerEntity playerEntity) {
		int i = this.getPuffState();
		if (playerEntity instanceof ServerPlayerEntity && i > 0 && playerEntity.damage(DamageSource.mob(this), (float)(1 + i))) {
			((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(new GameStateChangeS2CPacket(9, 0.0F));
			playerEntity.addPotionEffect(new StatusEffectInstance(StatusEffects.field_5899, 60 * i, 0));
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14553;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14888;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14748;
	}

	@Override
	protected SoundEvent getFlopSound() {
		return SoundEvents.field_15004;
	}

	@Override
	public EntityDimensions getDimensions(EntityPose entityPose) {
		return super.getDimensions(entityPose).scaled(getScaleForPuffState(this.getPuffState()));
	}

	private static float getScaleForPuffState(int i) {
		switch (i) {
			case 0:
				return 0.5F;
			case 1:
				return 0.7F;
			default:
				return 1.0F;
		}
	}

	static class class_1455 extends Goal {
		private final PufferfishEntity pufferfish;

		public class_1455(PufferfishEntity pufferfishEntity) {
			this.pufferfish = pufferfishEntity;
		}

		@Override
		public boolean canStart() {
			List<LivingEntity> list = this.pufferfish.world.getEntities(LivingEntity.class, this.pufferfish.getBoundingBox().expand(2.0), PufferfishEntity.field_6834);
			return !list.isEmpty();
		}

		@Override
		public void start() {
			this.pufferfish.field_6833 = 1;
			this.pufferfish.field_6832 = 0;
		}

		@Override
		public void stop() {
			this.pufferfish.field_6833 = 0;
		}

		@Override
		public boolean shouldContinue() {
			List<LivingEntity> list = this.pufferfish.world.getEntities(LivingEntity.class, this.pufferfish.getBoundingBox().expand(2.0), PufferfishEntity.field_6834);
			return !list.isEmpty();
		}
	}
}
