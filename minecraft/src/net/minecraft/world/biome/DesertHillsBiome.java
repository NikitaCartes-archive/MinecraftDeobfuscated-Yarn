package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.carver.Carver;
import net.minecraft.world.gen.config.ProbabilityConfig;
import net.minecraft.world.gen.config.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.config.decorator.CountDecoratorConfig;
import net.minecraft.world.gen.config.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.config.decorator.DecoratorConfig;
import net.minecraft.world.gen.config.decorator.DungeonDecoratorConfig;
import net.minecraft.world.gen.config.decorator.LakeDecoratorConfig;
import net.minecraft.world.gen.config.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.config.feature.BlockClusterFeatureConfig;
import net.minecraft.world.gen.config.feature.BushFeatureConfig;
import net.minecraft.world.gen.config.feature.FeatureConfig;
import net.minecraft.world.gen.config.feature.GrassFeatureConfig;
import net.minecraft.world.gen.config.feature.LakeFeatureConfig;
import net.minecraft.world.gen.config.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.config.feature.OreFeatureConfig;
import net.minecraft.world.gen.config.feature.SpringFeatureConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class DesertHillsBiome extends Biome {
	public DesertHillsBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.SAND_CONFIG)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.DESERT)
				.depth(0.45F)
				.scale(0.3F)
				.temperature(2.0F)
				.downfall(0.0F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent(null)
		);
		this.addStructureFeature(Feature.DESERT_PYRAMID, FeatureConfig.DEFAULT);
		this.addStructureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL));
		this.addStructureFeature(Feature.STRONGHOLD, FeatureConfig.DEFAULT);
		this.addCarver(GenerationStep.Carver.field_13169, configureCarver(Carver.CAVE, new ProbabilityConfig(0.14285715F)));
		this.addCarver(GenerationStep.Carver.field_13169, configureCarver(Carver.RAVINE, new ProbabilityConfig(0.02F)));
		this.addDefaultFeatures();
		this.addFeature(
			GenerationStep.Feature.field_13171,
			configureFeature(Feature.field_13573, new LakeFeatureConfig(Blocks.field_10164.getDefaultState()), Decorator.field_14237, new LakeDecoratorConfig(80))
		);
		this.addFeature(
			GenerationStep.Feature.field_13172, configureFeature(Feature.field_13579, FeatureConfig.DEFAULT, Decorator.field_14265, new DungeonDecoratorConfig(8))
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10566.getDefaultState(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 256)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10255.getDefaultState(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(8, 0, 0, 256)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10474.getDefaultState(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10508.getDefaultState(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10115.getDefaultState(), 33),
				Decorator.field_14241,
				new RangeDecoratorConfig(10, 0, 0, 80)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10418.getDefaultState(), 17),
				Decorator.field_14241,
				new RangeDecoratorConfig(20, 0, 0, 128)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10212.getDefaultState(), 9),
				Decorator.field_14241,
				new RangeDecoratorConfig(20, 0, 0, 64)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10571.getDefaultState(), 9),
				Decorator.field_14241,
				new RangeDecoratorConfig(2, 0, 0, 32)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10080.getDefaultState(), 8),
				Decorator.field_14241,
				new RangeDecoratorConfig(8, 0, 0, 16)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10442.getDefaultState(), 8),
				Decorator.field_14241,
				new RangeDecoratorConfig(1, 0, 0, 16)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13517,
				new OreFeatureConfig(OreFeatureConfig.Target.NATURAL_STONE, Blocks.field_10090.getDefaultState(), 7),
				Decorator.field_14252,
				new CountDepthDecoratorConfig(1, 16, 16)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13509,
				new BlockClusterFeatureConfig(
					Blocks.field_10102.getDefaultState(), 7, 2, Lists.<BlockState>newArrayList(Blocks.field_10566.getDefaultState(), Blocks.field_10219.getDefaultState())
				),
				Decorator.field_14245,
				new CountDecoratorConfig(3)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13509,
				new BlockClusterFeatureConfig(
					Blocks.field_10460.getDefaultState(), 4, 1, Lists.<BlockState>newArrayList(Blocks.field_10566.getDefaultState(), Blocks.field_10460.getDefaultState())
				),
				Decorator.field_14245,
				new CountDecoratorConfig(1)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13176,
			configureFeature(
				Feature.field_13509,
				new BlockClusterFeatureConfig(
					Blocks.field_10255.getDefaultState(), 6, 2, Lists.<BlockState>newArrayList(Blocks.field_10566.getDefaultState(), Blocks.field_10219.getDefaultState())
				),
				Decorator.field_14245,
				new CountDecoratorConfig(1)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13178, configureFeature(Feature.DEFAULT_FLOWER, FeatureConfig.DEFAULT, Decorator.field_14253, new CountDecoratorConfig(2))
		);
		this.addFeature(
			GenerationStep.Feature.field_13178,
			configureFeature(Feature.field_13511, new GrassFeatureConfig(Blocks.field_10479.getDefaultState()), Decorator.field_14240, new CountDecoratorConfig(1))
		);
		this.addFeature(
			GenerationStep.Feature.field_13178, configureFeature(Feature.field_13548, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(2))
		);
		this.addFeature(
			GenerationStep.Feature.field_13178,
			configureFeature(Feature.field_13519, new BushFeatureConfig(Blocks.field_10251.getDefaultState()), Decorator.field_14263, new ChanceDecoratorConfig(4))
		);
		this.addFeature(
			GenerationStep.Feature.field_13178,
			configureFeature(Feature.field_13519, new BushFeatureConfig(Blocks.field_10559.getDefaultState()), Decorator.field_14263, new ChanceDecoratorConfig(8))
		);
		this.addFeature(
			GenerationStep.Feature.field_13178, configureFeature(Feature.field_13583, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(60))
		);
		this.addFeature(
			GenerationStep.Feature.field_13178, configureFeature(Feature.field_13524, FeatureConfig.DEFAULT, Decorator.field_14263, new ChanceDecoratorConfig(32))
		);
		this.addFeature(
			GenerationStep.Feature.field_13178, configureFeature(Feature.field_13554, FeatureConfig.DEFAULT, Decorator.field_14240, new CountDecoratorConfig(10))
		);
		this.addFeature(
			GenerationStep.Feature.field_13178,
			configureFeature(
				Feature.field_13513, new SpringFeatureConfig(Fluids.WATER.getDefaultState()), Decorator.field_14255, new RangeDecoratorConfig(50, 8, 8, 256)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13178,
			configureFeature(
				Feature.field_13513, new SpringFeatureConfig(Fluids.LAVA.getDefaultState()), Decorator.field_14266, new RangeDecoratorConfig(20, 8, 16, 256)
			)
		);
		this.addFeature(
			GenerationStep.Feature.field_13173, configureFeature(Feature.field_13592, FeatureConfig.DEFAULT, Decorator.field_14259, new ChanceDecoratorConfig(1000))
		);
		this.addFeature(
			GenerationStep.Feature.field_13177, configureFeature(Feature.field_13516, FeatureConfig.DEFAULT, Decorator.field_14246, new ChanceDecoratorConfig(64))
		);
		this.addFeature(GenerationStep.Feature.field_13179, configureFeature(Feature.field_13539, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
		this.addSpawn(EntityCategory.field_6303, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ZOMBIE, 19, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 1, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.HUSK, 80, 4, 4));
	}
}
