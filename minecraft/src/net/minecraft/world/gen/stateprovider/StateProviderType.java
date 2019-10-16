package net.minecraft.world.gen.stateprovider;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class StateProviderType<P extends StateProvider> {
	public static final StateProviderType<SimpleStateProvider> SIMPLE_STATE_PROVIDER = register("simple_state_provider", SimpleStateProvider::new);
	public static final StateProviderType<WeightedStateProvider> WEIGHTED_STATE_PROVIDER = register("weighted_state_provider", WeightedStateProvider::new);
	public static final StateProviderType<PlainFlowerStateProvider> PLAIN_FLOWER_PROVIDER = register("plain_flower_provider", PlainFlowerStateProvider::new);
	public static final StateProviderType<ForestFlowerStateProvider> FOREST_FLOWER_PROVIDER = register("forest_flower_provider", ForestFlowerStateProvider::new);
	private final Function<Dynamic<?>, P> configDeserializer;

	private static <P extends StateProvider> StateProviderType<P> register(String string, Function<Dynamic<?>, P> function) {
		return Registry.register(Registry.BLOCK_STATE_PROVIDER_TYPE, string, new StateProviderType<>(function));
	}

	private StateProviderType(Function<Dynamic<?>, P> function) {
		this.configDeserializer = function;
	}

	public P deserialize(Dynamic<?> dynamic) {
		return (P)this.configDeserializer.apply(dynamic);
	}
}
