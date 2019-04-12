package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.raid.Raid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class PatrolEntity extends HostileEntity {
	private BlockPos patrolTarget;
	private boolean patrolLeader;
	private boolean patrolling;

	protected PatrolEntity(EntityType<? extends PatrolEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(4, new PatrolEntity.PatrolGoal<>(this, 0.7, 0.595));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		if (this.patrolTarget != null) {
			compoundTag.put("PatrolTarget", TagHelper.serializeBlockPos(this.patrolTarget));
		}

		compoundTag.putBoolean("PatrolLeader", this.patrolLeader);
		compoundTag.putBoolean("Patrolling", this.patrolling);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("PatrolTarget")) {
			this.patrolTarget = TagHelper.deserializeBlockPos(compoundTag.getCompound("PatrolTarget"));
		}

		this.patrolLeader = compoundTag.getBoolean("PatrolLeader");
		this.patrolling = compoundTag.getBoolean("Patrolling");
	}

	@Override
	public double getHeightOffset() {
		return -0.45;
	}

	public boolean canLead() {
		return true;
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		if (spawnType != SpawnType.field_16527
			&& spawnType != SpawnType.field_16467
			&& spawnType != SpawnType.field_16474
			&& this.random.nextFloat() < 0.06F
			&& this.canLead()) {
			this.patrolLeader = true;
		}

		if (this.isPatrolLeader()) {
			this.setEquippedStack(EquipmentSlot.HEAD, Raid.OMINOUS_BANNER);
			this.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0F);
		}

		if (spawnType == SpawnType.field_16527) {
			this.patrolling = true;
		}

		return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	@Override
	public boolean canImmediatelyDespawn(double d) {
		return !this.patrolling || d > 16384.0;
	}

	public void setPatrolTarget(BlockPos blockPos) {
		this.patrolTarget = blockPos;
		this.patrolling = true;
	}

	public BlockPos getPatrolTarget() {
		return this.patrolTarget;
	}

	public boolean hasPatrolTarget() {
		return this.patrolTarget != null;
	}

	public void setPatrolLeader(boolean bl) {
		this.patrolLeader = bl;
		this.patrolling = true;
	}

	public boolean isPatrolLeader() {
		return this.patrolLeader;
	}

	public boolean hasNoRaid() {
		return true;
	}

	public void setRandomPatrolTarget() {
		this.patrolTarget = new BlockPos(this).add(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
		this.patrolling = true;
	}

	protected boolean isRaidCenterSet() {
		return this.patrolling;
	}

	public static class PatrolGoal<T extends PatrolEntity> extends Goal {
		private final T owner;
		private final double leaderSpeed;
		private final double fellowSpeed;

		public PatrolGoal(T patrolEntity, double d, double e) {
			this.owner = patrolEntity;
			this.leaderSpeed = d;
			this.fellowSpeed = e;
			this.setControls(EnumSet.of(Goal.Control.field_18405));
		}

		@Override
		public boolean canStart() {
			return this.owner.isRaidCenterSet() && this.owner.getTarget() == null && !this.owner.hasPassengers() && this.owner.hasPatrolTarget();
		}

		@Override
		public void start() {
		}

		@Override
		public void stop() {
		}

		@Override
		public void tick() {
			boolean bl = this.owner.isPatrolLeader();
			EntityNavigation entityNavigation = this.owner.getNavigation();
			if (entityNavigation.isIdle()) {
				if (bl && this.owner.getPatrolTarget().isWithinDistance(this.owner.getPos(), 10.0)) {
					this.owner.setRandomPatrolTarget();
				} else {
					Vec3d vec3d = new Vec3d(this.owner.getPatrolTarget());
					Vec3d vec3d2 = new Vec3d(this.owner.x, this.owner.y, this.owner.z);
					Vec3d vec3d3 = vec3d2.subtract(vec3d);
					vec3d = vec3d3.rotateY(90.0F).multiply(0.4).add(vec3d);
					Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
					BlockPos blockPos = new BlockPos((int)vec3d4.x, (int)vec3d4.y, (int)vec3d4.z);
					blockPos = this.owner.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
					if (!entityNavigation.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), bl ? this.fellowSpeed : this.leaderSpeed)) {
						this.wander();
					} else if (bl) {
						for (PatrolEntity patrolEntity : this.owner
							.world
							.getEntities(PatrolEntity.class, this.owner.getBoundingBox().expand(16.0), patrolEntityx -> !patrolEntityx.isPatrolLeader() && patrolEntityx.hasNoRaid())) {
							patrolEntity.setPatrolTarget(blockPos);
						}
					}
				}
			}
		}

		private void wander() {
			Random random = this.owner.getRand();
			BlockPos blockPos = this.owner
				.world
				.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(this.owner).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
			this.owner.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), this.leaderSpeed);
		}
	}
}
