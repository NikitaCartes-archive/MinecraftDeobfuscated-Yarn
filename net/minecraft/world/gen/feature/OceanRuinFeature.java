/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureSpawns;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

public class OceanRuinFeature
extends StructureFeature {
    public static final Codec<OceanRuinFeature> CODEC = RecordCodecBuilder.create(instance -> OceanRuinFeature.method_41608(instance).and(instance.group(((MapCodec)BiomeType.CODEC.fieldOf("biome_temp")).forGetter(oceanRuinFeature -> oceanRuinFeature.field_37808), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("large_probability")).forGetter(oceanRuinFeature -> Float.valueOf(oceanRuinFeature.field_37809)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("cluster_probability")).forGetter(oceanRuinFeature -> Float.valueOf(oceanRuinFeature.field_37810)))).apply((Applicative<OceanRuinFeature, ?>)instance, OceanRuinFeature::new));
    public final BiomeType field_37808;
    public final float field_37809;
    public final float field_37810;

    public OceanRuinFeature(RegistryEntryList<Biome> registryEntryList, Map<SpawnGroup, StructureSpawns> map, GenerationStep.Feature feature, boolean bl, BiomeType biomeType, float f, float g) {
        super(registryEntryList, map, feature, bl);
        this.field_37808 = biomeType;
        this.field_37809 = f;
        this.field_37810 = g;
    }

    @Override
    public Optional<StructureFeature.class_7150> method_38676(StructureFeature.class_7149 arg) {
        return OceanRuinFeature.method_41612(arg, Heightmap.Type.OCEAN_FLOOR_WG, structurePiecesCollector -> this.addPieces((StructurePiecesCollector)structurePiecesCollector, arg));
    }

    private void addPieces(StructurePiecesCollector structurePiecesCollector, StructureFeature.class_7149 arg) {
        BlockPos blockPos = new BlockPos(arg.chunkPos().getStartX(), 90, arg.chunkPos().getStartZ());
        BlockRotation blockRotation = BlockRotation.random(arg.random());
        OceanRuinGenerator.addPieces(arg.structureTemplateManager(), blockPos, blockRotation, structurePiecesCollector, arg.random(), this);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.OCEAN_RUIN;
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

