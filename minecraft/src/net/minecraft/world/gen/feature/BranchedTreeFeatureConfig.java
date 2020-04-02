package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacerType;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class BranchedTreeFeatureConfig extends TreeFeatureConfig {
	public final FoliagePlacer foliagePlacer;
	public final TrunkPlacer trunkPlacer;
	public final int maxFluidDepth;
	public final boolean noVines;

	protected BranchedTreeFeatureConfig(
		BlockStateProvider trunkProvider,
		BlockStateProvider leavesProvider,
		FoliagePlacer foliagePlacer,
		TrunkPlacer trunkPlacer,
		List<TreeDecorator> decorators,
		int heightRandA,
		boolean noVines
	) {
		super(trunkProvider, leavesProvider, decorators, trunkPlacer.getBaseHeight());
		this.foliagePlacer = foliagePlacer;
		this.trunkPlacer = trunkPlacer;
		this.maxFluidDepth = heightRandA;
		this.noVines = noVines;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("foliage_placer"), this.foliagePlacer.serialize(ops))
			.put(ops.createString("trunk_placer"), this.trunkPlacer.serialize(ops))
			.put(ops.createString("max_water_depth"), ops.createInt(this.maxFluidDepth))
			.put(ops.createString("ignore_vines"), ops.createBoolean(this.noVines));
		Dynamic<T> dynamic = new Dynamic<>(ops, ops.createMap(builder.build()));
		return dynamic.merge(super.serialize(ops));
	}

	public static <T> BranchedTreeFeatureConfig deserialize(Dynamic<T> data) {
		TreeFeatureConfig treeFeatureConfig = TreeFeatureConfig.deserialize(data);
		FoliagePlacerType<?> foliagePlacerType = Registry.FOLIAGE_PLACER_TYPE
			.get(new Identifier((String)data.get("foliage_placer").get("type").asString().orElseThrow(RuntimeException::new)));
		TrunkPlacerType<?> trunkPlacerType = Registry.TRUNK_PLACER_TYPE
			.get(new Identifier((String)data.get("trunk_placer").get("type").asString().orElseThrow(RuntimeException::new)));
		return new BranchedTreeFeatureConfig(
			treeFeatureConfig.trunkProvider,
			treeFeatureConfig.leavesProvider,
			foliagePlacerType.deserialize(data.get("foliage_placer").orElseEmptyMap()),
			trunkPlacerType.deserialize(data.get("trunk_placer").orElseEmptyMap()),
			treeFeatureConfig.decorators,
			data.get("max_water_depth").asInt(0),
			data.get("ignore_vines").asBoolean(false)
		);
	}

	public static class Builder extends TreeFeatureConfig.Builder {
		private final FoliagePlacer foliagePlacer;
		private final TrunkPlacer trunkPlacer;
		private List<TreeDecorator> treeDecorators = ImmutableList.of();
		private int maxFluidDepth;
		private boolean noVines;

		public Builder(BlockStateProvider trunkProvider, BlockStateProvider leavesProvider, FoliagePlacer foliagePlacer, TrunkPlacer trunkPlacer) {
			super(trunkProvider, leavesProvider);
			this.foliagePlacer = foliagePlacer;
			this.trunkPlacer = trunkPlacer;
		}

		public BranchedTreeFeatureConfig.Builder treeDecorators(List<TreeDecorator> treeDecorators) {
			this.treeDecorators = treeDecorators;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder maxFluidDepth(int maxFluidDepth) {
			this.maxFluidDepth = maxFluidDepth;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder noVines() {
			this.noVines = true;
			return this;
		}

		public BranchedTreeFeatureConfig build() {
			return new BranchedTreeFeatureConfig(
				this.trunkProvider, this.leavesProvider, this.foliagePlacer, this.trunkPlacer, this.treeDecorators, this.maxFluidDepth, this.noVines
			);
		}
	}
}
