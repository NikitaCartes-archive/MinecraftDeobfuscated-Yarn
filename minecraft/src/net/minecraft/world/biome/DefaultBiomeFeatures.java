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
import net.minecraft.world.gen.feature.VillageFeatureConfig;

public class DefaultBiomeFeatures {
	public static void addLandCarvers(Biome biome) {
		biome.method_8691(GenerationStep.Carver.AIR, Biome.method_8714(Carver.CAVE, new ProbabilityConfig(0.14285715F)));
		biome.method_8691(GenerationStep.Carver.AIR, Biome.method_8714(Carver.RAVINE, new ProbabilityConfig(0.02F)));
	}

	public static void addOceanCarvers(Biome biome) {
		biome.method_8691(GenerationStep.Carver.AIR, Biome.method_8714(Carver.CAVE, new ProbabilityConfig(0.06666667F)));
		biome.method_8691(GenerationStep.Carver.AIR, Biome.method_8714(Carver.RAVINE, new ProbabilityConfig(0.02F)));
		biome.method_8691(GenerationStep.Carver.LIQUID, Biome.method_8714(Carver.UNDERWATER_RAVINE, new ProbabilityConfig(0.02F)));
		biome.method_8691(GenerationStep.Carver.LIQUID, Biome.method_8714(Carver.UNDERWATER_CAVE, new ProbabilityConfig(0.06666667F)));
	}

	public static void addDefaultStructures(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Biome.method_8699(Feature.field_13547, new MineshaftFeatureConfig(0.004F, MineshaftFeature.Type.NORMAL), Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.method_8699(Feature.field_16655, new PillagerOutpostFeatureConfig(0.004), Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Biome.method_8699(Feature.field_13565, FeatureConfig.field_13603, Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.method_8699(Feature.field_13520, FeatureConfig.field_13603, Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.method_8699(Feature.field_13515, FeatureConfig.field_13603, Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.method_8699(Feature.field_13586, FeatureConfig.field_13603, Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.method_8699(Feature.field_13527, FeatureConfig.field_13603, Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.method_8699(Feature.field_13589, new ShipwreckFeatureConfig(false), Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.method_8699(Feature.field_13588, FeatureConfig.field_13603, Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES, Biome.method_8699(Feature.field_13528, FeatureConfig.field_13603, Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.method_8699(Feature.field_13536, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.COLD, 0.3F, 0.9F), Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Biome.method_8699(Feature.field_13538, new BuriedTreasureFeatureConfig(0.01F), Decorator.NOPE, DecoratorConfig.field_13436)
		);
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.method_8699(Feature.field_13587, new VillageFeatureConfig("village/plains/town_centers", 6), Decorator.NOPE, DecoratorConfig.field_13436)
		);
	}

	public static void addDefaultLakes(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.method_8699(Feature.field_13573, new LakeFeatureConfig(Blocks.field_10382.method_9564()), Decorator.field_14242, new LakeDecoratorConfig(4))
		);
		biome.method_8719(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.method_8699(Feature.field_13573, new LakeFeatureConfig(Blocks.field_10164.method_9564()), Decorator.field_14237, new LakeDecoratorConfig(80))
		);
	}

	public static void addDesertLakes(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.method_8699(Feature.field_13573, new LakeFeatureConfig(Blocks.field_10164.method_9564()), Decorator.field_14237, new LakeDecoratorConfig(80))
		);
	}

	public static void addDungeons(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_STRUCTURES,
			Biome.method_8699(Feature.field_13579, FeatureConfig.field_13603, Decorator.field_14265, new DungeonDecoratorConfig(8))
		);
	}

	public static void addMineables(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10566.method_9564(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 256)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10255.method_9564(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(8, 0, 0, 256)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10474.method_9564(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10508.method_9564(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10115.method_9564(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
	}

	public static void addDefaultOres(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10418.method_9564(), 17),
				Decorator.field_14241,
				new RangeDecoratorConfig(20, 0, 0, 128)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10212.method_9564(), 9),
				Decorator.field_14241,
				new RangeDecoratorConfig(20, 0, 0, 64)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10571.method_9564(), 9),
				Decorator.field_14241,
				new RangeDecoratorConfig(2, 0, 0, 32)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10080.method_9564(), 8),
				Decorator.field_14241,
				new RangeDecoratorConfig(8, 0, 0, 16)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10442.method_9564(), 8),
				Decorator.field_14241,
				new RangeDecoratorConfig(1, 0, 0, 16)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10090.method_9564(), 7),
				Decorator.field_14252,
				new CountDepthDecoratorConfig(1, 16, 16)
			)
		);
	}

	public static void addExtraGoldOre(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10571.method_9564(), 9),
				Decorator.field_14241,
				new RangeDecoratorConfig(20, 32, 32, 80)
			)
		);
	}

	public static void addEmeraldOre(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13594,
				new EmeraldOreFeatureConfig(Blocks.field_10340.method_9564(), Blocks.field_10013.method_9564()),
				Decorator.field_14268,
				DecoratorConfig.field_13436
			)
		);
	}

	public static void addInfestedStone(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Biome.method_8699(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10277.method_9564(), 9),
				Decorator.field_14241,
				new RangeDecoratorConfig(7, 0, 0, 64)
			)
		);
	}

	public static void addDefaultDisks(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13509,
				new DiskFeatureConfig(
					Blocks.field_10102.method_9564(), 7, 2, Lists.<BlockState>newArrayList(Blocks.field_10566.method_9564(), Blocks.field_10219.method_9564())
				),
				Decorator.field_14245,
				new CountDecoratorConfig(3)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13509,
				new DiskFeatureConfig(
					Blocks.field_10460.method_9564(), 4, 1, Lists.<BlockState>newArrayList(Blocks.field_10566.method_9564(), Blocks.field_10460.method_9564())
				),
				Decorator.field_14245,
				new CountDecoratorConfig(1)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13509,
				new DiskFeatureConfig(
					Blocks.field_10255.method_9564(), 6, 2, Lists.<BlockState>newArrayList(Blocks.field_10566.method_9564(), Blocks.field_10219.method_9564())
				),
				Decorator.field_14245,
				new CountDecoratorConfig(1)
			)
		);
	}

	public static void addClay(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_ORES,
			Biome.method_8699(
				Feature.field_13509,
				new DiskFeatureConfig(
					Blocks.field_10460.method_9564(), 4, 1, Lists.<BlockState>newArrayList(Blocks.field_10566.method_9564(), Blocks.field_10460.method_9564())
				),
				Decorator.field_14245,
				new CountDecoratorConfig(1)
			)
		);
	}

	public static void addMossyRocks(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.method_8699(Feature.field_13584, new BoulderFeatureConfig(Blocks.field_9989.method_9564(), 0), Decorator.field_14264, new CountDecoratorConfig(3))
		);
	}

	public static void addLargeFerns(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13576, new DoublePlantFeatureConfig(Blocks.field_10313.method_9564()), Decorator.field_14253, new CountDecoratorConfig(7))
		);
	}

	public static void addSweetBerryBushesSnowy(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_17004, FeatureConfig.field_13603, Decorator.field_14263, new ChanceDecoratorConfig(12))
		);
	}

	public static void addSweetBerryBushes(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_17004, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(1))
		);
	}

	public static void addBamboo(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13540, new ProbabilityConfig(0.0F), Decorator.field_14240, new CountDecoratorConfig(16))
		);
	}

	public static void addBambooJungleTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13540,
				new ProbabilityConfig(0.2F),
				Decorator.field_14247,
				new TopSolidHeightmapNoiseBiasedDecoratorConfig(160, 80.0, 0.3, Heightmap.Type.WORLD_SURFACE_WG)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13529, Feature.field_13537, Feature.field_13558},
					new FeatureConfig[]{FeatureConfig.field_13603, FeatureConfig.field_13603, FeatureConfig.field_13603},
					new float[]{0.05F, 0.15F, 0.7F},
					Feature.field_13590,
					FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(30, 0.1F, 1)
			)
		);
	}

	public static void addTaigaTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13581},
					new FeatureConfig[]{FeatureConfig.field_13603},
					new float[]{0.33333334F},
					Feature.field_13577,
					FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addWaterBiomeOakTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13529}, new FeatureConfig[]{FeatureConfig.field_13603}, new float[]{0.1F}, Feature.field_13510, FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(0, 0.1F, 1)
			)
		);
	}

	public static void addBirchTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13566, FeatureConfig.field_13603, Decorator.field_14267, new CountExtraChanceDecoratorConfig(10, 0.1F, 1))
		);
	}

	public static void addForestTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13566, Feature.field_13529},
					new FeatureConfig[]{FeatureConfig.field_13603, FeatureConfig.field_13603},
					new float[]{0.2F, 0.1F},
					Feature.field_13510,
					FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addTallBirchTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13578}, new FeatureConfig[]{FeatureConfig.field_13603}, new float[]{0.5F}, Feature.field_13566, FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addSavannaTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13545}, new FeatureConfig[]{FeatureConfig.field_13603}, new float[]{0.8F}, Feature.field_13510, FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(1, 0.1F, 1)
			)
		);
	}

	public static void addExtraSavannaTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13545}, new FeatureConfig[]{FeatureConfig.field_13603}, new float[]{0.8F}, Feature.field_13510, FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(2, 0.1F, 1)
			)
		);
	}

	public static void addMountainTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13577, Feature.field_13529},
					new FeatureConfig[]{FeatureConfig.field_13603, FeatureConfig.field_13603},
					new float[]{0.666F, 0.1F},
					Feature.field_13510,
					FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(0, 0.1F, 1)
			)
		);
	}

	public static void addExtraMountainTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13577, Feature.field_13529},
					new FeatureConfig[]{FeatureConfig.field_13603, FeatureConfig.field_13603},
					new float[]{0.666F, 0.1F},
					Feature.field_13510,
					FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(3, 0.1F, 1)
			)
		);
	}

	public static void addJungleTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13529, Feature.field_13537, Feature.field_13558},
					new FeatureConfig[]{FeatureConfig.field_13603, FeatureConfig.field_13603, FeatureConfig.field_13603},
					new float[]{0.1F, 0.5F, 0.33333334F},
					Feature.field_13508,
					FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(50, 0.1F, 1)
			)
		);
	}

	public static void addJungleEdgeTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13529, Feature.field_13537},
					new FeatureConfig[]{FeatureConfig.field_13603, FeatureConfig.field_13603},
					new float[]{0.1F, 0.5F},
					Feature.field_13508,
					FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(2, 0.1F, 1)
			)
		);
	}

	public static void addBadlandsPlateauTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13510, FeatureConfig.field_13603, Decorator.field_14267, new CountExtraChanceDecoratorConfig(5, 0.1F, 1))
		);
	}

	public static void addSnowySpruceTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13577, FeatureConfig.field_13603, Decorator.field_14267, new CountExtraChanceDecoratorConfig(0, 0.1F, 1))
		);
	}

	public static void addGiantSpruceTaigaTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13580, Feature.field_13581},
					new FeatureConfig[]{FeatureConfig.field_13603, FeatureConfig.field_13603},
					new float[]{0.33333334F, 0.33333334F},
					Feature.field_13577,
					FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addGiantTreeTaigaTrees(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13580, Feature.field_13543, Feature.field_13581},
					new FeatureConfig[]{FeatureConfig.field_13603, FeatureConfig.field_13603, FeatureConfig.field_13603},
					new float[]{0.025641026F, 0.30769232F, 0.33333334F},
					Feature.field_13577,
					FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(10, 0.1F, 1)
			)
		);
	}

	public static void addJungleGrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13590, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(25))
		);
	}

	public static void addSavannaTallGrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13576, new DoublePlantFeatureConfig(Blocks.field_10214.method_9564()), Decorator.field_14253, new CountDecoratorConfig(7))
		);
	}

	public static void addShatteredSavanaGrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.method_9564()), Decorator.field_14240, new CountDecoratorConfig(5))
		);
	}

	public static void addSavannaGrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.method_9564()), Decorator.field_14240, new CountDecoratorConfig(20))
		);
	}

	public static void addBadlandsGrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.method_9564()), Decorator.field_14240, new CountDecoratorConfig(1))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13548, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(20))
		);
	}

	public static void addForestFlowers(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13512,
				new RandomRandomFeatureConfig(
					new Feature[]{Feature.field_13576, Feature.field_13576, Feature.field_13576, Feature.field_13582},
					new FeatureConfig[]{
						new DoublePlantFeatureConfig(Blocks.field_10378.method_9564()),
						new DoublePlantFeatureConfig(Blocks.field_10430.method_9564()),
						new DoublePlantFeatureConfig(Blocks.field_10003.method_9564()),
						FeatureConfig.field_13603
					},
					0
				),
				Decorator.field_14253,
				new CountDecoratorConfig(5)
			)
		);
	}

	public static void addForestGrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.method_9564()), Decorator.field_14240, new CountDecoratorConfig(2))
		);
	}

	public static void addSwampFeatures(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13530, FeatureConfig.field_13603, Decorator.field_14267, new CountExtraChanceDecoratorConfig(2, 0.1F, 1))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13533, FeatureConfig.field_13603, Decorator.field_14253, new CountDecoratorConfig(1))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.method_9564()), Decorator.field_14240, new CountDecoratorConfig(5))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13548, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(1))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13542, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(4))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.method_9564()), Decorator.field_14234, new CountChanceDecoratorConfig(8, 0.25F)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.method_9564()), Decorator.field_14261, new CountChanceDecoratorConfig(8, 0.125F)
			)
		);
	}

	public static void addMushroomFieldsFeatures(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13550,
				new RandomBooleanFeatureConfig(Feature.field_13571, FeatureConfig.field_13603, Feature.field_13531, FeatureConfig.field_13603),
				Decorator.field_14238,
				new CountDecoratorConfig(1)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.method_9564()), Decorator.field_14234, new CountChanceDecoratorConfig(1, 0.25F)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.method_9564()), Decorator.field_14261, new CountChanceDecoratorConfig(1, 0.125F)
			)
		);
	}

	public static void addPlainsFeatures(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13593,
				new RandomFeatureConfig(
					new Feature[]{Feature.field_13529},
					new FeatureConfig[]{FeatureConfig.field_13603},
					new float[]{0.33333334F},
					Feature.field_13510,
					FeatureConfig.field_13603
				),
				Decorator.field_14267,
				new CountExtraChanceDecoratorConfig(0, 0.05F, 1)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13549, FeatureConfig.field_13603, Decorator.field_14254, new NoiseHeightmapDecoratorConfig(-0.8, 15, 4))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.method_9564()), Decorator.field_14236, new NoiseHeightmapDecoratorConfig(-0.8, 5, 10)
			)
		);
	}

	public static void addDesertDeadBushes(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13548, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(2))
		);
	}

	public static void addGiantTaigaGrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13521, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(7))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13548, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(1))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.method_9564()), Decorator.field_14234, new CountChanceDecoratorConfig(3, 0.25F)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.method_9564()), Decorator.field_14261, new CountChanceDecoratorConfig(3, 0.125F)
			)
		);
	}

	public static void addDefaultFlowers(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13541, FeatureConfig.field_13603, Decorator.field_14253, new CountDecoratorConfig(2))
		);
	}

	public static void addExtraDefaultFlowers(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13541, FeatureConfig.field_13603, Decorator.field_14253, new CountDecoratorConfig(4))
		);
	}

	public static void addDefaultGrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.method_9564()), Decorator.field_14240, new CountDecoratorConfig(1))
		);
	}

	public static void addTaigaGrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13521, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(1))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.method_9564()), Decorator.field_14234, new CountChanceDecoratorConfig(1, 0.25F)
			)
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.method_9564()), Decorator.field_14261, new CountChanceDecoratorConfig(1, 0.125F)
			)
		);
	}

	public static void addPlainsTallGrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13576, new DoublePlantFeatureConfig(Blocks.field_10214.method_9564()), Decorator.field_14254, new NoiseHeightmapDecoratorConfig(-0.8, 0, 7)
			)
		);
	}

	public static void addDefaultMushrooms(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.method_9564()), Decorator.field_14263, new ChanceDecoratorConfig(4))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.method_9564()), Decorator.field_14263, new ChanceDecoratorConfig(8))
		);
	}

	public static void addDefaultVegetation(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13583, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(10))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13524, FeatureConfig.field_13603, Decorator.field_14263, new ChanceDecoratorConfig(32))
		);
	}

	public static void addBadlandsVegetation(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13583, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(13))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13524, FeatureConfig.field_13603, Decorator.field_14263, new ChanceDecoratorConfig(32))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13554, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(5))
		);
	}

	public static void addJungleVegetation(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13534, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(1))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13559, FeatureConfig.field_13603, Decorator.field_14249, new CountDecoratorConfig(50))
		);
	}

	public static void addDesertVegetation(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13583, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(60))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13524, FeatureConfig.field_13603, Decorator.field_14263, new ChanceDecoratorConfig(32))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13554, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(10))
		);
	}

	public static void addSwampVegetation(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13583, FeatureConfig.field_13603, Decorator.field_14240, new CountDecoratorConfig(20))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13524, FeatureConfig.field_13603, Decorator.field_14263, new ChanceDecoratorConfig(32))
		);
	}

	public static void addDesertFeatures(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.method_8699(Feature.field_13592, FeatureConfig.field_13603, Decorator.field_14259, new ChanceDecoratorConfig(1000))
		);
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Biome.method_8699(Feature.field_13516, FeatureConfig.field_13603, Decorator.field_14246, new ChanceDecoratorConfig(64))
		);
	}

	public static void addFossils(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.UNDERGROUND_DECORATION,
			Biome.method_8699(Feature.field_13516, FeatureConfig.field_13603, Decorator.field_14246, new ChanceDecoratorConfig(64))
		);
	}

	public static void addKelp(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13535,
				FeatureConfig.field_13603,
				Decorator.field_14247,
				new TopSolidHeightmapNoiseBiasedDecoratorConfig(120, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG)
			)
		);
	}

	public static void addSeagrassOnStone(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13518,
				new SimpleBlockFeatureConfig(
					Blocks.field_10376.method_9564(),
					new BlockState[]{Blocks.field_10340.method_9564()},
					new BlockState[]{Blocks.field_10382.method_9564()},
					new BlockState[]{Blocks.field_10382.method_9564()}
				),
				Decorator.field_14229,
				new CarvingMaskDecoratorConfig(GenerationStep.Carver.LIQUID, 0.1F)
			)
		);
	}

	public static void addSeagrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13567, new SeagrassFeatureConfig(80, 0.3), Decorator.field_14231, DecoratorConfig.field_13436)
		);
	}

	public static void addMoreSeagrass(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13567, new SeagrassFeatureConfig(80, 0.8), Decorator.field_14231, DecoratorConfig.field_13436)
		);
	}

	public static void addLessKelp(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(
				Feature.field_13535,
				FeatureConfig.field_13603,
				Decorator.field_14247,
				new TopSolidHeightmapNoiseBiasedDecoratorConfig(80, 80.0, 0.0, Heightmap.Type.OCEAN_FLOOR_WG)
			)
		);
	}

	public static void addSprings(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13513, new SpringFeatureConfig(Fluids.WATER.method_15785()), Decorator.field_14255, new RangeDecoratorConfig(50, 8, 8, 256))
		);
		biome.method_8719(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Biome.method_8699(Feature.field_13513, new SpringFeatureConfig(Fluids.LAVA.method_15785()), Decorator.field_14266, new RangeDecoratorConfig(20, 8, 16, 256))
		);
	}

	public static void addIcebergs(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.method_8699(Feature.field_13544, new IcebergFeatureConfig(Blocks.field_10225.method_9564()), Decorator.field_14243, new ChanceDecoratorConfig(16))
		);
		biome.method_8719(
			GenerationStep.Feature.LOCAL_MODIFICATIONS,
			Biome.method_8699(Feature.field_13544, new IcebergFeatureConfig(Blocks.field_10384.method_9564()), Decorator.field_14243, new ChanceDecoratorConfig(200))
		);
	}

	public static void addBlueIce(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.SURFACE_STRUCTURES,
			Biome.method_8699(Feature.field_13560, FeatureConfig.field_13603, Decorator.field_14260, new RangeDecoratorConfig(20, 30, 32, 64))
		);
	}

	public static void addFrozenTopLayer(Biome biome) {
		biome.method_8719(
			GenerationStep.Feature.TOP_LAYER_MODIFICATION,
			Biome.method_8699(Feature.field_13539, FeatureConfig.field_13603, Decorator.NOPE, DecoratorConfig.field_13436)
		);
	}
}
