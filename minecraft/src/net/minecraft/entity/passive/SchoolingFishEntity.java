package net.minecraft.entity.passive;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowGroupLeaderGoal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class SchoolingFishEntity extends FishEntity {
	private SchoolingFishEntity leader;
	private int groupSize = 1;

	public SchoolingFishEntity(EntityType<? extends SchoolingFishEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(5, new FollowGroupLeaderGoal(this));
	}

	@Override
	public int getLimitPerChunk() {
		return this.getMaxGroupSize();
	}

	public int getMaxGroupSize() {
		return super.getLimitPerChunk();
	}

	@Override
	protected boolean hasSelfControl() {
		return !this.hasLeader();
	}

	public boolean hasLeader() {
		return this.leader != null && this.leader.isAlive();
	}

	public SchoolingFishEntity joinGroupOf(SchoolingFishEntity schoolingFishEntity) {
		this.leader = schoolingFishEntity;
		schoolingFishEntity.increaseGroupSize();
		return schoolingFishEntity;
	}

	public void leaveGroup() {
		this.leader.decreaseGroupSize();
		this.leader = null;
	}

	private void increaseGroupSize() {
		this.groupSize++;
	}

	private void decreaseGroupSize() {
		this.groupSize--;
	}

	public boolean canHaveMoreFishInGroup() {
		return this.hasOtherFishInGroup() && this.groupSize < this.getMaxGroupSize();
	}

	@Override
	public void tick() {
		super.tick();
		if (this.hasOtherFishInGroup() && this.world.random.nextInt(200) == 1) {
			List<FishEntity> list = this.world.getNonSpectatingEntities(this.getClass(), this.getBoundingBox().expand(8.0, 8.0, 8.0));
			if (list.size() <= 1) {
				this.groupSize = 1;
			}
		}
	}

	public boolean hasOtherFishInGroup() {
		return this.groupSize > 1;
	}

	public boolean isCloseEnoughToLeader() {
		return this.squaredDistanceTo(this.leader) <= 121.0;
	}

	public void moveTowardLeader() {
		if (this.hasLeader()) {
			this.getNavigation().startMovingTo(this.leader, 1.0);
		}
	}

	public void pullInOtherFish(Stream<SchoolingFishEntity> stream) {
		stream.limit((long)(this.getMaxGroupSize() - this.groupSize))
			.filter(schoolingFishEntity -> schoolingFishEntity != this)
			.forEach(schoolingFishEntity -> schoolingFishEntity.joinGroupOf(this));
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (entityData == null) {
			entityData = new SchoolingFishEntity.Data(this);
		} else {
			this.joinGroupOf(((SchoolingFishEntity.Data)entityData).leader);
		}

		return entityData;
	}

	public static class Data implements EntityData {
		public final SchoolingFishEntity leader;

		public Data(SchoolingFishEntity schoolingFishEntity) {
			this.leader = schoolingFishEntity;
		}
	}
}
