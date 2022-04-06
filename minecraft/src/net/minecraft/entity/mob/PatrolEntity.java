package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public abstract class PatrolEntity extends HostileEntity {
	@Nullable
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
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.patrolTarget != null) {
			nbt.put("PatrolTarget", NbtHelper.fromBlockPos(this.patrolTarget));
		}

		nbt.putBoolean("PatrolLeader", this.patrolLeader);
		nbt.putBoolean("Patrolling", this.patrolling);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("PatrolTarget")) {
			this.patrolTarget = NbtHelper.toBlockPos(nbt.getCompound("PatrolTarget"));
		}

		this.patrolLeader = nbt.getBoolean("PatrolLeader");
		this.patrolling = nbt.getBoolean("Patrolling");
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
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		if (spawnReason != SpawnReason.PATROL
			&& spawnReason != SpawnReason.EVENT
			&& spawnReason != SpawnReason.STRUCTURE
			&& this.random.nextFloat() < 0.06F
			&& this.canLead()) {
			this.patrolLeader = true;
		}

		if (this.isPatrolLeader()) {
			this.equipStack(EquipmentSlot.HEAD, Raid.getOminousBanner());
			this.setEquipmentDropChance(EquipmentSlot.HEAD, 2.0F);
		}

		if (spawnReason == SpawnReason.PATROL) {
			this.patrolling = true;
		}

		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	public static boolean canSpawn(EntityType<? extends PatrolEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, AbstractRandom random) {
		return world.getLightLevel(LightType.BLOCK, pos) > 8 ? false : canSpawnIgnoreLightLevel(type, world, spawnReason, pos, random);
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return !this.patrolling || distanceSquared > 16384.0;
	}

	public void setPatrolTarget(BlockPos targetPos) {
		this.patrolTarget = targetPos;
		this.patrolling = true;
	}

	/**
	 * Returns the position this patrol entity is walking to.
	 */
	public BlockPos getPatrolTarget() {
		return this.patrolTarget;
	}

	public boolean hasPatrolTarget() {
		return this.patrolTarget != null;
	}

	public void setPatrolLeader(boolean patrolLeader) {
		this.patrolLeader = patrolLeader;
		this.patrolling = true;
	}

	public boolean isPatrolLeader() {
		return this.patrolLeader;
	}

	public boolean hasNoRaid() {
		return true;
	}

	public void setRandomPatrolTarget() {
		this.patrolTarget = this.getBlockPos().add(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
		this.patrolling = true;
	}

	protected boolean isRaidCenterSet() {
		return this.patrolling;
	}

	protected void setPatrolling(boolean patrolling) {
		this.patrolling = patrolling;
	}

	public static class PatrolGoal<T extends PatrolEntity> extends Goal {
		private static final int field_30474 = 200;
		private final T entity;
		private final double leaderSpeed;
		private final double followSpeed;
		private long nextPatrolSearchTime;

		public PatrolGoal(T entity, double leaderSpeed, double followSpeed) {
			this.entity = entity;
			this.leaderSpeed = leaderSpeed;
			this.followSpeed = followSpeed;
			this.nextPatrolSearchTime = -1L;
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			boolean bl = this.entity.world.getTime() < this.nextPatrolSearchTime;
			return this.entity.isRaidCenterSet() && this.entity.getTarget() == null && !this.entity.hasPassengers() && this.entity.hasPatrolTarget() && !bl;
		}

		@Override
		public void start() {
		}

		@Override
		public void stop() {
		}

		@Override
		public void tick() {
			boolean bl = this.entity.isPatrolLeader();
			EntityNavigation entityNavigation = this.entity.getNavigation();
			if (entityNavigation.isIdle()) {
				List<PatrolEntity> list = this.findPatrolTargets();
				if (this.entity.isRaidCenterSet() && list.isEmpty()) {
					this.entity.setPatrolling(false);
				} else if (bl && this.entity.getPatrolTarget().isWithinDistance(this.entity.getPos(), 10.0)) {
					this.entity.setRandomPatrolTarget();
				} else {
					Vec3d vec3d = Vec3d.ofBottomCenter(this.entity.getPatrolTarget());
					Vec3d vec3d2 = this.entity.getPos();
					Vec3d vec3d3 = vec3d2.subtract(vec3d);
					vec3d = vec3d3.rotateY(90.0F).multiply(0.4).add(vec3d);
					Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
					BlockPos blockPos = new BlockPos(vec3d4);
					blockPos = this.entity.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, blockPos);
					if (!entityNavigation.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), bl ? this.followSpeed : this.leaderSpeed)) {
						this.wander();
						this.nextPatrolSearchTime = this.entity.world.getTime() + 200L;
					} else if (bl) {
						for (PatrolEntity patrolEntity : list) {
							patrolEntity.setPatrolTarget(blockPos);
						}
					}
				}
			}
		}

		private List<PatrolEntity> findPatrolTargets() {
			return this.entity
				.world
				.getEntitiesByClass(
					PatrolEntity.class, this.entity.getBoundingBox().expand(16.0), patrolEntity -> patrolEntity.hasNoRaid() && !patrolEntity.isPartOf(this.entity)
				);
		}

		private boolean wander() {
			AbstractRandom abstractRandom = this.entity.getRandom();
			BlockPos blockPos = this.entity
				.world
				.getTopPosition(
					Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, this.entity.getBlockPos().add(-8 + abstractRandom.nextInt(16), 0, -8 + abstractRandom.nextInt(16))
				);
			return this.entity.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), this.leaderSpeed);
		}
	}
}
