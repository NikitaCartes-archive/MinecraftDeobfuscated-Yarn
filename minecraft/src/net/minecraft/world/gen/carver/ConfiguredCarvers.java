package net.minecraft.world.gen.carver;

import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.ProbabilityConfig;

public class ConfiguredCarvers {
	public static final ConfiguredCarver<ProbabilityConfig> field_25942 = register("cave", Carver.field_13304.method_28614(new ProbabilityConfig(0.14285715F)));
	public static final ConfiguredCarver<ProbabilityConfig> field_25943 = register("canyon", Carver.field_13295.method_28614(new ProbabilityConfig(0.02F)));
	public static final ConfiguredCarver<ProbabilityConfig> field_25944 = register(
		"ocean_cave", Carver.field_13304.method_28614(new ProbabilityConfig(0.06666667F))
	);
	public static final ConfiguredCarver<ProbabilityConfig> field_25945 = register(
		"underwater_canyon", Carver.field_13303.method_28614(new ProbabilityConfig(0.02F))
	);
	public static final ConfiguredCarver<ProbabilityConfig> field_25946 = register(
		"underwater_cave", Carver.field_13300.method_28614(new ProbabilityConfig(0.06666667F))
	);
	public static final ConfiguredCarver<ProbabilityConfig> field_25947 = register("nether_cave", Carver.field_13297.method_28614(new ProbabilityConfig(0.2F)));

	private static <WC extends CarverConfig> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
	}
}
