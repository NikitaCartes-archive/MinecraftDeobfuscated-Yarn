package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class HugeMushroomFeatureConfig implements FeatureConfig {
	public final BlockStateProvider capProvider;
	public final BlockStateProvider stemProvider;
	public final int capSize;

	public HugeMushroomFeatureConfig(BlockStateProvider capProvider, BlockStateProvider stemProvider, int capSize) {
		this.capProvider = capProvider;
		this.stemProvider = stemProvider;
		this.capSize = capSize;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("cap_provider"), this.capProvider.serialize(ops))
			.put(ops.createString("stem_provider"), this.stemProvider.serialize(ops))
			.put(ops.createString("foliage_radius"), ops.createInt(this.capSize));
		return new Dynamic<>(ops, ops.createMap(builder.build()));
	}

	public static <T> HugeMushroomFeatureConfig deserialize(Dynamic<T> dynamic) {
		BlockStateProviderType<?> blockStateProviderType = Registry.BLOCK_STATE_PROVIDER_TYPE
			.get(new Identifier((String)dynamic.get("cap_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		BlockStateProviderType<?> blockStateProviderType2 = Registry.BLOCK_STATE_PROVIDER_TYPE
			.get(new Identifier((String)dynamic.get("stem_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		return new HugeMushroomFeatureConfig(
			blockStateProviderType.deserialize(dynamic.get("cap_provider").orElseEmptyMap()),
			blockStateProviderType2.deserialize(dynamic.get("stem_provider").orElseEmptyMap()),
			dynamic.get("foliage_radius").asInt(2)
		);
	}
}
