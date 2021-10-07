package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record BlockColumnFeatureConfig() implements FeatureConfig {
	private final List<BlockColumnFeatureConfig.Layer> layers;
	private final Direction direction;
	private final BlockPredicate allowedPlacement;
	private final boolean prioritizeTip;
	public static final Codec<BlockColumnFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockColumnFeatureConfig.Layer.CODEC.listOf().fieldOf("layers").forGetter(BlockColumnFeatureConfig::layers),
					Direction.CODEC.fieldOf("direction").forGetter(BlockColumnFeatureConfig::direction),
					BlockPredicate.BASE_CODEC.fieldOf("allowed_placement").forGetter(BlockColumnFeatureConfig::allowedPlacement),
					Codec.BOOL.fieldOf("prioritize_tip").forGetter(BlockColumnFeatureConfig::prioritizeTip)
				)
				.apply(instance, BlockColumnFeatureConfig::new)
	);

	public BlockColumnFeatureConfig(List<BlockColumnFeatureConfig.Layer> list, Direction direction, BlockPredicate blockPredicate, boolean bl) {
		this.layers = list;
		this.direction = direction;
		this.allowedPlacement = blockPredicate;
		this.prioritizeTip = bl;
	}

	public static BlockColumnFeatureConfig.Layer createLayer(IntProvider height, BlockStateProvider state) {
		return new BlockColumnFeatureConfig.Layer(height, state);
	}

	public static BlockColumnFeatureConfig create(IntProvider height, BlockStateProvider state) {
		return new BlockColumnFeatureConfig(List.of(createLayer(height, state)), Direction.UP, BlockPredicate.matchingBlock(Blocks.AIR, BlockPos.ORIGIN), false);
	}

	public static record Layer() {
		private final IntProvider height;
		private final BlockStateProvider state;
		public static final Codec<BlockColumnFeatureConfig.Layer> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						IntProvider.NON_NEGATIVE_CODEC.fieldOf("height").forGetter(BlockColumnFeatureConfig.Layer::height),
						BlockStateProvider.TYPE_CODEC.fieldOf("provider").forGetter(BlockColumnFeatureConfig.Layer::state)
					)
					.apply(instance, BlockColumnFeatureConfig.Layer::new)
		);

		public Layer(IntProvider intProvider, BlockStateProvider blockStateProvider) {
			this.height = intProvider;
			this.state = blockStateProvider;
		}
	}
}
