/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.AbstractTempleFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class OceanRuinFeature
extends AbstractTempleFeature<OceanRuinFeatureConfig> {
    public OceanRuinFeature(Function<Dynamic<?>, ? extends OceanRuinFeatureConfig> function) {
        super(function);
    }

    @Override
    public String getName() {
        return "Ocean_Ruin";
    }

    @Override
    public int getRadius() {
        return 3;
    }

    @Override
    protected int getSpacing(ChunkGenerator<?> chunkGenerator) {
        return ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getOceanRuinSpacing();
    }

    @Override
    protected int getSeparation(ChunkGenerator<?> chunkGenerator) {
        return ((ChunkGeneratorConfig)chunkGenerator.getConfig()).getOceanRuinSeparation();
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    protected int getSeedModifier() {
        return 14357621;
    }

    public static enum BiomeType {
        WARM("warm"),
        COLD("cold");

        private static final Map<String, BiomeType> nameMap;
        private final String name;

        private BiomeType(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static BiomeType byName(String name) {
            return nameMap.get(name);
        }

        static {
            nameMap = Arrays.stream(BiomeType.values()).collect(Collectors.toMap(BiomeType::getName, biomeType -> biomeType));
        }
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> structureFeature, int i, int j, BlockBox blockBox, int k, long l) {
            super(structureFeature, i, j, blockBox, k, l);
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
            OceanRuinFeatureConfig oceanRuinFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.OCEAN_RUIN);
            int i = x * 16;
            int j = z * 16;
            BlockPos blockPos = new BlockPos(i, 90, j);
            BlockRotation blockRotation = BlockRotation.values()[this.random.nextInt(BlockRotation.values().length)];
            OceanRuinGenerator.addPieces(structureManager, blockPos, blockRotation, this.children, this.random, oceanRuinFeatureConfig);
            this.setBoundingBoxFromChildren();
        }
    }
}

