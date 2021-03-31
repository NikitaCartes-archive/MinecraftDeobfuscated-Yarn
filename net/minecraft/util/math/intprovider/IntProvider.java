/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.intprovider;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProviderType;
import net.minecraft.util.registry.Registry;

public abstract class IntProvider {
    private static final Codec<Either<Integer, IntProvider>> INT_CODEC = Codec.either(Codec.INT, Registry.INT_PROVIDER_TYPE.dispatch(IntProvider::getType, IntProviderType::codec));
    public static final Codec<IntProvider> VALUE_CODEC = INT_CODEC.xmap(either -> either.map(ConstantIntProvider::create, intProvider -> intProvider), intProvider -> intProvider.getType() == IntProviderType.CONSTANT ? Either.left(((ConstantIntProvider)intProvider).getValue()) : Either.right(intProvider));

    public static Codec<IntProvider> createValidatingCodec(int min, int max) {
        Function<IntProvider, DataResult> function = provider -> {
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

    public abstract int get(Random var1);

    public abstract int getMin();

    public abstract int getMax();

    public abstract IntProviderType<?> getType();
}

