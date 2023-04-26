package net.minecraft.world.chunk.light;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;

public class ChunkSkyLight {
	private static final int field_44711 = 16;
	public static final int field_44710 = Integer.MIN_VALUE;
	private final int minY;
	private final PaletteStorage palette;
	private final BlockPos.Mutable reusableBlockPos1 = new BlockPos.Mutable();
	private final BlockPos.Mutable reusableBlockPos2 = new BlockPos.Mutable();

	public ChunkSkyLight(HeightLimitView heightLimitView) {
		this.minY = heightLimitView.getBottomY() - 1;
		int i = heightLimitView.getTopY();
		int j = MathHelper.ceilLog2(i - this.minY + 1);
		this.palette = new PackedIntegerArray(j, 256);
	}

	public void refreshSurfaceY(Chunk chunk) {
		int i = chunk.getHighestNonEmptySection();
		if (i == -1) {
			this.fill(this.minY);
		} else {
			for (int j = 0; j < 16; j++) {
				for (int k = 0; k < 16; k++) {
					int l = Math.max(this.calculateSurfaceY(chunk, i, k, j), this.minY);
					this.set(getPackedIndex(k, j), l);
				}
			}
		}
	}

	private int calculateSurfaceY(Chunk chunk, int topSectionIndex, int localX, int localZ) {
		int i = ChunkSectionPos.getBlockCoord(chunk.sectionIndexToCoord(topSectionIndex) + 1);
		BlockPos.Mutable mutable = this.reusableBlockPos1.set(localX, i, localZ);
		BlockPos.Mutable mutable2 = this.reusableBlockPos2.set(mutable, Direction.DOWN);
		BlockState blockState = Blocks.AIR.getDefaultState();

		for (int j = topSectionIndex; j >= 0; j--) {
			ChunkSection chunkSection = chunk.getSection(j);
			if (chunkSection.isEmpty()) {
				blockState = Blocks.AIR.getDefaultState();
				int k = chunk.sectionIndexToCoord(j);
				mutable.setY(ChunkSectionPos.getBlockCoord(k));
				mutable2.setY(mutable.getY() - 1);
			} else {
				for (int k = 15; k >= 0; k--) {
					BlockState blockState2 = chunkSection.getBlockState(localX, k, localZ);
					if (faceBlocksLight(chunk, mutable, blockState, mutable2, blockState2)) {
						return mutable.getY();
					}

					blockState = blockState2;
					mutable.set(mutable2);
					mutable2.move(Direction.DOWN);
				}
			}
		}

		return this.minY;
	}

	public boolean isSkyLightAccessible(BlockView blockView, int localX, int y, int localZ) {
		int i = y + 1;
		int j = getPackedIndex(localX, localZ);
		int k = this.get(j);
		if (i < k) {
			return false;
		} else {
			BlockPos blockPos = this.reusableBlockPos1.set(localX, y + 1, localZ);
			BlockState blockState = blockView.getBlockState(blockPos);
			BlockPos blockPos2 = this.reusableBlockPos2.set(localX, y, localZ);
			BlockState blockState2 = blockView.getBlockState(blockPos2);
			if (this.isSkyLightAccessible(blockView, j, k, blockPos, blockState, blockPos2, blockState2)) {
				return true;
			} else {
				BlockPos blockPos3 = this.reusableBlockPos1.set(localX, y - 1, localZ);
				BlockState blockState3 = blockView.getBlockState(blockPos3);
				return this.isSkyLightAccessible(blockView, j, k, blockPos2, blockState2, blockPos3, blockState3);
			}
		}
	}

	private boolean isSkyLightAccessible(
		BlockView blockView, int packedIndex, int value, BlockPos upperPos, BlockState upperState, BlockPos lowerPos, BlockState lowerState
	) {
		int i = upperPos.getY();
		if (faceBlocksLight(blockView, upperPos, upperState, lowerPos, lowerState)) {
			if (i > value) {
				this.set(packedIndex, i);
				return true;
			}
		} else if (i == value) {
			this.set(packedIndex, this.locateLightBlockingBlockBelow(blockView, lowerPos, lowerState));
			return true;
		}

		return false;
	}

	private int locateLightBlockingBlockBelow(BlockView blockView, BlockPos pos, BlockState blockState) {
		BlockPos.Mutable mutable = this.reusableBlockPos1.set(pos);
		BlockPos.Mutable mutable2 = this.reusableBlockPos2.set(pos, Direction.DOWN);
		BlockState blockState2 = blockState;

		while (mutable2.getY() >= this.minY) {
			BlockState blockState3 = blockView.getBlockState(mutable2);
			if (faceBlocksLight(blockView, mutable, blockState2, mutable2, blockState3)) {
				return mutable.getY();
			}

			blockState2 = blockState3;
			mutable.set(mutable2);
			mutable2.move(Direction.DOWN);
		}

		return this.minY;
	}

	private static boolean faceBlocksLight(BlockView blockView, BlockPos upperPos, BlockState upperState, BlockPos lowerPos, BlockState lowerState) {
		if (lowerState.getOpacity(blockView, lowerPos) != 0) {
			return true;
		} else {
			VoxelShape voxelShape = ChunkLightProvider.getOpaqueShape(blockView, upperPos, upperState, Direction.DOWN);
			VoxelShape voxelShape2 = ChunkLightProvider.getOpaqueShape(blockView, lowerPos, lowerState, Direction.UP);
			return VoxelShapes.unionCoversFullCube(voxelShape, voxelShape2);
		}
	}

	public int get(int localX, int localZ) {
		int i = this.get(getPackedIndex(localX, localZ));
		return this.convertMinY(i);
	}

	public int getMaxSurfaceY() {
		int i = Integer.MIN_VALUE;

		for (int j = 0; j < this.palette.getSize(); j++) {
			int k = this.palette.get(j);
			if (k > i) {
				i = k;
			}
		}

		return this.convertMinY(i + this.minY);
	}

	private void fill(int y) {
		int i = y - this.minY;

		for (int j = 0; j < this.palette.getSize(); j++) {
			this.palette.set(j, i);
		}
	}

	private void set(int index, int y) {
		this.palette.set(index, y - this.minY);
	}

	private int get(int index) {
		return this.palette.get(index) + this.minY;
	}

	private int convertMinY(int y) {
		return y == this.minY ? Integer.MIN_VALUE : y;
	}

	private static int getPackedIndex(int localX, int localZ) {
		return localX + localZ * 16;
	}
}
