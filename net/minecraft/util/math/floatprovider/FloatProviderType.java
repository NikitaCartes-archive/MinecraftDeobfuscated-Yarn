/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.floatprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.registry.Registry;

public interface FloatProviderType<P extends FloatProvider> {
    public static final FloatProviderType<ConstantFloatProvider> CONSTANT = FloatProviderType.register("constant", ConstantFloatProvider.CODEC);
    public static final FloatProviderType<UniformFloatProvider> UNIFORM = FloatProviderType.register("uniform", UniformFloatProvider.CODEC);
    public static final FloatProviderType<ClampedNormalFloatProvider> CLAMPED_NORMAL = FloatProviderType.register("clamped_normal", ClampedNormalFloatProvider.CODEC);
    public static final FloatProviderType<TrapezoidFloatProvider> TRAPEZOID = FloatProviderType.register("trapezoid", TrapezoidFloatProvider.CODEC);

    public Codec<P> codec();

    public static <P extends FloatProvider> FloatProviderType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registry.FLOAT_PROVIDER_TYPE, id, () -> codec);
    }
}

