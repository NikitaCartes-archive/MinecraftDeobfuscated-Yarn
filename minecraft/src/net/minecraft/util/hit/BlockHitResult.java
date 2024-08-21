package net.minecraft.util.hit;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BlockHitResult extends HitResult {
	private final Direction side;
	private final BlockPos blockPos;
	private final boolean missed;
	private final boolean insideBlock;
	private final boolean againstWorldBorder;

	public static BlockHitResult createMissed(Vec3d pos, Direction side, BlockPos blockPos) {
		return new BlockHitResult(true, pos, side, blockPos, false, false);
	}

	public BlockHitResult(Vec3d pos, Direction side, BlockPos blockPos, boolean insideBlock) {
		this(false, pos, side, blockPos, insideBlock, false);
	}

	public BlockHitResult(Vec3d pos, Direction side, BlockPos blockPos, boolean insideBlock, boolean againstWorldBorder) {
		this(false, pos, side, blockPos, insideBlock, againstWorldBorder);
	}

	private BlockHitResult(boolean missed, Vec3d pos, Direction side, BlockPos blockPos, boolean insideBlock, boolean againstWorldBorder) {
		super(pos);
		this.missed = missed;
		this.side = side;
		this.blockPos = blockPos;
		this.insideBlock = insideBlock;
		this.againstWorldBorder = againstWorldBorder;
	}

	public BlockHitResult withSide(Direction side) {
		return new BlockHitResult(this.missed, this.pos, side, this.blockPos, this.insideBlock, this.againstWorldBorder);
	}

	public BlockHitResult withBlockPos(BlockPos blockPos) {
		return new BlockHitResult(this.missed, this.pos, this.side, blockPos, this.insideBlock, this.againstWorldBorder);
	}

	public BlockHitResult againstWorldBorder() {
		return new BlockHitResult(this.missed, this.pos, this.side, this.blockPos, this.insideBlock, true);
	}

	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public Direction getSide() {
		return this.side;
	}

	@Override
	public HitResult.Type getType() {
		return this.missed ? HitResult.Type.MISS : HitResult.Type.BLOCK;
	}

	public boolean isInsideBlock() {
		return this.insideBlock;
	}

	public boolean isAgainstWorldBorder() {
		return this.againstWorldBorder;
	}
}
