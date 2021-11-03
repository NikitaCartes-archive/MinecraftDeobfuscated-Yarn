/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.floatprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.registry.Registry;

public abstract class FloatProvider {
    private static final Codec<Either<Float, FloatProvider>> FLOAT_CODEC = Codec.either(Codec.FLOAT, Registry.FLOAT_PROVIDER_TYPE.dispatch(FloatProvider::getType, FloatProviderType::codec));
    public static final Codec<FloatProvider> VALUE_CODEC = FLOAT_CODEC.xmap(either -> either.map(ConstantFloatProvider::create, provider -> provider), provider -> provider.getType() == FloatProviderType.CONSTANT ? Either.left(Float.valueOf(((ConstantFloatProvider)provider).getValue())) : Either.right(provider));

    public static Codec<FloatProvider> createValidatedCodec(float min, float max) {
        Function<FloatProvider, DataResult> function = provider -> {
            if (provider.getMin() < min) {
                return DataResult.error("Value provider too low: " + min + " [" + provider.getMin() + "-" + provider.getMax() + "]");
            }
            if (provider.getMax() > max) {
                return DataResult.error("Value provider too high: " + max + " [" + provider.getMin() + "-" + provider.getMax() + "]");
            }
            return DataResult.success(provider);
        };
        return VALUE_CODEC.flatXmap(function, function);
    }

    public abstract float get(Random var1);

    public abstract float getMin();

    public abstract float getMax();

    public abstract FloatProviderType<?> getType();
}

