package net.minecraft.util.hit;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BlockHitResult extends HitResult {
	private final Direction side;
	private final BlockPos blockPos;
	private final boolean missed;
	private final boolean field_17591;

	public static BlockHitResult createMissed(Vec3d vec3d, Direction direction, BlockPos blockPos) {
		return new BlockHitResult(true, vec3d, direction, blockPos, false);
	}

	public BlockHitResult(Vec3d vec3d, Direction direction, BlockPos blockPos, boolean bl) {
		this(false, vec3d, direction, blockPos, bl);
	}

	private BlockHitResult(boolean bl, Vec3d vec3d, Direction direction, BlockPos blockPos, boolean bl2) {
		super(vec3d);
		this.missed = bl;
		this.side = direction;
		this.blockPos = blockPos;
		this.field_17591 = bl2;
	}

	public BlockHitResult withSide(Direction direction) {
		return new BlockHitResult(this.missed, this.pos, direction, this.blockPos, this.field_17591);
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public Direction getSide() {
		return this.side;
	}

	@Override
	public HitResult.Type getType() {
		return this.missed ? HitResult.Type.field_1333 : HitResult.Type.field_1332;
	}

	public boolean method_17781() {
		return this.field_17591;
	}
}
