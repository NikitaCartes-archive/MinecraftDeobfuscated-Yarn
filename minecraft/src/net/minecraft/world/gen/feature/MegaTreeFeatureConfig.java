package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.stateprovider.StateProvider;

public class MegaTreeFeatureConfig extends TreeFeatureConfig {
	public final int field_21233;

	protected MegaTreeFeatureConfig(StateProvider stateProvider, StateProvider stateProvider2, List<TreeDecorator> list, int i, int j) {
		super(stateProvider, stateProvider2, list, i);
		this.field_21233 = j;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		Dynamic<T> dynamic = new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("height_interval"), ops.createInt(this.field_21233))));
		return dynamic.merge(super.serialize(ops));
	}

	public static <T> MegaTreeFeatureConfig method_23408(Dynamic<T> dynamic) {
		TreeFeatureConfig treeFeatureConfig = TreeFeatureConfig.deserialize(dynamic);
		return new MegaTreeFeatureConfig(
			treeFeatureConfig.trunkProvider,
			treeFeatureConfig.leavesProvider,
			treeFeatureConfig.decorators,
			treeFeatureConfig.baseHeight,
			dynamic.get("height_interval").asInt(0)
		);
	}

	public static class Builder extends TreeFeatureConfig.Builder {
		private List<TreeDecorator> field_21234 = ImmutableList.of();
		private int field_21235;
		private int field_21236;

		public Builder(StateProvider stateProvider, StateProvider stateProvider2) {
			super(stateProvider, stateProvider2);
		}

		public MegaTreeFeatureConfig.Builder method_23411(List<TreeDecorator> list) {
			this.field_21234 = list;
			return this;
		}

		public MegaTreeFeatureConfig.Builder method_23410(int i) {
			this.field_21235 = i;
			return this;
		}

		public MegaTreeFeatureConfig.Builder method_23412(int i) {
			this.field_21236 = i;
			return this;
		}

		public MegaTreeFeatureConfig method_23409() {
			return new MegaTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.field_21234, this.field_21235, this.field_21236);
		}
	}
}
