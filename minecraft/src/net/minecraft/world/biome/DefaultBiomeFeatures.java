package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CountChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DungeonDecoratorConfig;
import net.minecraft.world.gen.decorator.LakeDecoratorConfig;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.feature.BuriedTreasureFeatureConfig;
import net.minecraft.world.gen.feature.BushFeatureConfig;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.DoublePlantFeatureConfig;
import net.minecraft.world.gen.feature.EmeraldOreFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.GrassFeatureConfig;
import net.minecraft.world.gen.feature.IcebergFeatureConfig;
import net.minecraft.world.gen.feature.LakeFeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PillagerOutpostFeatureConfig;
import net.minecraft.world.gen.feature.PlantedFeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.world.gen.feature.VillageFeatureConfig;

public class DefaultBiomeFeatures {
	public static void addLandCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CAVE, new ProbabilityConfig(0.14285715F)));
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CANYON, new ProbabilityConfig(0.02F)));
	}

	public static void addOceanCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CAVE, new ProbabilityConfig(0.06666667F)));
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CANYON, new ProbabilityConfig(0.02F)));
		biome.addCarver(GenerationStep.Carver.LIQUID, Biome.configureCarver(Carver.UNDERWATER_CANYON, new ProbabilityConfig(0.02F)));
		biome.addCarver(GenerationStep.Carver.LIQUID, Biome.configureCarver(Carver.UNDERWATER_CAVE, new ProbabilityConfig(0.06666667F)));
	}

	public static void addDefaultStructures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Biome.configureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.NORMAL), Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.configureFeature(Feature.PILLAGER_OUTPOST, new PillagerOutpostFeatureConfig(0.004), Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES, Biome.configureFeature(Feature.STRONGHOLD, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.configureFeature(Feature.SWAMP_HUT, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.configureFeature(Feature.DESERT_PYRAMID, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.configureFeature(Feature.JUNGLE_TEMPLE, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.configureFeature(Feature.IGLOO, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.configureFeature(Feature.SHIPWRECK, new ShipwreckFeatureConfig(false), Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.configureFeature(Feature.OCEAN_MONUMENT, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.configureFeature(Feature.WOODLAND_MANSION, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.configureFeature(Feature.OCEAN_RUIN, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.9F), Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Biome.configureFeature(Feature.BURIED_TREASURE, new BuriedTreasureFeatureConfig(0.01F), Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.configureFeature(Feature.VILLAGE, new VillageFeatureConfig("village/plains/town_centers", 6), Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
	}

	public static void addDefaultLakes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(Feature.LAKE, new LakeFeatureConfig(Blocks.WATER.getDefaultState()), Decorator.WATER_LAKE, new LakeDecoratorConfig(4))
		);
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(Feature.LAKE, new LakeFeatureConfig(Blocks.LAVA.getDefaultState()), Decorator.LAVA_LAKE, new LakeDecoratorConfig(80))
		);
	}

	public static void addDesertLakes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(Feature.LAKE, new LakeFeatureConfig(Blocks.LAVA.getDefaultState()), Decorator.LAVA_LAKE, new LakeDecoratorConfig(80))
		);
	}

	public static void addDungeons(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Biome.configureFeature(Feature.MONSTER_ROOM, FeatureConfig.DEFAULT, Decorator.DUNGEONS, new DungeonDecoratorConfig(8))
		);
	}

	public static void addMineables(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.DIRT.getDefaultState(), 33),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(10, 0, 0, 256)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.GRAVEL.getDefaultState(), 33),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(8, 0, 0, 256)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.GRANITE.getDefaultState(), 33),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.DIORITE.getDefaultState(), 33),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.ANDESITE.getDefaultState(), 33),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
	}

	public static void addDefaultOres(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.COAL_ORE.getDefaultState(), 17),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(20, 0, 0, 128)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.IRON_ORE.getDefaultState(), 9),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(20, 0, 0, 64)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.GOLD_ORE.getDefaultState(), 9),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(2, 0, 0, 32)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.REDSTONE_ORE.getDefaultState(), 8),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(8, 0, 0, 16)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.DIAMOND_ORE.getDefaultState(), 8),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(1, 0, 0, 16)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.LAPIS_ORE.getDefaultState(), 7),
				Decorator.COUNT_DEPTH_AVERAGE,
				new CountDepthDecoratorConfig(1, 16, 16)
			)
		);
	}

	public static void addExtraGoldOre(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.GOLD_ORE.getDefaultState(), 9),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(20, 32, 32, 80)
			)
		);
	}

	public static void addEmeraldOre(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.EMERALD_ORE,
				new EmeraldOreFeatureConfig(Blocks.STONE.getDefaultState(), Blocks.EMERALD_ORE.getDefaultState()),
				Decorator.EMERALD_ORE,
				DecoratorConfig.DEFAULT
			)
		);
	}

	public static void addInfestedStone(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Biome.configureFeature(
				Feature.ORE,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.INFESTED_STONE.getDefaultState(), 9),
				Decorator.COUNT_RANGE,
				new RangeDecoratorConfig(7, 0, 0, 64)
			)
		);
	}

	public static void addDefaultDisks(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.DISK,
				new DiskFeatureConfig(
					Blocks.SAND.getDefaultState(), 7, 2, Lists.<BlockState>newArrayList(Blocks.DIRT.getDefaultState(), Blocks.GRASS_BLOCK.getDefaultState())
				),
				Decorator.COUNT_TOP_SOLID,
				new CountDecoratorConfig(3)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.DISK,
				new DiskFeatureConfig(Blocks.CLAY.getDefaultState(), 4, 1, Lists.<BlockState>newArrayList(Blocks.DIRT.getDefaultState(), Blocks.CLAY.getDefaultState())),
				Decorator.COUNT_TOP_SOLID,
				new CountDecoratorConfig(1)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.DISK,
				new DiskFeatureConfig(
					Blocks.GRAVEL.getDefaultState(), 6, 2, Lists.<BlockState>newArrayList(Blocks.DIRT.getDefaultState(), Blocks.GRASS_BLOCK.getDefaultState())
				),
				Decorator.COUNT_TOP_SOLID,
				new CountDecoratorConfig(1)
			)
		);
	}

	public static void addClay(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.DISK,
				new DiskFeatureConfig(Blocks.CLAY.getDefaultState(), 4, 1, Lists.<BlockState>newArrayList(Blocks.DIRT.getDefaultState(), Blocks.CLAY.getDefaultState())),
				Decorator.COUNT_TOP_SOLID,
				new CountDecoratorConfig(1)
			)
		);
	}

	public static void addMossyRocks(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(
				Feature.FOREST_ROCK, new BoulderFeatureConfig(Blocks.MOSSY_COBBLESTONE.getDefaultState(), 0), Decorator.FOREST_ROCK, new CountDecoratorConfig(3)
			)
		);
	}

	public static void addLargeFerns(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.DOUBLE_PLANT, new DoublePlantFeatureConfig(Blocks.LARGE_FERN.getDefaultState()), Decorator.COUNT_HEIGHTMAP_32, new CountDecoratorConfig(7)
			)
		);
	}

	public static void addSweetBerryBushesSnowy(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.SWEET_BERRY_BUSH, FeatureConfig.DEFAULT, Decorator.CHANCE_HEIGHTMAP_DOUBLE, new ChanceDecoratorConfig(12))
		);
	}

	public static void addSweetBerryBushes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.SWEET_BERRY_BUSH, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(1))
		);
	}

	public static void addBamboo(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.BAMBOO, new ProbabilityConfig(0.0F), Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(16))
		);
	}

	public static void addBambooJungleTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.BAMBOO,
				new ProbabilityConfig(0.2F),
				Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED,
				new TopSolidHeightmapNoiseBiasedDecoratorConfig(160, 80.0, 0.3, Heightmap.Type.WORLD_SURFACE_WG)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.FANCY_TREE, Feature.JUNGLE_GROUND_BUSH, Feature.MEGA_JUNGLE_TREE},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.05F, 0.15F, 0.7F},
					Feature.JUNGLE_GRASS,
					FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(30, 0.1F, 1)
			)
		);
	}

	public static void addTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.PINE_TREE}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.33333334F}, Feature.SPRUCE_TREE, FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addWaterBiomeOakTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.FANCY_TREE}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.1F}, Feature.NORMAL_TREE, FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(0, 0.1F, 1)
			)
		);
	}

	public static void addBirchTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.BIRCH_TREE, FeatureConfig.DEFAULT, Decorator.COUNT_EXTRA_HEIGHTMAP, new CountExtraChanceDecoratorConfig(10, 0.1F, 1))
		);
	}

	public static void addForestTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.BIRCH_TREE, Feature.FANCY_TREE},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.2F, 0.1F},
					Feature.NORMAL_TREE,
					FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addTallBirchTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.SUPER_BIRCH_TREE}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.5F}, Feature.BIRCH_TREE, FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addSavannaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.SAVANNA_TREE}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.8F}, Feature.NORMAL_TREE, FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(1, 0.1F, 1)
			)
		);
	}

	public static void addExtraSavannaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.SAVANNA_TREE}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.8F}, Feature.NORMAL_TREE, FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(2, 0.1F, 1)
			)
		);
	}

	public static void addMountainTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.SPRUCE_TREE, Feature.FANCY_TREE},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.666F, 0.1F},
					Feature.NORMAL_TREE,
					FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(0, 0.1F, 1)
			)
		);
	}

	public static void addExtraMountainTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.SPRUCE_TREE, Feature.FANCY_TREE},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.666F, 0.1F},
					Feature.NORMAL_TREE,
					FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(3, 0.1F, 1)
			)
		);
	}

	public static void addJungleTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.FANCY_TREE, Feature.JUNGLE_GROUND_BUSH, Feature.MEGA_JUNGLE_TREE},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.1F, 0.5F, 0.33333334F},
					Feature.JUNGLE_TREE,
					FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(50, 0.1F, 1)
			)
		);
	}

	public static void addJungleEdgeTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.FANCY_TREE, Feature.JUNGLE_GROUND_BUSH},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.1F, 0.5F},
					Feature.JUNGLE_TREE,
					FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(2, 0.1F, 1)
			)
		);
	}

	public static void addBadlandsPlateauTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.NORMAL_TREE, FeatureConfig.DEFAULT, Decorator.COUNT_EXTRA_HEIGHTMAP, new CountExtraChanceDecoratorConfig(5, 0.1F, 1))
		);
	}

	public static void addSnowySpruceTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.SPRUCE_TREE, FeatureConfig.DEFAULT, Decorator.COUNT_EXTRA_HEIGHTMAP, new CountExtraChanceDecoratorConfig(0, 0.1F, 1))
		);
	}

	public static void addGiantSpruceTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.MEGA_SPRUCE_TREE, Feature.PINE_TREE},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.33333334F, 0.33333334F},
					Feature.SPRUCE_TREE,
					FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addGiantTreeTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.MEGA_SPRUCE_TREE, Feature.MEGA_PINE_TREE, Feature.PINE_TREE},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.025641026F, 0.30769232F, 0.33333334F},
					Feature.SPRUCE_TREE,
					FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addJungleGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.JUNGLE_GRASS, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(25))
		);
	}

	public static void addSavannaTallGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.DOUBLE_PLANT, new DoublePlantFeatureConfig(Blocks.TALL_GRASS.getDefaultState()), Decorator.COUNT_HEIGHTMAP_32, new CountDecoratorConfig(7)
			)
		);
	}

	public static void addShatteredSavannaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.GRASS, new GrassFeatureConfig(Blocks.GRASS.getDefaultState()), Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(5))
		);
	}

	public static void addSavannaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.GRASS, new GrassFeatureConfig(Blocks.GRASS.getDefaultState()), Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(20))
		);
	}

	public static void addBadlandsGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.GRASS, new GrassFeatureConfig(Blocks.GRASS.getDefaultState()), Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.DEAD_BUSH, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(20))
		);
	}

	public static void addForestFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_RANDOM_SELECTOR,
				new RandomRandomFeatureConfig(
					new Feature[]{Feature.DOUBLE_PLANT, Feature.DOUBLE_PLANT, Feature.DOUBLE_PLANT, Feature.GENERAL_FOREST_FLOWER},
					new FeatureConfig[]{
						new DoublePlantFeatureConfig(Blocks.LILAC.getDefaultState()),
						new DoublePlantFeatureConfig(Blocks.ROSE_BUSH.getDefaultState()),
						new DoublePlantFeatureConfig(Blocks.PEONY.getDefaultState()),
						FeatureConfig.DEFAULT
					},
					0
				),
				Decorator.COUNT_HEIGHTMAP_32,
				new CountDecoratorConfig(5)
			)
		);
	}

	public static void addForestGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.GRASS, new GrassFeatureConfig(Blocks.GRASS.getDefaultState()), Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(2))
		);
	}

	public static void addSwampFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.SWAMP_TREE, FeatureConfig.DEFAULT, Decorator.COUNT_EXTRA_HEIGHTMAP, new CountExtraChanceDecoratorConfig(2, 0.1F, 1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.SWAMP_FLOWER, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_32, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.GRASS, new GrassFeatureConfig(Blocks.GRASS.getDefaultState()), Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(5))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.DEAD_BUSH, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.WATERLILY, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(4))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.BUSH, new BushFeatureConfig(Blocks.BROWN_MUSHROOM.getDefaultState()), Decorator.COUNT_CHANCE_HEIGHTMAP, new CountChanceDecoratorConfig(8, 0.25F)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.BUSH,
				new BushFeatureConfig(Blocks.RED_MUSHROOM.getDefaultState()),
				Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE,
				new CountChanceDecoratorConfig(8, 0.125F)
			)
		);
	}

	public static void addMushroomFieldsFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_BOOLEAN_SELECTOR,
				new RandomBooleanFeatureConfig(Feature.HUGE_RED_MUSHROOM, new PlantedFeatureConfig(false), Feature.HUGE_BROWN_MUSHROOM, new PlantedFeatureConfig(false)),
				Decorator.COUNT_HEIGHTMAP,
				new CountDecoratorConfig(1)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.BUSH, new BushFeatureConfig(Blocks.BROWN_MUSHROOM.getDefaultState()), Decorator.COUNT_CHANCE_HEIGHTMAP, new CountChanceDecoratorConfig(1, 0.25F)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.BUSH,
				new BushFeatureConfig(Blocks.RED_MUSHROOM.getDefaultState()),
				Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE,
				new CountChanceDecoratorConfig(1, 0.125F)
			)
		);
	}

	public static void addPlainsFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.RANDOM_SELECTOR,
				new RandomFeatureConfig(
					new Feature[]{Feature.FANCY_TREE}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.33333334F}, Feature.NORMAL_TREE, FeatureConfig.DEFAULT
				),
				Decorator.COUNT_EXTRA_HEIGHTMAP,
				new CountExtraChanceDecoratorConfig(0, 0.05F, 1)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.PLAIN_FLOWER, FeatureConfig.DEFAULT, Decorator.NOISE_HEIGHTMAP_32, new NoiseHeightmapDecoratorConfig(-0.8, 15, 4))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.GRASS, new GrassFeatureConfig(Blocks.GRASS.getDefaultState()), Decorator.NOISE_HEIGHTMAP_DOUBLE, new NoiseHeightmapDecoratorConfig(-0.8, 5, 10)
			)
		);
	}

	public static void addDesertDeadBushes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.DEAD_BUSH, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(2))
		);
	}

	public static void addGiantTaigaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.TAIGA_GRASS, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(7))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.DEAD_BUSH, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.BUSH, new BushFeatureConfig(Blocks.BROWN_MUSHROOM.getDefaultState()), Decorator.COUNT_CHANCE_HEIGHTMAP, new CountChanceDecoratorConfig(3, 0.25F)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.BUSH,
				new BushFeatureConfig(Blocks.RED_MUSHROOM.getDefaultState()),
				Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE,
				new CountChanceDecoratorConfig(3, 0.125F)
			)
		);
	}

	public static void addDefaultFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.DEFAULT_FLOWER, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_32, new CountDecoratorConfig(2))
		);
	}

	public static void addExtraDefaultFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.DEFAULT_FLOWER, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_32, new CountDecoratorConfig(4))
		);
	}

	public static void addDefaultGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.GRASS, new GrassFeatureConfig(Blocks.GRASS.getDefaultState()), Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(1))
		);
	}

	public static void addTaigaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.TAIGA_GRASS, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.BUSH, new BushFeatureConfig(Blocks.BROWN_MUSHROOM.getDefaultState()), Decorator.COUNT_CHANCE_HEIGHTMAP, new CountChanceDecoratorConfig(1, 0.25F)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.BUSH,
				new BushFeatureConfig(Blocks.RED_MUSHROOM.getDefaultState()),
				Decorator.COUNT_CHANCE_HEIGHTMAP_DOUBLE,
				new CountChanceDecoratorConfig(1, 0.125F)
			)
		);
	}

	public static void addPlainsTallGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.DOUBLE_PLANT,
				new DoublePlantFeatureConfig(Blocks.TALL_GRASS.getDefaultState()),
				Decorator.NOISE_HEIGHTMAP_32,
				new NoiseHeightmapDecoratorConfig(-0.8, 0, 7)
			)
		);
	}

	public static void addDefaultMushrooms(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.BUSH, new BushFeatureConfig(Blocks.BROWN_MUSHROOM.getDefaultState()), Decorator.CHANCE_HEIGHTMAP_DOUBLE, new ChanceDecoratorConfig(4)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.BUSH, new BushFeatureConfig(Blocks.RED_MUSHROOM.getDefaultState()), Decorator.CHANCE_HEIGHTMAP_DOUBLE, new ChanceDecoratorConfig(8)
			)
		);
	}

	public static void addDefaultVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.REED, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(10))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.PUMPKIN, FeatureConfig.DEFAULT, Decorator.CHANCE_HEIGHTMAP_DOUBLE, new ChanceDecoratorConfig(32))
		);
	}

	public static void addBadlandsVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.REED, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(13))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.PUMPKIN, FeatureConfig.DEFAULT, Decorator.CHANCE_HEIGHTMAP_DOUBLE, new ChanceDecoratorConfig(32))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.CACTUS, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(5))
		);
	}

	public static void addJungleVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.MELON, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.VINES, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHT_64, new CountDecoratorConfig(50))
		);
	}

	public static void addDesertVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.REED, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(60))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.PUMPKIN, FeatureConfig.DEFAULT, Decorator.CHANCE_HEIGHTMAP_DOUBLE, new ChanceDecoratorConfig(32))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.CACTUS, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(10))
		);
	}

	public static void addSwampVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.REED, FeatureConfig.DEFAULT, Decorator.COUNT_HEIGHTMAP_DOUBLE, new CountDecoratorConfig(20))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.PUMPKIN, FeatureConfig.DEFAULT, Decorator.CHANCE_HEIGHTMAP_DOUBLE, new ChanceDecoratorConfig(32))
		);
	}

	public static void addDesertFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.configureFeature(Feature.DESERT_WELL, FeatureConfig.DEFAULT, Decorator.CHANCE_HEIGHTMAP, new ChanceDecoratorConfig(1000))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Biome.configureFeature(Feature.FOSSIL, FeatureConfig.DEFAULT, Decorator.CHANCE_PASSTHROUGH, new ChanceDecoratorConfig(64))
		);
	}

	public static void addFossils(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Biome.configureFeature(Feature.FOSSIL, FeatureConfig.DEFAULT, Decorator.CHANCE_PASSTHROUGH, new ChanceDecoratorConfig(64))
		);
	}

	public static void addKelp(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.KELP,
				FeatureConfig.DEFAULT,
				Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED,
				new TopSolidHeightmapNoiseBiasedDecoratorConfig(120, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG)
			)
		);
	}

	public static void addSeagrassOnStone(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.SIMPLE_BLOCK,
				new SimpleBlockFeatureConfig(
					Blocks.SEAGRASS.getDefaultState(),
					new BlockState[]{Blocks.STONE.getDefaultState()},
					new BlockState[]{Blocks.WATER.getDefaultState()},
					new BlockState[]{Blocks.WATER.getDefaultState()}
				),
				Decorator.CARVING_MASK,
				new CarvingMaskDecoratorConfig(GenerationStep.Carver.LIQUID, 0.1F)
			)
		);
	}

	public static void addSeagrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.SEAGRASS, new SeagrassFeatureConfig(80, 0.3), Decorator.TOP_SOLID_HEIGHTMAP, DecoratorConfig.DEFAULT)
		);
	}

	public static void addMoreSeagrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.SEAGRASS, new SeagrassFeatureConfig(80, 0.8), Decorator.TOP_SOLID_HEIGHTMAP, DecoratorConfig.DEFAULT)
		);
	}

	public static void addLessKelp(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.KELP,
				FeatureConfig.DEFAULT,
				Decorator.TOP_SOLID_HEIGHTMAP_NOISE_BIASED,
				new TopSolidHeightmapNoiseBiasedDecoratorConfig(80, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG)
			)
		);
	}

	public static void addSprings(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.SPRING_FEATURE, new SpringFeatureConfig(Fluids.WATER.getDefaultState()), Decorator.COUNT_BIASED_RANGE, new RangeDecoratorConfig(50, 8, 8, 256)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.SPRING_FEATURE, new SpringFeatureConfig(Fluids.LAVA.getDefaultState()), Decorator.COUNT_VERY_BIASED_RANGE, new RangeDecoratorConfig(20, 8, 16, 256)
			)
		);
	}

	public static void addIcebergs(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(Feature.ICEBERG, new IcebergFeatureConfig(Blocks.PACKED_ICE.getDefaultState()), Decorator.ICEBERG, new ChanceDecoratorConfig(16))
		);
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(Feature.ICEBERG, new IcebergFeatureConfig(Blocks.BLUE_ICE.getDefaultState()), Decorator.ICEBERG, new ChanceDecoratorConfig(200))
		);
	}

	public static void addBlueIce(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.configureFeature(Feature.BLUE_ICE, FeatureConfig.DEFAULT, Decorator.RANDOM_COUNT_RANGE, new RangeDecoratorConfig(20, 30, 32, 64))
		);
	}

	public static void addFrozenTopLayer(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.TOP_LAYER_MODIFICATION,
			Biome.configureFeature(Feature.FREEZE_TOP_LAYER, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
	}
}
