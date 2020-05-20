package net.minecraft.world.gen.feature.size;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.OptionalInt;
import net.minecraft.util.registry.Registry;

public abstract class FeatureSize {
	public static final Codec<FeatureSize> field_24922 = Registry.FEATURE_SIZE_TYPE.dispatch(FeatureSize::method_28824, FeatureSizeType::method_28825);
	protected final OptionalInt minClippedHeight;

	protected static <S extends FeatureSize> RecordCodecBuilder<S, OptionalInt> method_28820() {
		return Codec.INT
			.optionalFieldOf("min_clipped_height")
			.<OptionalInt>xmap(
				optional -> (OptionalInt)optional.map(OptionalInt::of).orElse(OptionalInt.empty()),
				optionalInt -> optionalInt.isPresent() ? Optional.of(optionalInt.getAsInt()) : Optional.empty()
			)
			.forGetter(featureSize -> featureSize.minClippedHeight);
	}

	public FeatureSize(OptionalInt optionalInt) {
		this.minClippedHeight = optionalInt;
	}

	protected abstract FeatureSizeType<?> method_28824();

	public abstract int method_27378(int i, int j);

	public OptionalInt getMinClippedHeight() {
		return this.minClippedHeight;
	}
}
