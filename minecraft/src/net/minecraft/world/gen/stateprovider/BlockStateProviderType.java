package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class BlockStateProviderType<P extends BlockStateProvider> {
	public static final BlockStateProviderType<SimpleBlockStateProvider> SIMPLE_STATE_PROVIDER = register("simple_state_provider", SimpleBlockStateProvider.CODEC);
	public static final BlockStateProviderType<WeightedBlockStateProvider> WEIGHTED_STATE_PROVIDER = register(
		"weighted_state_provider", WeightedBlockStateProvider.CODEC
	);
	public static final BlockStateProviderType<PlainsFlowerBlockStateProvider> PLAIN_FLOWER_PROVIDER = register(
		"plain_flower_provider", PlainsFlowerBlockStateProvider.CODEC
	);
	public static final BlockStateProviderType<ForestFlowerBlockStateProvider> FOREST_FLOWER_PROVIDER = register(
		"forest_flower_provider", ForestFlowerBlockStateProvider.CODEC
	);
	public static final BlockStateProviderType<PillarBlockStateProvider> ROTATED_BLOCK_PROVIDER = register(
		"rotated_block_provider", PillarBlockStateProvider.CODEC
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
