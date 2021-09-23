package net.minecraft.world.gen.carver;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredCarvers {
	public static final ConfiguredCarver<CaveCarverConfig> CAVE = register(
		"cave",
		Carver.CAVE
			.configure(
				new CaveCarverConfig(
					0.15F,
					UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(180)),
					UniformFloatProvider.create(0.1F, 0.9F),
					YOffset.aboveBottom(8),
					CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()),
					UniformFloatProvider.create(0.7F, 1.4F),
					UniformFloatProvider.create(0.8F, 1.3F),
					UniformFloatProvider.create(-1.0F, -0.4F)
				)
			)
	);
	public static final ConfiguredCarver<CaveCarverConfig> CAVE_EXTRA_UNDERGROUND = register(
		"cave_extra_underground",
		Carver.CAVE
			.configure(
				new CaveCarverConfig(
					0.07F,
					UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(47)),
					UniformFloatProvider.create(0.1F, 0.9F),
					YOffset.aboveBottom(8),
					CarverDebugConfig.create(false, Blocks.OAK_BUTTON.getDefaultState()),
					UniformFloatProvider.create(0.7F, 1.4F),
					UniformFloatProvider.create(0.8F, 1.3F),
					UniformFloatProvider.create(-1.0F, -0.4F)
				)
			)
	);
	public static final ConfiguredCarver<RavineCarverConfig> CANYON = register(
		"canyon",
		Carver.RAVINE
			.configure(
				new RavineCarverConfig(
					0.01F,
					UniformHeightProvider.create(YOffset.fixed(10), YOffset.fixed(67)),
					ConstantFloatProvider.create(3.0F),
					YOffset.aboveBottom(8),
					CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()),
					UniformFloatProvider.create(-0.125F, 0.125F),
					new RavineCarverConfig.Shape(
						UniformFloatProvider.create(0.75F, 1.0F), TrapezoidFloatProvider.create(0.0F, 6.0F, 2.0F), 3, UniformFloatProvider.create(0.75F, 1.0F), 1.0F, 0.0F
					)
				)
			)
	);
	public static final ConfiguredCarver<CaveCarverConfig> NETHER_CAVE = register(
		"nether_cave",
		Carver.NETHER_CAVE
			.configure(
				new CaveCarverConfig(
					0.2F,
					UniformHeightProvider.create(YOffset.fixed(0), YOffset.belowTop(1)),
					ConstantFloatProvider.create(0.5F),
					YOffset.aboveBottom(10),
					false,
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(-0.7F)
				)
			)
	);

	private static <WC extends CarverConfig> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
	}
}
