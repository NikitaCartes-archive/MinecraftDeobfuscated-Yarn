package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class HitResult {
	private BlockPos blockPos;
	public HitResult.Type type;
	public Direction side;
	public Vec3d pos;
	public Entity entity;

	public HitResult(Vec3d vec3d, Direction direction, BlockPos blockPos) {
		this(HitResult.Type.BLOCK, vec3d, direction, blockPos);
	}

	public HitResult(Entity entity) {
		this(entity, new Vec3d(entity.x, entity.y, entity.z));
	}

	public HitResult(HitResult.Type type, Vec3d vec3d, Direction direction, BlockPos blockPos) {
		this.type = type;
		this.blockPos = blockPos;
		this.side = direction;
		this.pos = new Vec3d(vec3d.x, vec3d.y, vec3d.z);
	}

	public HitResult(Entity entity, Vec3d vec3d) {
		this.type = HitResult.Type.ENTITY;
		this.entity = entity;
		this.pos = vec3d;
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public String toString() {
		return "HitResult{type=" + this.type + ", blockpos=" + this.blockPos + ", f=" + this.side + ", pos=" + this.pos + ", entity=" + this.entity + '}';
	}

	public static enum Type {
		NONE,
		BLOCK,
		ENTITY;
	}
}
