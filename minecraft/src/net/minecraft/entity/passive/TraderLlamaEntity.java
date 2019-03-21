package net.minecraft.entity.passive;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class TraderLlamaEntity extends LlamaEntity {
	private int despawnDelay;

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
		return EntityType.field_17714.create(this.world);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("DespawnDelay", this.despawnDelay);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("DespawnDelay", 99)) {
			this.despawnDelay = compoundTag.getInt("DespawnDelay");
		}
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
		this.targetSelector.add(1, new TraderLlamaEntity.DefendTraderGoal(this));
	}

	public void setDespawnDelay(int i) {
		this.despawnDelay = i;
	}

	@Override
	protected void method_6726(PlayerEntity playerEntity) {
		Entity entity = this.getHoldingEntity();
		if (!(entity instanceof WanderingTraderEntity)) {
			super.method_6726(playerEntity);
		}
	}

	@Override
	public void tick() {
		super.tick();
		if (this.despawnDelay > 0 && --this.despawnDelay == 0 && this.getHoldingEntity() instanceof WanderingTraderEntity) {
			WanderingTraderEntity wanderingTraderEntity = (WanderingTraderEntity)this.getHoldingEntity();
			int i = wanderingTraderEntity.getDespawnDelay();
			if (i > 0) {
				this.despawnDelay = i;
			} else {
				this.invalidate();
			}
		}
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		EntityData entityData2 = super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (spawnType == SpawnType.field_16467) {
			this.setBreedingAge(0);
		}

		return entityData2;
	}

	public class DefendTraderGoal extends TrackTargetGoal {
		private final LlamaEntity field_17718;
		private LivingEntity offender;
		private int traderLastAttackedTime;

		public DefendTraderGoal(LlamaEntity llamaEntity) {
			super(llamaEntity, false);
			this.field_17718 = llamaEntity;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18408));
		}

		@Override
		public boolean canStart() {
			if (!this.field_17718.isLeashed()) {
				return false;
			} else {
				Entity entity = this.field_17718.getHoldingEntity();
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
			this.entity.setTarget(this.offender);
			Entity entity = this.field_17718.getHoldingEntity();
			if (entity instanceof WanderingTraderEntity) {
				this.traderLastAttackedTime = ((WanderingTraderEntity)entity).getLastAttackedTime();
			}

			super.start();
		}
	}
}
