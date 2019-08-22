package net.minecraft.world.biome.source;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.registry.Registry;

public class BiomeSourceType<C extends BiomeSourceConfig, T extends BiomeSource> {
	public static final BiomeSourceType<CheckerboardBiomeSourceConfig, CheckerboardBiomeSource> CHECKERBOARD = register(
		"checkerboard", CheckerboardBiomeSource::new, CheckerboardBiomeSourceConfig::new
	);
	public static final BiomeSourceType<FixedBiomeSourceConfig, FixedBiomeSource> FIXED = register("fixed", FixedBiomeSource::new, FixedBiomeSourceConfig::new);
	public static final BiomeSourceType<VanillaLayeredBiomeSourceConfig, VanillaLayeredBiomeSource> VANILLA_LAYERED = register(
		"vanilla_layered", VanillaLayeredBiomeSource::new, VanillaLayeredBiomeSourceConfig::new
	);
	public static final BiomeSourceType<TheEndBiomeSourceConfig, TheEndBiomeSource> THE_END = register(
		"the_end", TheEndBiomeSource::new, TheEndBiomeSourceConfig::new
	);
	private final Function<C, T> biomeSource;
	private final Supplier<C> config;

	private static <C extends BiomeSourceConfig, T extends BiomeSource> BiomeSourceType<C, T> register(
		String string, Function<C, T> function, Supplier<C> supplier
	) {
		return Registry.register(Registry.BIOME_SOURCE_TYPE, string, new BiomeSourceType<>(function, supplier));
	}

	public BiomeSourceType(Function<C, T> function, Supplier<C> supplier) {
		this.biomeSource = function;
		this.config = supplier;
	}

	public T applyConfig(C biomeSourceConfig) {
		return (T)this.biomeSource.apply(biomeSourceConfig);
	}

	public C getConfig() {
		return (C)this.config.get();
	}
}
