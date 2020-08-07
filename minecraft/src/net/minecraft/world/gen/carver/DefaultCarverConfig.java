package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;

public class DefaultCarverConfig implements CarverConfig {
	public static final Codec<DefaultCarverConfig> field_24829 = Codec.unit((Supplier<DefaultCarverConfig>)(() -> DefaultCarverConfig.field_24830));
	public static final DefaultCarverConfig field_24830 = new DefaultCarverConfig();
}
