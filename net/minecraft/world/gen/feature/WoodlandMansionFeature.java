/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.structure.WoodlandMansionGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class WoodlandMansionFeature
extends StructureFeature<DefaultFeatureConfig> {
    public WoodlandMansionFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function) {
        super(function);
    }

    @Override
    protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
        int m = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getMansionDistance();
        int n = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getMansionSeparation();
        int o = i + m * k;
        int p = j + m * l;
        int q = o < 0 ? o - m + 1 : o;
        int r = p < 0 ? p - m + 1 : p;
        int s = q / m;
        int t = r / m;
        ((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), s, t, 10387319);
        s *= m;
        t *= m;
        return new ChunkPos(s += (random.nextInt(m - n) + random.nextInt(m - n)) / 2, t += (random.nextInt(m - n) + random.nextInt(m - n)) / 2);
    }

    @Override
    public boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int i, int j, Biome biome) {
        ChunkPos chunkPos = this.getStart(chunkGenerator, random, i, j, 0, 0);
        if (i == chunkPos.x && j == chunkPos.z) {
            Set<Biome> set = chunkGenerator.getBiomeSource().getBiomesInArea(i * 16 + 9, chunkGenerator.getSeaLevel(), j * 16 + 9, 32);
            for (Biome biome2 : set) {
                if (chunkGenerator.hasStructure(biome2, this)) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Mansion";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
            super(structureFeature, i, j, blockBox, k, l);
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
            BlockRotation blockRotation = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
            int k = 5;
            int l = 5;
            if (blockRotation == BlockRotation.CLOCKWISE_90) {
                k = -5;
            } else if (blockRotation == BlockRotation.CLOCKWISE_180) {
                k = -5;
                l = -5;
            } else if (blockRotation == BlockRotation.COUNTERCLOCKWISE_90) {
                l = -5;
            }
            int m = (i << 4) + 7;
            int n = (j << 4) + 7;
            int o = chunkGenerator.getHeightInGround(m, n, Heightmap.Type.WORLD_SURFACE_WG);
            int p = chunkGenerator.getHeightInGround(m, n + l, Heightmap.Type.WORLD_SURFACE_WG);
            int q = chunkGenerator.getHeightInGround(m + k, n, Heightmap.Type.WORLD_SURFACE_WG);
            int r = chunkGenerator.getHeightInGround(m + k, n + l, Heightmap.Type.WORLD_SURFACE_WG);
            int s = Math.min(Math.min(o, p), Math.min(q, r));
            if (s < 60) {
                return;
            }
            BlockPos blockPos = new BlockPos(i * 16 + 8, s + 1, j * 16 + 8);
            LinkedList<WoodlandMansionGenerator.Piece> list = Lists.newLinkedList();
            WoodlandMansionGenerator.addPieces(structureManager, blockPos, blockRotation, list, this.random);
            this.children.addAll(list);
            this.setBoundingBoxFromChildren();
        }

        @Override
        public void generateStructure(IWorld iWorld, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
            super.generateStructure(iWorld, chunkGenerator, random, blockBox, chunkPos);
            int i = this.boundingBox.minY;
            for (int j = blockBox.minX; j <= blockBox.maxX; ++j) {
                for (int k = blockBox.minZ; k <= blockBox.maxZ; ++k) {
                    BlockPos blockPos2;
                    BlockPos blockPos = new BlockPos(j, i, k);
                    if (iWorld.isAir(blockPos) || !this.boundingBox.contains(blockPos)) continue;
                    boolean bl = false;
                    for (StructurePiece structurePiece : this.children) {
                        if (!structurePiece.getBoundingBox().contains(blockPos)) continue;
                        bl = true;
                        break;
                    }
                    if (!bl) continue;
                    for (int l = i - 1; l > 1 && (iWorld.isAir(blockPos2 = new BlockPos(j, l, k)) || iWorld.getBlockState(blockPos2).getMaterial().isLiquid()); --l) {
                        iWorld.setBlockState(blockPos2, Blocks.COBBLESTONE.getDefaultState(), 2);
                    }
                }
            }
        }
    }
}

