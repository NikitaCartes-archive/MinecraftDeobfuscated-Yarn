package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.lang.runtime.ObjectMethods;
import java.util.List;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public final class BlockColumnFeatureConfig extends Record implements FeatureConfig {
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

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",BlockColumnFeatureConfig,"layers;direction;allowWater;prioritizeTip",BlockColumnFeatureConfig::layers,BlockColumnFeatureConfig::direction,BlockColumnFeatureConfig::allowWater,BlockColumnFeatureConfig::prioritizeTip>(
			this
		);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",BlockColumnFeatureConfig,"layers;direction;allowWater;prioritizeTip",BlockColumnFeatureConfig::layers,BlockColumnFeatureConfig::direction,BlockColumnFeatureConfig::allowWater,BlockColumnFeatureConfig::prioritizeTip>(
			this
		);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",BlockColumnFeatureConfig,"layers;direction;allowWater;prioritizeTip",BlockColumnFeatureConfig::layers,BlockColumnFeatureConfig::direction,BlockColumnFeatureConfig::allowWater,BlockColumnFeatureConfig::prioritizeTip>(
			this, object
		);
	}

	public List<BlockColumnFeatureConfig.Layer> layers() {
		return this.layers;
	}

	public Direction direction() {
		return this.direction;
	}

	public boolean allowWater() {
		return this.allowWater;
	}

	public boolean prioritizeTip() {
		return this.prioritizeTip;
	}

	public static final class Layer extends Record {
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

		public final String toString() {
			return ObjectMethods.bootstrap<"toString",BlockColumnFeatureConfig.Layer,"height;state",BlockColumnFeatureConfig.Layer::height,BlockColumnFeatureConfig.Layer::state>(
				this
			);
		}

		public final int hashCode() {
			return ObjectMethods.bootstrap<"hashCode",BlockColumnFeatureConfig.Layer,"height;state",BlockColumnFeatureConfig.Layer::height,BlockColumnFeatureConfig.Layer::state>(
				this
			);
		}

		public final boolean equals(Object object) {
			return ObjectMethods.bootstrap<"equals",BlockColumnFeatureConfig.Layer,"height;state",BlockColumnFeatureConfig.Layer::height,BlockColumnFeatureConfig.Layer::state>(
				this, object
			);
		}

		public IntProvider height() {
			return this.height;
		}

		public BlockStateProvider state() {
			return this.state;
		}
	}
}
