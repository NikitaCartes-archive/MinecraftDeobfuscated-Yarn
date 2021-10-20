/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math.intprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.ClampedIntProvider;
import net.minecraft.util.math.intprovider.ClampedNormalIntProvider;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.intprovider.WeightedListIntProvider;
import net.minecraft.util.registry.Registry;

public interface IntProviderType<P extends IntProvider> {
    public static final IntProviderType<ConstantIntProvider> CONSTANT = IntProviderType.register("constant", ConstantIntProvider.CODEC);
    public static final IntProviderType<UniformIntProvider> UNIFORM = IntProviderType.register("uniform", UniformIntProvider.CODEC);
    public static final IntProviderType<BiasedToBottomIntProvider> BIASED_TO_BOTTOM = IntProviderType.register("biased_to_bottom", BiasedToBottomIntProvider.CODEC);
    public static final IntProviderType<ClampedIntProvider> CLAMPED = IntProviderType.register("clamped", ClampedIntProvider.CODEC);
    public static final IntProviderType<WeightedListIntProvider> WEIGHTED_LIST = IntProviderType.register("weighted_list", WeightedListIntProvider.CODEC);
    public static final IntProviderType<ClampedNormalIntProvider> CLAMPED_NORMAL = IntProviderType.register("clamped_normal", ClampedNormalIntProvider.CODEC);

    public Codec<P> codec();

    public static <P extends IntProvider> IntProviderType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registry.INT_PROVIDER_TYPE, id, () -> codec);
    }
}

