package net.minecraft;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.LlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class class_3986 extends LlamaEntity {
	private int field_17716;

	public class_3986(World world) {
		super(EntityType.field_17714, world);
		this.teleporting = true;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_6807() {
		return true;
	}

	@Override
	protected LlamaEntity method_18004() {
		return new class_3986(this.world);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("DespawnDelay", this.field_17716);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("DespawnDelay", 99)) {
			this.field_17716 = compoundTag.getInt("DespawnDelay");
		}
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(1, new EscapeDangerGoal(this, 2.0));
		this.targetSelector.add(1, new class_3986.class_3987(this));
		this.targetSelector.add(2, new FollowTargetGoal(this, ZombieEntity.class, true));
		this.targetSelector.add(2, new FollowTargetGoal(this, IllagerEntity.class, true));
	}

	public void method_18005(int i) {
		this.field_17716 = i;
	}

	@Override
	public void update() {
		super.update();
		if (this.field_17716 > 0 && --this.field_17716 == 0 && this.isLeashed() && this.getHoldingEntity() instanceof WanderingTraderEntity) {
			WanderingTraderEntity wanderingTraderEntity = (WanderingTraderEntity)this.getHoldingEntity();
			int i = wanderingTraderEntity.method_18014();
			if (i > 0) {
				this.field_17716 = i;
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

	public class class_3987 extends TrackTargetGoal {
		private final LlamaEntity field_17718;
		private LivingEntity field_17719;
		private int field_17720;

		public class_3987(LlamaEntity llamaEntity) {
			super(llamaEntity, false);
			this.field_17718 = llamaEntity;
			this.setControlBits(1);
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
					this.field_17719 = wanderingTraderEntity.getAttacker();
					int i = wanderingTraderEntity.getLastAttackedTime();
					return i != this.field_17720 && this.canTrack(this.field_17719, false);
				}
			}
		}

		@Override
		public void start() {
			this.entity.setTarget(this.field_17719);
			Entity entity = this.field_17718.getHoldingEntity();
			if (class_3986.this.isLeashed() && entity instanceof WanderingTraderEntity) {
				this.field_17720 = ((WanderingTraderEntity)entity).getLastAttackedTime();
			}

			super.start();
		}
	}
}
