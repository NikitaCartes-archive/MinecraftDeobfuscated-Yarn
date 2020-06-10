/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.chunk.light;

import java.util.Arrays;
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
import net.minecraft.world.chunk.light.ChunkLightingView;
import net.minecraft.world.chunk.light.LevelPropagator;
import net.minecraft.world.chunk.light.LightStorage;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

public abstract class ChunkLightProvider<M extends ChunkToNibbleArrayMap<M>, S extends LightStorage<M>>
extends LevelPropagator
implements ChunkLightingView {
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
        ((LightStorage)this.lightStorage).updateAll();
        if (((LightStorage)this.lightStorage).hasLight(ChunkSectionPos.fromGlobalPos(id))) {
            super.resetLevel(id);
        }
    }

    @Nullable
    private BlockView getChunk(int chunkX, int chunkZ) {
        long l = ChunkPos.toLong(chunkX, chunkZ);
        for (int i = 0; i < 2; ++i) {
            if (l != this.cachedChunkPositions[i]) continue;
            return this.cachedChunks[i];
        }
        BlockView blockView = this.chunkProvider.getChunk(chunkX, chunkZ);
        for (int j = 1; j > 0; --j) {
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
        boolean bl;
        int j;
        if (pos == Long.MAX_VALUE) {
            if (mutableInt != null) {
                mutableInt.setValue(0);
            }
            return Blocks.AIR.getDefaultState();
        }
        int i = ChunkSectionPos.getSectionCoord(BlockPos.unpackLongX(pos));
        BlockView blockView = this.getChunk(i, j = ChunkSectionPos.getSectionCoord(BlockPos.unpackLongZ(pos)));
        if (blockView == null) {
            if (mutableInt != null) {
                mutableInt.setValue(16);
            }
            return Blocks.BEDROCK.getDefaultState();
        }
        this.reusableBlockPos.set(pos);
        BlockState blockState = blockView.getBlockState(this.reusableBlockPos);
        boolean bl2 = bl = blockState.isOpaque() && blockState.hasSidedTransparency();
        if (mutableInt != null) {
            mutableInt.setValue(blockState.getOpacity(this.chunkProvider.getWorld(), this.reusableBlockPos));
        }
        return bl ? blockState : Blocks.AIR.getDefaultState();
    }

    protected VoxelShape getOpaqueShape(BlockState world, long pos, Direction facing) {
        return world.isOpaque() ? world.getCullingFace(this.chunkProvider.getWorld(), this.reusableBlockPos.set(pos), facing) : VoxelShapes.empty();
    }

    public static int getRealisticOpacity(BlockView world, BlockState state1, BlockPos pos1, BlockState state2, BlockPos pos2, Direction direction, int opacity2) {
        VoxelShape voxelShape2;
        boolean bl2;
        boolean bl = state1.isOpaque() && state1.hasSidedTransparency();
        boolean bl3 = bl2 = state2.isOpaque() && state2.hasSidedTransparency();
        if (!bl && !bl2) {
            return opacity2;
        }
        VoxelShape voxelShape = bl ? state1.getCullingShape(world, pos1) : VoxelShapes.empty();
        VoxelShape voxelShape3 = voxelShape2 = bl2 ? state2.getCullingShape(world, pos2) : VoxelShapes.empty();
        if (VoxelShapes.adjacentSidesCoverSquare(voxelShape, voxelShape2, direction)) {
            return 16;
        }
        return opacity2;
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
        if (id == Long.MAX_VALUE) {
            return 0;
        }
        return 15 - ((LightStorage)this.lightStorage).get(id);
    }

    protected int getCurrentLevelFromArray(ChunkNibbleArray array, long blockPos) {
        return 15 - array.get(ChunkSectionPos.getLocalCoord(BlockPos.unpackLongX(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongY(blockPos)), ChunkSectionPos.getLocalCoord(BlockPos.unpackLongZ(blockPos)));
    }

    @Override
    protected void setLevel(long id, int level) {
        ((LightStorage)this.lightStorage).set(id, Math.min(15, 15 - level));
    }

    @Override
    protected int getPropagatedLevel(long sourceId, long targetId, int level) {
        return 0;
    }

    public boolean hasUpdates() {
        return this.hasPendingUpdates() || ((LevelPropagator)this.lightStorage).hasPendingUpdates() || ((LightStorage)this.lightStorage).hasLightUpdates();
    }

    public int doLightUpdates(int maxSteps, boolean doSkylight, boolean skipEdgeLightPropagation) {
        if (!this.field_15794) {
            if (((LevelPropagator)this.lightStorage).hasPendingUpdates() && (maxSteps = ((LevelPropagator)this.lightStorage).applyPendingUpdates(maxSteps)) == 0) {
                return maxSteps;
            }
            ((LightStorage)this.lightStorage).updateLightArrays(this, doSkylight, skipEdgeLightPropagation);
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
        ((LightStorage)this.lightStorage).notifyChunkProvider();
        return maxSteps;
    }

    protected void setLightArray(long pos, @Nullable ChunkNibbleArray lightArray, boolean bl) {
        ((LightStorage)this.lightStorage).setLightArray(pos, lightArray, bl);
    }

    @Override
    @Nullable
    public ChunkNibbleArray getLightArray(ChunkSectionPos pos) {
        return ((LightStorage)this.lightStorage).getLightArray(pos.asLong());
    }

    @Override
    public int getLightLevel(BlockPos blockPos) {
        return ((LightStorage)this.lightStorage).getLight(blockPos.asLong());
    }

    @Environment(value=EnvType.CLIENT)
    public String method_22875(long l) {
        return "" + ((LightStorage)this.lightStorage).getLevel(l);
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
    public void updateSectionStatus(ChunkSectionPos pos, boolean status) {
        ((LightStorage)this.lightStorage).updateSectionStatus(pos.asLong(), status);
    }

    public void setLightEnabled(ChunkPos pos, boolean lightEnabled) {
        long l = ChunkSectionPos.withZeroZ(ChunkSectionPos.asLong(pos.x, 0, pos.z));
        ((LightStorage)this.lightStorage).setLightEnabled(l, lightEnabled);
    }

    public void setRetainData(ChunkPos pos, boolean retainData) {
        long l = ChunkSectionPos.withZeroZ(ChunkSectionPos.asLong(pos.x, 0, pos.z));
        ((LightStorage)this.lightStorage).setRetainData(l, retainData);
    }
}

