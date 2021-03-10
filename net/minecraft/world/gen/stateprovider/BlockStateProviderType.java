/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.ForestFlowerBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PlainsFlowerBlockStateProvider;
import net.minecraft.world.gen.stateprovider.RandomizedIntBlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class BlockStateProviderType<P extends BlockStateProvider> {
    public static final BlockStateProviderType<SimpleBlockStateProvider> SIMPLE_STATE_PROVIDER = BlockStateProviderType.register("simple_state_provider", SimpleBlockStateProvider.CODEC);
    public static final BlockStateProviderType<WeightedBlockStateProvider> WEIGHTED_STATE_PROVIDER = BlockStateProviderType.register("weighted_state_provider", WeightedBlockStateProvider.CODEC);
    public static final BlockStateProviderType<PlainsFlowerBlockStateProvider> PLAIN_FLOWER_PROVIDER = BlockStateProviderType.register("plain_flower_provider", PlainsFlowerBlockStateProvider.CODEC);
    public static final BlockStateProviderType<ForestFlowerBlockStateProvider> FOREST_FLOWER_PROVIDER = BlockStateProviderType.register("forest_flower_provider", ForestFlowerBlockStateProvider.CODEC);
    public static final BlockStateProviderType<PillarBlockStateProvider> ROTATED_BLOCK_PROVIDER = BlockStateProviderType.register("rotated_block_provider", PillarBlockStateProvider.CODEC);
    public static final BlockStateProviderType<RandomizedIntBlockStateProvider> RANDOMIZED_INT_STATE_PROVIDER = BlockStateProviderType.register("randomized_int_state_provider", RandomizedIntBlockStateProvider.CODEC);
    private final Codec<P> codec;

    private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String id, Codec<P> codec) {
        return Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, id, new BlockStateProviderType<P>(codec));
    }

    private BlockStateProviderType(Codec<P> codec) {
        this.codec = codec;
    }

    public Codec<P> getCodec() {
        return this.codec;
    }
}

