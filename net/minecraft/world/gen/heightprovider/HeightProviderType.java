/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.heightprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.heightprovider.BiasedToBottomHeightProvider;
import net.minecraft.world.gen.heightprovider.ConstantHeightProvider;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public interface HeightProviderType<P extends HeightProvider> {
    public static final HeightProviderType<ConstantHeightProvider> CONSTANT = HeightProviderType.register("constant", ConstantHeightProvider.CONSTANT_CODEC);
    public static final HeightProviderType<UniformHeightProvider> UNIFORM = HeightProviderType.register("uniform", UniformHeightProvider.UNIFORM_CODEC);
    public static final HeightProviderType<BiasedToBottomHeightProvider> BIASED_TO_BOTTOM = HeightProviderType.register("biased_to_bottom", BiasedToBottomHeightProvider.BIASED_TO_BOTTOM_CODEC);

    public Codec<P> codec();

    public static <P extends HeightProvider> HeightProviderType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registry.HEIGHT_PROVIDER_TYPE, id, () -> codec);
    }
}

