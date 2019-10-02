package net.minecraft.world.chunk.light;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.ChunkToNibbleArrayMap;

public abstract class ChunkLightProvider<M extends ChunkToNibbleArrayMap<M>, S extends LightStorage<M>> extends LevelPropagator implements ChunkLightingView {
	private static final Direction[] DIRECTIONS = Direction.values();
	protected final ChunkProvider chunkProvider;
	protected final LightType type;
	protected final S lightStorage;
	private boolean field_15794;
	protected final BlockPos.Mutable reusableBlockPos = new BlockPos.Mutable();
	private final long[] cachedChunkPositions = new long[2];
	private final BlockView[] cachedChunks = new BlockView[2];

	public ChunkLightProvider(ChunkProvider chunkProvider, LightType lightType, S lightStorage) {
		super(16, 256, 8192);
		this.chunkProvider = chunkProvider;
		this.type = lightType;
		this.lightStorage = lightStorage;
		this.clearChunkCache();
	}

	@Override
	protected void resetLevel(long l) {
		this.lightStorage.updateAll();
		if (this.lightStorage.hasLight(ChunkSectionPos.fromGlobalPos(l))) {
			super.resetLevel(l);
		}
	}

	@Nullable
	private BlockView getChunk(int i, int j) {
		long l = ChunkPos.toLong(i, j);

		for (int k = 0; k < 2; k++) {
			if (l == this.cachedChunkPositions[k]) {
				return this.cachedChunks[k];
			}
		}

		BlockView blockView = this.chunkProvider.getChunk(i, j);

		for (int m = 1; m > 0; m--) {
			this.cachedChunkPositions[m] = this.cachedChunkPositions[m - 1];
			this.cachedChunks[m] = this.cachedChunks[m - 1];
		}

		this.cachedChunkPositions[0] = l;
		this.cachedChunks[0] = blockView;
		return blockView;
	}

	private void clearChunkCache() {
		Arrays.fill(this.cachedChunkPositions, ChunkPos.MARKER);
		Arrays.fill(this.cachedChunks, null);
	}

	protected BlockState getStateForLighting(long l, @Nullable AtomicInteger atomicInteger) {
		if (l == Long.MAX_VALUE) {
			if (atomicInteger != null) {
				atomicInteger.set(0);
			}

			return Blocks.AIR.getDefaultState();
		} else {
			int i = ChunkSectionPos.getSectionCoord(BlockPos.unpackLongX(l));
			int j = ChunkSectionPos.getSectionCoord(BlockPos.unpackLongZ(l));
			BlockView blockView = this.getChunk(i, j);
			if (blockView == null) {
				if (atomicInteger != null) {
					atomicInteger.set(16);
				}

				return Blocks.BEDROCK.getDefaultState();
			} else {
				this.reusableBlockPos.set(l);
				BlockState blockState = blockView.getBlockState(this.reusableBlockPos);
				boolean bl = blockState.isOpaque() && blockState.hasSidedTransparency();
				if (atomicInteger != null) {
					atomicInteger.set(blockState.getOpacity(this.chunkProvider.getWorld(), this.reusableBlockPos));
				}

				return bl ? blockState : Blocks.AIR.getDefaultState();
			}
		}
	}

	protected VoxelShape getOpaqueShape(BlockState blockState, long l, Direction direction) {
		return blockState.isOpaque() ? blockState.getCullingShape(this.chunkProvider.getWorld(), this.reusableBlockPos.set(l), direction) : VoxelShapes.empty();
	}

	public static int getRealisticOpacity(
		BlockView blockView, BlockState blockState, BlockPos blockPos, BlockState blockState2, BlockPos blockPos2, Direction direction, int i
	) {
		boolean bl = blockState.isOpaque() && blockState.hasSidedTransparency();
		boolean bl2 = blockState2.isOpaque() && blockState2.hasSidedTransparency();
		if (!bl && !bl2) {
			return i;
		} else {
			VoxelShape voxelShape = bl ? blockState.getCullingShape(blockView, blockPos) : VoxelShapes.empty();
			VoxelShape voxelShape2 = bl2 ? blockState2.getCullingShape(blockView, blockPos2) : VoxelShapes.empty();
			return VoxelShapes.adjacentSidesCoverSquare(voxelShape, voxelShape2, direction) ? 16 : i;
		}
	}

	@Override
	protected boolean isMarker(long l) {
		return l == Long.MAX_VALUE;
	}

	@Override
	protected int recalculateLevel(long l, long m, int i) {
		return 0;
	}

	@Override
	protected int getLevel(long l) {
		return l == Long.MAX_VALUE ? 0 : 15 - this.lightStorage.get(l);
	}

	protected int getCurrentLevelFromArray(ChunkNibbleArray chunkNibbleArray, long l) {
		return 15
			- chunkNibbleArray.get(
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(l)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(l)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(l))
			);
	}

	@Override
	protected void setLevel(long l, int i) {
		this.lightStorage.set(l, Math.min(15, 15 - i));
	}

	@Override
	protected int getPropagatedLevel(long l, long m, int i) {
		return 0;
	}

	public boolean hasUpdates() {
		return this.hasPendingUpdates() || this.lightStorage.hasPendingUpdates() || this.lightStorage.hasLightUpdates();
	}

	public int doLightUpdates(int i, boolean bl, boolean bl2) {
		if (!this.field_15794) {
			if (this.lightStorage.hasPendingUpdates()) {
				i = this.lightStorage.applyPendingUpdates(i);
				if (i == 0) {
					return i;
				}
			}

			this.lightStorage.updateLightArrays(this, bl, bl2);
		}

		this.field_15794 = true;
		if (this.hasPendingUpdates()) {
			i = this.applyPendingUpdates(i);
			this.clearChunkCache();
			if (i == 0) {
				return i;
			}
		}

		this.field_15794 = false;
		this.lightStorage.notifyChunkProvider();
		return i;
	}

	protected void setLightArray(long l, @Nullable ChunkNibbleArray chunkNibbleArray) {
		this.lightStorage.setLightArray(l, chunkNibbleArray);
	}

	@Nullable
	@Override
	public ChunkNibbleArray getLightArray(ChunkSectionPos chunkSectionPos) {
		return this.lightStorage.getLightArray(chunkSectionPos.asLong());
	}

	@Override
	public int getLightLevel(BlockPos blockPos) {
		return this.lightStorage.getLight(blockPos.asLong());
	}

	@Environment(EnvType.CLIENT)
	public String method_22875(long l) {
		return "" + this.lightStorage.getLevel(l);
	}

	public void checkBlock(BlockPos blockPos) {
		long l = blockPos.asLong();
		this.resetLevel(l);

		for (Direction direction : DIRECTIONS) {
			this.resetLevel(BlockPos.offset(l, direction));
		}
	}

	public void addLightSource(BlockPos blockPos, int i) {
	}

	@Override
	public void updateSectionStatus(ChunkSectionPos chunkSectionPos, boolean bl) {
		this.lightStorage.updateSectionStatus(chunkSectionPos.asLong(), bl);
	}

	public void setLightEnabled(ChunkPos chunkPos, boolean bl) {
		long l = ChunkSectionPos.withZeroZ(ChunkSectionPos.asLong(chunkPos.x, 0, chunkPos.z));
		this.lightStorage.updateAll();
		this.lightStorage.setLightEnabled(l, bl);
	}

	public void setRetainData(ChunkPos chunkPos, boolean bl) {
		long l = ChunkSectionPos.withZeroZ(ChunkSectionPos.asLong(chunkPos.x, 0, chunkPos.z));
		this.lightStorage.setRetainData(l, bl);
	}
}
