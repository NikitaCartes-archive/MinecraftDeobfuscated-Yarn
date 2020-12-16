package net.minecraft.world.gen.feature.size;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class TwoLayersFeatureSize extends FeatureSize {
	public static final Codec<TwoLayersFeatureSize> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(0, 81).fieldOf("limit").orElse(1).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.limit),
					Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.lowerSize),
					Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.upperSize),
					createCodec()
				)
				.apply(instance, TwoLayersFeatureSize::new)
	);
	private final int limit;
	private final int lowerSize;
	private final int upperSize;

	public TwoLayersFeatureSize(int limit, int lowerSize, int upperSize) {
		this(limit, lowerSize, upperSize, OptionalInt.empty());
	}

	public TwoLayersFeatureSize(int limit, int lowerSize, int upperSize, OptionalInt minClippedHeight) {
		super(minClippedHeight);
		this.limit = limit;
		this.lowerSize = lowerSize;
		this.upperSize = upperSize;
	}

	@Override
	protected FeatureSizeType<?> getType() {
		return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
	}

	@Override
	public int getRadius(int height, int y) {
		return y < this.limit ? this.lowerSize : this.upperSize;
	}
}
