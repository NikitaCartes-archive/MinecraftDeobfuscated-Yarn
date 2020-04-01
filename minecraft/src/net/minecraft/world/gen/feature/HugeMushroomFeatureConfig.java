package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MushroomBlock;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;

public class HugeMushroomFeatureConfig implements FeatureConfig {
	public final BlockStateProvider capProvider;
	public final BlockStateProvider stemProvider;
	public final int capSize;
	private static final List<BlockState> field_23579 = (List<BlockState>)Registry.BLOCK
		.stream()
		.map(Block::getDefaultState)
		.filter(blockState -> blockState.contains(MushroomBlock.UP) && blockState.contains(MushroomBlock.NORTH))
		.collect(ImmutableList.toImmutableList());

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

	public static HugeMushroomFeatureConfig method_26613(Random random) {
		return new HugeMushroomFeatureConfig(BlockStateProvider.method_26660(random, field_23579), BlockStateProvider.method_26659(random), random.nextInt(15));
	}
}
