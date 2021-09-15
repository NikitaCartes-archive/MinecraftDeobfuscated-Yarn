/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.Blocks;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;

public class ConfiguredSurfaceBuilders {
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> BADLANDS = ConfiguredSurfaceBuilders.register("badlands", SurfaceBuilder.BADLANDS.withConfig(SurfaceBuilder.BADLANDS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> BASALT_DELTAS = ConfiguredSurfaceBuilders.register("basalt_deltas", SurfaceBuilder.BASALT_DELTAS.withConfig(SurfaceBuilder.BASALT_DELTA_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> CRIMSON_FOREST = ConfiguredSurfaceBuilders.register("crimson_forest", SurfaceBuilder.NETHER_FOREST.withConfig(SurfaceBuilder.CRIMSON_NYLIUM_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> DESERT = ConfiguredSurfaceBuilders.register("desert", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.SAND_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> END = ConfiguredSurfaceBuilders.register("end", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.END_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> ERODED_BADLANDS = ConfiguredSurfaceBuilders.register("eroded_badlands", SurfaceBuilder.ERODED_BADLANDS.withConfig(SurfaceBuilder.BADLANDS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> FROZEN_OCEAN = ConfiguredSurfaceBuilders.register("frozen_ocean", SurfaceBuilder.FROZEN_OCEAN.withConfig(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> FULL_SAND = ConfiguredSurfaceBuilders.register("full_sand", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.SAND_SAND_UNDERWATER_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GIANT_TREE_TAIGA = ConfiguredSurfaceBuilders.register("giant_tree_taiga", SurfaceBuilder.GIANT_TREE_TAIGA.withConfig(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GRASS = ConfiguredSurfaceBuilders.register("grass", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GRAVELLY_MOUNTAIN = ConfiguredSurfaceBuilders.register("gravelly_mountain", SurfaceBuilder.GRAVELLY_MOUNTAIN.withConfig(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> ICE_SPIKES = ConfiguredSurfaceBuilders.register("ice_spikes", SurfaceBuilder.DEFAULT.withConfig(new TernarySurfaceConfig(Blocks.SNOW_BLOCK.getDefaultState(), Blocks.DIRT.getDefaultState(), Blocks.GRAVEL.getDefaultState())));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> MOUNTAIN = ConfiguredSurfaceBuilders.register("mountain", SurfaceBuilder.MOUNTAIN.withConfig(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> MYCELIUM = ConfiguredSurfaceBuilders.register("mycelium", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.MYCELIUM_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> NETHER = ConfiguredSurfaceBuilders.register("nether", SurfaceBuilder.NETHER.withConfig(SurfaceBuilder.NETHER_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> NOPE = ConfiguredSurfaceBuilders.register("nope", SurfaceBuilder.NOPE.withConfig(SurfaceBuilder.STONE_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> OCEAN_SAND = ConfiguredSurfaceBuilders.register("ocean_sand", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.GRASS_SAND_UNDERWATER_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> SHATTERED_SAVANNA = ConfiguredSurfaceBuilders.register("shattered_savanna", SurfaceBuilder.SHATTERED_SAVANNA.withConfig(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> SOUL_SAND_VALLEY = ConfiguredSurfaceBuilders.register("soul_sand_valley", SurfaceBuilder.SOUL_SAND_VALLEY.withConfig(SurfaceBuilder.SOUL_SAND_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> STONE = ConfiguredSurfaceBuilders.register("stone", SurfaceBuilder.DEFAULT.withConfig(SurfaceBuilder.STONE_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> SWAMP = ConfiguredSurfaceBuilders.register("swamp", SurfaceBuilder.SWAMP.withConfig(SurfaceBuilder.GRASS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> WARPED_FOREST = ConfiguredSurfaceBuilders.register("warped_forest", SurfaceBuilder.NETHER_FOREST.withConfig(SurfaceBuilder.WARPED_NYLIUM_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> WOODED_BADLANDS = ConfiguredSurfaceBuilders.register("wooded_badlands", SurfaceBuilder.WOODED_BADLANDS.withConfig(SurfaceBuilder.BADLANDS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> GROVE = ConfiguredSurfaceBuilders.register("grove", SurfaceBuilder.GROVE.withConfig(SurfaceBuilder.DIRT_SNOW_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> SNOWCAPPED_PEAKS = ConfiguredSurfaceBuilders.register("snowcapped_peaks", SurfaceBuilder.SNOWCAPPED_PEAKS.withConfig(SurfaceBuilder.SNOW_PEAKS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> LOFTY_PEAKS = ConfiguredSurfaceBuilders.register("lofty_peaks", SurfaceBuilder.LOFTY_PEAKS.withConfig(SurfaceBuilder.LOFTY_PEAKS_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> STONY_PEAKS = ConfiguredSurfaceBuilders.register("stony_peaks", SurfaceBuilder.STONY_PEAKS.withConfig(SurfaceBuilder.STONE_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> SNOWY_SLOPES = ConfiguredSurfaceBuilders.register("snowy_slopes", SurfaceBuilder.SNOWY_SLOPES.withConfig(SurfaceBuilder.SNOW_CONFIG));
    public static final ConfiguredSurfaceBuilder<TernarySurfaceConfig> STONE_SHORE = ConfiguredSurfaceBuilders.register("stone_shore", SurfaceBuilder.STONE_SHORE.withConfig(SurfaceBuilder.STONE_CONFIG));

    private static <SC extends SurfaceConfig> ConfiguredSurfaceBuilder<SC> register(String id, ConfiguredSurfaceBuilder<SC> configuredSurfaceBuilder) {
        return BuiltinRegistries.add(BuiltinRegistries.CONFIGURED_SURFACE_BUILDER, id, configuredSurfaceBuilder);
    }
}

