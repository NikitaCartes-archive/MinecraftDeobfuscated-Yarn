package net.minecraft.world.gen.carver;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.ProbabilityConfig;

public class ConfiguredCarvers {
	public static final ConfiguredCarver<ProbabilityConfig> CAVE = register("cave", Carver.CAVE.method_28614(new ProbabilityConfig(0.14285715F)));
	public static final ConfiguredCarver<ProbabilityConfig> CANYON = register("canyon", Carver.CANYON.method_28614(new ProbabilityConfig(0.02F)));
	public static final ConfiguredCarver<ProbabilityConfig> OCEAN_CAVE = register("ocean_cave", Carver.CAVE.method_28614(new ProbabilityConfig(0.06666667F)));
	public static final ConfiguredCarver<ProbabilityConfig> UNDERWATER_CANYON = register(
		"underwater_canyon", Carver.UNDERWATER_CANYON.method_28614(new ProbabilityConfig(0.02F))
	);
	public static final ConfiguredCarver<ProbabilityConfig> UNDERWATER_CAVE = register(
		"underwater_cave", Carver.UNDERWATER_CAVE.method_28614(new ProbabilityConfig(0.06666667F))
	);
	public static final ConfiguredCarver<ProbabilityConfig> NETHER_CAVE = register("nether_cave", Carver.NETHER_CAVE.method_28614(new ProbabilityConfig(0.2F)));

	private static <WC extends CarverConfig> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
	}
}
