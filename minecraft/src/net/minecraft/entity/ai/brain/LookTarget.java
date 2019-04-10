package net.minecraft.entity.ai.brain;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface LookTarget {
	BlockPos getBlockPos();

	Vec3d getPos();

	boolean isSeenBy(LivingEntity livingEntity);
}
