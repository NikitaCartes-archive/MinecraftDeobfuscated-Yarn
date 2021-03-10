package net.minecraft.world.gen.carver;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.YOffset;

public class ConfiguredCarvers {
	public static final ConfiguredCarver<CarverConfig> CAVE = register(
		"cave", Carver.CAVE.configure(new CarverConfig(0.25F, CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState())))
	);
	public static final ConfiguredCarver<RavineCarverConfig> CANYON = register(
		"canyon",
		Carver.RAVINE
			.configure(
				new RavineCarverConfig(
					0.02F,
					CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()),
					YOffset.fixed(10),
					YOffset.fixed(67),
					UniformIntDistribution.of(3),
					UniformFloatProvider.create(0.75F, 0.25F),
					UniformFloatProvider.create(-0.125F, 0.25F),
					TrapezoidFloatProvider.create(0.0F, 6.0F, 2.0F),
					3,
					UniformFloatProvider.create(0.75F, 0.25F),
					1.0F,
					0.0F
				)
			)
	);
	public static final ConfiguredCarver<CarverConfig> OCEAN_CAVE = register(
		"ocean_cave", Carver.CAVE.configure(new CarverConfig(0.125F, CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState())))
	);
	public static final ConfiguredCarver<CarverConfig> NETHER_CAVE = register("nether_cave", Carver.NETHER_CAVE.configure(new CarverConfig(0.2F)));
	public static final ConfiguredCarver<RavineCarverConfig> CRACK = register(
		"crack",
		Carver.RAVINE
			.configure(
				new RavineCarverConfig(
					0.00125F,
					CarverDebugConfig.create(false, Blocks.OAK_BUTTON.getDefaultState()),
					YOffset.fixed(40),
					YOffset.fixed(80),
					UniformIntDistribution.of(6, 2),
					UniformFloatProvider.create(0.5F, 0.5F),
					UniformFloatProvider.create(-0.125F, 0.25F),
					UniformFloatProvider.create(0.0F, 1.0F),
					6,
					UniformFloatProvider.create(0.25F, 0.75F),
					0.0F,
					5.0F
				)
			)
	);

	private static <WC extends CarverConfig> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
	}
}
