package net.minecraft.entity.ai.brain;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class WalkTarget {
	private final LookTarget lookTarget;
	private final float field_18461;
	private final int field_18462;

	public WalkTarget(BlockPos blockPos, float f, int i) {
		this(new BlockPosLookTarget(blockPos), f, i);
	}

	public WalkTarget(Vec3d vec3d, float f, int i) {
		this(new BlockPosLookTarget(new BlockPos(vec3d)), f, i);
	}

	public WalkTarget(LookTarget lookTarget, float f, int i) {
		this.lookTarget = lookTarget;
		this.field_18461 = f;
		this.field_18462 = i;
	}

	public LookTarget getLookTarget() {
		return this.lookTarget;
	}

	public float method_19095() {
		return this.field_18461;
	}

	public int method_19096() {
		return this.field_18462;
	}
}
