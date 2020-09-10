package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.Blocks;
import net.minecraft.util.registry.BuiltinRegistries;

public class ConfiguredSurfaceBuilders {
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> BADLANDS = register(
		"badlands", SurfaceBuilder.BADLANDS.withConfig(SurfaceBuilder.BADLANDS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> BASALT_DELTAS = register(
		"basalt_deltas", SurfaceBuilder.BASALT_DELTAS.withConfig(SurfaceBuilder.BASALT_DELTA_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> CRIMSON_FOREST = register(
		"crimson_forest", SurfaceBuilder.NETHER_FOREST.withConfig(SurfaceBuilder.CRIMSON_NYLIUM_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> DESERT = register("desert", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.SAND_CONFIG));
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> END = register("end", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.END_CONFIG));
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> ERODED_BADLANDS = register(
		"eroded_badlands", SurfaceBuilder.ERODED_BADLANDS.withConfig(SurfaceBuilder.BADLANDS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> FROZEN_OCEAN = register(
		"frozen_ocean", SurfaceBuilder.FROZEN_OCEAN.withConfig(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> FULL_SAND = register(
		"full_sand", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.SAND_SAND_UNDERWATER_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GIANT_TREE_TAIGA = register(
		"giant_tree_taiga", SurfaceBuilder.GIANT_TREE_TAIGA.withConfig(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GRASS = register("grass", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.GRASS_CONFIG));
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GRAVELLY_MOUNTAIN = register(
		"gravelly_mountain", SurfaceBuilder.GRAVELLY_MOUNTAIN.withConfig(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> ICE_SPIKES = register(
		"ice_spikes",
		SurfaceBuilder.DEFAULT
			.withConfig(new TernarySurfaceConfig(Blocks.SNOW_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState()))
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> MOUNTAIN = register(
		"mountain", SurfaceBuilder.MOUNTAIN.withConfig(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> MYCELIUM = register(
		"mycelium", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.MYCELIUM_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> NETHER = register("nether", SurfaceBuilder.NETHER.withConfig(SurfaceBuilder.NETHER_CONFIG));
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> NOPE = register("nope", SurfaceBuilder.NOPE.withConfig(SurfaceBuilder.STONE_CONFIG));
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> OCEAN_SAND = register(
		"ocean_sand", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.GRASS_SAND_UNDERWATER_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> SHATTERED_SAVANNA = register(
		"shattered_savanna", SurfaceBuilder.SHATTERED_SAVANNA.withConfig(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> SOUL_SAND_VALLEY = register(
		"soul_sand_valley", SurfaceBuilder.SOUL_SAND_VALLEY.withConfig(SurfaceBuilder.SOUL_SAND_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> STONE = register("stone", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.STONE_CONFIG));
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> SWAMP = register("swamp", SurfaceBuilder.SWAMP.withConfig(SurfaceBuilder.GRASS_CONFIG));
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> WARPED_FOREST = register(
		"warped_forest", SurfaceBuilder.NETHER_FOREST.withConfig(SurfaceBuilder.WARPED_NYLIUM_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> WOODED_BADLANDS = register(
		"wooded_badlands", SurfaceBuilder.WOODED_BADLANDS.withConfig(SurfaceBuilder.BADLANDS_CONFIG)
	);

	private static <SC extends SurfaceConfig> ConfiguredSurfaceBuilder<SC> register(String id, ConfiguredSurfaceBuilder<SC> configuredSurfaceBuilder) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, id, configuredSurfaceBuilder);
	}
}
