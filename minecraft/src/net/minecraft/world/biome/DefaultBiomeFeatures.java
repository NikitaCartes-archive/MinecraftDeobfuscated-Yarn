package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.Heightmap;
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
import net.minecraft.world.gen.feature.NewVillageFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PillagerOutpostFeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.SpringFeatureConfig;

public class DefaultBiomeFeatures {
	public static void addLandCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CAVE, new ProbabilityConfig(0.14285715F)));
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.RAVINE, new ProbabilityConfig(0.02F)));
	}

	public static void addOceanCarvers(Biome biome) {
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.CAVE, new ProbabilityConfig(0.06666667F)));
		biome.addCarver(GenerationStep.Carver.AIR, Biome.configureCarver(Carver.RAVINE, new ProbabilityConfig(0.02F)));
		biome.addCarver(GenerationStep.Carver.LIQUID, Biome.configureCarver(Carver.UNDERWATER_RAVINE, new ProbabilityConfig(0.02F)));
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
			Biome.configureFeature(Feature.VILLAGE, new NewVillageFeatureConfig("village/plains/town_centers", 6), Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
	}

	public static void addDefaultLakes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(Feature.field_13573, new LakeFeatureConfig(Blocks.field_10382.getDefaultState()), Decorator.field_14242, new LakeDecoratorConfig(4))
		);
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(Feature.field_13573, new LakeFeatureConfig(Blocks.field_10164.getDefaultState()), Decorator.field_14237, new LakeDecoratorConfig(80))
		);
	}

	public static void addDesertLakes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(Feature.field_13573, new LakeFeatureConfig(Blocks.field_10164.getDefaultState()), Decorator.field_14237, new LakeDecoratorConfig(80))
		);
	}

	public static void addDungeons(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Biome.configureFeature(Feature.field_13579, FeatureConfig.DEFAULT, Decorator.field_14265, new DungeonDecoratorConfig(8))
		);
	}

	public static void addMineables(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10566.getDefaultState(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 256)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10255.getDefaultState(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(8, 0, 0, 256)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10474.getDefaultState(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10508.getDefaultState(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10115.getDefaultState(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
	}

	public static void addDefaultOres(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10418.getDefaultState(), 17),
				Decorator.field_14241,
				new RangeDecoratorConfig(20, 0, 0, 128)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10212.getDefaultState(), 9),
				Decorator.field_14241,
				new RangeDecoratorConfig(20, 0, 0, 64)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10571.getDefaultState(), 9),
				Decorator.field_14241,
				new RangeDecoratorConfig(2, 0, 0, 32)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10080.getDefaultState(), 8),
				Decorator.field_14241,
				new RangeDecoratorConfig(8, 0, 0, 16)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10442.getDefaultState(), 8),
				Decorator.field_14241,
				new RangeDecoratorConfig(1, 0, 0, 16)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10090.getDefaultState(), 7),
				Decorator.field_14252,
				new CountDepthDecoratorConfig(1, 16, 16)
			)
		);
	}

	public static void addExtraGoldOre(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10571.getDefaultState(), 9),
				Decorator.field_14241,
				new RangeDecoratorConfig(20, 32, 32, 80)
			)
		);
	}

	public static void addEmeraldOre(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13594,
				new EmeraldOreFeatureConfig(Blocks.field_10340.getDefaultState(), Blocks.field_10013.getDefaultState()),
				Decorator.field_14268,
				DecoratorConfig.DEFAULT
			)
		);
	}

	public static void addInfestedStone(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Biome.configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10277.getDefaultState(), 9),
				Decorator.field_14241,
				new RangeDecoratorConfig(7, 0, 0, 64)
			)
		);
	}

	public static void addDefaultDisks(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13509,
				new DiskFeatureConfig(
					Blocks.field_10102.getDefaultState(), 7, 2, Lists.<BlockState>newArrayList(Blocks.field_10566.getDefaultState(), Blocks.field_10219.getDefaultState())
				),
				Decorator.field_14245,
				new CountDecoratorConfig(3)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13509,
				new DiskFeatureConfig(
					Blocks.field_10460.getDefaultState(), 4, 1, Lists.<BlockState>newArrayList(Blocks.field_10566.getDefaultState(), Blocks.field_10460.getDefaultState())
				),
				Decorator.field_14245,
				new CountDecoratorConfig(1)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13509,
				new DiskFeatureConfig(
					Blocks.field_10255.getDefaultState(), 6, 2, Lists.<BlockState>newArrayList(Blocks.field_10566.getDefaultState(), Blocks.field_10219.getDefaultState())
				),
				Decorator.field_14245,
				new CountDecoratorConfig(1)
			)
		);
	}

	public static void addClay(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.configureFeature(
				Feature.field_13509,
				new DiskFeatureConfig(
					Blocks.field_10460.getDefaultState(), 4, 1, Lists.<BlockState>newArrayList(Blocks.field_10566.getDefaultState(), Blocks.field_10460.getDefaultState())
				),
				Decorator.field_14245,
				new CountDecoratorConfig(1)
			)
		);
	}

	public static void addMossyRocks(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(
				Feature.field_13584, new BoulderFeatureConfig(Blocks.field_9989.getDefaultState(), 0), Decorator.field_14264, new CountDecoratorConfig(3)
			)
		);
	}

	public static void addLargeFerns(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13576, new DoublePlantFeatureConfig(Blocks.field_10313.getDefaultState()), Decorator.field_14253, new CountDecoratorConfig(7)
			)
		);
	}

	public static void addSweetBerryBushesSnowy(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_17004, FeatureConfig.DEFAULT, Decorator.field_14263, new ChanceDecoratorConfig(12))
		);
	}

	public static void addSweetBerryBushes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_17004, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(1))
		);
	}

	public static void addBamboo(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13540, new ProbabilityConfig(0.0F), Decorator.field_14240, new CountDecoratorConfig(16))
		);
	}

	public static void addBambooJungleTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13540,
				new ProbabilityConfig(0.2F),
				Decorator.field_14247,
				new TopSolidHeightmapNoiseBiasedDecoratorConfig(160, 80.0, 0.3, Heightmap.Type.WORLD_SURFACE_WG)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13529, Feature.field_13537, Feature.field_13558},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.05F, 0.15F, 0.7F},
					Feature.field_13590,
					FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(30, 0.1F, 1)
			)
		);
	}

	public static void addTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13581}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.33333334F}, Feature.field_13577, FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addWaterBiomeOakTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13529}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.1F}, Feature.field_13510, FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(0, 0.1F, 1)
			)
		);
	}

	public static void addBirchTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13566, FeatureConfig.DEFAULT, Decorator.field_14267, new CountExtraChanceDecoratorConfig(10, 0.1F, 1))
		);
	}

	public static void addForestTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13566, Feature.field_13529},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.2F, 0.1F},
					Feature.field_13510,
					FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addTallBirchTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13578}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.5F}, Feature.field_13566, FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addSavannaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13545}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.8F}, Feature.field_13510, FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(1, 0.1F, 1)
			)
		);
	}

	public static void addExtraSavannaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13545}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.8F}, Feature.field_13510, FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(2, 0.1F, 1)
			)
		);
	}

	public static void addMountainTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13577, Feature.field_13529},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.666F, 0.1F},
					Feature.field_13510,
					FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(0, 0.1F, 1)
			)
		);
	}

	public static void addExtraMountainTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13577, Feature.field_13529},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.666F, 0.1F},
					Feature.field_13510,
					FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(3, 0.1F, 1)
			)
		);
	}

	public static void addJungleTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13529, Feature.field_13537, Feature.field_13558},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.1F, 0.5F, 0.33333334F},
					Feature.field_13508,
					FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(50, 0.1F, 1)
			)
		);
	}

	public static void addJungleEdgeTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13529, Feature.field_13537},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.1F, 0.5F},
					Feature.field_13508,
					FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(2, 0.1F, 1)
			)
		);
	}

	public static void addBadlandsPlateauTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13510, FeatureConfig.DEFAULT, Decorator.field_14267, new CountExtraChanceDecoratorConfig(5, 0.1F, 1))
		);
	}

	public static void addSnowySpruceTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13577, FeatureConfig.DEFAULT, Decorator.field_14267, new CountExtraChanceDecoratorConfig(0, 0.1F, 1))
		);
	}

	public static void addGiantSpruceTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13580, Feature.field_13581},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.33333334F, 0.33333334F},
					Feature.field_13577,
					FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addGiantTreeTaigaTrees(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13580, Feature.field_13543, Feature.field_13581},
					new FeatureConfig[]{FeatureConfig.DEFAULT, FeatureConfig.DEFAULT, FeatureConfig.DEFAULT},
					new float[]{0.025641026F, 0.30769232F, 0.33333334F},
					Feature.field_13577,
					FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addJungleGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13590, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(25))
		);
	}

	public static void addSavannaTallGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13576, new DoublePlantFeatureConfig(Blocks.field_10214.getDefaultState()), Decorator.field_14253, new CountDecoratorConfig(7)
			)
		);
	}

	public static void addShatteredSavanaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.getDefaultState()), Decorator.field_14240, new CountDecoratorConfig(5))
		);
	}

	public static void addSavannaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.getDefaultState()), Decorator.field_14240, new CountDecoratorConfig(20)
			)
		);
	}

	public static void addBadlandsGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.getDefaultState()), Decorator.field_14240, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13548, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(20))
		);
	}

	public static void addForestFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13512,
				new RandomRandomFeatureConfig(
					new Feature[]{Feature.field_13576, Feature.field_13576, Feature.field_13576, Feature.GENERAL_FOREST_FLOWER},
					new FeatureConfig[]{
						new DoublePlantFeatureConfig(Blocks.field_10378.getDefaultState()),
						new DoublePlantFeatureConfig(Blocks.field_10430.getDefaultState()),
						new DoublePlantFeatureConfig(Blocks.field_10003.getDefaultState()),
						FeatureConfig.DEFAULT
					},
					0
				),
				Decorator.field_14253,
				new CountDecoratorConfig(5)
			)
		);
	}

	public static void addForestGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.getDefaultState()), Decorator.field_14240, new CountDecoratorConfig(2))
		);
	}

	public static void addSwampFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13530, FeatureConfig.DEFAULT, Decorator.field_14267, new CountExtraChanceDecoratorConfig(2, 0.1F, 1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.SWAMP_FLOWER, FeatureConfig.DEFAULT, Decorator.field_14253, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.getDefaultState()), Decorator.field_14240, new CountDecoratorConfig(5))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13548, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13542, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(4))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.getDefaultState()), Decorator.field_14234, new CountChanceDecoratorConfig(8, 0.25F)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.getDefaultState()), Decorator.field_14261, new CountChanceDecoratorConfig(8, 0.125F)
			)
		);
	}

	public static void addMushroomFieldsFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13550,
				new RandomBooleanFeatureConfig(Feature.field_13571, FeatureConfig.DEFAULT, Feature.field_13531, FeatureConfig.DEFAULT),
				Decorator.field_14238,
				new CountDecoratorConfig(1)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.getDefaultState()), Decorator.field_14234, new CountChanceDecoratorConfig(1, 0.25F)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.getDefaultState()), Decorator.field_14261, new CountChanceDecoratorConfig(1, 0.125F)
			)
		);
	}

	public static void addPlainsFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13529}, new FeatureConfig[]{FeatureConfig.DEFAULT}, new float[]{0.33333334F}, Feature.field_13510, FeatureConfig.DEFAULT
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(0, 0.05F, 1)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.PLAIN_FLOWER, FeatureConfig.DEFAULT, Decorator.field_14254, new NoiseHeightmapDecoratorConfig(-0.8, 15, 4))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.getDefaultState()), Decorator.field_14236, new NoiseHeightmapDecoratorConfig(-0.8, 5, 10)
			)
		);
	}

	public static void addDesertDeadBushes(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13548, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(2))
		);
	}

	public static void addGiantTaigaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13521, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(7))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13548, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.getDefaultState()), Decorator.field_14234, new CountChanceDecoratorConfig(3, 0.25F)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.getDefaultState()), Decorator.field_14261, new CountChanceDecoratorConfig(3, 0.125F)
			)
		);
	}

	public static void addDefaultFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.DEFAULT_FLOWER, FeatureConfig.DEFAULT, Decorator.field_14253, new CountDecoratorConfig(2))
		);
	}

	public static void addExtraDefaultFlowers(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.DEFAULT_FLOWER, FeatureConfig.DEFAULT, Decorator.field_14253, new CountDecoratorConfig(4))
		);
	}

	public static void addDefaultGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.getDefaultState()), Decorator.field_14240, new CountDecoratorConfig(1))
		);
	}

	public static void addTaigaGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13521, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.getDefaultState()), Decorator.field_14234, new CountChanceDecoratorConfig(1, 0.25F)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.getDefaultState()), Decorator.field_14261, new CountChanceDecoratorConfig(1, 0.125F)
			)
		);
	}

	public static void addPlainsTallGrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13576,
				new DoublePlantFeatureConfig(Blocks.field_10214.getDefaultState()),
				Decorator.field_14254,
				new NoiseHeightmapDecoratorConfig(-0.8, 0, 7)
			)
		);
	}

	public static void addDefaultMushrooms(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.getDefaultState()), Decorator.field_14263, new ChanceDecoratorConfig(4))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.getDefaultState()), Decorator.field_14263, new ChanceDecoratorConfig(8))
		);
	}

	public static void addDefaultVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13583, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(10))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13524, FeatureConfig.DEFAULT, Decorator.field_14263, new ChanceDecoratorConfig(32))
		);
	}

	public static void addBadlandsVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13583, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(13))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13524, FeatureConfig.DEFAULT, Decorator.field_14263, new ChanceDecoratorConfig(32))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13554, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(5))
		);
	}

	public static void addJungleVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13534, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(1))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13559, FeatureConfig.DEFAULT, Decorator.field_14249, new CountDecoratorConfig(50))
		);
	}

	public static void addDesertVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13583, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(60))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13524, FeatureConfig.DEFAULT, Decorator.field_14263, new ChanceDecoratorConfig(32))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13554, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(10))
		);
	}

	public static void addSwampVegetation(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13583, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(20))
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13524, FeatureConfig.DEFAULT, Decorator.field_14263, new ChanceDecoratorConfig(32))
		);
	}

	public static void addDesertFeatures(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.configureFeature(Feature.field_13592, FeatureConfig.DEFAULT, Decorator.field_14259, new ChanceDecoratorConfig(1000))
		);
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Biome.configureFeature(Feature.field_13516, FeatureConfig.DEFAULT, Decorator.field_14246, new ChanceDecoratorConfig(64))
		);
	}

	public static void addFossils(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Biome.configureFeature(Feature.field_13516, FeatureConfig.DEFAULT, Decorator.field_14246, new ChanceDecoratorConfig(64))
		);
	}

	public static void addKelp(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13535,
				FeatureConfig.DEFAULT,
				Decorator.field_14247,
				new TopSolidHeightmapNoiseBiasedDecoratorConfig(120, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG)
			)
		);
	}

	public static void addSeagrassOnStone(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13518,
				new SimpleBlockFeatureConfig(
					Blocks.field_10376.getDefaultState(),
					new BlockState[]{Blocks.field_10340.getDefaultState()},
					new BlockState[]{Blocks.field_10382.getDefaultState()},
					new BlockState[]{Blocks.field_10382.getDefaultState()}
				),
				Decorator.field_14229,
				new CarvingMaskDecoratorConfig(GenerationStep.Carver.LIQUID, 0.1F)
			)
		);
	}

	public static void addSeagrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13567, new SeagrassFeatureConfig(80, 0.3), Decorator.field_14231, DecoratorConfig.DEFAULT)
		);
	}

	public static void addMoreSeagrass(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(Feature.field_13567, new SeagrassFeatureConfig(80, 0.8), Decorator.field_14231, DecoratorConfig.DEFAULT)
		);
	}

	public static void addLessKelp(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13535,
				FeatureConfig.DEFAULT,
				Decorator.field_14247,
				new TopSolidHeightmapNoiseBiasedDecoratorConfig(80, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG)
			)
		);
	}

	public static void addSprings(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13513, new SpringFeatureConfig(Fluids.WATER.getDefaultState()), Decorator.field_14255, new RangeDecoratorConfig(50, 8, 8, 256)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.configureFeature(
				Feature.field_13513, new SpringFeatureConfig(Fluids.LAVA.getDefaultState()), Decorator.field_14266, new RangeDecoratorConfig(20, 8, 16, 256)
			)
		);
	}

	public static void addIcebergs(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(
				Feature.field_13544, new IcebergFeatureConfig(Blocks.field_10225.getDefaultState()), Decorator.field_14243, new ChanceDecoratorConfig(16)
			)
		);
		biome.addFeature(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.configureFeature(
				Feature.field_13544, new IcebergFeatureConfig(Blocks.field_10384.getDefaultState()), Decorator.field_14243, new ChanceDecoratorConfig(200)
			)
		);
	}

	public static void addBlueIce(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.configureFeature(Feature.field_13560, FeatureConfig.DEFAULT, Decorator.field_14260, new RangeDecoratorConfig(20, 30, 32, 64))
		);
	}

	public static void addFrozenTopLayer(Biome biome) {
		biome.addFeature(
			GenerationStep.Feature.TOP_LAYER_MODIFICATION, Biome.configureFeature(Feature.field_13539, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT)
		);
	}
}
