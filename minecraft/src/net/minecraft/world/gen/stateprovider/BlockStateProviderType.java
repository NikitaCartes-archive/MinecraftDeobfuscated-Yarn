package net.minecraft.world.gen.stateprovider;

import com.mojang.serialization.Codec;
import net.minecraft.util.registry.Registry;

public class BlockStateProviderType<P extends BlockStateProvider> {
	public static final BlockStateProviderType<SimpleBlockStateProvider> SIMPLE_STATE_PROVIDER = register(
		"simple_state_provider", SimpleBlockStateProvider.field_24945
	);
	public static final BlockStateProviderType<WeightedBlockStateProvider> WEIGHTED_STATE_PROVIDER = register(
		"weighted_state_provider", WeightedBlockStateProvider.field_24946
	);
	public static final BlockStateProviderType<PlainsFlowerBlockStateProvider> PLAIN_FLOWER_PROVIDER = register(
		"plain_flower_provider", PlainsFlowerBlockStateProvider.field_24942
	);
	public static final BlockStateProviderType<ForestFlowerBlockStateProvider> FOREST_FLOWER_PROVIDER = register(
		"forest_flower_provider", ForestFlowerBlockStateProvider.field_24940
	);
	public static final BlockStateProviderType<PillarBlockStateProvider> ROTATED_BLOCK_PROVIDER = register(
		"rotated_block_provider", PillarBlockStateProvider.field_24944
	);
	private final Codec<P> field_24939;

	private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String id, Codec<P> codec) {
		return Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, id, new BlockStateProviderType<>(codec));
	}

	private BlockStateProviderType(Codec<P> codec) {
		this.field_24939 = codec;
	}

	public Codec<P> method_28863() {
		return this.field_24939;
	}
}
