package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.StateProvider;
import net.minecraft.world.gen.stateprovider.StateProviderType;

public class BlockPileFeatureConfig implements FeatureConfig {
	public final StateProvider field_21229;

	public BlockPileFeatureConfig(StateProvider stateProvider) {
		this.field_21229 = stateProvider;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("state_provider"), this.field_21229.serialize(ops));
		return new Dynamic<>(ops, ops.createMap(builder.build()));
	}

	public static <T> BlockPileFeatureConfig deserialize(Dynamic<T> dynamic) {
		StateProviderType<?> stateProviderType = Registry.BLOCK_STATE_PROVIDER_TYPE
			.get(new Identifier((String)dynamic.get("state_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		return new BlockPileFeatureConfig(stateProviderType.deserialize(dynamic.get("state_provider").orElseEmptyMap()));
	}
}
