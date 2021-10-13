package net.minecraft.world.gen.surfacebuilder;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.gen.YOffset;

public class VanillaSurfaceRules {
	private static final MaterialRules.MaterialRule AIR = block(Blocks.AIR);
	private static final MaterialRules.MaterialRule WHITE_TERRACOTTA = block(Blocks.WHITE_TERRACOTTA);
	private static final MaterialRules.MaterialRule ORANGE_TERRACOTTA = block(Blocks.ORANGE_TERRACOTTA);
	private static final MaterialRules.MaterialRule TERRACOTTA = block(Blocks.TERRACOTTA);
	private static final MaterialRules.MaterialRule RED_SAND = block(Blocks.RED_SAND);
	private static final MaterialRules.MaterialRule STONE = block(Blocks.STONE);
	private static final MaterialRules.MaterialRule DIRT = block(Blocks.DIRT);
	private static final MaterialRules.MaterialRule PODZOL = block(Blocks.PODZOL);
	private static final MaterialRules.MaterialRule COARSE_DIRT = block(Blocks.COARSE_DIRT);
	private static final MaterialRules.MaterialRule MYCELIUM = block(Blocks.MYCELIUM);
	private static final MaterialRules.MaterialRule GRASS_BLOCK = block(Blocks.GRASS_BLOCK);
	private static final MaterialRules.MaterialRule CALCITE = block(Blocks.CALCITE);
	private static final MaterialRules.MaterialRule GRAVEL = block(Blocks.GRAVEL);
	private static final MaterialRules.MaterialRule SAND = block(Blocks.SAND);
	private static final MaterialRules.MaterialRule PACKED_ICE = block(Blocks.PACKED_ICE);
	private static final MaterialRules.MaterialRule SNOW_BLOCK = block(Blocks.SNOW_BLOCK);
	private static final MaterialRules.MaterialRule POWDER_SNOW = block(Blocks.POWDER_SNOW);
	private static final MaterialRules.MaterialRule ICE = block(Blocks.ICE);
	private static final MaterialRules.MaterialRule WATER = block(Blocks.WATER);
	private static final MaterialRules.MaterialRule LAVA = block(Blocks.LAVA);
	private static final MaterialRules.MaterialRule NETHERRACK = block(Blocks.NETHERRACK);
	private static final MaterialRules.MaterialRule SOUL_SAND = block(Blocks.SOUL_SAND);
	private static final MaterialRules.MaterialRule SOUL_SOIL = block(Blocks.SOUL_SOIL);
	private static final MaterialRules.MaterialRule BASALT = block(Blocks.BASALT);
	private static final MaterialRules.MaterialRule BLACKSTONE = block(Blocks.BLACKSTONE);
	private static final MaterialRules.MaterialRule WARPED_WART_BLOCK = block(Blocks.WARPED_WART_BLOCK);
	private static final MaterialRules.MaterialRule WARPED_NYLIUM = block(Blocks.WARPED_NYLIUM);
	private static final MaterialRules.MaterialRule NETHER_WART_BLOCK = block(Blocks.NETHER_WART_BLOCK);
	private static final MaterialRules.MaterialRule CRIMSON_NYLIUM = block(Blocks.CRIMSON_NYLIUM);
	private static final MaterialRules.MaterialRule END_STONE = block(Blocks.END_STONE);

	private static MaterialRules.MaterialRule block(Block block) {
		return MaterialRules.block(block.getDefaultState());
	}

	public static MaterialRules.MaterialRule createOverworldSurfaceRule() {
		MaterialRules.MaterialCondition materialCondition = MaterialRules.aboveY(YOffset.fixed(97), 2);
		MaterialRules.MaterialCondition materialCondition2 = MaterialRules.aboveY(YOffset.fixed(160), 0);
		MaterialRules.MaterialCondition materialCondition3 = MaterialRules.aboveYWithStoneDepth(YOffset.fixed(63), -1);
		MaterialRules.MaterialCondition materialCondition4 = MaterialRules.aboveYWithStoneDepth(YOffset.fixed(74), 1);
		MaterialRules.MaterialCondition materialCondition5 = MaterialRules.aboveY(YOffset.fixed(62), 0);
		MaterialRules.MaterialCondition materialCondition6 = MaterialRules.aboveY(YOffset.fixed(63), 0);
		MaterialRules.MaterialCondition materialCondition7 = MaterialRules.water(-1, 0);
		MaterialRules.MaterialCondition materialCondition8 = MaterialRules.water(0, 0);
		MaterialRules.MaterialCondition materialCondition9 = MaterialRules.waterWithStoneDepth(-6, -1);
		MaterialRules.MaterialCondition materialCondition10 = MaterialRules.hole();
		MaterialRules.MaterialCondition materialCondition11 = MaterialRules.biome(BiomeKeys.FROZEN_OCEAN, BiomeKeys.DEEP_FROZEN_OCEAN);
		MaterialRules.MaterialCondition materialCondition12 = MaterialRules.steepSlope();
		MaterialRules.MaterialRule materialRule = MaterialRules.sequence(
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.STONY_PEAKS), MaterialRules.sequence(MaterialRules.condition(noiseThreshold("calcite", -6, -0.0125, 0.0125), CALCITE), STONE)
			),
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.STONY_SHORE), MaterialRules.sequence(MaterialRules.condition(noiseThreshold("gravel", -5, -0.05, 0.05), GRAVEL), STONE)
			),
			MaterialRules.condition(MaterialRules.biome(BiomeKeys.WINDSWEPT_HILLS), MaterialRules.condition(surfaceNoiseThreshold(1.0), STONE)),
			MaterialRules.condition(MaterialRules.biome(BiomeKeys.WARM_OCEAN, BiomeKeys.DEEP_WARM_OCEAN, BiomeKeys.DESERT, BiomeKeys.BEACH, BiomeKeys.SNOWY_BEACH), SAND),
			MaterialRules.condition(MaterialRules.biome(BiomeKeys.DRIPSTONE_CAVES), STONE)
		);
		MaterialRules.MaterialRule materialRule2 = MaterialRules.condition(noiseThreshold("powder_snow_under", -3, 0.45, 0.58), POWDER_SNOW);
		MaterialRules.MaterialRule materialRule3 = MaterialRules.condition(noiseThreshold("powder_snow_surface", -3, 0.35, 0.6), POWDER_SNOW);
		MaterialRules.MaterialRule materialRule4 = MaterialRules.sequence(
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.FROZEN_PEAKS),
				MaterialRules.sequence(
					MaterialRules.condition(materialCondition12, PACKED_ICE),
					MaterialRules.condition(noiseThreshold("packed_ice_under", -4, -0.5, 0.2), PACKED_ICE),
					MaterialRules.condition(noiseThreshold("ice_under", -1, -0.0625, 0.025), ICE),
					SNOW_BLOCK
				)
			),
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.SNOWY_SLOPES), MaterialRules.sequence(MaterialRules.condition(materialCondition12, STONE), materialRule2, SNOW_BLOCK)
			),
			MaterialRules.condition(MaterialRules.biome(BiomeKeys.JAGGED_PEAKS), STONE),
			MaterialRules.condition(MaterialRules.biome(BiomeKeys.GROVE), MaterialRules.sequence(materialRule2, DIRT)),
			materialRule,
			MaterialRules.condition(MaterialRules.biome(BiomeKeys.WINDSWEPT_SAVANNA), MaterialRules.condition(surfaceNoiseThreshold(1.75), STONE)),
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS),
				MaterialRules.sequence(
					MaterialRules.condition(surfaceNoiseThreshold(2.0), GRAVEL),
					MaterialRules.condition(surfaceNoiseThreshold(1.0), STONE),
					MaterialRules.condition(surfaceNoiseThreshold(-1.0), DIRT),
					GRAVEL
				)
			),
			DIRT
		);
		MaterialRules.MaterialRule materialRule5 = MaterialRules.sequence(
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.FROZEN_PEAKS),
				MaterialRules.sequence(
					MaterialRules.condition(materialCondition12, PACKED_ICE),
					MaterialRules.condition(noiseThreshold("packed_ice_surface", -4, 0.0, 0.2), PACKED_ICE),
					MaterialRules.condition(noiseThreshold("ice_surface", -1, 0.0, 0.025), ICE),
					SNOW_BLOCK
				)
			),
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.SNOWY_SLOPES), MaterialRules.sequence(MaterialRules.condition(materialCondition12, STONE), materialRule3, SNOW_BLOCK)
			),
			MaterialRules.condition(MaterialRules.biome(BiomeKeys.JAGGED_PEAKS), MaterialRules.sequence(MaterialRules.condition(materialCondition12, STONE), SNOW_BLOCK)),
			MaterialRules.condition(MaterialRules.biome(BiomeKeys.GROVE), MaterialRules.sequence(materialRule3, SNOW_BLOCK)),
			materialRule,
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.WINDSWEPT_SAVANNA),
				MaterialRules.sequence(MaterialRules.condition(surfaceNoiseThreshold(1.75), STONE), MaterialRules.condition(surfaceNoiseThreshold(-0.5), COARSE_DIRT))
			),
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.WINDSWEPT_GRAVELLY_HILLS),
				MaterialRules.sequence(
					MaterialRules.condition(surfaceNoiseThreshold(2.0), GRAVEL),
					MaterialRules.condition(surfaceNoiseThreshold(1.0), STONE),
					MaterialRules.condition(surfaceNoiseThreshold(-1.0), GRASS_BLOCK),
					GRAVEL
				)
			),
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.OLD_GROWTH_PINE_TAIGA, BiomeKeys.OLD_GROWTH_SPRUCE_TAIGA),
				MaterialRules.sequence(MaterialRules.condition(surfaceNoiseThreshold(1.75), COARSE_DIRT), MaterialRules.condition(surfaceNoiseThreshold(-0.95), PODZOL))
			),
			MaterialRules.condition(MaterialRules.biome(BiomeKeys.WARM_OCEAN, BiomeKeys.DEEP_WARM_OCEAN, BiomeKeys.DESERT, BiomeKeys.BEACH, BiomeKeys.SNOWY_BEACH), SAND),
			MaterialRules.condition(MaterialRules.biome(BiomeKeys.ICE_SPIKES), SNOW_BLOCK),
			MaterialRules.condition(MaterialRules.biome(BiomeKeys.MUSHROOM_FIELDS), MYCELIUM),
			GRASS_BLOCK
		);
		MaterialRules.MaterialCondition materialCondition13 = MaterialRules.noiseThreshold(
			"surface", new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0, 1.0, 1.0), -0.909, -0.5454
		);
		MaterialRules.MaterialCondition materialCondition14 = MaterialRules.noiseThreshold(
			"surface", new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0, 1.0, 1.0), -0.1818, 0.1818
		);
		MaterialRules.MaterialCondition materialCondition15 = MaterialRules.noiseThreshold(
			"surface", new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0, 1.0, 1.0), 0.5454, 0.909
		);
		return MaterialRules.sequence(
			MaterialRules.condition(
				MaterialRules.STONE_DEPTH_FLOOR,
				MaterialRules.sequence(
					MaterialRules.condition(
						MaterialRules.biome(BiomeKeys.WOODED_BADLANDS),
						MaterialRules.condition(
							materialCondition,
							MaterialRules.sequence(
								MaterialRules.condition(materialCondition13, COARSE_DIRT),
								MaterialRules.condition(materialCondition14, COARSE_DIRT),
								MaterialRules.condition(materialCondition15, COARSE_DIRT),
								GRASS_BLOCK
							)
						)
					),
					MaterialRules.condition(
						MaterialRules.biome(BiomeKeys.SWAMP),
						MaterialRules.condition(
							materialCondition5,
							MaterialRules.condition(
								MaterialRules.not(materialCondition6),
								MaterialRules.condition(MaterialRules.noiseThreshold("swamp", new DoublePerlinNoiseSampler.NoiseParameters(-2, 1.0), 0.0), WATER)
							)
						)
					)
				)
			),
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.BADLANDS, BiomeKeys.ERODED_BADLANDS, BiomeKeys.WOODED_BADLANDS),
				MaterialRules.sequence(
					MaterialRules.condition(
						MaterialRules.STONE_DEPTH_FLOOR,
						MaterialRules.sequence(
							MaterialRules.condition(materialCondition2, ORANGE_TERRACOTTA),
							MaterialRules.condition(
								materialCondition4,
								MaterialRules.sequence(
									MaterialRules.condition(materialCondition13, TERRACOTTA),
									MaterialRules.condition(materialCondition14, TERRACOTTA),
									MaterialRules.condition(materialCondition15, TERRACOTTA),
									MaterialRules.terracottaBands()
								)
							),
							MaterialRules.condition(materialCondition7, RED_SAND),
							MaterialRules.condition(MaterialRules.not(materialCondition10), ORANGE_TERRACOTTA),
							MaterialRules.condition(materialCondition9, WHITE_TERRACOTTA),
							GRAVEL
						)
					),
					MaterialRules.condition(
						materialCondition3,
						MaterialRules.sequence(
							MaterialRules.condition(materialCondition6, MaterialRules.condition(MaterialRules.not(materialCondition4), ORANGE_TERRACOTTA)),
							MaterialRules.terracottaBands()
						)
					),
					MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR_WITH_RUN_DEPTH, MaterialRules.condition(materialCondition9, WHITE_TERRACOTTA))
				)
			),
			MaterialRules.condition(
				MaterialRules.STONE_DEPTH_FLOOR,
				MaterialRules.condition(
					materialCondition7,
					MaterialRules.sequence(
						MaterialRules.condition(
							materialCondition11,
							MaterialRules.condition(
								materialCondition10,
								MaterialRules.sequence(MaterialRules.condition(materialCondition8, AIR), MaterialRules.condition(MaterialRules.temperature(), ICE), WATER)
							)
						),
						materialRule5
					)
				)
			),
			MaterialRules.condition(
				MaterialRules.STONE_DEPTH_FLOOR,
				MaterialRules.condition(materialCondition9, MaterialRules.condition(materialCondition11, MaterialRules.condition(materialCondition10, WATER)))
			),
			MaterialRules.condition(MaterialRules.STONE_DEPTH_FLOOR_WITH_RUN_DEPTH, MaterialRules.condition(materialCondition9, materialRule4)),
			MaterialRules.condition(
				MaterialRules.STONE_DEPTH_FLOOR,
				MaterialRules.sequence(
					MaterialRules.condition(MaterialRules.biome(BiomeKeys.FROZEN_PEAKS, BiomeKeys.JAGGED_PEAKS), STONE),
					MaterialRules.condition(
						MaterialRules.biome(BiomeKeys.WARM_OCEAN, BiomeKeys.DEEP_WARM_OCEAN, BiomeKeys.LUKEWARM_OCEAN, BiomeKeys.DEEP_LUKEWARM_OCEAN), SAND
					),
					GRAVEL
				)
			)
		);
	}

	public static MaterialRules.MaterialRule createNetherSurfaceRule() {
		MaterialRules.MaterialCondition materialCondition = MaterialRules.aboveY(YOffset.fixed(31), 0);
		MaterialRules.MaterialCondition materialCondition2 = MaterialRules.aboveY(YOffset.fixed(32), 0);
		MaterialRules.MaterialCondition materialCondition3 = MaterialRules.aboveYWithStoneDepth(YOffset.fixed(30), 0);
		MaterialRules.MaterialCondition materialCondition4 = MaterialRules.not(MaterialRules.aboveYWithStoneDepth(YOffset.fixed(35), 0));
		MaterialRules.MaterialCondition materialCondition5 = MaterialRules.hole();
		MaterialRules.MaterialCondition materialCondition6 = MaterialRules.noiseThreshold(
			"soul_sand_layer", new DoublePerlinNoiseSampler.NoiseParameters(-8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334), -0.012
		);
		MaterialRules.MaterialCondition materialCondition7 = MaterialRules.noiseThreshold(
			"gravel_layer", new DoublePerlinNoiseSampler.NoiseParameters(-8, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334), -0.012
		);
		MaterialRules.MaterialCondition materialCondition8 = MaterialRules.noiseThreshold(
			"patch", new DoublePerlinNoiseSampler.NoiseParameters(-5, 1.0, 0.0, 0.0, 0.0, 0.0, 0.013333333333333334), -0.012
		);
		MaterialRules.MaterialCondition materialCondition9 = MaterialRules.noiseThreshold(
			"netherrack", new DoublePerlinNoiseSampler.NoiseParameters(-3, 1.0, 0.0, 0.0, 0.35), 0.54
		);
		MaterialRules.MaterialCondition materialCondition10 = MaterialRules.noiseThreshold(
			"nether_wart", new DoublePerlinNoiseSampler.NoiseParameters(-3, 1.0, 0.0, 0.0, 0.9), 1.17
		);
		MaterialRules.MaterialCondition materialCondition11 = MaterialRules.noiseThreshold(
			"nether_state_selector", new DoublePerlinNoiseSampler.NoiseParameters(-4, 1.0), 0.0
		);
		MaterialRules.MaterialRule materialRule = MaterialRules.condition(
			materialCondition8, MaterialRules.condition(materialCondition3, MaterialRules.condition(materialCondition4, GRAVEL))
		);
		return MaterialRules.sequence(
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.BASALT_DELTAS),
				MaterialRules.sequence(
					MaterialRules.condition(MaterialRules.STONE_DEPTH_CEILING, BASALT),
					MaterialRules.condition(
						MaterialRules.STONE_DEPTH_FLOOR_WITH_RUN_DEPTH, MaterialRules.sequence(materialRule, MaterialRules.condition(materialCondition11, BASALT), BLACKSTONE)
					)
				)
			),
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.SOUL_SAND_VALLEY),
				MaterialRules.sequence(
					MaterialRules.condition(MaterialRules.STONE_DEPTH_CEILING, MaterialRules.sequence(MaterialRules.condition(materialCondition11, SOUL_SAND), SOUL_SOIL)),
					MaterialRules.condition(
						MaterialRules.STONE_DEPTH_FLOOR_WITH_RUN_DEPTH, MaterialRules.sequence(materialRule, MaterialRules.condition(materialCondition11, SOUL_SAND), SOUL_SOIL)
					)
				)
			),
			MaterialRules.condition(
				MaterialRules.STONE_DEPTH_FLOOR,
				MaterialRules.sequence(
					MaterialRules.condition(MaterialRules.not(materialCondition2), MaterialRules.condition(materialCondition5, LAVA)),
					MaterialRules.condition(
						MaterialRules.biome(BiomeKeys.WARPED_FOREST),
						MaterialRules.condition(
							MaterialRules.not(materialCondition9),
							MaterialRules.condition(materialCondition, MaterialRules.sequence(MaterialRules.condition(materialCondition10, WARPED_WART_BLOCK), WARPED_NYLIUM))
						)
					),
					MaterialRules.condition(
						MaterialRules.biome(BiomeKeys.CRIMSON_FOREST),
						MaterialRules.condition(
							MaterialRules.not(materialCondition9),
							MaterialRules.condition(materialCondition, MaterialRules.sequence(MaterialRules.condition(materialCondition10, NETHER_WART_BLOCK), CRIMSON_NYLIUM))
						)
					)
				)
			),
			MaterialRules.condition(
				MaterialRules.biome(BiomeKeys.NETHER_WASTES),
				MaterialRules.sequence(
					MaterialRules.condition(
						MaterialRules.STONE_DEPTH_FLOOR_WITH_RUN_DEPTH,
						MaterialRules.condition(
							materialCondition6,
							MaterialRules.sequence(
								MaterialRules.condition(
									MaterialRules.not(materialCondition5), MaterialRules.condition(materialCondition3, MaterialRules.condition(materialCondition4, SOUL_SAND))
								),
								NETHERRACK
							)
						)
					),
					MaterialRules.condition(
						MaterialRules.STONE_DEPTH_FLOOR,
						MaterialRules.condition(
							materialCondition,
							MaterialRules.condition(
								materialCondition4,
								MaterialRules.condition(
									materialCondition7,
									MaterialRules.sequence(MaterialRules.condition(materialCondition2, GRAVEL), MaterialRules.condition(MaterialRules.not(materialCondition5), GRAVEL))
								)
							)
						)
					)
				)
			),
			NETHERRACK
		);
	}

	public static MaterialRules.MaterialRule getEndStoneRule() {
		return END_STONE;
	}

	private static MaterialRules.MaterialCondition noiseThreshold(String name, int i, double minThreshold, double maxThreshold) {
		return MaterialRules.noiseThreshold(name, new DoublePerlinNoiseSampler.NoiseParameters(-3 + i, 1.0, 1.0, 1.0, 1.0), minThreshold, maxThreshold);
	}

	private static MaterialRules.MaterialCondition surfaceNoiseThreshold(double min) {
		return MaterialRules.noiseThreshold("surface", new DoublePerlinNoiseSampler.NoiseParameters(-7, 1.0, 1.0, 1.0, 1.0), min / 8.25, Double.POSITIVE_INFINITY);
	}
}
