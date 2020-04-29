package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.feature.size.FeatureSizeType;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProviderType;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class TreeFeatureConfig implements FeatureConfig {
	public final BlockStateProvider trunkProvider;
	public final BlockStateProvider leavesProvider;
	public final List<TreeDecorator> decorators;
	public transient boolean skipFluidCheck;
	public final FoliagePlacer foliagePlacer;
	public final TrunkPlacer trunkPlacer;
	public final FeatureSize featureSize;
	public final int baseHeight;
	public final boolean ignoreVines;
	public final Heightmap.Type heightmap;

	protected TreeFeatureConfig(
		BlockStateProvider trunkProvider,
		BlockStateProvider leavesProvider,
		FoliagePlacer foliagePlacer,
		TrunkPlacer trunkPlacer,
		FeatureSize featureSize,
		List<TreeDecorator> list,
		int i,
		boolean bl,
		Heightmap.Type heightmapType
	) {
		this.trunkProvider = trunkProvider;
		this.leavesProvider = leavesProvider;
		this.decorators = list;
		this.foliagePlacer = foliagePlacer;
		this.featureSize = featureSize;
		this.trunkPlacer = trunkPlacer;
		this.baseHeight = i;
		this.ignoreVines = bl;
		this.heightmap = heightmapType;
	}

	public void ignoreFluidCheck() {
		this.skipFluidCheck = true;
	}

	public TreeFeatureConfig setTreeDecorators(List<TreeDecorator> list) {
		return new TreeFeatureConfig(
			this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.trunkPlacer, this.featureSize, list, this.baseHeight, this.ignoreVines, this.heightmap
		);
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("trunk_provider"), this.trunkProvider.serialize(ops))
			.put(ops.createString("leaves_provider"), this.leavesProvider.serialize(ops))
			.put(ops.createString("decorators"), ops.createList(this.decorators.stream().map(treeDecorator -> treeDecorator.serialize(ops))))
			.put(ops.createString("foliage_placer"), this.foliagePlacer.serialize(ops))
			.put(ops.createString("trunk_placer"), this.trunkPlacer.serialize(ops))
			.put(ops.createString("minimum_size"), this.featureSize.serialize(ops))
			.put(ops.createString("max_water_depth"), ops.createInt(this.baseHeight))
			.put(ops.createString("ignore_vines"), ops.createBoolean(this.ignoreVines))
			.put(ops.createString("heightmap"), ops.createString(this.heightmap.getName()));
		return new Dynamic<>(ops, ops.createMap(builder.build()));
	}

	public static <T> TreeFeatureConfig deserialize(Dynamic<T> configDeserializer) {
		BlockStateProviderType<?> blockStateProviderType = Registry.BLOCK_STATE_PROVIDER_TYPE
			.get(new Identifier((String)configDeserializer.get("trunk_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		BlockStateProviderType<?> blockStateProviderType2 = Registry.BLOCK_STATE_PROVIDER_TYPE
			.get(new Identifier((String)configDeserializer.get("leaves_provider").get("type").asString().orElseThrow(RuntimeException::new)));
		FoliagePlacerType<?> foliagePlacerType = Registry.FOLIAGE_PLACER_TYPE
			.get(new Identifier((String)configDeserializer.get("foliage_placer").get("type").asString().orElseThrow(RuntimeException::new)));
		TrunkPlacerType<?> trunkPlacerType = Registry.TRUNK_PLACER_TYPE
			.get(new Identifier((String)configDeserializer.get("trunk_placer").get("type").asString().orElseThrow(RuntimeException::new)));
		FeatureSizeType<?> featureSizeType = Registry.FEATURE_SIZE_TYPE
			.get(new Identifier((String)configDeserializer.get("minimum_size").get("type").asString().orElseThrow(RuntimeException::new)));
		return new TreeFeatureConfig(
			blockStateProviderType.deserialize(configDeserializer.get("trunk_provider").orElseEmptyMap()),
			blockStateProviderType2.deserialize(configDeserializer.get("leaves_provider").orElseEmptyMap()),
			foliagePlacerType.deserialize(configDeserializer.get("foliage_placer").orElseEmptyMap()),
			trunkPlacerType.deserialize(configDeserializer.get("trunk_placer").orElseEmptyMap()),
			featureSizeType.method_27381(configDeserializer.get("minimum_size").orElseEmptyMap()),
			configDeserializer.get("decorators")
				.asList(
					dynamic -> Registry.TREE_DECORATOR_TYPE
							.get(new Identifier((String)dynamic.get("type").asString().orElseThrow(RuntimeException::new)))
							.method_23472(dynamic)
				),
			configDeserializer.get("max_water_depth").asInt(0),
			configDeserializer.get("ignore_vines").asBoolean(false),
			Heightmap.Type.byName(configDeserializer.get("heightmap").asString(""))
		);
	}

	public static class Builder {
		public final BlockStateProvider trunkProvider;
		public final BlockStateProvider leavesProvider;
		private final FoliagePlacer field_24140;
		private final TrunkPlacer field_24141;
		private final FeatureSize field_24142;
		private List<TreeDecorator> decorators = ImmutableList.of();
		private int baseHeight;
		private boolean field_24143;
		private Heightmap.Type heightmap = Heightmap.Type.OCEAN_FLOOR;

		public Builder(
			BlockStateProvider trunkProvider, BlockStateProvider leavesProvider, FoliagePlacer foliagePlacer, TrunkPlacer trunkPlacer, FeatureSize featureSize
		) {
			this.trunkProvider = trunkProvider;
			this.leavesProvider = leavesProvider;
			this.field_24140 = foliagePlacer;
			this.field_24141 = trunkPlacer;
			this.field_24142 = featureSize;
		}

		public TreeFeatureConfig.Builder method_27376(List<TreeDecorator> list) {
			this.decorators = list;
			return this;
		}

		public TreeFeatureConfig.Builder baseHeight(int baseHeight) {
			this.baseHeight = baseHeight;
			return this;
		}

		public TreeFeatureConfig.Builder method_27374() {
			this.field_24143 = true;
			return this;
		}

		public TreeFeatureConfig.Builder method_27375(Heightmap.Type type) {
			this.heightmap = type;
			return this;
		}

		public TreeFeatureConfig build() {
			return new TreeFeatureConfig(
				this.trunkProvider,
				this.leavesProvider,
				this.field_24140,
				this.field_24141,
				this.field_24142,
				this.decorators,
				this.baseHeight,
				this.field_24143,
				this.heightmap
			);
		}
	}
}
