package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.stateprovider.StateProvider;

public class MegaTreeFeatureConfig extends AbstractTreeFeatureConfig {
	public final int field_21233;

	protected MegaTreeFeatureConfig(StateProvider stateProvider, StateProvider stateProvider2, List<TreeDecorator> list, int i, int j) {
		super(stateProvider, stateProvider2, list, i);
		this.field_21233 = j;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		Dynamic<T> dynamic = new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("height_interval"), dynamicOps.createInt(this.field_21233)))
		);
		return dynamic.merge(super.serialize(dynamicOps));
	}

	public static <T> MegaTreeFeatureConfig method_23408(Dynamic<T> dynamic) {
		AbstractTreeFeatureConfig abstractTreeFeatureConfig = AbstractTreeFeatureConfig.deserialize(dynamic);
		return new MegaTreeFeatureConfig(
			abstractTreeFeatureConfig.trunkProvider,
			abstractTreeFeatureConfig.leavesProvider,
			abstractTreeFeatureConfig.decorators,
			abstractTreeFeatureConfig.baseHeight,
			dynamic.get("height_interval").asInt(0)
		);
	}

	public static class class_4637 extends AbstractTreeFeatureConfig.Builder {
		private List<TreeDecorator> field_21234 = ImmutableList.of();
		private int field_21235;
		private int field_21236;

		public class_4637(StateProvider stateProvider, StateProvider stateProvider2) {
			super(stateProvider, stateProvider2);
		}

		public MegaTreeFeatureConfig.class_4637 method_23411(List<TreeDecorator> list) {
			this.field_21234 = list;
			return this;
		}

		public MegaTreeFeatureConfig.class_4637 method_23410(int i) {
			this.field_21235 = i;
			return this;
		}

		public MegaTreeFeatureConfig.class_4637 method_23412(int i) {
			this.field_21236 = i;
			return this;
		}

		public MegaTreeFeatureConfig method_23409() {
			return new MegaTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.field_21234, this.field_21235, this.field_21236);
		}
	}
}
