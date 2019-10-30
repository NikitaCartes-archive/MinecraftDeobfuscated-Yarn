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
import net.minecraft.world.gen.stateprovider.StateProvider;

public class BranchedTreeFeatureConfig extends TreeFeatureConfig {
	public final FoliagePlacer foliagePlacer;
	public final int heightRandA;
	public final int heightRandB;
	public final int trunkHeight;
	public final int trunkHeightRandom;
	public final int trunkTopOffsetRandom;
	public final int field_21265;
	public final int field_21266;
	public final int field_21267;
	public final int field_21268;
	public final boolean field_21269;

	protected BranchedTreeFeatureConfig(
		StateProvider stateProvider,
		StateProvider stateProvider2,
		FoliagePlacer foliagePlacer,
		List<TreeDecorator> list,
		int i,
		int heightRandA,
		int heightRandB,
		int trunkHeight,
		int trunkHeightRandom,
		int trunkTopOffsetRandom,
		int j,
		int k,
		int l,
		int m,
		boolean bl
	) {
		super(stateProvider, stateProvider2, list, i);
		this.foliagePlacer = foliagePlacer;
		this.heightRandA = heightRandA;
		this.heightRandB = heightRandB;
		this.trunkHeight = trunkHeight;
		this.trunkHeightRandom = trunkHeightRandom;
		this.trunkTopOffsetRandom = trunkTopOffsetRandom;
		this.field_21265 = j;
		this.field_21266 = k;
		this.field_21267 = l;
		this.field_21268 = m;
		this.field_21269 = bl;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
		builder.put(ops.createString("foliage_placer"), this.foliagePlacer.serialize(ops))
			.put(ops.createString("height_rand_a"), ops.createInt(this.heightRandA))
			.put(ops.createString("height_rand_b"), ops.createInt(this.heightRandB))
			.put(ops.createString("trunk_height"), ops.createInt(this.trunkHeight))
			.put(ops.createString("trunk_height_random"), ops.createInt(this.trunkHeightRandom))
			.put(ops.createString("trunk_top_offset"), ops.createInt(this.trunkTopOffsetRandom))
			.put(ops.createString("trunk_top_offset_random"), ops.createInt(this.field_21265))
			.put(ops.createString("foliage_height"), ops.createInt(this.field_21266))
			.put(ops.createString("foliage_height_random"), ops.createInt(this.field_21267))
			.put(ops.createString("max_water_depth"), ops.createInt(this.field_21268))
			.put(ops.createString("ignore_vines"), ops.createBoolean(this.field_21269));
		Dynamic<T> dynamic = new Dynamic<>(ops, ops.createMap(builder.build()));
		return dynamic.merge(super.serialize(ops));
	}

	public static <T> BranchedTreeFeatureConfig method_23426(Dynamic<T> dynamic) {
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
		private final FoliagePlacer field_21270;
		private List<TreeDecorator> treeDecorators = ImmutableList.of();
		private int field_21272;
		private int field_21273;
		private int field_21274;
		private int field_21275 = -1;
		private int field_21276;
		private int field_21277;
		private int field_21278;
		private int field_21279 = -1;
		private int field_21280;
		private int field_21281;
		private boolean field_21282;

		public Builder(StateProvider trunkProvider, StateProvider leavesProvider, FoliagePlacer foliagePlacer) {
			super(trunkProvider, leavesProvider);
			this.field_21270 = foliagePlacer;
		}

		public BranchedTreeFeatureConfig.Builder method_23429(List<TreeDecorator> list) {
			this.treeDecorators = list;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder method_23428(int i) {
			this.field_21272 = i;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder method_23430(int i) {
			this.field_21273 = i;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder method_23432(int i) {
			this.field_21274 = i;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder method_23433(int i) {
			this.field_21275 = i;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder method_23434(int i) {
			this.field_21276 = i;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder method_23435(int i) {
			this.field_21277 = i;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder method_23436(int i) {
			this.field_21278 = i;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder method_23437(int i) {
			this.field_21279 = i;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder method_23438(int i) {
			this.field_21280 = i;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder method_23439(int i) {
			this.field_21281 = i;
			return this;
		}

		public BranchedTreeFeatureConfig.Builder method_23427() {
			this.field_21282 = true;
			return this;
		}

		public BranchedTreeFeatureConfig method_23431() {
			return new BranchedTreeFeatureConfig(
				this.trunkProvider,
				this.leavesProvider,
				this.field_21270,
				this.treeDecorators,
				this.field_21272,
				this.field_21273,
				this.field_21274,
				this.field_21275,
				this.field_21276,
				this.field_21277,
				this.field_21278,
				this.field_21279,
				this.field_21280,
				this.field_21281,
				this.field_21282
			);
		}
	}
}
