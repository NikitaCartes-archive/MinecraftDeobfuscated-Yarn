/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.class_6622;
import net.minecraft.class_6626;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class OceanRuinFeature
extends StructureFeature<OceanRuinFeatureConfig> {
    public OceanRuinFeature(Codec<OceanRuinFeatureConfig> codec) {
        super(codec, OceanRuinFeature::method_38700);
    }

    private static void method_38700(class_6626 arg, OceanRuinFeatureConfig oceanRuinFeatureConfig, class_6622.class_6623 arg2) {
        if (!arg2.method_38707(Heightmap.Type.OCEAN_FLOOR_WG)) {
            return;
        }
        BlockPos blockPos = new BlockPos(arg2.chunkPos().getStartX(), 90, arg2.chunkPos().getStartZ());
        BlockRotation blockRotation = BlockRotation.random(arg2.random());
        OceanRuinGenerator.addPieces(arg2.structureManager(), blockPos, blockRotation, arg, arg2.random(), oceanRuinFeatureConfig);
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

