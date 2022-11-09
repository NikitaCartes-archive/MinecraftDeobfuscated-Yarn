/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.DualNoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseThresholdBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import net.minecraft.world.gen.stateprovider.RandomizedIntBlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class BlockStateProviderType<P extends BlockStateProvider> {
    public static final BlockStateProviderType<SimpleBlockStateProvider> SIMPLE_STATE_PROVIDER = BlockStateProviderType.register("simple_state_provider", SimpleBlockStateProvider.CODEC);
    public static final BlockStateProviderType<WeightedBlockStateProvider> WEIGHTED_STATE_PROVIDER = BlockStateProviderType.register("weighted_state_provider", WeightedBlockStateProvider.CODEC);
    public static final BlockStateProviderType<NoiseThresholdBlockStateProvider> NOISE_THRESHOLD_PROVIDER = BlockStateProviderType.register("noise_threshold_provider", NoiseThresholdBlockStateProvider.CODEC);
    public static final BlockStateProviderType<NoiseBlockStateProvider> NOISE_PROVIDER = BlockStateProviderType.register("noise_provider", NoiseBlockStateProvider.CODEC);
    public static final BlockStateProviderType<DualNoiseBlockStateProvider> DUAL_NOISE_PROVIDER = BlockStateProviderType.register("dual_noise_provider", DualNoiseBlockStateProvider.DUAL_CODEC);
    public static final BlockStateProviderType<PillarBlockStateProvider> ROTATED_BLOCK_PROVIDER = BlockStateProviderType.register("rotated_block_provider", PillarBlockStateProvider.CODEC);
    public static final BlockStateProviderType<RandomizedIntBlockStateProvider> RANDOMIZED_INT_STATE_PROVIDER = BlockStateProviderType.register("randomized_int_state_provider", RandomizedIntBlockStateProvider.CODEC);
    private final Codec<P> codec;

    private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registries.BLOCK_STATE_PROVIDER_TYPE, id, new BlockStateProviderType<P>(codec));
    }

    private BlockStateProviderType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> getCodec() {
        return this.codec;
    }
}

