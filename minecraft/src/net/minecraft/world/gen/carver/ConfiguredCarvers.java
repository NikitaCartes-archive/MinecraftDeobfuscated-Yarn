package net.minecraft.world.gen.carver;

import net.minecraft.class_6108;
import net.minecraft.class_6120;
import net.minecraft.class_6124;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.YOffset;

public class ConfiguredCarvers {
	public static final ConfiguredCarver<class_6108> OLD_CAVE = register(
		"old_cave",
		Carver.CAVE
			.configure(
				new class_6108(
					0.14285715F,
					class_6120.method_35377(YOffset.fixed(0), YOffset.fixed(127), 8),
					ConstantFloatProvider.create(0.5F),
					YOffset.aboveBottom(11),
					CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(-0.7F)
				)
			)
	);
	public static final ConfiguredCarver<class_6108> CAVE = register(
		"cave",
		Carver.CAVE
			.configure(
				new class_6108(
					0.33333334F,
					class_6124.method_35396(YOffset.aboveBottom(8), YOffset.fixed(126)),
					UniformFloatProvider.create(0.1F, 0.9F),
					YOffset.aboveBottom(9),
					CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()),
					UniformFloatProvider.create(0.3F, 1.8F),
					UniformFloatProvider.create(0.5F, 1.8F),
					UniformFloatProvider.create(-1.0F, 0.0F)
				)
			)
	);
	public static final ConfiguredCarver<RavineCarverConfig> OLD_CANYON = register(
		"old_canyon",
		Carver.RAVINE
			.configure(
				new RavineCarverConfig(
					0.02F,
					class_6120.method_35377(YOffset.fixed(20), YOffset.fixed(67), 8),
					ConstantFloatProvider.create(3.0F),
					YOffset.aboveBottom(11),
					CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()),
					UniformFloatProvider.create(-0.125F, 0.125F),
					new RavineCarverConfig.class_6107(
						UniformFloatProvider.create(0.75F, 1.0F), TrapezoidFloatProvider.create(0.0F, 6.0F, 2.0F), 3, UniformFloatProvider.create(0.75F, 1.0F), 1.0F, 0.0F
					)
				)
			)
	);
	public static final ConfiguredCarver<RavineCarverConfig> CANYON = register(
		"canyon",
		Carver.RAVINE
			.configure(
				new RavineCarverConfig(
					0.02F,
					class_6124.method_35396(YOffset.fixed(10), YOffset.fixed(67)),
					ConstantFloatProvider.create(3.0F),
					YOffset.aboveBottom(9),
					CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()),
					UniformFloatProvider.create(-0.125F, 0.125F),
					new RavineCarverConfig.class_6107(
						UniformFloatProvider.create(0.75F, 1.0F), TrapezoidFloatProvider.create(0.0F, 6.0F, 2.0F), 3, UniformFloatProvider.create(0.75F, 1.0F), 1.0F, 0.0F
					)
				)
			)
	);
	public static final ConfiguredCarver<class_6108> OLD_OCEAN_CAVE = register(
		"old_ocean_cave",
		Carver.CAVE
			.configure(
				new class_6108(
					0.06666667F,
					class_6120.method_35377(YOffset.fixed(0), YOffset.fixed(127), 8),
					ConstantFloatProvider.create(0.5F),
					YOffset.aboveBottom(11),
					CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(-0.7F)
				)
			)
	);
	public static final ConfiguredCarver<class_6108> OCEAN_CAVE = register(
		"ocean_cave",
		Carver.CAVE
			.configure(
				new class_6108(
					0.14285715F,
					class_6120.method_35377(YOffset.fixed(0), YOffset.fixed(127), 8),
					UniformFloatProvider.create(0.1F, 0.9F),
					YOffset.aboveBottom(9),
					CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(1.0F),
					UniformFloatProvider.create(-1.0F, 0.0F)
				)
			)
	);
	public static final ConfiguredCarver<class_6108> NETHER_CAVE = register(
		"nether_cave",
		Carver.NETHER_CAVE
			.configure(
				new class_6108(
					0.2F,
					class_6124.method_35396(YOffset.fixed(0), YOffset.belowTop(1)),
					ConstantFloatProvider.create(0.5F),
					YOffset.aboveBottom(11),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(1.0F),
					ConstantFloatProvider.create(-0.7F)
				)
			)
	);
	public static final ConfiguredCarver<RavineCarverConfig> CRACK = register(
		"crack",
		Carver.RAVINE
			.configure(
				new RavineCarverConfig(
					0.00125F,
					class_6124.method_35396(YOffset.fixed(40), YOffset.fixed(80)),
					UniformFloatProvider.create(6.0F, 8.0F),
					YOffset.aboveBottom(9),
					CarverDebugConfig.create(false, Blocks.OAK_BUTTON.getDefaultState()),
					UniformFloatProvider.create(-0.125F, 0.125F),
					new RavineCarverConfig.class_6107(
						UniformFloatProvider.create(0.5F, 1.0F), UniformFloatProvider.create(0.0F, 1.0F), 6, UniformFloatProvider.create(0.25F, 1.0F), 0.0F, 5.0F
					)
				)
			)
	);

	private static <WC extends CarverConfig> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
	}
}
