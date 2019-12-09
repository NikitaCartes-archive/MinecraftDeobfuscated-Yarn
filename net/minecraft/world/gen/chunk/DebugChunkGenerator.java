/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.DebugChunkGeneratorConfig;

public class DebugChunkGenerator
extends ChunkGenerator<DebugChunkGeneratorConfig> {
    private static final List<BlockState> BLOCK_STATES = StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap(block -> block.getStateManager().getStates().stream()).collect(Collectors.toList());
    private static final int X_SIDE_LENGTH = MathHelper.ceil(MathHelper.sqrt(BLOCK_STATES.size()));
    private static final int Z_SIDE_LENGTH = MathHelper.ceil((float)BLOCK_STATES.size() / (float)X_SIDE_LENGTH);
    protected static final BlockState AIR = Blocks.AIR.getDefaultState();
    protected static final BlockState BARRIER = Blocks.BARRIER.getDefaultState();

    public DebugChunkGenerator(IWorld world, BiomeSource biomeSource, DebugChunkGeneratorConfig config) {
        super(world, biomeSource, config);
    }

    @Override
    public void buildSurface(ChunkRegion chunkRegion, Chunk chunk) {
    }

    @Override
    public void carve(BiomeAccess biomeAccess, Chunk chunk, GenerationStep.Carver carver) {
    }

    @Override
    public int getSpawnHeight() {
        return this.world.getSeaLevel() + 1;
    }

    @Override
    public void generateFeatures(ChunkRegion region) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = region.getCenterChunkX();
        int j = region.getCenterChunkZ();
        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                int m = (i << 4) + k;
                int n = (j << 4) + l;
                region.setBlockState(mutable.set(m, 60, n), BARRIER, 2);
                BlockState blockState = DebugChunkGenerator.getBlockState(m, n);
                if (blockState == null) continue;
                region.setBlockState(mutable.set(m, 70, n), blockState, 2);
            }
        }
    }

    @Override
    public void populateNoise(IWorld world, Chunk chunk) {
    }

    @Override
    public int getHeightOnGround(int x, int z, Heightmap.Type heightmapType) {
        return 0;
    }

    public static BlockState getBlockState(int x, int z) {
        int i;
        BlockState blockState = AIR;
        if (x > 0 && z > 0 && x % 2 != 0 && z % 2 != 0 && (x /= 2) <= X_SIDE_LENGTH && (z /= 2) <= Z_SIDE_LENGTH && (i = MathHelper.abs(x * X_SIDE_LENGTH + z)) < BLOCK_STATES.size()) {
            blockState = BLOCK_STATES.get(i);
        }
        return blockState;
    }
}

