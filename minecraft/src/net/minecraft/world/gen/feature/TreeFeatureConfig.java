package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.stateprovider.StateProvider;
import net.minecraft.world.gen.stateprovider.StateProviderType;

public class TreeFeatureConfig implements FeatureConfig {
	public final StateProvider trunkProvider;
	public final StateProvider leavesProvider;
	public final List<TreeDecorator> decorators;
	public final int baseHeight;

	protected TreeFeatureConfig(StateProvider trunkProvider, StateProvider leavesProvider, List<TreeDecorator> decorators, int baseHeight) {
		this.trunkProvider = trunkProvider;
		this.leavesProvider = leavesProvider;
		this.decorators = decorators;
		this.baseHeight = baseHeight;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("trunk_provider"), this.trunkProvider.serialize(ops))
			.put(ops.createString("leaves_provider"), this.leavesProvider.serialize(ops))
			.put(ops.createString("decorators"), ops.createList(this.decorators.stream().map(treeDecorator -> treeDecorator.serialize(ops))))
			.put(ops.createString("base_height"), ops.createInt(this.baseHeight));
		return new Dynamic<>(ops, ops.createMap(builder.build()));
	}

	public static <T> TreeFeatureConfig deserialize(Dynamic<T> configDeserializer) {
		StateProviderType<?> stateProviderType = Registry.BLOCK_STATE_PROVIDER_TYPE
			.get(new Identifier((String)configDeserializer.get("trunk_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		StateProviderType<?> stateProviderType2 = Registry.BLOCK_STATE_PROVIDER_TYPE
			.get(new Identifier((String)configDeserializer.get("leaves_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		return new TreeFeatureConfig(
			stateProviderType.deserialize(configDeserializer.get("trunk_provider").orElseEmptyMap()),
			stateProviderType2.deserialize(configDeserializer.get("leaves_provider").orElseEmptyMap()),
			configDeserializer.get("decorators")
				.asList(
					dynamic -> Registry.TREE_DECORATOR_TYPE
							.get(new Identifier((String)dynamic.get("type").asString().orElseThrow(RuntimeException::new)))
							.method_23472(dynamic)
				),
			configDeserializer.get("base_height").asInt(0)
		);
	}

	public static class Builder {
		public final StateProvider trunkProvider;
		public final StateProvider leavesProvider;
		private List<TreeDecorator> decorators = Lists.<TreeDecorator>newArrayList();
		private int baseHeight = 0;

		public Builder(StateProvider trunkProvider, StateProvider leavesProvider) {
			this.trunkProvider = trunkProvider;
			this.leavesProvider = leavesProvider;
		}

		public TreeFeatureConfig.Builder baseHeight(int baseHeight) {
			this.baseHeight = baseHeight;
			return this;
		}

		public TreeFeatureConfig build() {
			return new TreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.decorators, this.baseHeight);
		}
	}
}
