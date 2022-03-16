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
import net.minecraft.structure.StructureSet;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.placement.StructurePlacementType;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.ChunkRandom;
import org.jetbrains.annotations.Nullable;

public abstract class StructurePlacement {
    public static final Codec<StructurePlacement> TYPE_CODEC = Registry.STRUCTURE_PLACEMENT.getCodec().dispatch(StructurePlacement::getType, StructurePlacementType::codec);
    private static final int field_37775 = 10387320;
    private final Vec3i locateOffset;
    private final FrequencyReductionMethod frequencyReductionMethod;
    private final float frequency;
    private final int salt;
    private final Optional<class_7152> exclusionZone;

    protected static <S extends StructurePlacement> Products.P5<RecordCodecBuilder.Mu<S>, Vec3i, FrequencyReductionMethod, Float, Integer, Optional<class_7152>> method_41637(RecordCodecBuilder.Instance<S> instance) {
        return instance.group(Vec3i.createOffsetCodec(16).optionalFieldOf("locate_offset", Vec3i.ZERO).forGetter(StructurePlacement::getLocateOffset), FrequencyReductionMethod.CODEC.optionalFieldOf("frequency_reduction_method", FrequencyReductionMethod.DEFAULT).forGetter(StructurePlacement::getFrequencyReductionMethod), Codec.floatRange(0.0f, 1.0f).optionalFieldOf("frequency", Float.valueOf(1.0f)).forGetter(StructurePlacement::getFrequency), ((MapCodec)Codecs.NONNEGATIVE_INT.fieldOf("salt")).forGetter(StructurePlacement::getSalt), class_7152.field_37781.optionalFieldOf("exclusion_zone").forGetter(StructurePlacement::getExclusionZone));
    }

    protected StructurePlacement(Vec3i locateOffset, FrequencyReductionMethod frequencyReductionMethod, float frequency, int salt, Optional<class_7152> exclusionZone) {
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

    protected Optional<class_7152> getExclusionZone() {
        return this.exclusionZone;
    }

    public boolean method_41639(ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, long l, int i, int j) {
        if (!this.isStartChunk(chunkGenerator, noiseConfig, l, i, j)) {
            return false;
        }
        if (this.frequency < 1.0f && !this.frequencyReductionMethod.shouldGenerate(l, this.salt, i, j, this.frequency)) {
            return false;
        }
        return !this.exclusionZone.isPresent() || !this.exclusionZone.get().method_41648(chunkGenerator, noiseConfig, l, i, j);
    }

    protected abstract boolean isStartChunk(ChunkGenerator var1, NoiseConfig var2, long var3, int var5, int var6);

    public BlockPos method_41636(ChunkPos chunkPos) {
        return new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ()).add(this.getLocateOffset());
    }

    public abstract StructurePlacementType<?> getType();

    private static boolean defaultShouldGenerate(long seed, int regionX, int regionZ, int salt, float frequency) {
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
        chunkRandom.setRegionSeed(seed, regionX, regionZ, salt);
        return chunkRandom.nextFloat() < frequency;
    }

    private static boolean legacyType3ShouldGenerate(long seed, int i, int chunkX, int chunkZ, float frequency) {
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
        chunkRandom.setCarverSeed(seed, chunkX, chunkZ);
        return chunkRandom.nextDouble() < (double)frequency;
    }

    private static boolean legacyType2ShouldGenerate(long seed, int i, int regionX, int regionZ, float frequency) {
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
        chunkRandom.setRegionSeed(seed, regionX, regionZ, 10387320);
        return chunkRandom.nextFloat() < frequency;
    }

    private static boolean legacyType1ShouldGenerate(long seed, int i, int j, int k, float frequency) {
        int l = j >> 4;
        int m = k >> 4;
        ChunkRandom chunkRandom = new ChunkRandom(new AtomicSimpleRandom(0L));
        chunkRandom.setSeed((long)(l ^ m << 4) ^ seed);
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

        public boolean shouldGenerate(long seed, int i, int j, int k, float chance) {
            return this.generationPredicate.shouldGenerate(seed, i, j, k, chance);
        }

        @Nullable
        public static FrequencyReductionMethod get(String name) {
            for (FrequencyReductionMethod frequencyReductionMethod : FrequencyReductionMethod.values()) {
                if (!frequencyReductionMethod.name.equals(name)) continue;
                return frequencyReductionMethod;
            }
            return null;
        }

        @Override
        public String asString() {
            return this.name;
        }

        static {
            CODEC = StringIdentifiable.createCodec(FrequencyReductionMethod::values, FrequencyReductionMethod::get);
        }
    }

    @Deprecated
    public record class_7152(RegistryEntry<StructureSet> otherSet, int chunkCount) {
        public static final Codec<class_7152> field_37781 = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)RegistryElementCodec.of(Registry.STRUCTURE_SET_KEY, StructureSet.CODEC, false).fieldOf("other_set")).forGetter(class_7152::otherSet), ((MapCodec)Codec.intRange(1, 16).fieldOf("chunk_count")).forGetter(class_7152::chunkCount)).apply((Applicative<class_7152, ?>)instance, class_7152::new));

        boolean method_41648(ChunkGenerator chunkGenerator, NoiseConfig noiseConfig, long l, int i, int j) {
            return chunkGenerator.method_41053(this.otherSet, noiseConfig, l, i, j, this.chunkCount);
        }
    }

    @FunctionalInterface
    public static interface GenerationPredicate {
        public boolean shouldGenerate(long var1, int var3, int var4, int var5, float var6);
    }
}

