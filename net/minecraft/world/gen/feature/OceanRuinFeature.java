/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.StructureFeature;

public class OceanRuinFeature
extends StructureFeature {
    public static final Codec<OceanRuinFeature> CODEC = RecordCodecBuilder.create(instance -> instance.group(OceanRuinFeature.configCodecBuilder(instance), ((MapCodec)BiomeType.CODEC.fieldOf("biome_temp")).forGetter(oceanRuinFeature -> oceanRuinFeature.field_37808), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("large_probability")).forGetter(oceanRuinFeature -> Float.valueOf(oceanRuinFeature.field_37809)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("cluster_probability")).forGetter(oceanRuinFeature -> Float.valueOf(oceanRuinFeature.field_37810))).apply((Applicative<OceanRuinFeature, ?>)instance, OceanRuinFeature::new));
    public final BiomeType field_37808;
    public final float field_37809;
    public final float field_37810;

    public OceanRuinFeature(StructureFeature.Config config, BiomeType biomeType, float f, float g) {
        super(config);
        this.field_37808 = biomeType;
        this.field_37809 = f;
        this.field_37810 = g;
    }

    @Override
    public Optional<StructureFeature.StructurePosition> getStructurePosition(StructureFeature.Context context) {
        return OceanRuinFeature.getStructurePosition(context, Heightmap.Type.OCEAN_FLOOR_WG, structurePiecesCollector -> this.addPieces((StructurePiecesCollector)structurePiecesCollector, context));
    }

    private void addPieces(StructurePiecesCollector structurePiecesCollector, StructureFeature.Context context) {
        BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
        BlockRotation blockRotation = BlockRotation.random(context.random());
        OceanRuinGenerator.addPieces(context.structureManager(), blockPos, blockRotation, structurePiecesCollector, context.random(), this);
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
        private final String name;

        private BiomeType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(BiomeType::values);
        }
    }
}

