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
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.PlacementModifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class PlacedFeature {
    public static final Codec<PlacedFeature> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature")).forGetter(placedFeature -> placedFeature.feature), ((MapCodec)PlacementModifier.CODEC.listOf().fieldOf("placement")).forGetter(placedFeature -> placedFeature.placementModifiers)).apply((Applicative<PlacedFeature, ?>)instance, PlacedFeature::new));
    public static final Codec<Supplier<PlacedFeature>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.PLACED_FEATURE_KEY, CODEC);
    public static final Codec<List<Supplier<PlacedFeature>>> LIST_CODEC = RegistryElementCodec.method_31194(Registry.PLACED_FEATURE_KEY, CODEC);
    private final Supplier<ConfiguredFeature<?, ?>> feature;
    private final List<PlacementModifier> placementModifiers;

    public PlacedFeature(Supplier<ConfiguredFeature<?, ?>> feature, List<PlacementModifier> placementModifiers) {
        this.feature = feature;
        this.placementModifiers = placementModifiers;
    }

    public boolean generateUnregistered(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos) {
        return this.generate(new DecoratorContext(world, generator, Optional.empty()), random, pos);
    }

    public boolean generate(StructureWorldAccess world, ChunkGenerator generator, Random random, BlockPos pos) {
        return this.generate(new DecoratorContext(world, generator, Optional.of(this)), random, pos);
    }

    private boolean generate(DecoratorContext context, Random random, BlockPos pos) {
        Stream<BlockPos> stream = Stream.of(pos);
        for (PlacementModifier placementModifier : this.placementModifiers) {
            stream = stream.flatMap(blockPos -> placementModifier.getPositions(context, random, (BlockPos)blockPos));
        }
        ConfiguredFeature<?, ?> configuredFeature = this.feature.get();
        MutableBoolean mutableBoolean = new MutableBoolean();
        stream.forEach(blockPos -> {
            if (configuredFeature.generate(context.getWorld(), context.getChunkGenerator(), random, (BlockPos)blockPos)) {
                mutableBoolean.setTrue();
            }
        });
        return mutableBoolean.isTrue();
    }

    public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
        return this.feature.get().getDecoratedFeatures();
    }

    public String toString() {
        return "Placed " + Registry.FEATURE.getId((Feature<?>)this.feature.get().getFeature());
    }
}

