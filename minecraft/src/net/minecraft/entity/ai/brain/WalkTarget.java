package net.minecraft.entity.ai.brain;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WalkTarget {
	private final LookTarget lookTarget;
	private final float speed;
	private final int completionRange;

	public WalkTarget(BlockPos pos, float speed, int completionRange) {
		this(new BlockPosLookTarget(pos), speed, completionRange);
	}

	public WalkTarget(Vec3d pos, float speed, int completionRange) {
		this(new BlockPosLookTarget(BlockPos.ofFloored(pos)), speed, completionRange);
	}

	public WalkTarget(Entity entity, float speed, int completionRange) {
		this(new EntityLookTarget(entity, false), speed, completionRange);
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
