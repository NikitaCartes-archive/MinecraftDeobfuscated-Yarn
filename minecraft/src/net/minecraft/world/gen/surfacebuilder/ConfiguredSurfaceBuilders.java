package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.Blocks;
import net.minecraft.util.registry.BuiltinRegistries;

public class ConfiguredSurfaceBuilders {
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26318 = register(
		"badlands", SurfaceBuilder.field_15698.method_30478(SurfaceBuilder.BADLANDS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26319 = register(
		"basalt_deltas", SurfaceBuilder.field_23926.method_30478(SurfaceBuilder.BASALT_DELTA_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26320 = register(
		"crimson_forest", SurfaceBuilder.field_22216.method_30478(SurfaceBuilder.CRIMSON_NYLIUM_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26321 = register(
		"desert", SurfaceBuilder.field_15701.method_30478(SurfaceBuilder.SAND_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26322 = register(
		"end", SurfaceBuilder.field_15701.method_30478(SurfaceBuilder.END_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26323 = register(
		"eroded_badlands", SurfaceBuilder.field_15684.method_30478(SurfaceBuilder.BADLANDS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26324 = register(
		"frozen_ocean", SurfaceBuilder.field_15699.method_30478(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26325 = register(
		"full_sand", SurfaceBuilder.field_15701.method_30478(SurfaceBuilder.SAND_SAND_UNDERWATER_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26326 = register(
		"giant_tree_taiga", SurfaceBuilder.field_15688.method_30478(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26327 = register(
		"grass", SurfaceBuilder.field_15701.method_30478(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26328 = register(
		"gravelly_mountain", SurfaceBuilder.field_15702.method_30478(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26329 = register(
		"ice_spikes",
		SurfaceBuilder.field_15701
			.method_30478(new TernarySurfaceConfig(Blocks.field_10491.getDefaultState(), Blocks.field_10566.getDefaultState(), Blocks.field_10255.getDefaultState()))
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26330 = register(
		"mountain", SurfaceBuilder.field_15692.method_30478(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26331 = register(
		"mycelium", SurfaceBuilder.field_15701.method_30478(SurfaceBuilder.MYCELIUM_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26332 = register(
		"nether", SurfaceBuilder.field_15693.method_30478(SurfaceBuilder.NETHER_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26333 = register(
		"nope", SurfaceBuilder.field_15683.method_30478(SurfaceBuilder.STONE_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26334 = register(
		"ocean_sand", SurfaceBuilder.field_15701.method_30478(SurfaceBuilder.GRASS_SAND_UNDERWATER_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26335 = register(
		"shattered_savanna", SurfaceBuilder.field_15680.method_30478(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26336 = register(
		"soul_sand_valley", SurfaceBuilder.field_22217.method_30478(SurfaceBuilder.SOUL_SAND_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26337 = register(
		"stone", SurfaceBuilder.field_15701.method_30478(SurfaceBuilder.STONE_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26338 = register(
		"swamp", SurfaceBuilder.field_15681.method_30478(SurfaceBuilder.GRASS_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26339 = register(
		"warped_forest", SurfaceBuilder.field_22216.method_30478(SurfaceBuilder.WARPED_NYLIUM_CONFIG)
	);
	public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> field_26340 = register(
		"wooded_badlands", SurfaceBuilder.field_15689.method_30478(SurfaceBuilder.BADLANDS_CONFIG)
	);

	private static <SC extends SurfaceConfig> ConfiguredSurfaceBuilder<SC> register(String id, ConfiguredSurfaceBuilder<SC> configuredSurfaceBuilder) {
		return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, id, configuredSurfaceBuilder);
	}
}
