/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.chunk.placement;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.CheckedRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.gen.chunk.placement.StructurePlacementCalculator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;

public abstract class StructurePlacement {
    public static final Codec<StructurePlacement> TYPE_CODEC = Registries.STRUCTURE_PLACEMENT.getCodec().dispatch(StructurePlacement::getType, StructurePlacementType::codec);
    private static final int ARBITRARY_SALT = 10387320;
    private final Vec3i locateOffset;
    private final FrequencyReductionMethod frequencyReductionMethod;
    private final float frequency;
    private final int salt;
    private final Optional<ExclusionZone> exclusionZone;

    protected static <S extends StructurePlacement> Products.P5<RecordCodecBuilder.Mu<S>, Vec3i, FrequencyReductionMethod, Float, Integer, Optional<ExclusionZone>> buildCodec(RecordCodecBuilder.Instance<S> instance) {
        return instance.group(Vec3i.createOffsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(StructurePlacement::getLocateOffset), FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", FrequencyReductionMethod.DEFAULT).forGetter(StructurePlacement::getFrequencyReductionMethod), Codec.floatRange(0.0f, 1.0f).optionalFieldOf("frequency", Float.valueOf(1.0f)).forGetter(StructurePlacement::getFrequency), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("salt")).forGetter(StructurePlacement::getSalt), ExclusionZone.CODEC.optionalFieldOf("exclusion_zone").forGetter(StructurePlacement::getExclusionZone));
    }

    protected StructurePlacement(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<ExclusionZone> exclusionZone) {
        this.locateOffset = locateOffset;
        this.frequencyReductionMethod = frequencyReductionMethod;
        this.frequency = frequency;
        this.salt = salt;
        this.exclusionZone = exclusionZone;
    }

    protected Vec3i getLocateOffset() {
        return this.locateOffset;
    }

    protected FrequencyReductionMethod getFrequencyReductionMethod() {
        return this.frequencyReductionMethod;
    }

    protected float getFrequency() {
        return this.frequency;
    }

    protected int getSalt() {
        return this.salt;
    }

    protected Optional<ExclusionZone> getExclusionZone() {
        return this.exclusionZone;
    }

    public boolean shouldGenerate(StructurePlacementCalculator calculator, int chunkX, int chunkZ) {
        if (!this.isStartChunk(calculator, chunkX, chunkZ)) {
            return false;
        }
        if (this.frequency < 1.0f && !this.frequencyReductionMethod.shouldGenerate(calculator.getStructureSeed(), this.salt, chunkX, chunkZ, this.frequency)) {
            return false;
        }
        return !this.exclusionZone.isPresent() || !this.exclusionZone.get().shouldExclude(calculator, chunkX, chunkZ);
    }

    protected abstract boolean isStartChunk(StructurePlacementCalculator var1, int var2, int var3);

    public BlockPos getLocatePos(ChunkPos chunkPos) {
        return new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ()).add(this.getLocateOffset());
    }

    public abstract StructurePlacementType<?> getType();

    private static boolean defaultShouldGenerate(long seed, int salt, int chunkX, int chunkZ, float frequency) {
        ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
        chunkRandom.setRegionSeed(seed, salt, chunkX, chunkZ);
        return chunkRandom.nextFloat() < frequency;
    }

    private static boolean legacyType3ShouldGenerate(long seed, int salt, int chunkX, int chunkZ, float frequency) {
        ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
        chunkRandom.setCarverSeed(seed, chunkX, chunkZ);
        return chunkRandom.nextDouble() < (double)frequency;
    }

    private static boolean legacyType2ShouldGenerate(long seed, int salt, int chunkX, int chunkZ, float frequency) {
        ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
        chunkRandom.setRegionSeed(seed, chunkX, chunkZ, 10387320);
        return chunkRandom.nextFloat() < frequency;
    }

    private static boolean legacyType1ShouldGenerate(long seed, int salt, int chunkX, int chunkZ, float frequency) {
        int i = chunkX >> 4;
        int j = chunkZ >> 4;
        ChunkRandom chunkRandom = new ChunkRandom(new CheckedRandom(0L));
        chunkRandom.setSeed((long)(i ^ j << 4) ^ seed);
        chunkRandom.nextInt();
        return chunkRandom.nextInt((int)(1.0f / frequency)) == 0;
    }

    public static enum FrequencyReductionMethod implements StringIdentifiable
    {
        DEFAULT("default", StructurePlacement::defaultShouldGenerate),
        LEGACY_TYPE_1("legacy_type_1", StructurePlacement::legacyType1ShouldGenerate),
        LEGACY_TYPE_2("legacy_type_2", StructurePlacement::legacyType2ShouldGenerate),
        LEGACY_TYPE_3("legacy_type_3", StructurePlacement::legacyType3ShouldGenerate);

        public static final Codec<FrequencyReductionMethod> CODEC;
        private final String name;
        private final GenerationPredicate generationPredicate;

        private FrequencyReductionMethod(String name, GenerationPredicate generationPredicate) {
            this.name = name;
            this.generationPredicate = generationPredicate;
        }

        public boolean shouldGenerate(long seed, int salt, int chunkX, int chunkZ, float chance) {
            return this.generationPredicate.shouldGenerate(seed, salt, chunkX, chunkZ, chance);
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(FrequencyReductionMethod::values);
        }
    }

    @Deprecated
    public record ExclusionZone(RegistryEntry<StructureSet> otherSet, int chunkCount) {
        public static final Codec<ExclusionZone> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)RegistryElementCodec.of(RegistryKeys.STRUCTURE_SET_WORLDGEN, StructureSet.CODEC, false).fieldOf("other_set")).forGetter(ExclusionZone::otherSet), ((MapCodec)Codec.intRange(1, 16).fieldOf("chunk_count")).forGetter(ExclusionZone::chunkCount)).apply((Applicative<ExclusionZone, ?>)instance, ExclusionZone::new));

        boolean shouldExclude(StructurePlacementCalculator calculator, int centerChunkX, int centerChunkZ) {
            return calculator.canGenerate(this.otherSet, centerChunkX, centerChunkZ, this.chunkCount);
        }
    }

    @FunctionalInterface
    public static interface GenerationPredicate {
        public boolean shouldGenerate(long var1, int var3, int var4, int var5, float var6);
    }
}

