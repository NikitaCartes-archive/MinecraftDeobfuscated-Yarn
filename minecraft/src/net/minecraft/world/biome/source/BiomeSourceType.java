package net.minecraft.world.biome.source;

import java.util.function.Function;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.level.LevelProperties;

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
	private final Function<LevelProperties, C> config;

	private static <C extends BiomeSourceConfig, T extends BiomeSource> BiomeSourceType<C, T> register(
		String id, Function<C, T> biomeSource, Function<LevelProperties, C> function
	) {
		return Registry.register(Registry.BIOME_SOURCE_TYPE, id, new BiomeSourceType<>(biomeSource, function));
	}

	private BiomeSourceType(Function<C, T> biomeSource, Function<LevelProperties, C> function) {
		this.biomeSource = biomeSource;
		this.config = function;
	}

	public T applyConfig(C config) {
		return (T)this.biomeSource.apply(config);
	}

	public C getConfig(LevelProperties levelProperties) {
		return (C)this.config.apply(levelProperties);
	}
}
