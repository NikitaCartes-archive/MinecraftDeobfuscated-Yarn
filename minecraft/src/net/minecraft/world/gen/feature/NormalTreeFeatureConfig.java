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

public class NormalTreeFeatureConfig extends AbstractTreeFeatureConfig {
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

	protected NormalTreeFeatureConfig(
		StateProvider stateProvider,
		StateProvider stateProvider2,
		FoliagePlacer foliagePlacer,
		List<TreeDecorator> list,
		int i,
		int j,
		int k,
		int l,
		int m,
		int n,
		int o,
		int p,
		int q,
		int r,
		boolean bl
	) {
		super(stateProvider, stateProvider2, list, i);
		this.foliagePlacer = foliagePlacer;
		this.heightRandA = j;
		this.heightRandB = k;
		this.trunkHeight = l;
		this.trunkHeightRandom = m;
		this.trunkTopOffsetRandom = n;
		this.field_21265 = o;
		this.field_21266 = p;
		this.field_21267 = q;
		this.field_21268 = r;
		this.field_21269 = bl;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
		builder.put(dynamicOps.createString("foliage_placer"), this.foliagePlacer.serialize(dynamicOps))
			.put(dynamicOps.createString("height_rand_a"), dynamicOps.createInt(this.heightRandA))
			.put(dynamicOps.createString("height_rand_b"), dynamicOps.createInt(this.heightRandB))
			.put(dynamicOps.createString("trunk_height"), dynamicOps.createInt(this.trunkHeight))
			.put(dynamicOps.createString("trunk_height_random"), dynamicOps.createInt(this.trunkHeightRandom))
			.put(dynamicOps.createString("trunk_top_offset"), dynamicOps.createInt(this.trunkTopOffsetRandom))
			.put(dynamicOps.createString("trunk_top_offset_random"), dynamicOps.createInt(this.field_21265))
			.put(dynamicOps.createString("foliage_height"), dynamicOps.createInt(this.field_21266))
			.put(dynamicOps.createString("foliage_height_random"), dynamicOps.createInt(this.field_21267))
			.put(dynamicOps.createString("max_water_depth"), dynamicOps.createInt(this.field_21268))
			.put(dynamicOps.createString("ignore_vines"), dynamicOps.createBoolean(this.field_21269));
		Dynamic<T> dynamic = new Dynamic<>(dynamicOps, dynamicOps.createMap(builder.build()));
		return dynamic.merge(super.serialize(dynamicOps));
	}

	public static <T> NormalTreeFeatureConfig method_23426(Dynamic<T> dynamic) {
		AbstractTreeFeatureConfig abstractTreeFeatureConfig = AbstractTreeFeatureConfig.deserialize(dynamic);
		FoliagePlacerType<?> foliagePlacerType = Registry.FOLIAGE_PLACER_TYPE
			.get(new Identifier((String)dynamic.get("foliage_placer").get("type").asString().orElseThrow(RuntimeException::new)));
		return new NormalTreeFeatureConfig(
			abstractTreeFeatureConfig.trunkProvider,
			abstractTreeFeatureConfig.leavesProvider,
			foliagePlacerType.deserialize(dynamic.get("foliage_placer").orElseEmptyMap()),
			abstractTreeFeatureConfig.decorators,
			abstractTreeFeatureConfig.baseHeight,
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

	public static class Builder extends AbstractTreeFeatureConfig.Builder {
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

		public Builder(StateProvider stateProvider, StateProvider stateProvider2, FoliagePlacer foliagePlacer) {
			super(stateProvider, stateProvider2);
			this.field_21270 = foliagePlacer;
		}

		public NormalTreeFeatureConfig.Builder method_23429(List<TreeDecorator> list) {
			this.treeDecorators = list;
			return this;
		}

		public NormalTreeFeatureConfig.Builder method_23428(int i) {
			this.field_21272 = i;
			return this;
		}

		public NormalTreeFeatureConfig.Builder method_23430(int i) {
			this.field_21273 = i;
			return this;
		}

		public NormalTreeFeatureConfig.Builder method_23432(int i) {
			this.field_21274 = i;
			return this;
		}

		public NormalTreeFeatureConfig.Builder method_23433(int i) {
			this.field_21275 = i;
			return this;
		}

		public NormalTreeFeatureConfig.Builder method_23434(int i) {
			this.field_21276 = i;
			return this;
		}

		public NormalTreeFeatureConfig.Builder method_23435(int i) {
			this.field_21277 = i;
			return this;
		}

		public NormalTreeFeatureConfig.Builder method_23436(int i) {
			this.field_21278 = i;
			return this;
		}

		public NormalTreeFeatureConfig.Builder method_23437(int i) {
			this.field_21279 = i;
			return this;
		}

		public NormalTreeFeatureConfig.Builder method_23438(int i) {
			this.field_21280 = i;
			return this;
		}

		public NormalTreeFeatureConfig.Builder method_23439(int i) {
			this.field_21281 = i;
			return this;
		}

		public NormalTreeFeatureConfig.Builder method_23427() {
			this.field_21282 = true;
			return this;
		}

		public NormalTreeFeatureConfig method_23431() {
			return new NormalTreeFeatureConfig(
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
