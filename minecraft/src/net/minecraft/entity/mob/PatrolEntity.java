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
import net.minecraft.world.LightType;
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
			this.setEquippedStack(EquipmentSlot.field_6169, Raid.getOminousBanner());
			this.setEquipmentDropChance(EquipmentSlot.field_6169, 2.0F);
		}

		if (spawnType == SpawnType.field_16527) {
			this.patrolling = true;
		}

		return super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
	}

	public static boolean method_20739(EntityType<? extends PatrolEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return iWorld.getLightLevel(LightType.field_9282, blockPos) > 8 ? false : method_20681(entityType, iWorld, spawnType, blockPos, random);
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
		private final T actor;
		private final double leaderSpeed;
		private final double fellowSpeed;

		public PatrolGoal(T patrolEntity, double d, double e) {
			this.actor = patrolEntity;
			this.leaderSpeed = d;
			this.fellowSpeed = e;
			this.setControls(EnumSet.of(Goal.Control.field_18405));
		}

		@Override
		public boolean canStart() {
			return this.actor.isRaidCenterSet() && this.actor.getTarget() == null && !this.actor.hasPassengers() && this.actor.hasPatrolTarget();
		}

		@Override
		public void start() {
		}

		@Override
		public void stop() {
		}

		@Override
		public void tick() {
			boolean bl = this.actor.isPatrolLeader();
			EntityNavigation entityNavigation = this.actor.getNavigation();
			if (entityNavigation.isIdle()) {
				if (bl && this.actor.getPatrolTarget().isWithinDistance(this.actor.getPos(), 10.0)) {
					this.actor.setRandomPatrolTarget();
				} else {
					Vec3d vec3d = new Vec3d(this.actor.getPatrolTarget());
					Vec3d vec3d2 = new Vec3d(this.actor.x, this.actor.y, this.actor.z);
					Vec3d vec3d3 = vec3d2.subtract(vec3d);
					vec3d = vec3d3.rotateY(90.0F).multiply(0.4).add(vec3d);
					Vec3d vec3d4 = vec3d.subtract(vec3d2).normalize().multiply(10.0).add(vec3d2);
					BlockPos blockPos = new BlockPos(vec3d4);
					blockPos = this.actor.world.getTopPosition(Heightmap.Type.field_13203, blockPos);
					if (!entityNavigation.startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), bl ? this.fellowSpeed : this.leaderSpeed)) {
						this.wander();
					} else if (bl) {
						for (PatrolEntity patrolEntity : this.actor
							.world
							.getEntities(PatrolEntity.class, this.actor.getBoundingBox().expand(16.0), patrolEntityx -> !patrolEntityx.isPatrolLeader() && patrolEntityx.hasNoRaid())) {
							patrolEntity.setPatrolTarget(blockPos);
						}
					}
				}
			}
		}

		private void wander() {
			Random random = this.actor.getRand();
			BlockPos blockPos = this.actor
				.world
				.getTopPosition(Heightmap.Type.field_13203, new BlockPos(this.actor).add(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));
			this.actor.getNavigation().startMovingTo((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), this.leaderSpeed);
		}
	}
}
