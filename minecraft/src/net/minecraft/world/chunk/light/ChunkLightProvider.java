package net.minecraft.world.chunk.light;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.util.LevelPropagator;
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
import net.minecraft.world.chunk.WorldNibbleStorage;

public abstract class ChunkLightProvider<M extends WorldNibbleStorage<M>, S extends LightStorage<M>> extends LevelPropagator implements ChunkLightingView {
	private static final Direction[] DIRECTIONS = Direction.values();
	protected final ChunkProvider chunkProvider;
	protected final LightType type;
	protected final S lightStorage;
	private boolean field_15794;
	private final BlockPos.Mutable field_19284 = new BlockPos.Mutable();
	private final long[] field_17397 = new long[2];
	private final BlockView[] field_17398 = new BlockView[2];

	public ChunkLightProvider(ChunkProvider chunkProvider, LightType lightType, S lightStorage) {
		super(16, 256, 8192);
		this.chunkProvider = chunkProvider;
		this.type = lightType;
		this.lightStorage = lightStorage;
		this.method_17530();
	}

	@Override
	protected void fullyUpdate(long l) {
		this.lightStorage.updateAll();
		if (this.lightStorage.hasChunk(ChunkSectionPos.toChunkLong(l))) {
			super.fullyUpdate(l);
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

	protected VoxelShape method_20479(long l, @Nullable AtomicInteger atomicInteger) {
		if (l == Long.MAX_VALUE) {
			if (atomicInteger != null) {
				atomicInteger.set(0);
			}

			return VoxelShapes.empty();
		} else {
			int i = ChunkSectionPos.toChunkCoord(BlockPos.unpackLongX(l));
			int j = ChunkSectionPos.toChunkCoord(BlockPos.unpackLongZ(l));
			BlockView blockView = this.method_17529(i, j);
			if (blockView == null) {
				if (atomicInteger != null) {
					atomicInteger.set(16);
				}

				return VoxelShapes.fullCube();
			} else {
				this.field_19284.setFromLong(l);
				BlockState blockState = blockView.getBlockState(this.field_19284);
				boolean bl = blockState.isFullBoundsCubeForCulling() && blockState.hasSidedTransparency();
				if (atomicInteger != null) {
					atomicInteger.set(blockState.getLightSubtracted(this.chunkProvider.getWorld(), this.field_19284));
				}

				return bl ? blockState.method_11615(this.chunkProvider.getWorld(), this.field_19284) : VoxelShapes.empty();
			}
		}
	}

	public static int method_20049(
		BlockView blockView, BlockState blockState, BlockPos blockPos, BlockState blockState2, BlockPos blockPos2, Direction direction, int i
	) {
		boolean bl = blockState.isFullBoundsCubeForCulling() && blockState.hasSidedTransparency();
		boolean bl2 = blockState2.isFullBoundsCubeForCulling() && blockState2.hasSidedTransparency();
		if (!bl && !bl2) {
			return i;
		} else {
			VoxelShape voxelShape = bl ? blockState.method_11615(blockView, blockPos) : VoxelShapes.empty();
			VoxelShape voxelShape2 = bl2 ? blockState2.method_11615(blockView, blockPos2) : VoxelShapes.empty();
			return VoxelShapes.method_1080(voxelShape, voxelShape2, direction) ? 16 : i;
		}
	}

	@Override
	protected boolean isInvalid(long l) {
		return l == Long.MAX_VALUE;
	}

	@Override
	protected int getMergedLevel(long l, long m, int i) {
		return 0;
	}

	@Override
	protected int getLevel(long l) {
		return l == Long.MAX_VALUE ? 0 : 15 - this.lightStorage.get(l);
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
	protected void setLevel(long l, int i) {
		this.lightStorage.set(l, Math.min(15, 15 - i));
	}

	@Override
	protected int getPropagatedLevel(long l, long m, int i) {
		return 0;
	}

	public boolean hasUpdates() {
		return this.hasLevelUpdates() || this.lightStorage.hasLevelUpdates() || this.lightStorage.hasLightUpdates();
	}

	public int doLightUpdates(int i, boolean bl, boolean bl2) {
		if (!this.field_15794) {
			if (this.lightStorage.hasLevelUpdates()) {
				i = this.lightStorage.updateAllRecursively(i);
				if (i == 0) {
					return i;
				}
			}

			this.lightStorage.processUpdates(this, bl, bl2);
		}

		this.field_15794 = true;
		if (this.hasLevelUpdates()) {
			i = this.updateAllRecursively(i);
			this.method_17530();
			if (i == 0) {
				return i;
			}
		}

		this.field_15794 = false;
		this.lightStorage.notifyChunkProvider();
		return i;
	}

	protected void setSection(long l, ChunkNibbleArray chunkNibbleArray) {
		this.lightStorage.scheduleToUpdate(l, chunkNibbleArray);
	}

	@Nullable
	@Override
	public ChunkNibbleArray getChunkLightArray(ChunkSectionPos chunkSectionPos) {
		return this.lightStorage.method_20533(chunkSectionPos.asLong());
	}

	@Override
	public int getLightLevel(BlockPos blockPos) {
		return this.lightStorage.getLight(blockPos.asLong());
	}

	@Environment(EnvType.CLIENT)
	public String method_15520(long l) {
		return "" + this.lightStorage.getLevel(l);
	}

	public void queueLightCheck(BlockPos blockPos) {
		long l = blockPos.asLong();
		this.fullyUpdate(l);

		for (Direction direction : DIRECTIONS) {
			this.fullyUpdate(BlockPos.offset(l, direction));
		}
	}

	public void method_15514(BlockPos blockPos, int i) {
	}

	@Override
	public void updateSectionStatus(ChunkSectionPos chunkSectionPos, boolean bl) {
		this.lightStorage.scheduleChunkLightUpdate(chunkSectionPos.asLong(), bl);
	}

	public void method_15512(ChunkPos chunkPos, boolean bl) {
		long l = ChunkSectionPos.toLightStorageIndex(ChunkSectionPos.asLong(chunkPos.x, 0, chunkPos.z));
		this.lightStorage.updateAll();
		this.lightStorage.method_15535(l, bl);
	}
}
