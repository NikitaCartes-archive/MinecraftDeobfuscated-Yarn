/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.placementmodifier;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.FeaturePlacementContext;
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifierType;

public class SurfaceThresholdFilterPlacementModifier
extends AbstractConditionalPlacementModifier {
    public static final Codec<SurfaceThresholdFilterPlacementModifier> MODIFIER_CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Heightmap.Type.CODEC.fieldOf("heightmap")).forGetter(surfaceThresholdFilterPlacementModifier -> surfaceThresholdFilterPlacementModifier.heightmap), Codec.INT.optionalFieldOf("min_inclusive", Integer.MIN_VALUE).forGetter(surfaceThresholdFilterPlacementModifier -> surfaceThresholdFilterPlacementModifier.min), Codec.INT.optionalFieldOf("max_inclusive", Integer.MAX_VALUE).forGetter(surfaceThresholdFilterPlacementModifier -> surfaceThresholdFilterPlacementModifier.max)).apply((Applicative<SurfaceThresholdFilterPlacementModifier, ?>)instance, SurfaceThresholdFilterPlacementModifier::new));
    private final Heightmap.Type heightmap;
    private final int min;
    private final int max;

    private SurfaceThresholdFilterPlacementModifier(Heightmap.Type heightmap, int min, int max) {
        this.heightmap = heightmap;
        this.min = min;
        this.max = max;
    }

    public static SurfaceThresholdFilterPlacementModifier of(Heightmap.Type heightmap, int min, int max) {
        return new SurfaceThresholdFilterPlacementModifier(heightmap, min, max);
    }

    @Override
    protected boolean shouldPlace(FeaturePlacementContext context, AbstractRandom random, BlockPos pos) {
        long l = context.getTopY(this.heightmap, pos.getX(), pos.getZ());
        long m = l + (long)this.min;
        long n = l + (long)this.max;
        return m <= (long)pos.getY() && (long)pos.getY() <= n;
    }

    @Override
    public PlacementModifierType<?> getType() {
        return PlacementModifierType.SURFACE_RELATIVE_THRESHOLD_FILTER;
    }
}

