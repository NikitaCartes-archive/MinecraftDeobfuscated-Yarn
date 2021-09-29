package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.lang.runtime.ObjectMethods;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public final class SimpleBlockFeatureConfig extends Record implements FeatureConfig {
	private final BlockStateProvider toPlace;
	public static final Codec<SimpleBlockFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(BlockStateProvider.TYPE_CODEC.fieldOf("to_place").forGetter(simpleBlockFeatureConfig -> simpleBlockFeatureConfig.toPlace))
				.apply(instance, SimpleBlockFeatureConfig::new)
	);

	public SimpleBlockFeatureConfig(BlockStateProvider toPlace) {
		this.toPlace = toPlace;
	}

	public final String toString() {
		return ObjectMethods.bootstrap<"toString",SimpleBlockFeatureConfig,"toPlace",SimpleBlockFeatureConfig::toPlace>(this);
	}

	public final int hashCode() {
		return ObjectMethods.bootstrap<"hashCode",SimpleBlockFeatureConfig,"toPlace",SimpleBlockFeatureConfig::toPlace>(this);
	}

	public final boolean equals(Object object) {
		return ObjectMethods.bootstrap<"equals",SimpleBlockFeatureConfig,"toPlace",SimpleBlockFeatureConfig::toPlace>(this, object);
	}

	public BlockStateProvider toPlace() {
		return this.toPlace;
	}
}
