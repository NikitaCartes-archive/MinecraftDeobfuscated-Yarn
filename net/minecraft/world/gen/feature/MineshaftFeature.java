/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.structure.MineshaftGenerator;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

public class MineshaftFeature
extends StructureFeature<MineshaftFeatureConfig> {
    public MineshaftFeature(Function<Dynamic<?>, ? extends MineshaftFeatureConfig> configFactory) {
        super(configFactory);
    }

    @Override
    public boolean shouldStartAt(BiomeAccess biomeAccess, ChunkGenerator<?> chunkGenerator, Random random, int chunkZ, int i, Biome biome) {
        ((ChunkRandom)random).setStructureSeed(chunkGenerator.getSeed(), chunkZ, i);
        if (chunkGenerator.hasStructure(biome, this)) {
            MineshaftFeatureConfig mineshaftFeatureConfig = chunkGenerator.getStructureConfig(biome, this);
            double d = mineshaftFeatureConfig.probability;
            return random.nextDouble() < d;
        }
        return false;
    }

    @Override
    public StructureFeature.StructureStartFactory getStructureStartFactory() {
        return Start::new;
    }

    @Override
    public String getName() {
        return "Mineshaft";
    }

    @Override
    public int getRadius() {
        return 8;
    }

    public static class Start
    extends StructureStart {
        public Start(StructureFeature<?> structureFeature, int chunkX, int chunkZ, BlockBox blockBox, int i, long l) {
            super(structureFeature, chunkX, chunkZ, blockBox, i, l);
        }

        @Override
        public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
            MineshaftFeatureConfig mineshaftFeatureConfig = chunkGenerator.getStructureConfig(biome, Feature.MINESHAFT);
            MineshaftGenerator.MineshaftRoom mineshaftRoom = new MineshaftGenerator.MineshaftRoom(0, this.random, (x << 4) + 2, (z << 4) + 2, mineshaftFeatureConfig.type);
            this.children.add(mineshaftRoom);
            mineshaftRoom.method_14918(mineshaftRoom, this.children, this.random);
            this.setBoundingBoxFromChildren();
            if (mineshaftFeatureConfig.type == Type.MESA) {
                int i = -5;
                int j = chunkGenerator.getSeaLevel() - this.boundingBox.maxY + this.boundingBox.getBlockCountY() / 2 - -5;
                this.boundingBox.offset(0, j, 0);
                for (StructurePiece structurePiece : this.children) {
                    structurePiece.translate(0, j, 0);
                }
            } else {
                this.method_14978(chunkGenerator.getSeaLevel(), this.random, 10);
            }
        }
    }

    public static enum Type {
        NORMAL("normal"),
        MESA("mesa");

        private static final Map<String, Type> nameMap;
        private final String name;

        private Type(String string2) {
            this.name = string2;
        }

        public String getName() {
            return this.name;
        }

        public static Type byName(String nam) {
            return nameMap.get(nam);
        }

        public static Type byIndex(int index) {
            if (index < 0 || index >= Type.values().length) {
                return NORMAL;
            }
            return Type.values()[index];
        }

        static {
            nameMap = Arrays.stream(Type.values()).collect(Collectors.toMap(Type::getName, type -> type));
        }
    }
}

