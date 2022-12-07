/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.floatprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.function.Function;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatProviderType;
import net.minecraft.util.math.floatprovider.FloatSupplier;

public abstract class FloatProvider
implements FloatSupplier {
    private static final Codec<Either<Float, FloatProvider>> FLOAT_CODEC = Codec.either(Codec.FLOAT, Registries.FLOAT_PROVIDER_TYPE.getCodec().dispatch(FloatProvider::getType, FloatProviderType::codec));
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

    public abstract float getMin();

    public abstract float getMax();

    public abstract FloatProviderType<?> getType();
}

