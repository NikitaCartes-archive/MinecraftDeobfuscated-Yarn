/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.class_6834;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructurePiecesGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class OceanRuinFeature
extends StructureFeature<OceanRuinFeatureConfig> {
    public OceanRuinFeature(Codec<OceanRuinFeatureConfig> configCodec) {
        super(configCodec, class_6834.simple(class_6834.checkForBiomeOnTop(Heightmap.Type.OCEAN_FLOOR_WG), OceanRuinFeature::addPieces));
    }

    private static void addPieces(StructurePiecesCollector collector, StructurePiecesGenerator.Context<OceanRuinFeatureConfig> context) {
        BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
        BlockRotation blockRotation = BlockRotation.random(context.random());
        OceanRuinGenerator.addPieces(context.structureManager(), blockPos, blockRotation, collector, context.random(), context.config());
    }

    public static enum BiomeType implements StringIdentifiable
    {
        WARM("warm"),
        COLD("cold");

        public static final Codec<BiomeType> CODEC;
        private static final Map<String, BiomeType> BY_NAME;
        private final String name;

        private BiomeType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Nullable
        public static BiomeType byName(String name) {
            return BY_NAME.get(name);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(BiomeType::values, BiomeType::byName);
            BY_NAME = Arrays.stream(BiomeType.values()).collect(Collectors.toMap(BiomeType::getName, biomeType -> biomeType));
        }
    }
}

