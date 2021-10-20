package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SmallDripstoneFeatureConfig implements FeatureConfig {
	public static final Codec<SmallDripstoneFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.floatRange(0.0F, 1.0F)
						.fieldOf("chance_of_taller_dripstone")
						.orElse(0.2F)
						.forGetter(smallDripstoneFeatureConfig -> smallDripstoneFeatureConfig.chanceOfTallerDripstone),
					Codec.floatRange(0.0F, 1.0F)
						.fieldOf("chance_of_directional_spread")
						.orElse(0.7F)
						.forGetter(smallDripstoneFeatureConfig -> smallDripstoneFeatureConfig.field_35416),
					Codec.floatRange(0.0F, 1.0F)
						.fieldOf("chance_of_spread_radius2")
						.orElse(0.5F)
						.forGetter(smallDripstoneFeatureConfig -> smallDripstoneFeatureConfig.field_35417),
					Codec.floatRange(0.0F, 1.0F)
						.fieldOf("chance_of_spread_radius3")
						.orElse(0.5F)
						.forGetter(smallDripstoneFeatureConfig -> smallDripstoneFeatureConfig.field_35418)
				)
				.apply(instance, SmallDripstoneFeatureConfig::new)
	);
	public final float chanceOfTallerDripstone;
	public final float field_35416;
	public final float field_35417;
	public final float field_35418;

	public SmallDripstoneFeatureConfig(float f, float g, float h, float chanceOfTallerDripstone) {
		this.chanceOfTallerDripstone = f;
		this.field_35416 = g;
		this.field_35417 = h;
		this.field_35418 = chanceOfTallerDripstone;
	}
}
