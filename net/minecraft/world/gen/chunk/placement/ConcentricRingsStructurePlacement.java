/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk.placement;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryCodecs;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacement;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.noise.NoiseConfig;

public class ConcentricRingsStructurePlacement
extends StructurePlacement {
    public static final Codec<ConcentricRingsStructurePlacement> CODEC = RecordCodecBuilder.create(instance -> ConcentricRingsStructurePlacement.method_41629(instance).apply((Applicative<RecordCodecBuilder.Mu<ConcentricRingsStructurePlacement>, ?>)instance, ConcentricRingsStructurePlacement::new));
    private final int field_37768;
    private final int field_37769;
    private final int structureCount;
    private final RegistryEntryList<Biome> biasedToBiomes;

    private static Products.P9<RecordCodecBuilder.Mu<ConcentricRingsStructurePlacement>, Vec3i, StructurePlacement.FrequencyReductionMethod, Float, Integer, Optional<StructurePlacement.class_7152>, Integer, Integer, Integer, RegistryEntryList<Biome>> method_41629(RecordCodecBuilder.Instance<ConcentricRingsStructurePlacement> instance) {
        Products.P5<RecordCodecBuilder.Mu<ConcentricRingsStructurePlacement>, Vec3i, StructurePlacement.FrequencyReductionMethod, Float, Integer, Optional<StructurePlacement.class_7152>> p5 = ConcentricRingsStructurePlacement.method_41637(instance);
        Products.P4<ConcentricRingsStructurePlacement, Integer, Integer, Integer, RegistryEntryList> p4 = instance.group(((MapCodec)Codec.intRange(0, 1023).fieldOf("distance")).forGetter(ConcentricRingsStructurePlacement::method_41627), ((MapCodec)Codec.intRange(0, 1023).fieldOf("spread")).forGetter(ConcentricRingsStructurePlacement::method_41628), ((MapCodec)Codec.intRange(1, 4095).fieldOf("count")).forGetter(ConcentricRingsStructurePlacement::getStructureCount), ((MapCodec)RegistryCodecs.entryList(Registry.BIOME_KEY).fieldOf("preferred_biomes")).forGetter(ConcentricRingsStructurePlacement::getBiasedToBiomes));
        return new Products.P9<ConcentricRingsStructurePlacement, Vec3i, StructurePlacement.FrequencyReductionMethod, Float, Integer, Optional<StructurePlacement.class_7152>, Integer, Integer, Integer, RegistryEntryList>(p5.t1(), p5.t2(), p5.t3(), p5.t4(), p5.t5(), p4.t1(), p4.t2(), p4.t3(), p4.t4());
    }

    public ConcentricRingsStructurePlacement(Vec3i locateOffset, StructurePlacement.FrequencyReductionMethod generationPredicateType, float f, int i, Optional<StructurePlacement.class_7152> optional, int j, int k, int structureCount, RegistryEntryList<Biome> biasedToBiomes) {
        super(locateOffset, generationPredicateType, f, i, optional);
        this.field_37768 = j;
        this.field_37769 = k;
        this.structureCount = structureCount;
        this.biasedToBiomes = biasedToBiomes;
    }

    public ConcentricRingsStructurePlacement(int i, int j, int structureCount, RegistryEntryList<Biome> biasedToBiomes) {
        this(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0f, 0, Optional.empty(), i, j, structureCount, biasedToBiomes);
    }

    public int method_41627() {
        return this.field_37768;
    }

    public int method_41628() {
        return this.field_37769;
    }

    public int getStructureCount() {
        return this.structureCount;
    }

    public RegistryEntryList<Biome> getBiasedToBiomes() {
        return this.biasedToBiomes;
    }

    @Override
    protected boolean isStartChunk(ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, long l, int i, int j) {
        List<ChunkPos> list = chunkGenerator.getConcentricRingsStartChunks(this, noiseConfig);
        if (list == null) {
            return false;
        }
        return list.contains(new ChunkPos(i, j));
    }

    @Override
    public StructurePlacementType<?> getType() {
        return StructurePlacementType.CONCENTRIC_RINGS;
    }
}

