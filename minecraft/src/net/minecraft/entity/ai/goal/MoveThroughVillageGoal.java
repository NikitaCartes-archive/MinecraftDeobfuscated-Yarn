package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

public class MoveThroughVillageGoal extends Goal {
	protected final MobEntityWithAi mob;
	private final double speed;
	private Path targetPath;
	private BlockPos target;
	private final boolean requiresNighttime;
	private final List<BlockPos> visitedTargets = Lists.<BlockPos>newArrayList();
	private final int distance;
	private final BooleanSupplier doorPassingThroughGetter;

	public MoveThroughVillageGoal(MobEntityWithAi mob, double speed, boolean requiresNighttime, int distance, BooleanSupplier doorPassingThroughGetter) {
		this.mob = mob;
		this.speed = speed;
		this.requiresNighttime = requiresNighttime;
		this.distance = distance;
		this.doorPassingThroughGetter = doorPassingThroughGetter;
		this.setControls(EnumSet.of(Goal.Control.MOVE));
		if (!(mob.getNavigation() instanceof MobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
		}
	}

	@Override
	public boolean canStart() {
		this.forgetOldTarget();
		if (this.requiresNighttime && this.mob.world.isDay()) {
			return false;
		} else {
			ServerWorld serverWorld = (ServerWorld)this.mob.world;
			BlockPos blockPos = new BlockPos(this.mob);
			if (!serverWorld.isNearOccupiedPointOfInterest(blockPos, 6)) {
				return false;
			} else {
				Vec3d vec3d = TargetFinder.findGroundTarget(
					this.mob,
					15,
					7,
					blockPos2x -> {
						if (!serverWorld.isNearOccupiedPointOfInterest(blockPos2x)) {
							return Double.NEGATIVE_INFINITY;
						} else {
							Optional<BlockPos> optionalx = serverWorld.getPointOfInterestStorage()
								.getPosition(PointOfInterestType.ALWAYS_TRUE, this::shouldVisit, blockPos2x, 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
							return !optionalx.isPresent() ? Double.NEGATIVE_INFINITY : -((BlockPos)optionalx.get()).getSquaredDistance(blockPos);
						}
					}
				);
				if (vec3d == null) {
					return false;
				} else {
					Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage()
						.getPosition(PointOfInterestType.ALWAYS_TRUE, this::shouldVisit, new BlockPos(vec3d), 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
					if (!optional.isPresent()) {
						return false;
					} else {
						this.target = ((BlockPos)optional.get()).toImmutable();
						MobNavigation mobNavigation = (MobNavigation)this.mob.getNavigation();
						boolean bl = mobNavigation.canEnterOpenDoors();
						mobNavigation.setCanPathThroughDoors(this.doorPassingThroughGetter.getAsBoolean());
						this.targetPath = mobNavigation.findPathTo(this.target, 0);
						mobNavigation.setCanPathThroughDoors(bl);
						if (this.targetPath == null) {
							Vec3d vec3d2 = TargetFinder.findTargetTowards(this.mob, 10, 7, new Vec3d(this.target));
							if (vec3d2 == null) {
								return false;
							}

							mobNavigation.setCanPathThroughDoors(this.doorPassingThroughGetter.getAsBoolean());
							this.targetPath = this.mob.getNavigation().findPathTo(vec3d2.x, vec3d2.y, vec3d2.z, 0);
							mobNavigation.setCanPathThroughDoors(bl);
							if (this.targetPath == null) {
								return false;
							}
						}

						for (int i = 0; i < this.targetPath.getLength(); i++) {
							PathNode pathNode = this.targetPath.getNode(i);
							BlockPos blockPos2 = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
							if (DoorBlock.method_24795(this.mob.world, blockPos2)) {
								this.targetPath = this.mob.getNavigation().findPathTo((double)pathNode.x, (double)pathNode.y, (double)pathNode.z, 0);
								break;
							}
						}

						return this.targetPath != null;
					}
				}
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.mob.getNavigation().isIdle() ? false : !this.target.isWithinDistance(this.mob.getPos(), (double)(this.mob.getWidth() + (float)this.distance));
	}

	@Override
	public void start() {
		this.mob.getNavigation().startMovingAlong(this.targetPath, this.speed);
	}

	@Override
	public void stop() {
		if (this.mob.getNavigation().isIdle() || this.target.isWithinDistance(this.mob.getPos(), (double)this.distance)) {
			this.visitedTargets.add(this.target);
		}
	}

	private boolean shouldVisit(BlockPos pos) {
		for (BlockPos blockPos : this.visitedTargets) {
			if (Objects.equals(pos, blockPos)) {
				return false;
			}
		}

		return true;
	}

	private void forgetOldTarget() {
		if (this.visitedTargets.size() > 15) {
			this.visitedTargets.remove(0);
		}
	}
}
