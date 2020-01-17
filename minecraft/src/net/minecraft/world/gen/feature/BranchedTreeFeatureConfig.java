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

public class BranchedTreeFeatureConfig extends TreeFeatureConfig {
	public final FoliagePlacer foliagePlacer;
	public final int heightRandA;
	public final int heightRandB;
	public final int trunkHeight;
	public final int trunkHeightRandom;
	public final int trunkTopOffset;
	public final int trunkTopOffsetRandom;
	public final int foliageHeight;
	public final int foliageHeightRandom;
	public final int maxWaterDepth;
	public final boolean noVines;

	protected BranchedTreeFeatureConfig(
		BlockStateProvider trunkProvider,
		BlockStateProvider leavesProvider,
		FoliagePlacer foliagePlacer,
		List<TreeDecorator> treeDecorators,
		int baseHeight,
		int heightRandA,
		int heightRandB,
		int trunkHeight,
		int trunkHeightRandom,
		int trunkTopOffset,
		int trunkTopOffsetRandom,
		int foliageHeight,
		int foliageHeightRandom,
		int maxWaterDepth,
		boolean noVines
	) {
		super(trunkProvider, leavesProvider, treeDecorators, baseHeight);
		this.foliagePlacer = foliagePlacer;
		this.heightRandA = heightRandA;
		this.heightRandB = heightRandB;
		this.trunkHeight = trunkHeight;
		this.trunkHeightRandom = trunkHeightRandom;
		this.trunkTopOffset = trunkTopOffset;
		this.trunkTopOffsetRandom = trunkTopOffsetRandom;
		this.foliageHeight = foliageHeight;
		this.foliageHeightRandom = foliageHeightRandom;
		this.maxWaterDepth = maxWaterDepth;
		this.noVines = noVines;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("foliage_placer"), this.foliagePlacer.serialize(ops))
			.put(ops.createString("height_rand_a"), ops.createInt(this.heightRandA))
			.put(ops.createString("height_rand_b"), ops.createInt(this.heightRandB))
			.put(ops.createString("trunk_height"), ops.createInt(this.trunkHeight))
			.put(ops.createString("trunk_height_random"), ops.createInt(this.trunkHeightRandom))
			.put(ops.createString("trunk_top_offset"), ops.createInt(this.trunkTopOffset))
			.put(ops.createString("trunk_top_offset_random"), ops.createInt(this.trunkTopOffsetRandom))
			.put(ops.createString("foliage_height"), ops.createInt(this.foliageHeight))
			.put(ops.createString("foliage_height_random"), ops.createInt(this.foliageHeightRandom))
			.put(ops.createString("max_water_depth"), ops.createInt(this.maxWaterDepth))
			.put(ops.createString("ignore_vines"), ops.createBoolean(this.noVines));
		Dynamic<T> dynamic = new Dynamic<>(ops, ops.createMap(builder.build()));
		return dynamic.merge(super.serialize(ops));
	}

	public static <T> BranchedTreeFeatureConfig deserialize(Dynamic<T> dynamic) {
		TreeFeatureConfig treeFeatureConfig = TreeFeatureConfig.deserialize(dynamic);
		FoliagePlacerType<?> foliagePlacerType = Registry.FOLIAGE_PLACER_TYPE
			.get(new Identifier((String)dynamic.get("foliage_placer").get("type").asString().orElseThrow(RuntimeException::new)));
		return new BranchedTreeFeatureConfig(
			treeFeatureConfig.trunkProvider,
			treeFeatureConfig.leavesProvider,
			foliagePlacerType.deserialize(dynamic.get("foliage_placer").orElseEmptyMap()),
			treeFeatureConfig.decorators,
			treeFeatureConfig.baseHeight,
			dynamic.get("height_rand_a").asInt(0),
			dynamic.get("height_rand_b").asInt(0),
			dynamic.get("trunk_height").asInt(-1),
			dynamic.get("trunk_height_random").asInt(0),
			dynamic.get("trunk_top_offset").asInt(0),
			dynamic.get("trunk_top_offset_random").asInt(0),
			dynamic.get("foliage_height").asInt(-1),
			dynamic.get("foliage_height_random").asInt(0),
			dynamic.get("max_water_depth").asInt(0),
			dynamic.get("ignore_vines").asBoolean(false)
		);
	}

	public static class Builder extends TreeFeatureConfig.Builder {
		private final FoliagePlacer foliagePlacer;
		private List<TreeDecorator> treeDecorators = ImmutableList.of();
		private int field_21272;
		private int heightRandA;
		private int heightRandB;
		private int trunkHeight = -1;
		private int trunkHeightRandom;
		private int trunkTopOffset;
		private int trunkTopOffsetRandom;
		private int foliageHeight = -1;
		private int foliageHeightRandom;
		private int maxWaterDepth;
		private boolean noVines;

		public Builder(BlockStateProvider trunkProvider, BlockStateProvider leavesProvider, FoliagePlacer foliagePlacer) {
			super(trunkProvider, leavesProvider);
			this.foliagePlacer = foliagePlacer;
		}

		public BranchedTreeFeatureConfig.Builder treeDecorators(List<TreeDecorator> treeDecorators) {
			this.treeDecorators = treeDecorators;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder baseHeight(int i) {
			this.field_21272 = i;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder heightRandA(int heightRandA) {
			this.heightRandA = heightRandA;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder heightRandB(int heightRandB) {
			this.heightRandB = heightRandB;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder trunkHeight(int trunkHeight) {
			this.trunkHeight = trunkHeight;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder trunkHeightRandom(int trunkHeightRandom) {
			this.trunkHeightRandom = trunkHeightRandom;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder trunkTopOffset(int trunkTopOffset) {
			this.trunkTopOffset = trunkTopOffset;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder trunkTopOffsetRandom(int trunkTopOffsetRandom) {
			this.trunkTopOffsetRandom = trunkTopOffsetRandom;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder foliageHeight(int foliageHeight) {
			this.foliageHeight = foliageHeight;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder foliageHeightRandom(int foliageHeightRandom) {
			this.foliageHeightRandom = foliageHeightRandom;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder maxWaterDepth(int maxWaterDepth) {
			this.maxWaterDepth = maxWaterDepth;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder noVines() {
			this.noVines = true;
			return this;
		}

		public BranchedTreeFeatureConfig build() {
			return new BranchedTreeFeatureConfig(
				this.trunkProvider,
				this.leavesProvider,
				this.foliagePlacer,
				this.treeDecorators,
				this.field_21272,
				this.heightRandA,
				this.heightRandB,
				this.trunkHeight,
				this.trunkHeightRandom,
				this.trunkTopOffset,
				this.trunkTopOffsetRandom,
				this.foliageHeight,
				this.foliageHeightRandom,
				this.maxWaterDepth,
				this.noVines
			);
		}
	}
}
