package net.minecraft.world.chunk.light;

import java.util.Arrays;
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
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class ChunkLightProvider<M extends ChunkToNibbleArrayMap<M>, S extends LightStorage<M>> extends LevelPropagator implements ChunkLightingView {
	private static final Direction[] DIRECTIONS = Direction.values();
	protected final ChunkProvider chunkProvider;
	protected final LightType type;
	protected final S lightStorage;
	private boolean field_15794;
	protected final BlockPos.Mutable reusableBlockPos = new BlockPos.Mutable();
	private final long[] cachedChunkPositions = new long[2];
	private final BlockView[] cachedChunks = new BlockView[2];

	public ChunkLightProvider(ChunkProvider chunkProvider, LightType type, S lightStorage) {
		super(16, 256, 8192);
		this.chunkProvider = chunkProvider;
		this.type = type;
		this.lightStorage = lightStorage;
		this.clearChunkCache();
	}

	@Override
	protected void resetLevel(long id) {
		this.lightStorage.updateAll();
		if (this.lightStorage.hasSection(ChunkSectionPos.fromBlockPos(id))) {
			super.resetLevel(id);
		}
	}

	@Nullable
	private BlockView getChunk(int chunkX, int chunkZ) {
		long l = ChunkPos.toLong(chunkX, chunkZ);

		for (int i = 0; i < 2; i++) {
			if (l == this.cachedChunkPositions[i]) {
				return this.cachedChunks[i];
			}
		}

		BlockView blockView = this.chunkProvider.getChunk(chunkX, chunkZ);

		for (int j = 1; j > 0; j--) {
			this.cachedChunkPositions[j] = this.cachedChunkPositions[j - 1];
			this.cachedChunks[j] = this.cachedChunks[j - 1];
		}

		this.cachedChunkPositions[0] = l;
		this.cachedChunks[0] = blockView;
		return blockView;
	}

	private void clearChunkCache() {
		Arrays.fill(this.cachedChunkPositions, ChunkPos.MARKER);
		Arrays.fill(this.cachedChunks, null);
	}

	protected BlockState getStateForLighting(long pos, @Nullable MutableInt mutableInt) {
		if (pos == Long.MAX_VALUE) {
			if (mutableInt != null) {
				mutableInt.setValue(0);
			}

			return Blocks.AIR.getDefaultState();
		} else {
			int i = ChunkSectionPos.getSectionCoord(BlockPos.unpackLongX(pos));
			int j = ChunkSectionPos.getSectionCoord(BlockPos.unpackLongZ(pos));
			BlockView blockView = this.getChunk(i, j);
			if (blockView == null) {
				if (mutableInt != null) {
					mutableInt.setValue(16);
				}

				return Blocks.BEDROCK.getDefaultState();
			} else {
				this.reusableBlockPos.set(pos);
				BlockState blockState = blockView.getBlockState(this.reusableBlockPos);
				boolean bl = blockState.isOpaque() && blockState.hasSidedTransparency();
				if (mutableInt != null) {
					mutableInt.setValue(blockState.getOpacity(this.chunkProvider.getWorld(), this.reusableBlockPos));
				}

				return bl ? blockState : Blocks.AIR.getDefaultState();
			}
		}
	}

	protected VoxelShape getOpaqueShape(BlockState world, long pos, Direction facing) {
		return world.isOpaque() ? world.getCullingFace(this.chunkProvider.getWorld(), this.reusableBlockPos.set(pos), facing) : VoxelShapes.empty();
	}

	public static int getRealisticOpacity(BlockView world, BlockState state1, BlockPos pos1, BlockState state2, BlockPos pos2, Direction direction, int opacity2) {
		boolean bl = state1.isOpaque() && state1.hasSidedTransparency();
		boolean bl2 = state2.isOpaque() && state2.hasSidedTransparency();
		if (!bl && !bl2) {
			return opacity2;
		} else {
			VoxelShape voxelShape = bl ? state1.getCullingShape(world, pos1) : VoxelShapes.empty();
			VoxelShape voxelShape2 = bl2 ? state2.getCullingShape(world, pos2) : VoxelShapes.empty();
			return VoxelShapes.adjacentSidesCoverSquare(voxelShape, voxelShape2, direction) ? 16 : opacity2;
		}
	}

	@Override
	protected boolean isMarker(long id) {
		return id == Long.MAX_VALUE;
	}

	@Override
	protected int recalculateLevel(long id, long excludedId, int maxLevel) {
		return 0;
	}

	@Override
	protected int getLevel(long id) {
		return id == Long.MAX_VALUE ? 0 : 15 - this.lightStorage.get(id);
	}

	protected int getCurrentLevelFromSection(ChunkNibbleArray section, long blockPos) {
		return 15
			- section.get(
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)),
				ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos))
			);
	}

	@Override
	protected void setLevel(long id, int level) {
		this.lightStorage.set(id, Math.min(15, 15 - level));
	}

	@Override
	protected int getPropagatedLevel(long sourceId, long targetId, int level) {
		return 0;
	}

	public boolean hasUpdates() {
		return this.hasPendingUpdates() || this.lightStorage.hasPendingUpdates() || this.lightStorage.hasLightUpdates();
	}

	public int doLightUpdates(int maxSteps, boolean doSkylight, boolean skipEdgeLightPropagation) {
		if (!this.field_15794) {
			if (this.lightStorage.hasPendingUpdates()) {
				maxSteps = this.lightStorage.applyPendingUpdates(maxSteps);
				if (maxSteps == 0) {
					return maxSteps;
				}
			}

			this.lightStorage.updateLight(this, doSkylight, skipEdgeLightPropagation);
		}

		this.field_15794 = true;
		if (this.hasPendingUpdates()) {
			maxSteps = this.applyPendingUpdates(maxSteps);
			this.clearChunkCache();
			if (maxSteps == 0) {
				return maxSteps;
			}
		}

		this.field_15794 = false;
		this.lightStorage.notifyChanges();
		return maxSteps;
	}

	protected void enqueueSectionData(long sectionPos, @Nullable ChunkNibbleArray lightArray, boolean bl) {
		this.lightStorage.enqueueSectionData(sectionPos, lightArray, bl);
	}

	@Nullable
	@Override
	public ChunkNibbleArray getLightSection(ChunkSectionPos pos) {
		return this.lightStorage.getLightSection(pos.asLong());
	}

	@Override
	public int getLightLevel(BlockPos blockPos) {
		return this.lightStorage.getLight(blockPos.asLong());
	}

	@Environment(EnvType.CLIENT)
	public String displaySectionLevel(long sectionPos) {
		return "" + this.lightStorage.getLevel(sectionPos);
	}

	public void checkBlock(BlockPos pos) {
		long l = pos.asLong();
		this.resetLevel(l);

		for (Direction direction : DIRECTIONS) {
			this.resetLevel(BlockPos.offset(l, direction));
		}
	}

	public void addLightSource(BlockPos pos, int level) {
	}

	@Override
	public void setSectionStatus(ChunkSectionPos pos, boolean notReady) {
		this.lightStorage.setSectionStatus(pos.asLong(), notReady);
	}

	public void setColumnEnabled(ChunkPos pos, boolean enabled) {
		long l = ChunkSectionPos.withZeroY(ChunkSectionPos.asLong(pos.x, 0, pos.z));
		this.lightStorage.setColumnEnabled(l, enabled);
	}

	public void setRetainColumn(ChunkPos pos, boolean retainData) {
		long l = ChunkSectionPos.withZeroY(ChunkSectionPos.asLong(pos.x, 0, pos.z));
		this.lightStorage.setRetainColumn(l, retainData);
	}
}
