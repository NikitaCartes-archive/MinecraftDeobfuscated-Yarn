package net.minecraft.entity.ai.brain;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WalkTarget {
	private final LookTarget lookTarget;
	private final float speed;
	private final int completionRange;

	public WalkTarget(BlockPos pos, float speed, int completionRange) {
		this(new BlockPosLookTarget(pos), speed, completionRange);
	}

	public WalkTarget(Vec3d pos, float speedFactor, int completionRange) {
		this(new BlockPosLookTarget(new BlockPos(pos)), speedFactor, completionRange);
	}

	public WalkTarget(LookTarget lookTarget, float speed, int completionRange) {
		this.lookTarget = lookTarget;
		this.speed = speed;
		this.completionRange = completionRange;
	}

	public LookTarget getLookTarget() {
		return this.lookTarget;
	}

	public float getSpeed() {
		return this.speed;
	}

	public int getCompletionRange() {
		return this.completionRange;
	}
}
