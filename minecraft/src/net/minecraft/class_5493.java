package net.minecraft;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;

public class class_5493 {
	public static boolean method_30955(MobEntity mobEntity) {
		return mobEntity.getNavigation() instanceof MobNavigation;
	}

	public static boolean method_31517(PathAwareEntity pathAwareEntity, int i) {
		return pathAwareEntity.hasPositionTarget()
			&& pathAwareEntity.getPositionTarget().isWithinDistance(pathAwareEntity.getPos(), (double)(pathAwareEntity.getPositionTargetRange() + (float)i) + 1.0);
	}

	public static boolean method_31520(BlockPos blockPos, PathAwareEntity pathAwareEntity) {
		return blockPos.getY() < pathAwareEntity.world.getBottomY() || blockPos.getY() > pathAwareEntity.world.getTopY();
	}

	public static boolean method_31521(boolean bl, PathAwareEntity pathAwareEntity, BlockPos blockPos) {
		return bl && !pathAwareEntity.isInWalkTargetRange(blockPos);
	}

	public static boolean method_31519(EntityNavigation entityNavigation, BlockPos blockPos) {
		return !entityNavigation.isValidPosition(blockPos);
	}

	public static boolean method_31518(PathAwareEntity pathAwareEntity, BlockPos blockPos) {
		return pathAwareEntity.world.getFluidState(blockPos).isIn(FluidTags.WATER);
	}

	public static boolean method_31522(PathAwareEntity pathAwareEntity, BlockPos blockPos) {
		return pathAwareEntity.getPathfindingPenalty(LandPathNodeMaker.getLandNodeType(pathAwareEntity.world, blockPos.mutableCopy())) != 0.0F;
	}

	public static boolean method_31523(PathAwareEntity pathAwareEntity, BlockPos blockPos) {
		return pathAwareEntity.world.getBlockState(blockPos).getMaterial().isSolid();
	}
}
