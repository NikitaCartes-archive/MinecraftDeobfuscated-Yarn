package net.minecraft.entity.passive;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class TraderLlamaEntity extends LlamaEntity {
	private int despawnDelay = 47999;

	public TraderLlamaEntity(EntityType<? extends TraderLlamaEntity> entityType, World world) {
		super(entityType, world);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isTrader() {
		return true;
	}

	@Override
	protected LlamaEntity createChild() {
		return EntityType.TRADER_LLAMA.create(this.world);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("DespawnDelay", this.despawnDelay);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("DespawnDelay", 99)) {
			this.despawnDelay = tag.getInt("DespawnDelay");
		}
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
		this.targetSelector.add(1, new TraderLlamaEntity.DefendTraderGoal(this));
	}

	@Override
	protected void putPlayerOnBack(PlayerEntity player) {
		Entity entity = this.getHoldingEntity();
		if (!(entity instanceof WanderingTraderEntity)) {
			super.putPlayerOnBack(player);
		}
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!this.world.isClient) {
			this.tryDespawn();
		}
	}

	private void tryDespawn() {
		if (this.canDespawn()) {
			this.despawnDelay = this.heldByTrader() ? ((WanderingTraderEntity)this.getHoldingEntity()).getDespawnDelay() - 1 : this.despawnDelay - 1;
			if (this.despawnDelay <= 0) {
				this.detachLeash(true, false);
				this.remove();
			}
		}
	}

	private boolean canDespawn() {
		return !this.isTame() && !this.leashedByPlayer() && !this.hasPlayerRider();
	}

	private boolean heldByTrader() {
		return this.getHoldingEntity() instanceof WanderingTraderEntity;
	}

	private boolean leashedByPlayer() {
		return this.isLeashed() && !this.heldByTrader();
	}

	@Nullable
	@Override
	public EntityData initialize(
		WorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		if (spawnReason == SpawnReason.EVENT) {
			this.setBreedingAge(0);
		}

		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData();
			((PassiveEntity.PassiveData)entityData).setBabyAllowed(false);
		}

		return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
	}

	public class DefendTraderGoal extends TrackTargetGoal {
		private final LlamaEntity llama;
		private LivingEntity offender;
		private int traderLastAttackedTime;

		public DefendTraderGoal(LlamaEntity llama) {
			super(llama, false);
			this.llama = llama;
			this.setControls(EnumSet.of(Goal.Control.TARGET));
		}

		@Override
		public boolean canStart() {
			if (!this.llama.isLeashed()) {
				return false;
			} else {
				Entity entity = this.llama.getHoldingEntity();
				if (!(entity instanceof WanderingTraderEntity)) {
					return false;
				} else {
					WanderingTraderEntity wanderingTraderEntity = (WanderingTraderEntity)entity;
					this.offender = wanderingTraderEntity.getAttacker();
					int i = wanderingTraderEntity.getLastAttackedTime();
					return i != this.traderLastAttackedTime && this.canTrack(this.offender, TargetPredicate.DEFAULT);
				}
			}
		}

		@Override
		public void start() {
			this.mob.setTarget(this.offender);
			Entity entity = this.llama.getHoldingEntity();
			if (entity instanceof WanderingTraderEntity) {
				this.traderLastAttackedTime = ((WanderingTraderEntity)entity).getLastAttackedTime();
			}

			super.start();
		}
	}
}
