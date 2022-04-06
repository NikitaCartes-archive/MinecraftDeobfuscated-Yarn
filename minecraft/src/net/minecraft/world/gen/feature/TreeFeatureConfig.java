package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.feature.size.FeatureSize;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.root.RootPlacer;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.treedecorator.TreeDecorator;
import net.minecraft.world.gen.trunk.TrunkPlacer;

public class TreeFeatureConfig implements FeatureConfig {
	public static final Codec<TreeFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockStateProvider.TYPE_CODEC.fieldOf("trunk_provider").forGetter(config -> config.trunkProvider),
					TrunkPlacer.TYPE_CODEC.fieldOf("trunk_placer").forGetter(config -> config.trunkPlacer),
					BlockStateProvider.TYPE_CODEC.fieldOf("foliage_provider").forGetter(config -> config.foliageProvider),
					FoliagePlacer.TYPE_CODEC.fieldOf("foliage_placer").forGetter(config -> config.foliagePlacer),
					RootPlacer.TYPE_CODEC.optionalFieldOf("root_placer").forGetter(config -> config.rootPlacer),
					BlockStateProvider.TYPE_CODEC.fieldOf("dirt_provider").forGetter(config -> config.dirtProvider),
					FeatureSize.TYPE_CODEC.fieldOf("minimum_size").forGetter(config -> config.minimumSize),
					TreeDecorator.TYPE_CODEC.listOf().fieldOf("decorators").forGetter(config -> config.decorators),
					Codec.BOOL.fieldOf("ignore_vines").orElse(false).forGetter(config -> config.ignoreVines),
					Codec.BOOL.fieldOf("force_dirt").orElse(false).forGetter(config -> config.forceDirt)
				)
				.apply(instance, TreeFeatureConfig::new)
	);
	public final BlockStateProvider trunkProvider;
	public final BlockStateProvider dirtProvider;
	public final TrunkPlacer trunkPlacer;
	public final BlockStateProvider foliageProvider;
	public final FoliagePlacer foliagePlacer;
	public final Optional<RootPlacer> rootPlacer;
	public final FeatureSize minimumSize;
	public final List<TreeDecorator> decorators;
	public final boolean ignoreVines;
	public final boolean forceDirt;

	protected TreeFeatureConfig(
		BlockStateProvider trunkProvider,
		TrunkPlacer trunkPlacer,
		BlockStateProvider foliageProvider,
		FoliagePlacer foliagePlacer,
		Optional<RootPlacer> rootPlacer,
		BlockStateProvider dirtProvider,
		FeatureSize minimumSize,
		List<TreeDecorator> decorators,
		boolean ignoreVines,
		boolean forceDirt
	) {
		this.trunkProvider = trunkProvider;
		this.trunkPlacer = trunkPlacer;
		this.foliageProvider = foliageProvider;
		this.foliagePlacer = foliagePlacer;
		this.rootPlacer = rootPlacer;
		this.dirtProvider = dirtProvider;
		this.minimumSize = minimumSize;
		this.decorators = decorators;
		this.ignoreVines = ignoreVines;
		this.forceDirt = forceDirt;
	}

	public static class Builder {
		public final BlockStateProvider trunkProvider;
		private final TrunkPlacer trunkPlacer;
		public final BlockStateProvider foliageProvider;
		private final FoliagePlacer foliagePlacer;
		private final Optional<RootPlacer> rootPlacer;
		private BlockStateProvider dirtProvider;
		private final FeatureSize minimumSize;
		private List<TreeDecorator> decorators = ImmutableList.of();
		private boolean ignoreVines;
		private boolean forceDirt;

		public Builder(
			BlockStateProvider trunkProvider,
			TrunkPlacer trunkPlacer,
			BlockStateProvider foliageProvider,
			FoliagePlacer foliagePlacer,
			Optional<RootPlacer> rootPlacer,
			FeatureSize minimumSize
		) {
			this.trunkProvider = trunkProvider;
			this.trunkPlacer = trunkPlacer;
			this.foliageProvider = foliageProvider;
			this.dirtProvider = BlockStateProvider.of(Blocks.DIRT);
			this.foliagePlacer = foliagePlacer;
			this.rootPlacer = rootPlacer;
			this.minimumSize = minimumSize;
		}

		public Builder(
			BlockStateProvider trunkProvider, TrunkPlacer trunkPlacer, BlockStateProvider foliageProvider, FoliagePlacer foliagePlacer, FeatureSize minimumSize
		) {
			this(trunkProvider, trunkPlacer, foliageProvider, foliagePlacer, Optional.empty(), minimumSize);
		}

		public TreeFeatureConfig.Builder dirtProvider(BlockStateProvider dirtProvider) {
			this.dirtProvider = dirtProvider;
			return this;
		}

		public TreeFeatureConfig.Builder decorators(List<TreeDecorator> decorators) {
			this.decorators = decorators;
			return this;
		}

		public TreeFeatureConfig.Builder ignoreVines() {
			this.ignoreVines = true;
			return this;
		}

		public TreeFeatureConfig.Builder forceDirt() {
			this.forceDirt = true;
			return this;
		}

		public TreeFeatureConfig build() {
			return new TreeFeatureConfig(
				this.trunkProvider,
				this.trunkPlacer,
				this.foliageProvider,
				this.foliagePlacer,
				this.rootPlacer,
				this.dirtProvider,
				this.minimumSize,
				this.decorators,
				this.ignoreVines,
				this.forceDirt
			);
		}
	}
}
