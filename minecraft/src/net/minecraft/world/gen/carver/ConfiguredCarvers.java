package net.minecraft.world.gen.carver;

import net.minecraft.class_5865;
import net.minecraft.class_5866;
import net.minecraft.class_5869;
import net.minecraft.class_5871;
import net.minecraft.class_5872;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.YOffset;

public class ConfiguredCarvers {
	public static final ConfiguredCarver<class_5871> CAVE = register(
		"cave", Carver.CAVE.configure(new class_5871(0.25F, class_5872.method_33972(false, Blocks.CRIMSON_BUTTON.getDefaultState())))
	);
	public static final ConfiguredCarver<class_5869> CANYON = register(
		"canyon",
		Carver.CANYON
			.configure(
				new class_5869(
					0.02F,
					class_5872.method_33972(false, Blocks.WARPED_BUTTON.getDefaultState()),
					YOffset.fixed(10),
					YOffset.fixed(67),
					UniformIntDistribution.of(3),
					class_5866.method_33934(0.75F, 0.25F),
					class_5866.method_33934(-0.125F, 0.25F),
					class_5865.method_33926(0.0F, 6.0F, 2.0F),
					3,
					class_5866.method_33934(0.75F, 0.25F),
					1.0F,
					0.0F
				)
			)
	);
	public static final ConfiguredCarver<class_5871> OCEAN_CAVE = register(
		"ocean_cave", Carver.CAVE.configure(new class_5871(0.125F, class_5872.method_33972(false, Blocks.CRIMSON_BUTTON.getDefaultState())))
	);
	public static final ConfiguredCarver<class_5871> NETHER_CAVE = register("nether_cave", Carver.NETHER_CAVE.configure(new class_5871(0.2F)));
	public static final ConfiguredCarver<class_5869> CRACK = register(
		"crack",
		Carver.CANYON
			.configure(
				new class_5869(
					0.005F,
					class_5872.method_33972(false, Blocks.OAK_BUTTON.getDefaultState()),
					YOffset.fixed(40),
					YOffset.fixed(80),
					UniformIntDistribution.of(6, 2),
					class_5866.method_33934(0.5F, 0.5F),
					class_5866.method_33934(-0.125F, 0.25F),
					class_5866.method_33934(0.0F, 1.0F),
					6,
					class_5866.method_33934(0.25F, 0.75F),
					0.0F,
					5.0F
				)
			)
	);

	private static <WC extends class_5871> ConfiguredCarver<WC> register(String id, ConfiguredCarver<WC> configuredCarver) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_CARVER, id, configuredCarver);
	}
}
