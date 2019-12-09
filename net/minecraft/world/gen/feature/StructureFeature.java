/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class StructureFeature<C extends FeatureConfig>
extends Feature<C> {
    private static final Logger LOGGER = LogManager.getLogger();

    public StructureFeature(Function<Dynamic<?>, ? extends C> configFactory) {
        super(configFactory);
    }

    @Override
    public ConfiguredFeature<C, ? extends StructureFeature<C>> configure(C config) {
        return new ConfiguredFeature<C, StructureFeature>(this, config);
    }

    @Override
    public boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos pos, C config) {
        if (!world.getLevelProperties().hasStructures()) {
            return false;
        }
        int i = pos.getX() >> 4;
        int j = pos.getZ() >> 4;
        int k = i << 4;
        int l = j << 4;
        boolean bl = false;
        for (Long long_ : world.getChunk(i, j).getStructureReferences(this.getName())) {
            ChunkPos chunkPos = new ChunkPos(long_);
            StructureStart structureStart = world.getChunk(chunkPos.x, chunkPos.z).getStructureStart(this.getName());
            if (structureStart == null || structureStart == StructureStart.DEFAULT) continue;
            structureStart.generateStructure(world, generator, random, new BlockBox(k, l, k + 15, l + 15), new ChunkPos(i, j));
            bl = true;
        }
        return bl;
    }

    protected StructureStart isInsideStructure(IWorld world, BlockPos pos, boolean exact) {
        List<StructureStart> list = this.getStructureStarts(world, pos.getX() >> 4, pos.getZ() >> 4);
        for (StructureStart structureStart : list) {
            if (!structureStart.hasChildren() || !structureStart.getBoundingBox().contains(pos)) continue;
            if (!exact) {
                return structureStart;
            }
            for (StructurePiece structurePiece : structureStart.getChildren()) {
                if (!structurePiece.getBoundingBox().contains(pos)) continue;
                return structureStart;
            }
        }
        return StructureStart.DEFAULT;
    }

    public boolean isApproximatelyInsideStructure(IWorld iWorld, BlockPos blockPos) {
        return this.isInsideStructure(iWorld, blockPos, false).hasChildren();
    }

    public boolean isInsideStructure(IWorld iWorld, BlockPos blockPos) {
        return this.isInsideStructure(iWorld, blockPos, true).hasChildren();
    }

    @Nullable
    public BlockPos locateStructure(World world, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, BlockPos blockPos, int i, boolean skipExistingChunks) {
        if (!chunkGenerator.getBiomeSource().hasStructureFeature(this)) {
            return null;
        }
        int j = blockPos.getX() >> 4;
        int k = blockPos.getZ() >> 4;
        ChunkRandom chunkRandom = new ChunkRandom();
        block0: for (int l = 0; l <= i; ++l) {
            for (int m = -l; m <= l; ++m) {
                boolean bl = m == -l || m == l;
                for (int n = -l; n <= l; ++n) {
                    boolean bl2;
                    boolean bl3 = bl2 = n == -l || n == l;
                    if (!bl && !bl2) continue;
                    ChunkPos chunkPos = this.getStart(chunkGenerator, chunkRandom, j, k, m, n);
                    StructureStart structureStart = world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS).getStructureStart(this.getName());
                    if (structureStart != null && structureStart.hasChildren()) {
                        if (skipExistingChunks && structureStart.isInExistingChunk()) {
                            structureStart.incrementReferences();
                            return structureStart.getPos();
                        }
                        if (!skipExistingChunks) {
                            return structureStart.getPos();
                        }
                    }
                    if (l == 0) break;
                }
                if (l == 0) continue block0;
            }
        }
        return null;
    }

    private List<StructureStart> getStructureStarts(IWorld world, int chunkX, int chunkZ) {
        ArrayList<StructureStart> list = Lists.newArrayList();
        Chunk chunk = world.getChunk(chunkX, chunkZ, ChunkStatus.STRUCTURE_REFERENCES);
        LongIterator longIterator = chunk.getStructureReferences(this.getName()).iterator();
        while (longIterator.hasNext()) {
            long l = longIterator.nextLong();
            Chunk structureHolder = world.getChunk(ChunkPos.getPackedX(l), ChunkPos.getPackedZ(l), ChunkStatus.STRUCTURE_STARTS);
            StructureStart structureStart = structureHolder.getStructureStart(this.getName());
            if (structureStart == null) continue;
            list.add(structureStart);
        }
        return list;
    }

    protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
        return new ChunkPos(i + k, j + l);
    }

    public abstract boolean shouldStartAt(BiomeAccess var1, ChunkGenerator<?> var2, Random var3, int var4, int var5, Biome var6);

    public abstract StructureStartFactory getStructureStartFactory();

    public abstract String getName();

    public abstract int getRadius();

    public static interface StructureStartFactory {
        public StructureStart create(StructureFeature<?> var1, int var2, int var3, BlockBox var4, int var5, long var6);
    }
}

