/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class RandomFeatureEntry {
    public static final Codec<RandomFeatureEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ConfiguredFeature.CODEC.fieldOf("feature")).forGetter(randomFeatureEntry -> randomFeatureEntry.feature), ((MapCodec)Codec.floatRange(0.0f, 1.0f).fieldOf("chance")).forGetter(randomFeatureEntry -> Float.valueOf(randomFeatureEntry.chance))).apply((Applicative<RandomFeatureEntry, ?>)instance, RandomFeatureEntry::new));
    public final Supplier<ConfiguredFeature<?, ?>> feature;
    public final float chance;

    public RandomFeatureEntry(ConfiguredFeature<?, ?> feature, float chance) {
        this(() -> feature, chance);
    }

    private RandomFeatureEntry(Supplier<ConfiguredFeature<?, ?>> supplier, float f) {
        this.feature = supplier;
        this.chance = f;
    }

    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos) {
        return this.feature.get().generate(structureWorldAccess, chunkGenerator, random, blockPos);
    }
}

