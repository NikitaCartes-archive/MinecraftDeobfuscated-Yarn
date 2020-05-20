package net.minecraft.world.gen.feature.size;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;

public class TwoLayersFeatureSize extends FeatureSize {
	public static final Codec<TwoLayersFeatureSize> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("limit").withDefault(1).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.field_24155),
					Codec.INT.fieldOf("lower_size").withDefault(0).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.field_24156),
					Codec.INT.fieldOf("upper_size").withDefault(1).forGetter(twoLayersFeatureSize -> twoLayersFeatureSize.field_24157),
					method_28820()
				)
				.apply(instance, TwoLayersFeatureSize::new)
	);
	private final int field_24155;
	private final int field_24156;
	private final int field_24157;

	public TwoLayersFeatureSize(int i, int j, int k) {
		this(i, j, k, OptionalInt.empty());
	}

	public TwoLayersFeatureSize(int i, int j, int k, OptionalInt minClippedHeight) {
		super(minClippedHeight);
		this.field_24155 = i;
		this.field_24156 = j;
		this.field_24157 = k;
	}

	@Override
	protected FeatureSizeType<?> method_28824() {
		return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
	}

	@Override
	public int method_27378(int i, int j) {
		return j < this.field_24155 ? this.field_24156 : this.field_24157;
	}
}
