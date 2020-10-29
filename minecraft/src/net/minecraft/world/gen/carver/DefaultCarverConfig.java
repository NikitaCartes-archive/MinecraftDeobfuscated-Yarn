package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;

public class DefaultCarverConfig implements CarverConfig {
	public static final Codec<DefaultCarverConfig> CODEC = Codec.unit((Supplier<DefaultCarverConfig>)(() -> DefaultCarverConfig.INSTANCE));
	public static final DefaultCarverConfig INSTANCE = new DefaultCarverConfig();
}
