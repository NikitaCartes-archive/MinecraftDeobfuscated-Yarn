/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.structure;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.structure.OceanRuinGenerator;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

public class OceanRuinStructure
extends Structure {
    public static final Codec<OceanRuinStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(OceanRuinStructure.configCodecBuilder(instance), ((MapCodec)BiomeTemperature.CODEC.fieldOf("biome_temp")).forGetter(structure -> structure.biomeTemperature), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("large_probability")).forGetter(structure -> Float.valueOf(structure.largeProbability)), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("cluster_probability")).forGetter(structure -> Float.valueOf(structure.clusterProbability))).apply((Applicative<OceanRuinStructure, ?>)instance, OceanRuinStructure::new));
    public final BiomeTemperature biomeTemperature;
    public final float largeProbability;
    public final float clusterProbability;

    public OceanRuinStructure(Structure.Config config, BiomeTemperature biomeTemperature, float largeProbability, float clusterProbability) {
        super(config);
        this.biomeTemperature = biomeTemperature;
        this.largeProbability = largeProbability;
        this.clusterProbability = clusterProbability;
    }

    @Override
    public Optional<Structure.StructurePosition> getStructurePosition(Structure.Context context) {
        return OceanRuinStructure.getStructurePosition(context, Heightmap.Type.OCEAN_FLOOR_WG, collector -> this.addPieces((StructurePiecesCollector)collector, context));
    }

    private void addPieces(StructurePiecesCollector collector, Structure.Context context) {
        BlockPos blockPos = new BlockPos(context.chunkPos().getStartX(), 90, context.chunkPos().getStartZ());
        BlockRotation blockRotation = BlockRotation.random(context.random());
        OceanRuinGenerator.addPieces(context.structureTemplateManager(), blockPos, blockRotation, collector, context.random(), this);
    }

    @Override
    public StructureType<?> getType() {
        return StructureType.OCEAN_RUIN;
    }

    public static enum BiomeTemperature implements StringIdentifiable
    {
        WARM("warm"),
        COLD("cold");

        public static final Codec<BiomeTemperature> CODEC;
        private final String name;

        private BiomeTemperature(String name) {
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
            CODEC = StringIdentifiable.createCodec(BiomeTemperature::values);
        }
    }
}

