/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.gen.noise.NoiseType;

public record class_7056(NoiseType barrierNoise, NoiseType fluidLevelFloodednessNoise, NoiseType fluidLevelSpreadNoise, NoiseType lavaNoise, NoiseType temperature, NoiseType vegetation, NoiseType continents, NoiseType erosion, NoiseType depth, NoiseType ridges, NoiseType initialDensityWithoutJaggedness, NoiseType finalDensity, NoiseType veinToggle, NoiseType veinRidged, NoiseType veinGap) {
    public static final Codec<class_7056> field_37137 = RecordCodecBuilder.create(instance -> instance.group(class_7056.method_41125("barrier", class_7056::barrierNoise), class_7056.method_41125("fluid_level_floodedness", class_7056::fluidLevelFloodednessNoise), class_7056.method_41125("fluid_level_spread", class_7056::fluidLevelSpreadNoise), class_7056.method_41125("lava", class_7056::lavaNoise), class_7056.method_41125("temperature", class_7056::temperature), class_7056.method_41125("vegetation", class_7056::vegetation), class_7056.method_41125("continents", class_7056::continents), class_7056.method_41125("erosion", class_7056::erosion), class_7056.method_41125("depth", class_7056::depth), class_7056.method_41125("ridges", class_7056::ridges), class_7056.method_41125("initial_density_without_jaggedness", class_7056::initialDensityWithoutJaggedness), class_7056.method_41125("final_density", class_7056::finalDensity), class_7056.method_41125("vein_toggle", class_7056::veinToggle), class_7056.method_41125("vein_ridged", class_7056::veinRidged), class_7056.method_41125("vein_gap", class_7056::veinGap)).apply((Applicative<class_7056, ?>)instance, class_7056::new));

    private static RecordCodecBuilder<class_7056, NoiseType> method_41125(String string, Function<class_7056, NoiseType> function) {
        return ((MapCodec)NoiseType.field_37059.fieldOf(string)).forGetter(function);
    }

    public class_7056 method_41124(NoiseType.class_6915 arg) {
        return new class_7056(this.barrierNoise.method_40469(arg), this.fluidLevelFloodednessNoise.method_40469(arg), this.fluidLevelSpreadNoise.method_40469(arg), this.lavaNoise.method_40469(arg), this.temperature.method_40469(arg), this.vegetation.method_40469(arg), this.continents.method_40469(arg), this.erosion.method_40469(arg), this.depth.method_40469(arg), this.ridges.method_40469(arg), this.initialDensityWithoutJaggedness.method_40469(arg), this.finalDensity.method_40469(arg), this.veinToggle.method_40469(arg), this.veinRidged.method_40469(arg), this.veinGap.method_40469(arg));
    }
}

