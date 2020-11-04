package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.tree.TreeDecorator;
import net.minecraft.world.gen.trunk.TrunkPlacer;

public class TreeFeatureConfig implements FeatureConfig {
	public static final Codec<TreeFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockStateProvider.TYPE_CODEC.fieldOf("trunk_provider").forGetter(treeFeatureConfig -> treeFeatureConfig.trunkProvider),
					BlockStateProvider.TYPE_CODEC.fieldOf("leaves_provider").forGetter(treeFeatureConfig -> treeFeatureConfig.leavesProvider),
					FoliagePlacer.TYPE_CODEC.fieldOf("foliage_placer").forGetter(treeFeatureConfig -> treeFeatureConfig.foliagePlacer),
					TrunkPlacer.CODEC.fieldOf("trunk_placer").forGetter(treeFeatureConfig -> treeFeatureConfig.trunkPlacer),
					FeatureSize.TYPE_CODEC.fieldOf("minimum_size").forGetter(treeFeatureConfig -> treeFeatureConfig.minimumSize),
					TreeDecorator.TYPE_CODEC.listOf().fieldOf("decorators").forGetter(treeFeatureConfig -> treeFeatureConfig.decorators),
					Codec.INT.fieldOf("max_water_depth").orElse(0).forGetter(treeFeatureConfig -> treeFeatureConfig.maxWaterDepth),
					Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter(treeFeatureConfig -> treeFeatureConfig.ignoreVines),
					Heightmap.Type.CODEC.fieldOf("heightmap").forGetter(treeFeatureConfig -> treeFeatureConfig.heightmap)
				)
				.apply(instance, TreeFeatureConfig::new)
	);
	public final BlockStateProvider trunkProvider;
	public final BlockStateProvider leavesProvider;
	public final List<TreeDecorator> decorators;
	public transient boolean skipFluidCheck;
	public final FoliagePlacer foliagePlacer;
	public final TrunkPlacer trunkPlacer;
	public final FeatureSize minimumSize;
	public final int maxWaterDepth;
	public final boolean ignoreVines;
	public final Heightmap.Type heightmap;

	protected TreeFeatureConfig(
		BlockStateProvider trunkProvider,
		BlockStateProvider leavesProvider,
		FoliagePlacer foliagePlacer,
		TrunkPlacer trunkPlacer,
		FeatureSize minimumSize,
		List<TreeDecorator> decorators,
		int maxWaterDepth,
		boolean ignoreVines,
		Heightmap.Type heightmap
	) {
		this.trunkProvider = trunkProvider;
		this.leavesProvider = leavesProvider;
		this.decorators = decorators;
		this.foliagePlacer = foliagePlacer;
		this.minimumSize = minimumSize;
		this.trunkPlacer = trunkPlacer;
		this.maxWaterDepth = maxWaterDepth;
		this.ignoreVines = ignoreVines;
		this.heightmap = heightmap;
	}

	public void ignoreFluidCheck() {
		this.skipFluidCheck = true;
	}

	public TreeFeatureConfig setTreeDecorators(List<TreeDecorator> decorators) {
		return new TreeFeatureConfig(
			this.trunkProvider,
			this.leavesProvider,
			this.foliagePlacer,
			this.trunkPlacer,
			this.minimumSize,
			decorators,
			this.maxWaterDepth,
			this.ignoreVines,
			this.heightmap
		);
	}

	public static class Builder {
		public final BlockStateProvider trunkProvider;
		public final BlockStateProvider leavesProvider;
		private final FoliagePlacer foliagePlacer;
		private final TrunkPlacer trunkPlacer;
		private final FeatureSize minimumSize;
		private List<TreeDecorator> decorators = ImmutableList.of();
		private int maxWaterDepth;
		private boolean ignoreVines;
		private Heightmap.Type heightmap = Heightmap.Type.OCEAN_FLOOR;

		public Builder(
			BlockStateProvider trunkProvider, BlockStateProvider leavesProvider, FoliagePlacer foliagePlacer, TrunkPlacer trunkPlacer, FeatureSize minimumSize
		) {
			this.trunkProvider = trunkProvider;
			this.leavesProvider = leavesProvider;
			this.foliagePlacer = foliagePlacer;
			this.trunkPlacer = trunkPlacer;
			this.minimumSize = minimumSize;
		}

		public TreeFeatureConfig.Builder decorators(List<TreeDecorator> decorators) {
			this.decorators = decorators;
			return this;
		}

		public TreeFeatureConfig.Builder maxWaterDepth(int maxWaterDepth) {
			this.maxWaterDepth = maxWaterDepth;
			return this;
		}

		public TreeFeatureConfig.Builder ignoreVines() {
			this.ignoreVines = true;
			return this;
		}

		public TreeFeatureConfig.Builder heightmap(Heightmap.Type heightmap) {
			this.heightmap = heightmap;
			return this;
		}

		public TreeFeatureConfig build() {
			return new TreeFeatureConfig(
				this.trunkProvider,
				this.leavesProvider,
				this.foliagePlacer,
				this.trunkPlacer,
				this.minimumSize,
				this.decorators,
				this.maxWaterDepth,
				this.ignoreVines,
				this.heightmap
			);
		}
	}
}
