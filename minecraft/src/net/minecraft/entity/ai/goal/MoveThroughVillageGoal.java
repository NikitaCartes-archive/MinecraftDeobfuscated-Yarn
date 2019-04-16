package net.minecraft.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import net.minecraft.entity.ai.PathfindingUtil;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntityWithAi;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestStorage;
import net.minecraft.village.PointOfInterestType;

public class MoveThroughVillageGoal extends Goal {
	protected final MobEntityWithAi field_6525;
	private final double field_6520;
	private Path field_6523;
	private BlockPos field_18412;
	private final boolean field_6524;
	private final List<BlockPos> field_18413 = Lists.<BlockPos>newArrayList();
	private final int field_18414;
	private final BooleanSupplier field_18415;

	public MoveThroughVillageGoal(MobEntityWithAi mobEntityWithAi, double d, boolean bl, int i, BooleanSupplier booleanSupplier) {
		this.field_6525 = mobEntityWithAi;
		this.field_6520 = d;
		this.field_6524 = bl;
		this.field_18414 = i;
		this.field_18415 = booleanSupplier;
		this.setControls(EnumSet.of(Goal.Control.field_18405));
		if (!(mobEntityWithAi.getNavigation() instanceof MobNavigation)) {
			throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
		}
	}

	@Override
	public boolean canStart() {
		this.method_6297();
		if (this.field_6524 && this.field_6525.world.isDaylight()) {
			return false;
		} else {
			ServerWorld serverWorld = (ServerWorld)this.field_6525.world;
			BlockPos blockPos = new BlockPos(this.field_6525);
			if (!serverWorld.isNearOccupiedPointOfInterest(blockPos, 6)) {
				return false;
			} else {
				Vec3d vec3d = PathfindingUtil.findTargetStraight(
					this.field_6525,
					15,
					7,
					blockPos2x -> {
						if (!serverWorld.isNearOccupiedPointOfInterest(blockPos2x)) {
							return Double.NEGATIVE_INFINITY;
						} else {
							Optional<BlockPos> optionalx = serverWorld.getPointOfInterestStorage()
								.getPosition(PointOfInterestType.ALWAYS_TRUE, this::method_19052, blockPos2x, 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
							return !optionalx.isPresent() ? Double.NEGATIVE_INFINITY : -((BlockPos)optionalx.get()).getSquaredDistance(blockPos);
						}
					}
				);
				if (vec3d == null) {
					return false;
				} else {
					Optional<BlockPos> optional = serverWorld.getPointOfInterestStorage()
						.getPosition(PointOfInterestType.ALWAYS_TRUE, this::method_19052, new BlockPos(vec3d), 10, PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
					if (!optional.isPresent()) {
						return false;
					} else {
						this.field_18412 = ((BlockPos)optional.get()).toImmutable();
						MobNavigation mobNavigation = (MobNavigation)this.field_6525.getNavigation();
						boolean bl = mobNavigation.canEnterOpenDoors();
						mobNavigation.setCanPathThroughDoors(this.field_18415.getAsBoolean());
						this.field_6523 = mobNavigation.findPathTo(this.field_18412);
						mobNavigation.setCanPathThroughDoors(bl);
						if (this.field_6523 == null) {
							Vec3d vec3d2 = PathfindingUtil.method_6373(
								this.field_6525, 10, 7, new Vec3d((double)this.field_18412.getX(), (double)this.field_18412.getY(), (double)this.field_18412.getZ())
							);
							if (vec3d2 == null) {
								return false;
							}

							mobNavigation.setCanPathThroughDoors(this.field_18415.getAsBoolean());
							this.field_6523 = this.field_6525.getNavigation().findPathTo(vec3d2.x, vec3d2.y, vec3d2.z);
							mobNavigation.setCanPathThroughDoors(bl);
							if (this.field_6523 == null) {
								return false;
							}
						}

						for (int i = 0; i < this.field_6523.getLength(); i++) {
							PathNode pathNode = this.field_6523.getNode(i);
							BlockPos blockPos2 = new BlockPos(pathNode.x, pathNode.y + 1, pathNode.z);
							if (DoorInteractGoal.getDoor(this.field_6525.world, blockPos2)) {
								this.field_6523 = this.field_6525.getNavigation().findPathTo((double)pathNode.x, (double)pathNode.y, (double)pathNode.z);
								break;
							}
						}

						return this.field_6523 != null;
					}
				}
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.field_6525.getNavigation().isIdle()
			? false
			: !this.field_18412.isWithinDistance(this.field_6525.getPos(), (double)(this.field_6525.getWidth() + (float)this.field_18414));
	}

	@Override
	public void start() {
		this.field_6525.getNavigation().startMovingAlong(this.field_6523, this.field_6520);
	}

	@Override
	public void stop() {
		if (this.field_6525.getNavigation().isIdle() || this.field_18412.isWithinDistance(this.field_6525.getPos(), (double)this.field_18414)) {
			this.field_18413.add(this.field_18412);
		}
	}

	private boolean method_19052(BlockPos blockPos) {
		for (BlockPos blockPos2 : this.field_18413) {
			if (Objects.equals(blockPos, blockPos2)) {
				return false;
			}
		}

		return true;
	}

	private void method_6297() {
		if (this.field_18413.size() > 15) {
			this.field_18413.remove(0);
		}
	}
}
