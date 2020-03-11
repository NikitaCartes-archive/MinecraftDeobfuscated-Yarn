package net.minecraft.world.biome.source;

import java.util.function.Function;
import java.util.function.LongFunction;
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
	public static final BiomeSourceType<MultiNoiseBiomeSourceConfig, MultiNoiseBiomeSource> MULTI_NOISE = register(
		"multi_noise", MultiNoiseBiomeSource::new, MultiNoiseBiomeSourceConfig::new
	);
	private final Function<C, T> biomeSource;
	private final LongFunction<C> configFactory;

	private static <C extends BiomeSourceConfig, T extends BiomeSource> BiomeSourceType<C, T> register(
		String id, Function<C, T> biomeSource, LongFunction<C> longFunction
	) {
		return Registry.register(Registry.BIOME_SOURCE_TYPE, id, new BiomeSourceType<>(biomeSource, longFunction));
	}

	private BiomeSourceType(Function<C, T> biomeSource, LongFunction<C> configFactory) {
		this.biomeSource = biomeSource;
		this.configFactory = configFactory;
	}

	public T applyConfig(C config) {
		return (T)this.biomeSource.apply(config);
	}

	public C getConfig(long seed) {
		return (C)this.configFactory.apply(seed);
	}
}
