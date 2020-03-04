package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class MegaTreeFeatureConfig extends TreeFeatureConfig {
	public final int heightInterval;
	public final int crownHeight;

	protected MegaTreeFeatureConfig(
		BlockStateProvider trunkProvider, BlockStateProvider leavesProvider, List<TreeDecorator> decorators, int baseHeight, int heightInterval, int crownHeight
	) {
		super(trunkProvider, leavesProvider, decorators, baseHeight);
		this.heightInterval = heightInterval;
		this.crownHeight = crownHeight;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		Dynamic<T> dynamic = new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(ops.createString("height_interval"), ops.createInt(this.heightInterval), ops.createString("crown_height"), ops.createInt(this.crownHeight))
			)
		);
		return dynamic.merge(super.serialize(ops));
	}

	public static <T> MegaTreeFeatureConfig deserialize(Dynamic<T> dynamic) {
		TreeFeatureConfig treeFeatureConfig = TreeFeatureConfig.deserialize(dynamic);
		return new MegaTreeFeatureConfig(
			treeFeatureConfig.trunkProvider,
			treeFeatureConfig.leavesProvider,
			treeFeatureConfig.decorators,
			treeFeatureConfig.baseHeight,
			dynamic.get("height_interval").asInt(0),
			dynamic.get("crown_height").asInt(0)
		);
	}

	public static class Builder extends TreeFeatureConfig.Builder {
		private List<TreeDecorator> treeDecorators = ImmutableList.of();
		private int height;
		private int heightInterval;
		private int crownHeight;

		public Builder(BlockStateProvider blockStateProvider, BlockStateProvider blockStateProvider2) {
			super(blockStateProvider, blockStateProvider2);
		}

		public MegaTreeFeatureConfig.Builder treeDecorators(List<TreeDecorator> decorators) {
			this.treeDecorators = decorators;
			return this;
		}

		public MegaTreeFeatureConfig.Builder baseHeight(int i) {
			this.height = i;
			return this;
		}

		public MegaTreeFeatureConfig.Builder heightInterval(int heightInterval) {
			this.heightInterval = heightInterval;
			return this;
		}

		public MegaTreeFeatureConfig.Builder crownHeight(int crownHeight) {
			this.crownHeight = crownHeight;
			return this;
		}

		public MegaTreeFeatureConfig build() {
			return new MegaTreeFeatureConfig(this.trunkProvider, this.leavesProvider, this.treeDecorators, this.height, this.heightInterval, this.crownHeight);
		}
	}
}
