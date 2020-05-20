package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;

public class DefaultFeatureConfig implements FeatureConfig {
	public static final Codec<DefaultFeatureConfig> CODEC = Codec.unit((Supplier<DefaultFeatureConfig>)(() -> DefaultFeatureConfig.INSTANCE));
	public static final DefaultFeatureConfig INSTANCE = new DefaultFeatureConfig();
}
