/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.VillageGenerator;
import net.minecraft.structure.VillageStructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.VillageFeatureConfig;

public class VillageFeature
extends StructureFeature<VillageFeatureConfig> {
    public VillageFeature(Function<Dynamic<?>, ? extends VillageFeatureConfig> function) {
        super(function);
    }

    @Override
    protected ChunkPos getStart(ChunkGenerator<?> chunkGenerator, Random random, int i, int j, int k, int l) {
        int m = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getVillageDistance();
        int n = ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getVillageSeparation();
        int o = i + m * k;
        int p = j + m * l;
        int q = o < 0 ? o - m + 1 : o;
        int r = p < 0 ? p - m + 1 : p;
        int s = q / m;
        int t = r / m;
        ((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), s, t, 10387312);
        s *= m;
        t *= m;
        return new ChunkPos(s += random.nextInt(m - n), t += random.nextInt(m - n));
    }

    @Override
    public boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int i, int j, Biome biome) {
        ChunkPos chunkPos = this.getStart(chunkGenerator, random, i, j, 0, 0);
        if (i == chunkPos.x && j == chunkPos.z) {
            return chunkGenerator.hasStructure(biome, this);
        }
        return false;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Village";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    public static class Start
    extends VillageStructureStart {
        public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
            super(structureFeature, i, j, blockBox, k, l);
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
            VillageFeatureConfig villageFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.VILLAGE);
            BlockPos blockPos = new BlockPos(i * 16, 0, j * 16);
            VillageGenerator.addPieces(chunkGenerator, structureManager, blockPos, this.children, this.random, villageFeatureConfig);
            this.setBoundingBoxFromChildren();
        }
    }
}

