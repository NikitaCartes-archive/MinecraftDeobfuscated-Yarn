/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.DecoratorContext;
import net.minecraft.world.gen.decorator.PlacementModifier;
import net.minecraft.world.gen.decorator.PlacementModifierType;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class HeightRangePlacementModifier
extends PlacementModifier {
    public static final Codec<HeightRangePlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)HeightProvider.CODEC.fieldOf("height")).forGetter(heightRangePlacementModifier -> heightRangePlacementModifier.height)).apply((Applicative<HeightRangePlacementModifier, ?>)instance, HeightRangePlacementModifier::new));
    private final HeightProvider height;

    private HeightRangePlacementModifier(HeightProvider height) {
        this.height = height;
    }

    public static HeightRangePlacementModifier of(HeightProvider height) {
        return new HeightRangePlacementModifier(height);
    }

    public static HeightRangePlacementModifier uniform(YOffset minOffset, YOffset maxOffset) {
        return HeightRangePlacementModifier.of(UniformHeightProvider.create(minOffset, maxOffset));
    }

    public static HeightRangePlacementModifier trapezoid(YOffset minOffset, YOffset maxOffset) {
        return HeightRangePlacementModifier.of(TrapezoidHeightProvider.create(minOffset, maxOffset));
    }

    @Override
    public Stream<BlockPos> getPositions(DecoratorContext context, Random random, BlockPos pos) {
        return Stream.of(pos.withY(this.height.get(random, context)));
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.HEIGHT_RANGE;
    }
}

