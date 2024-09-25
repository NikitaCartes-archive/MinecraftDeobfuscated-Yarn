package net.minecraft.entity.passive;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class TraderLlamaEntity extends LlamaEntity {
	private int despawnDelay = 47999;

	public TraderLlamaEntity(EntityType<? extends TraderLlamaEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public boolean isTrader() {
		return true;
	}

	@Nullable
	@Override
	protected LlamaEntity createChild() {
		return EntityType.TRADER_LLAMA.create(this.getWorld(), SpawnReason.BREEDING);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("DespawnDelay", this.despawnDelay);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("DespawnDelay", NbtElement.NUMBER_TYPE)) {
			this.despawnDelay = nbt.getInt("DespawnDelay");
		}
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
		this.targetSelector.add(1, new TraderLlamaEntity.DefendTraderGoal(this));
		this.targetSelector.add(2, new ActiveTargetGoal(this, ZombieEntity.class, true, (entity, serverWorld) -> entity.getType() != EntityType.ZOMBIFIED_PIGLIN));
		this.targetSelector.add(2, new ActiveTargetGoal(this, IllagerEntity.class, true));
	}

	public void setDespawnDelay(int despawnDelay) {
		this.despawnDelay = despawnDelay;
	}

	@Override
	protected void putPlayerOnBack(PlayerEntity player) {
		Entity entity = this.getLeashHolder();
		if (!(entity instanceof WanderingTraderEntity)) {
			super.putPlayerOnBack(player);
		}
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (!this.getWorld().isClient) {
			this.tryDespawn();
		}
	}

	private void tryDespawn() {
		if (this.canDespawn()) {
			this.despawnDelay = this.heldByTrader() ? ((WanderingTraderEntity)this.getLeashHolder()).getDespawnDelay() - 1 : this.despawnDelay - 1;
			if (this.despawnDelay <= 0) {
				this.detachLeash(true, false);
				this.discard();
			}
		}
	}

	private boolean canDespawn() {
		return !this.isTame() && !this.leashedByPlayer() && !this.hasPlayerRider();
	}

	private boolean heldByTrader() {
		return this.getLeashHolder() instanceof WanderingTraderEntity;
	}

	private boolean leashedByPlayer() {
		return this.isLeashed() && !this.heldByTrader();
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		if (spawnReason == SpawnReason.EVENT) {
			this.setBreedingAge(0);
		}

		if (entityData == null) {
			entityData = new PassiveEntity.PassiveData(false);
		}

		return super.initialize(world, difficulty, spawnReason, entityData);
	}

	protected static class DefendTraderGoal extends TrackTargetGoal {
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
			} else if (!(this.llama.getLeashHolder() instanceof WanderingTraderEntity wanderingTraderEntity)) {
				return false;
			} else {
				this.offender = wanderingTraderEntity.getAttacker();
				int i = wanderingTraderEntity.getLastAttackedTime();
				return i != this.traderLastAttackedTime && this.canTrack(this.offender, TargetPredicate.DEFAULT);
			}
		}

		@Override
		public void start() {
			this.mob.setTarget(this.offender);
			Entity entity = this.llama.getLeashHolder();
			if (entity instanceof WanderingTraderEntity) {
				this.traderLastAttackedTime = ((WanderingTraderEntity)entity).getLastAttackedTime();
			}

			super.start();
		}
	}
}
