package net.minecraft.world.chunk.light;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.LevelIndexedProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
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
	private static final Direction[] field_16513 = Direction.values();
	protected final ChunkProvider chunkProvider;
	protected final LightType type;
	protected final S field_15793;
	private boolean field_15794;
	private final BlockPos.Mutable field_16514 = new BlockPos.Mutable();
	private final BlockPos.Mutable field_16515 = new BlockPos.Mutable();
	private final BlockPos.Mutable field_16512 = new BlockPos.Mutable();
	private long[] field_17397 = new long[2];
	private BlockView[] field_17398 = new BlockView[2];

	public ChunkLightProvider(ChunkProvider chunkProvider, LightType lightType, S lightStorage) {
		super(16, 256, 8192);
		this.chunkProvider = chunkProvider;
		this.type = lightType;
		this.field_15793 = lightStorage;
		this.method_17530();
	}

	@Override
	protected void scheduleNewUpdate(long l) {
		this.field_15793.updateAll();
		if (this.field_15793.hasChunk(ChunkSectionPos.toChunkLong(l))) {
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
		this.field_15793.updateAll();
		if (!BlockPos.isHeightInvalid(l) && !BlockPos.isHeightInvalid(m)) {
			this.field_16514.setFromLong(l);
			this.field_16515.setFromLong(m);
			int i = ChunkSectionPos.toChunkCoord(this.field_16514.getX());
			int j = ChunkSectionPos.toChunkCoord(this.field_16514.getZ());
			int k = ChunkSectionPos.toChunkCoord(this.field_16515.getX());
			int n = ChunkSectionPos.toChunkCoord(this.field_16515.getZ());
			BlockView blockView = this.method_17529(k, n);
			if (blockView == null) {
				return 16;
			} else {
				BlockState blockState = blockView.method_8320(this.field_16515);
				BlockView blockView2 = this.chunkProvider.getWorld();
				int o = blockState.method_11581(blockView2, this.field_16515);
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
						int p = Integer.signum(this.field_16515.getX() - this.field_16514.getX());
						int q = Integer.signum(this.field_16515.getY() - this.field_16514.getY());
						int r = Integer.signum(this.field_16515.getZ() - this.field_16514.getZ());
						Direction direction = Direction.fromVector(this.field_16512.set(p, q, r));
						if (direction == null) {
							return 16;
						} else {
							BlockState blockState2 = blockView3.method_8320(this.field_16514);
							boolean bl = blockState2.isFullBoundsCubeForCulling() && blockState2.method_16386();
							boolean bl2 = blockState.isFullBoundsCubeForCulling() && blockState.method_16386();
							if (!bl && !bl2) {
								return o;
							} else {
								VoxelShape voxelShape = bl ? blockState2.method_11615(blockView2, this.field_16514) : VoxelShapes.method_1073();
								VoxelShape voxelShape2 = bl2 ? blockState.method_11615(blockView2, this.field_16515) : VoxelShapes.method_1073();
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
		return l == Long.MAX_VALUE;
	}

	@Override
	protected int getMergedLevel(long l, long m, int i) {
		return 0;
	}

	@Override
	protected int getCurrentLevelFor(long l) {
		return l == Long.MAX_VALUE ? 0 : 15 - this.field_15793.get(l);
	}

	protected int getCurrentLevelFromArray(ChunkNibbleArray chunkNibbleArray, long l) {
		return 15
			- chunkNibbleArray.get(
				ChunkSectionPos.toLocalCoord(BlockPos.unpackLongX(l)),
				ChunkSectionPos.toLocalCoord(BlockPos.unpackLongY(l)),
				ChunkSectionPos.toLocalCoord(BlockPos.unpackLongZ(l))
			);
	}

	@Override
	protected void setLevelFor(long l, int i) {
		this.field_15793.set(l, Math.min(15, 15 - i));
	}

	@Override
	protected int getBaseLevelFor(long l, long m, int i) {
		return 0;
	}

	public boolean hasUpdates() {
		return this.hasLevelUpdates() || this.field_15793.hasLevelUpdates() || this.field_15793.hasLightUpdates();
	}

	public int doLightUpdates(int i, boolean bl, boolean bl2) {
		if (!this.field_15794) {
			if (this.field_15793.hasLevelUpdates()) {
				i = this.field_15793.updateLevels(i);
				if (i == 0) {
					return i;
				}
			}

			this.field_15793.processUpdates(this, bl, bl2);
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
		this.field_15793.notifyChunkProvider();
		return i;
	}

	protected void setSection(long l, ChunkNibbleArray chunkNibbleArray) {
		this.field_15793.scheduleToUpdate(l, chunkNibbleArray);
	}

	@Nullable
	@Override
	public ChunkNibbleArray method_15544(ChunkSectionPos chunkSectionPos) {
		return this.field_15793.getDataForChunk(chunkSectionPos.asLong(), false);
	}

	@Override
	public int method_15543(BlockPos blockPos) {
		return this.field_15793.getLight(blockPos.asLong());
	}

	@Environment(EnvType.CLIENT)
	public String method_15520(long l) {
		return "" + this.field_15793.getCurrentLevelFor(l);
	}

	public void method_15513(BlockPos blockPos) {
		long l = blockPos.asLong();
		this.scheduleNewUpdate(l);

		for (Direction direction : field_16513) {
			this.scheduleNewUpdate(BlockPos.method_10060(l, direction));
		}
	}

	public void method_15514(BlockPos blockPos, int i) {
	}

	@Override
	public void method_15551(ChunkSectionPos chunkSectionPos, boolean bl) {
		this.field_15793.scheduleChunkLightUpdate(chunkSectionPos.asLong(), bl);
	}

	public void method_15512(ChunkPos chunkPos, boolean bl) {
		long l = ChunkSectionPos.method_18693(ChunkSectionPos.asLong(chunkPos.x, 0, chunkPos.z));
		this.field_15793.method_15535(l, bl);
	}
}
