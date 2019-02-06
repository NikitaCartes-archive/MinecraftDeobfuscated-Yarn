package net.minecraft.world.chunk.light;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.LevelIndexedProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.chunk.ChunkProvider;
import net.minecraft.world.chunk.WorldNibbleStorage;

public abstract class ChunkLightProvider<M extends WorldNibbleStorage<M>, S extends LightStorage<M>> extends LevelIndexedProcessor implements ChunkLightingView {
	private static final Direction[] DIRECTIONS = Direction.values();
	protected final ChunkProvider chunkProvider;
	protected final LightType type;
	protected final S lightStorage;
	private boolean field_15794;
	private final BlockPos.Mutable srcMutablePos = new BlockPos.Mutable();
	private final BlockPos.Mutable destMutablePos = new BlockPos.Mutable();
	private final BlockPos.Mutable mutablePosGetLightBlockedBetween = new BlockPos.Mutable();
	private long[] field_17397 = new long[2];
	private BlockView[] field_17398 = new BlockView[2];

	public ChunkLightProvider(ChunkProvider chunkProvider, LightType lightType, S lightStorage) {
		super(16, 256, 8192);
		this.chunkProvider = chunkProvider;
		this.type = lightType;
		this.lightStorage = lightStorage;
		this.method_17530();
	}

	@Override
	protected void scheduleNewUpdate(long l) {
		this.lightStorage.updateAll();
		if (this.lightStorage.hasChunk(BlockPos.toChunkSectionOrigin(l))) {
			super.scheduleNewUpdate(l);
		}
	}

	@Nullable
	private BlockView method_17529(int i, int j) {
		long l = ChunkPos.toLong(i, j);

		for (int k = 0; k < 2; k++) {
			if (l == this.field_17397[k]) {
				return this.field_17398[k];
			}
		}

		BlockView blockView = this.chunkProvider.getChunk(i, j);

		for (int m = 1; m > 0; m--) {
			this.field_17397[m] = this.field_17397[m - 1];
			this.field_17398[m] = this.field_17398[m - 1];
		}

		this.field_17397[0] = l;
		this.field_17398[0] = blockView;
		return blockView;
	}

	private void method_17530() {
		Arrays.fill(this.field_17397, ChunkPos.INVALID);
		Arrays.fill(this.field_17398, null);
	}

	protected int getLightBlockedBetween(long l, long m) {
		this.lightStorage.updateAll();
		if (!BlockPos.isHeightInvalid(l) && !BlockPos.isHeightInvalid(m)) {
			this.srcMutablePos.setFromLong(l);
			this.destMutablePos.setFromLong(m);
			int i = this.srcMutablePos.getX() >> 4;
			int j = this.srcMutablePos.getZ() >> 4;
			int k = this.destMutablePos.getX() >> 4;
			int n = this.destMutablePos.getZ() >> 4;
			BlockView blockView = this.method_17529(k, n);
			if (blockView == null) {
				return 16;
			} else {
				BlockState blockState = blockView.getBlockState(this.destMutablePos);
				BlockView blockView2 = this.chunkProvider.getWorld();
				int o = blockState.getLightSubtracted(blockView2, this.destMutablePos);
				if (!blockState.method_16386() && o >= 15) {
					return 16;
				} else {
					BlockView blockView3;
					if (i == k && j == n) {
						blockView3 = blockView;
					} else {
						blockView3 = this.method_17529(i, j);
					}

					if (blockView3 == null) {
						return 16;
					} else {
						int p = Integer.signum(this.destMutablePos.getX() - this.srcMutablePos.getX());
						int q = Integer.signum(this.destMutablePos.getY() - this.srcMutablePos.getY());
						int r = Integer.signum(this.destMutablePos.getZ() - this.srcMutablePos.getZ());
						Direction direction = Direction.method_16365(this.mutablePosGetLightBlockedBetween.set(p, q, r));
						if (direction == null) {
							return 16;
						} else {
							BlockState blockState2 = blockView3.getBlockState(this.srcMutablePos);
							boolean bl = blockState2.isFullBoundsCubeForCulling() && blockState2.method_16386();
							boolean bl2 = blockState.isFullBoundsCubeForCulling() && blockState.method_16386();
							if (!bl && !bl2) {
								return o;
							} else {
								VoxelShape voxelShape = bl ? blockState2.method_11615(blockView2, this.srcMutablePos) : VoxelShapes.empty();
								VoxelShape voxelShape2 = bl2 ? blockState.method_11615(blockView2, this.destMutablePos) : VoxelShapes.empty();
								return VoxelShapes.method_1080(voxelShape, voxelShape2, direction) ? 16 : o;
							}
						}
					}
				}
			}
		} else {
			return 0;
		}
	}

	@Override
	protected boolean isInvalidIndex(long l) {
		return l == -1L;
	}

	@Override
	protected int getMergedLevel(long l, long m, int i) {
		return 0;
	}

	@Override
	protected int getCurrentLevelFor(long l) {
		return l == -1L ? 0 : 15 - this.lightStorage.get(l);
	}

	protected int getCurrentLevelFromArray(ChunkNibbleArray chunkNibbleArray, long l) {
		return 15 - chunkNibbleArray.get(BlockPos.unpackLongX(l) & 15, BlockPos.unpackLongY(l) & 15, BlockPos.unpackLongZ(l) & 15);
	}

	@Override
	protected void setLevelFor(long l, int i) {
		this.lightStorage.set(l, Math.min(15, 15 - i));
	}

	@Override
	protected int getBaseLevelFor(long l, long m, int i) {
		return 0;
	}

	public boolean method_15518() {
		return this.hasLevelUpdates() || this.lightStorage.hasLevelUpdates() || this.lightStorage.hasLightUpdates();
	}

	public int doLightUpdates(int i, boolean bl, boolean bl2) {
		if (!this.field_15794) {
			if (this.lightStorage.hasLevelUpdates()) {
				i = this.lightStorage.updateLevels(i);
				if (i == 0) {
					return i;
				}
			}

			this.lightStorage.processUpdates(this, bl, bl2);
		}

		this.field_15794 = true;
		if (this.hasLevelUpdates()) {
			i = this.updateLevels(i);
			this.method_17530();
			if (i == 0) {
				return i;
			}
		}

		this.field_15794 = false;
		this.lightStorage.notifyChunkProvider();
		return i;
	}

	protected void setSection(int i, int j, int k, ChunkNibbleArray chunkNibbleArray) {
		long l = BlockPos.asLong(i << 4, j << 4, k << 4);
		this.lightStorage.scheduleToUpdate(l, chunkNibbleArray);
	}

	@Nullable
	@Override
	public ChunkNibbleArray getChunkLightArray(int i, int j, int k) {
		long l = BlockPos.asLong(i << 4, j << 4, k << 4);
		return this.lightStorage.getDataForChunk(l, false);
	}

	@Override
	public int getLightLevel(BlockPos blockPos) {
		return this.lightStorage.getLight(blockPos.asLong());
	}

	@Environment(EnvType.CLIENT)
	public String method_15520(long l) {
		return "" + this.lightStorage.getCurrentLevelFor(l);
	}

	public void queueLightCheck(BlockPos blockPos) {
		long l = blockPos.asLong();
		this.scheduleNewUpdate(l);

		for (Direction direction : DIRECTIONS) {
			this.scheduleNewUpdate(BlockPos.offset(l, direction));
		}
	}

	public void method_15514(BlockPos blockPos, int i) {
	}

	@Override
	public void scheduleChunkLightUpdate(int i, int j, int k, boolean bl) {
		long l = BlockPos.asLong(i << 4, j << 4, k << 4);
		this.lightStorage.scheduleChunkLightUpdate(l, bl);
	}

	public void method_15512(int i, int j, boolean bl) {
		long l = BlockPos.removeY(BlockPos.asLong(i << 4, 0, j << 4));
		this.lightStorage.method_15535(l, bl);
	}
}
