/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import org.apache.commons.lang3.mutable.MutableBoolean;

public record PlacedFeature(RegistryEntry<ConfiguredFeature<?, ?>> feature, List<PlacementModifier> placementModifiers) {
    public static final Codec<PlacedFeature> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature")).forGetter(placedFeature -> placedFeature.feature), ((MapCodec)PlacementModifier.CODEC.listOf().fieldOf("placement")).forGetter(placedFeature -> placedFeature.placementModifiers)).apply((Applicative<PlacedFeature, ?>)instance, PlacedFeature::new));
    public static final Codec<RegistryEntry<PlacedFeature>> REGISTRY_CODEC = RegistryElementCodec.of(RegistryKeys.PLACED_FEATURE, CODEC);
    public static final Codec<RegistryEntryList<PlacedFeature>> LIST_CODEC = RegistryCodecs.entryList(RegistryKeys.PLACED_FEATURE, CODEC);
    public static final Codec<List<RegistryEntryList<PlacedFeature>>> LISTS_CODEC = RegistryCodecs.entryList(RegistryKeys.PLACED_FEATURE, CODEC, true).listOf();

    public boolean generateUnregistered(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos) {
        return this.generate(new FeaturePlacementContext(world, generator, Optional.empty()), random, pos);
    }

    /**
     * Generates a configured feature at the positions obtained by applying
     * placement modifiers to the given position.
     * 
     * <p>To calculate positions, it first creates a singleton stream of the
     * given position, then it applies placement modifiers with {@linkplain
     * Stream#flatMap flatMap} in order they appear in the list.
     */
    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos) {
        return this.generate(new FeaturePlacementContext(world, generator, Optional.of(this)), random, pos);
    }

    private boolean generate(FeaturePlacementContext context, Random random, BlockPos pos2) {
        Stream<BlockPos> stream = Stream.of(pos2);
        for (PlacementModifier placementModifier : this.placementModifiers) {
            stream = stream.flatMap(pos -> placementModifier.getPositions(context, random, (BlockPos)pos));
        }
        ConfiguredFeature<?, ?> configuredFeature = this.feature.value();
        MutableBoolean mutableBoolean = new MutableBoolean();
        stream.forEach(blockPos -> {
            if (configuredFeature.generate(context.getWorld(), context.getChunkGenerator(), random, (BlockPos)blockPos)) {
                mutableBoolean.setTrue();
            }
        });
        return mutableBoolean.isTrue();
    }

    public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
        return this.feature.value().getDecoratedFeatures();
    }

    @Override
    public String toString() {
        return "Placed " + this.feature;
    }
}

