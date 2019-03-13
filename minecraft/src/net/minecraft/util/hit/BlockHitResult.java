package net.minecraft.util.hit;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BlockHitResult extends HitResult {
	private final Direction field_17588;
	private final BlockPos field_17589;
	private final boolean missed;
	private final boolean field_17591;

	public static BlockHitResult method_17778(Vec3d vec3d, Direction direction, BlockPos blockPos) {
		return new BlockHitResult(true, vec3d, direction, blockPos, false);
	}

	public BlockHitResult(Vec3d vec3d, Direction direction, BlockPos blockPos, boolean bl) {
		this(false, vec3d, direction, blockPos, bl);
	}

	private BlockHitResult(boolean bl, Vec3d vec3d, Direction direction, BlockPos blockPos, boolean bl2) {
		super(vec3d);
		this.missed = bl;
		this.field_17588 = direction;
		this.field_17589 = blockPos;
		this.field_17591 = bl2;
	}

	public BlockHitResult method_17779(Direction direction) {
		return new BlockHitResult(this.missed, this.field_1329, direction, this.field_17589, this.field_17591);
	}

	public BlockPos method_17777() {
		return this.field_17589;
	}

	public Direction method_17780() {
		return this.field_17588;
	}

	@Override
	public HitResult.Type getType() {
		return this.missed ? HitResult.Type.NONE : HitResult.Type.BLOCK;
	}

	public boolean method_17781() {
		return this.field_17591;
	}
}
