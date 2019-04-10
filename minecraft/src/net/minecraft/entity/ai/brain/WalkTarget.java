package net.minecraft.entity.ai.brain;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WalkTarget {
	private final LookTarget lookTarget;
	private final float speed;
	private final int completionRange;

	public WalkTarget(BlockPos blockPos, float f, int i) {
		this(new BlockPosLookTarget(blockPos), f, i);
	}

	public WalkTarget(Vec3d vec3d, float f, int i) {
		this(new BlockPosLookTarget(new BlockPos(vec3d)), f, i);
	}

	public WalkTarget(LookTarget lookTarget, float f, int i) {
		this.lookTarget = lookTarget;
		this.speed = f;
		this.completionRange = i;
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
