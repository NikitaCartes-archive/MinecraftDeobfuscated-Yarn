package net.minecraft.world.gen.carver;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.BiasedToBottomHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredCarvers {
	public static final ConfiguredCarver<CaveCarverConfig> CAVE = register(
		"cave",
		Carver.CAVE
			.configure(
				new CaveCarverConfig(
					0.14285715F,
					BiasedToBottomHeightProvider.create(YOffset.fixed(0), YOffset.fixed(127), 8),
					ConstantFloatProvider.create(0.5F),
					YOffset.aboveBottom(10),
					false,
					CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(-0.7F)
				)
			)
	);
	public static final ConfiguredCarver<CaveCarverConfig> PROTOTYPE_CAVE = register(
		"prototype_cave",
		Carver.CAVE
			.configure(
				new CaveCarverConfig(
					0.33333334F,
					UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(126)),
					UniformFloatProvider.create(0.1F, 0.9F),
					YOffset.aboveBottom(8),
					false,
					CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()),
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
					0.02F,
					BiasedToBottomHeightProvider.create(YOffset.fixed(20), YOffset.fixed(67), 8),
					ConstantFloatProvider.create(3.0F),
					YOffset.aboveBottom(10),
					false,
					CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()),
					UniformFloatProvider.create(-0.125F, 0.125F),
					new RavineCarverConfig.Shape(
						UniformFloatProvider.create(0.75F, 1.0F), TrapezoidFloatProvider.create(0.0F, 6.0F, 2.0F), 3, UniformFloatProvider.create(0.75F, 1.0F), 1.0F, 0.0F
					)
				)
			)
	);
	public static final ConfiguredCarver<RavineCarverConfig> PROTOTYPE_CANYON = register(
		"prototype_canyon",
		Carver.RAVINE
			.configure(
				new RavineCarverConfig(
					0.02F,
					UniformHeightProvider.create(YOffset.fixed(10), YOffset.fixed(67)),
					ConstantFloatProvider.create(3.0F),
					YOffset.aboveBottom(8),
					false,
					CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()),
					UniformFloatProvider.create(-0.125F, 0.125F),
					new RavineCarverConfig.Shape(
						UniformFloatProvider.create(0.75F, 1.0F), TrapezoidFloatProvider.create(0.0F, 6.0F, 2.0F), 3, UniformFloatProvider.create(0.75F, 1.0F), 1.0F, 0.0F
					)
				)
			)
	);
	public static final ConfiguredCarver<CaveCarverConfig> OCEAN_CAVE = register(
		"ocean_cave",
		Carver.CAVE
			.configure(
				new CaveCarverConfig(
					0.06666667F,
					BiasedToBottomHeightProvider.create(YOffset.fixed(0), YOffset.fixed(127), 8),
					ConstantFloatProvider.create(0.5F),
					YOffset.aboveBottom(10),
					false,
					CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(-0.7F)
				)
			)
	);
	public static final ConfiguredCarver<RavineCarverConfig> UNDERWATER_CANYON = register(
		"underwater_canyon",
		Carver.UNDERWATER_CANYON
			.configure(
				new RavineCarverConfig(
					0.02F,
					BiasedToBottomHeightProvider.create(YOffset.fixed(20), YOffset.fixed(67), 8),
					ConstantFloatProvider.create(3.0F),
					YOffset.aboveBottom(10),
					false,
					CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()),
					UniformFloatProvider.create(-0.125F, 0.125F),
					new RavineCarverConfig.Shape(
						UniformFloatProvider.create(0.75F, 1.0F), TrapezoidFloatProvider.create(0.0F, 6.0F, 2.0F), 3, UniformFloatProvider.create(0.75F, 1.0F), 1.0F, 0.0F
					)
				)
			)
	);
	public static final ConfiguredCarver<CaveCarverConfig> UNDERWATER_CAVE = register(
		"underwater_cave",
		Carver.UNDERWATER_CAVE
			.configure(
				new CaveCarverConfig(
					0.06666667F,
					BiasedToBottomHeightProvider.create(YOffset.fixed(0), YOffset.fixed(127), 8),
					ConstantFloatProvider.create(0.5F),
					YOffset.aboveBottom(10),
					false,
					CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(-0.7F)
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
	public static final ConfiguredCarver<RavineCarverConfig> PROTOTYPE_CREVICE = register(
		"prototype_crevice",
		Carver.RAVINE
			.configure(
				new RavineCarverConfig(
					0.00125F,
					UniformHeightProvider.create(YOffset.fixed(40), YOffset.fixed(80)),
					UniformFloatProvider.create(6.0F, 8.0F),
					YOffset.aboveBottom(8),
					false,
					CarverDebugConfig.create(false, Blocks.OAK_BUTTON.getDefaultState()),
					UniformFloatProvider.create(-0.125F, 0.125F),
					new RavineCarverConfig.Shape(
						UniformFloatProvider.create(0.5F, 1.0F), UniformFloatProvider.create(0.0F, 1.0F), 6, UniformFloatProvider.create(0.25F, 1.0F), 0.0F, 5.0F
					)
				)
			)
	);

	private static <WC extends CarverConfig> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
	}
}
