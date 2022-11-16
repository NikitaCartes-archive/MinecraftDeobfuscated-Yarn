package net.minecraft.world.gen.carver;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.TrapezoidFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

public class ConfiguredCarvers {
	public static final RegistryKey<ConfiguredCarver<?>> CAVE = register("cave");
	public static final RegistryKey<ConfiguredCarver<?>> CAVE_EXTRA_UNDERGROUND = register("cave_extra_underground");
	public static final RegistryKey<ConfiguredCarver<?>> CANYON = register("canyon");
	public static final RegistryKey<ConfiguredCarver<?>> NETHER_CAVE = register("nether_cave");

	private static RegistryKey<ConfiguredCarver<?>> register(String id) {
		return RegistryKey.of(RegistryKeys.CONFIGURED_CARVER, new Identifier(id));
	}

	public static void bootstrap(Registerable<ConfiguredCarver<?>> carverRegisterable) {
		RegistryEntryLookup<Block> registryEntryLookup = carverRegisterable.getRegistryLookup(RegistryKeys.BLOCK);
		carverRegisterable.register(
			CAVE,
			Carver.CAVE
				.configure(
					new CaveCarverConfig(
						0.15F,
						UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(180)),
						UniformFloatProvider.create(0.1F, 0.9F),
						YOffset.aboveBottom(8),
						CarverDebugConfig.create(false, Blocks.CRIMSON_BUTTON.getDefaultState()),
						registryEntryLookup.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
						UniformFloatProvider.create(0.7F, 1.4F),
						UniformFloatProvider.create(0.8F, 1.3F),
						UniformFloatProvider.create(-1.0F, -0.4F)
					)
				)
		);
		carverRegisterable.register(
			CAVE_EXTRA_UNDERGROUND,
			Carver.CAVE
				.configure(
					new CaveCarverConfig(
						0.07F,
						UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.fixed(47)),
						UniformFloatProvider.create(0.1F, 0.9F),
						YOffset.aboveBottom(8),
						CarverDebugConfig.create(false, Blocks.OAK_BUTTON.getDefaultState()),
						registryEntryLookup.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
						UniformFloatProvider.create(0.7F, 1.4F),
						UniformFloatProvider.create(0.8F, 1.3F),
						UniformFloatProvider.create(-1.0F, -0.4F)
					)
				)
		);
		carverRegisterable.register(
			CANYON,
			Carver.RAVINE
				.configure(
					new RavineCarverConfig(
						0.01F,
						UniformHeightProvider.create(YOffset.fixed(10), YOffset.fixed(67)),
						ConstantFloatProvider.create(3.0F),
						YOffset.aboveBottom(8),
						CarverDebugConfig.create(false, Blocks.WARPED_BUTTON.getDefaultState()),
						registryEntryLookup.getOrThrow(BlockTags.OVERWORLD_CARVER_REPLACEABLES),
						UniformFloatProvider.create(-0.125F, 0.125F),
						new RavineCarverConfig.Shape(
							UniformFloatProvider.create(0.75F, 1.0F), TrapezoidFloatProvider.create(0.0F, 6.0F, 2.0F), 3, UniformFloatProvider.create(0.75F, 1.0F), 1.0F, 0.0F
						)
					)
				)
		);
		carverRegisterable.register(
			NETHER_CAVE,
			Carver.NETHER_CAVE
				.configure(
					new CaveCarverConfig(
						0.2F,
						UniformHeightProvider.create(YOffset.fixed(0), YOffset.belowTop(1)),
						ConstantFloatProvider.create(0.5F),
						YOffset.aboveBottom(10),
						registryEntryLookup.getOrThrow(BlockTags.NETHER_CARVER_REPLACEABLES),
						ConstantFloatProvider.create(1.0F),
						ConstantFloatProvider.create(1.0F),
						ConstantFloatProvider.create(-0.7F)
					)
				)
		);
	}
}
