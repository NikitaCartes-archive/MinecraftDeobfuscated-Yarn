package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class BlockStateProviderType<P extends BlockStateProvider> {
	public static final BlockStateProviderType<SimpleBlockStateProvider> field_21305 = register("simple_state_provider", SimpleBlockStateProvider.CODEC);
	public static final BlockStateProviderType<WeightedBlockStateProvider> field_21306 = register("weighted_state_provider", WeightedBlockStateProvider.CODEC);
	public static final BlockStateProviderType<PlainsFlowerBlockStateProvider> field_21307 = register(
		"plain_flower_provider", PlainsFlowerBlockStateProvider.CODEC
	);
	public static final BlockStateProviderType<ForestFlowerBlockStateProvider> field_21308 = register(
		"forest_flower_provider", ForestFlowerBlockStateProvider.CODEC
	);
	public static final BlockStateProviderType<PillarBlockStateProvider> field_24938 = register("rotated_block_provider", PillarBlockStateProvider.CODEC);
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
