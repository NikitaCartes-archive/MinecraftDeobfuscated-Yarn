package net.minecraft.world.gen.feature.size;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class ThreeLayersFeatureSize extends FeatureSize {
	public static final Codec<ThreeLayersFeatureSize> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(0, 80).fieldOf("limit").orElse(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.limit),
					Codec.intRange(0, 80).fieldOf("upper_limit").orElse(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.upperLimit),
					Codec.intRange(0, 16).fieldOf("lower_size").orElse(0).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.lowerSize),
					Codec.intRange(0, 16).fieldOf("middle_size").orElse(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.middleSize),
					Codec.intRange(0, 16).fieldOf("upper_size").orElse(1).forGetter(threeLayersFeatureSize -> threeLayersFeatureSize.upperSize),
					createCodec()
				)
				.apply(instance, ThreeLayersFeatureSize::new)
	);
	private final int limit;
	private final int upperLimit;
	private final int lowerSize;
	private final int middleSize;
	private final int upperSize;

	public ThreeLayersFeatureSize(int limit, int upperLimit, int lowerSize, int middleSize, int upperSize, OptionalInt minClippedHeight) {
		super(minClippedHeight);
		this.limit = limit;
		this.upperLimit = upperLimit;
		this.lowerSize = lowerSize;
		this.middleSize = middleSize;
		this.upperSize = upperSize;
	}

	@Override
	protected FeatureSizeType<?> getType() {
		return FeatureSizeType.THREE_LAYERS_FEATURE_SIZE;
	}

	@Override
	public int getRadius(int height, int y) {
		if (y < this.limit) {
			return this.lowerSize;
		} else {
			return y >= height - this.upperLimit ? this.upperSize : this.middleSize;
		}
	}
}
