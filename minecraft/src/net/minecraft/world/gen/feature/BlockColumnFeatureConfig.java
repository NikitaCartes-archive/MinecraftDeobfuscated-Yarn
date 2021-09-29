package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public record BlockColumnFeatureConfig() implements FeatureConfig {
	private final List<BlockColumnFeatureConfig.Layer> layers;
	private final Direction direction;
	private final boolean allowWater;
	private final boolean prioritizeTip;
	public static final Codec<BlockColumnFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockColumnFeatureConfig.Layer.CODEC.listOf().fieldOf("layers").forGetter(BlockColumnFeatureConfig::layers),
					Direction.CODEC.fieldOf("direction").forGetter(BlockColumnFeatureConfig::direction),
					Codec.BOOL.fieldOf("allow_water").forGetter(BlockColumnFeatureConfig::allowWater),
					Codec.BOOL.fieldOf("prioritize_tip").forGetter(BlockColumnFeatureConfig::prioritizeTip)
				)
				.apply(instance, BlockColumnFeatureConfig::new)
	);

	public BlockColumnFeatureConfig(List<BlockColumnFeatureConfig.Layer> list, Direction direction, boolean bl, boolean bl2) {
		this.layers = list;
		this.direction = direction;
		this.allowWater = bl;
		this.prioritizeTip = bl2;
	}

	public static BlockColumnFeatureConfig.Layer createLayer(IntProvider height, BlockStateProvider state) {
		return new BlockColumnFeatureConfig.Layer(height, state);
	}

	public static BlockColumnFeatureConfig create(IntProvider height, BlockStateProvider state) {
		return new BlockColumnFeatureConfig(List.of(createLayer(height, state)), Direction.UP, false, false);
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
