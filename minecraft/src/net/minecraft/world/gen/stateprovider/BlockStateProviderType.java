package net.minecraft.world.gen.stateprovider;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.class_5107;
import net.minecraft.util.registry.Registry;

public class BlockStateProviderType<P extends BlockStateProvider> {
	public static final BlockStateProviderType<SimpleBlockStateProvider> SIMPLE_STATE_PROVIDER = register("simple_state_provider", SimpleBlockStateProvider::new);
	public static final BlockStateProviderType<WeightedBlockStateProvider> WEIGHTED_STATE_PROVIDER = register(
		"weighted_state_provider", WeightedBlockStateProvider::new
	);
	public static final BlockStateProviderType<PlainsFlowerBlockStateProvider> PLAIN_FLOWER_PROVIDER = register(
		"plain_flower_provider", PlainsFlowerBlockStateProvider::new
	);
	public static final BlockStateProviderType<ForestFlowerBlockStateProvider> FOREST_FLOWER_PROVIDER = register(
		"forest_flower_provider", ForestFlowerBlockStateProvider::new
	);
	public static final BlockStateProviderType<class_5107> RAINBOW_PROVIDER = register("rainbow_provider", class_5107::new);
	private final Function<Dynamic<?>, P> configDeserializer;

	private static <P extends BlockStateProvider> BlockStateProviderType<P> register(String id, Function<Dynamic<?>, P> configDeserializer) {
		return Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, id, new BlockStateProviderType<>(configDeserializer));
	}

	private BlockStateProviderType(Function<Dynamic<?>, P> configDeserializer) {
		this.configDeserializer = configDeserializer;
	}

	public P deserialize(Dynamic<?> dynamic) {
		return (P)this.configDeserializer.apply(dynamic);
	}
}
