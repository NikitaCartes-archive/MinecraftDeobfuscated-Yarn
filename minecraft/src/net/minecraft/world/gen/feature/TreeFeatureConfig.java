package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.decorator.TreeDecorator;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.trunk.TrunkPlacer;

public class TreeFeatureConfig implements FeatureConfig {
	public static final Codec<TreeFeatureConfig> field_24921 = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockStateProvider.field_24937.fieldOf("trunk_provider").forGetter(treeFeatureConfig -> treeFeatureConfig.trunkProvider),
					BlockStateProvider.field_24937.fieldOf("leaves_provider").forGetter(treeFeatureConfig -> treeFeatureConfig.leavesProvider),
					FoliagePlacer.field_24931.fieldOf("foliage_placer").forGetter(treeFeatureConfig -> treeFeatureConfig.foliagePlacer),
					TrunkPlacer.field_24972.fieldOf("trunk_placer").forGetter(treeFeatureConfig -> treeFeatureConfig.trunkPlacer),
					FeatureSize.field_24922.fieldOf("minimum_size").forGetter(treeFeatureConfig -> treeFeatureConfig.featureSize),
					TreeDecorator.field_24962.listOf().fieldOf("decorators").forGetter(treeFeatureConfig -> treeFeatureConfig.decorators),
					Codec.INT.fieldOf("max_water_depth").withDefault(0).forGetter(treeFeatureConfig -> treeFeatureConfig.baseHeight),
					Codec.BOOL.fieldOf("ignore_vines").withDefault(false).forGetter(treeFeatureConfig -> treeFeatureConfig.ignoreVines),
					Heightmap.Type.field_24772.fieldOf("heightmap").forGetter(treeFeatureConfig -> treeFeatureConfig.heightmap)
				)
				.apply(instance, TreeFeatureConfig::new)
	);
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
