/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.heightprovider;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.heightprovider.BiasedToBottomHeightProvider;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import net.minecraft.world.gen.heightprovider.VeryBiasedToBottomHeightProvider;
import net.minecraft.world.gen.heightprovider.WeightedListHeightProvider;

public interface HeightProviderType<P extends HeightProvider> {
    public static final HeightProviderType<ConstantHeightProvider> CONSTANT = HeightProviderType.register("constant", ConstantHeightProvider.CONSTANT_CODEC);
    public static final HeightProviderType<UniformHeightProvider> UNIFORM = HeightProviderType.register("uniform", UniformHeightProvider.UNIFORM_CODEC);
    public static final HeightProviderType<BiasedToBottomHeightProvider> BIASED_TO_BOTTOM = HeightProviderType.register("biased_to_bottom", BiasedToBottomHeightProvider.BIASED_TO_BOTTOM_CODEC);
    public static final HeightProviderType<VeryBiasedToBottomHeightProvider> VERY_BIASED_TO_BOTTOM = HeightProviderType.register("very_biased_to_bottom", VeryBiasedToBottomHeightProvider.CODEC);
    public static final HeightProviderType<TrapezoidHeightProvider> TRAPEZOID = HeightProviderType.register("trapezoid", TrapezoidHeightProvider.CODEC);
    public static final HeightProviderType<WeightedListHeightProvider> WEIGHTED_LIST = HeightProviderType.register("weighted_list", WeightedListHeightProvider.WEIGHTED_LIST_CODEC);

    public Codec<P> codec();

    private static <P extends HeightProvider> HeightProviderType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registries.HEIGHT_PROVIDER_TYPE, id, () -> codec);
    }
}

