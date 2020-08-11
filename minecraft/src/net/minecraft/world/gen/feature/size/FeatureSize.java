package net.minecraft.world.gen.feature.size;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.util.registry.Registry;

public abstract class FeatureSize {
	public static final Codec<FeatureSize> TYPE_CODEC = Registry.FEATURE_SIZE_TYPE.dispatch(FeatureSize::getType, FeatureSizeType::getCodec);
	protected final OptionalInt minClippedHeight;

	protected static <S extends FeatureSize> RecordCodecBuilder<S, OptionalInt> createCodecBuilder() {
		return Codec.intRange(0, 80)
			.optionalFieldOf("min_clipped_height")
			.xmap(
				optional -> (OptionalInt)optional.map(OptionalInt::of).orElse(OptionalInt.empty()),
				optionalInt -> optionalInt.isPresent() ? Optional.of(optionalInt.getAsInt()) : Optional.empty()
			)
			.forGetter(featureSize -> featureSize.minClippedHeight);
	}

	public FeatureSize(OptionalInt minClippedHeight) {
		this.minClippedHeight = minClippedHeight;
	}

	protected abstract FeatureSizeType<?> getType();

	public abstract int method_27378(int i, int j);

	public OptionalInt getMinClippedHeight() {
		return this.minClippedHeight;
	}
}
