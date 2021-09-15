/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Predicate;
import net.minecraft.structure.DesertTempleGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class DesertPyramidFeature
extends StructureFeature<DefaultFeatureConfig> {
    public DesertPyramidFeature(Codec<DefaultFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public StructureFeature.StructureStartFactory<DefaultFeatureConfig> getStructureStartFactory() {
        return Start::new;
    }

    public static class Start
    extends StructureStart<DefaultFeatureConfig> {
        public Start(StructureFeature<DefaultFeatureConfig> structureFeature, ChunkPos chunkPos, int i, long l) {
            super(structureFeature, chunkPos, i, l);
        }

        @Override
        public void init(DynamicRegistryManager dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager structureManager, ChunkPos chunkPos, DefaultFeatureConfig defaultFeatureConfig, HeightLimitView heightLimitView, Predicate<Biome> predicate) {
            if (!StructureFeature.checkBiome(chunkGenerator, heightLimitView, predicate, Heightmap.Type.WORLD_SURFACE_WG, chunkPos.getCenterX(), chunkPos.getCenterZ())) {
                return;
            }
            if (StructureFeature.getLowestCornerInGroundHeight(chunkGenerator, 21, 21, chunkPos, heightLimitView) < chunkGenerator.getSeaLevel()) {
                return;
            }
            DesertTempleGenerator desertTempleGenerator = new DesertTempleGenerator(this.random, chunkPos.getStartX(), chunkPos.getStartZ());
            this.addPiece(desertTempleGenerator);
        }
    }
}

