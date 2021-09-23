package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class BlockStateProviderType<P extends BlockStateProvider> {
	public static final BlockStateProviderType<SimpleBlockStateProvider> SIMPLE_STATE_PROVIDER = register("simple_state_provider", SimpleBlockStateProvider.CODEC);
	public static final BlockStateProviderType<WeightedBlockStateProvider> WEIGHTED_STATE_PROVIDER = register(
		"weighted_state_provider", WeightedBlockStateProvider.CODEC
	);
	public static final BlockStateProviderType<NoiseThresholdBlockStateProvider> NOISE_THRESHOLD_PROVIDER = register(
		"noise_threshold_provider", NoiseThresholdBlockStateProvider.CODEC
	);
	public static final BlockStateProviderType<NoiseBlockStateProvider> NOISE_PROVIDER = register("noise_provider", NoiseBlockStateProvider.CODEC);
	public static final BlockStateProviderType<DualNoiseBlockStateProvider> DUAL_NOISE_PROVIDER = register(
		"dual_noise_provider", DualNoiseBlockStateProvider.DUAL_CODEC
	);
	public static final BlockStateProviderType<PillarBlockStateProvider> ROTATED_BLOCK_PROVIDER = register(
		"rotated_block_provider", PillarBlockStateProvider.CODEC
	);
	public static final BlockStateProviderType<RandomizedIntBlockStateProvider> RANDOMIZED_INT_STATE_PROVIDER = register(
		"randomized_int_state_provider", RandomizedIntBlockStateProvider.CODEC
	);
	private final Codec<P> codec;

	private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, id, new BlockStateProviderType<>(codec));
	}

	private BlockStateProviderType(Codec<P> codec) {
		this.codec = codec;
	}

	public Codec<P> getCodec() {
		return this.codec;
	}
}
