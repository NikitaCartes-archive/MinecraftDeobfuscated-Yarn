/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeatureConfig;

public class RandomFeatureEntry<FC extends FeatureConfig> {
    public static final Codec<RandomFeatureEntry<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ConfiguredFeature.field_24833.fieldOf("feature")).forGetter(randomFeatureEntry -> randomFeatureEntry.feature), ((MapCodec)Codec.FLOAT.fieldOf("chance")).forGetter(randomFeatureEntry -> Float.valueOf(randomFeatureEntry.chance))).apply((Applicative<RandomFeatureEntry, ?>)instance, RandomFeatureEntry::new));
    public final ConfiguredFeature<FC, ?> feature;
    public final float chance;

    public RandomFeatureEntry(ConfiguredFeature<FC, ?> feature, float chance) {
        this.feature = feature;
        this.chance = chance;
    }

    public boolean generate(ServerWorldAccess serverWorldAccess, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos) {
        return this.feature.generate(serverWorldAccess, structureAccessor, chunkGenerator, random, blockPos);
    }
}

