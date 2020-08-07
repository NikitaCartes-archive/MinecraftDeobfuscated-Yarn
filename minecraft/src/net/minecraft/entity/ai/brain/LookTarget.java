package net.minecraft.entity.ai.brain;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface LookTarget {
	Vec3d getPos();

	BlockPos getBlockPos();

	boolean isSeenBy(LivingEntity entity);
}
