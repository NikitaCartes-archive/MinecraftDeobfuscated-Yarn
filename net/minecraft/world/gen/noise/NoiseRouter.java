/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.noise;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record NoiseRouter(DensityFunction barrierNoise, DensityFunction fluidLevelFloodednessNoise, DensityFunction fluidLevelSpreadNoise, DensityFunction lavaNoise, DensityFunction temperature, DensityFunction vegetation, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction initialDensityWithoutJaggedness, DensityFunction finalDensity, DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap) {
    public static final Codec<NoiseRouter> field_37683 = RecordCodecBuilder.create(instance -> instance.group(NoiseRouter.method_41545("barrier", NoiseRouter::barrierNoise), NoiseRouter.method_41545("fluid_level_floodedness", NoiseRouter::fluidLevelFloodednessNoise), NoiseRouter.method_41545("fluid_level_spread", NoiseRouter::fluidLevelSpreadNoise), NoiseRouter.method_41545("lava", NoiseRouter::lavaNoise), NoiseRouter.method_41545("temperature", NoiseRouter::temperature), NoiseRouter.method_41545("vegetation", NoiseRouter::vegetation), NoiseRouter.method_41545("continents", NoiseRouter::continents), NoiseRouter.method_41545("erosion", NoiseRouter::erosion), NoiseRouter.method_41545("depth", NoiseRouter::depth), NoiseRouter.method_41545("ridges", NoiseRouter::ridges), NoiseRouter.method_41545("initial_density_without_jaggedness", NoiseRouter::initialDensityWithoutJaggedness), NoiseRouter.method_41545("final_density", NoiseRouter::finalDensity), NoiseRouter.method_41545("vein_toggle", NoiseRouter::veinToggle), NoiseRouter.method_41545("vein_ridged", NoiseRouter::veinRidged), NoiseRouter.method_41545("vein_gap", NoiseRouter::veinGap)).apply((Applicative<NoiseRouter, ?>)instance, NoiseRouter::new));

    private static RecordCodecBuilder<NoiseRouter, DensityFunction> method_41545(String string, Function<NoiseRouter, DensityFunction> function) {
        return ((MapCodec)DensityFunction.FUNCTION_CODEC.fieldOf(string)).forGetter(function);
    }

    public NoiseRouter method_41544(DensityFunction.DensityFunctionVisitor densityFunctionVisitor) {
        return new NoiseRouter(this.barrierNoise.apply(densityFunctionVisitor), this.fluidLevelFloodednessNoise.apply(densityFunctionVisitor), this.fluidLevelSpreadNoise.apply(densityFunctionVisitor), this.lavaNoise.apply(densityFunctionVisitor), this.temperature.apply(densityFunctionVisitor), this.vegetation.apply(densityFunctionVisitor), this.continents.apply(densityFunctionVisitor), this.erosion.apply(densityFunctionVisitor), this.depth.apply(densityFunctionVisitor), this.ridges.apply(densityFunctionVisitor), this.initialDensityWithoutJaggedness.apply(densityFunctionVisitor), this.finalDensity.apply(densityFunctionVisitor), this.veinToggle.apply(densityFunctionVisitor), this.veinRidged.apply(densityFunctionVisitor), this.veinGap.apply(densityFunctionVisitor));
    }
}

