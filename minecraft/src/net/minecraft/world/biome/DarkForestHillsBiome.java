package net.minecraft.world.biome;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class DarkForestHillsBiome extends Biome {
	public DarkForestHillsBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.GRASS_CONFIG)
				.precipitation(Biome.Precipitation.RAIN)
				.category(Biome.Category.FOREST)
				.depth(0.2F)
				.scale(0.4F)
				.temperature(0.7F)
				.downfall(0.8F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent("dark_forest")
		);
		this.addStructureFeature(Feature.WOODLAND_MANSION.method_23397(FeatureConfig.DEFAULT));
		this.addStructureFeature(Feature.MINESHAFT.method_23397(new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.NORMAL)));
		this.addStructureFeature(Feature.STRONGHOLD.method_23397(FeatureConfig.DEFAULT));
		DefaultBiomeFeatures.addLandCarvers(this);
		DefaultBiomeFeatures.addDefaultStructures(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addDungeons(this);
		this.addFeature(
			GenerationStep.Feature.VEGETAL_DECORATION,
			Feature.RANDOM_SELECTOR
				.method_23397(
					new RandomFeatureConfig(
						ImmutableList.of(
							Feature.HUGE_RED_MUSHROOM.method_23397(DefaultBiomeFeatures.field_21142).method_23387(0.025F),
							Feature.HUGE_BROWN_MUSHROOM.method_23397(DefaultBiomeFeatures.field_21143).method_23387(0.05F),
							Feature.DARK_OAK_TREE.method_23397(DefaultBiomeFeatures.field_21197).method_23387(0.6666667F),
							Feature.FANCY_TREE.method_23397(DefaultBiomeFeatures.field_21190).method_23387(0.1F)
						),
						Feature.NORMAL_TREE.method_23397(DefaultBiomeFeatures.field_21126)
					)
				)
				.method_23388(Decorator.DARK_OAK_TREE.method_23475(DecoratorConfig.DEFAULT))
		);
		DefaultBiomeFeatures.addForestFlowers(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		DefaultBiomeFeatures.addDefaultFlowers(this);
		DefaultBiomeFeatures.addForestGrass(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addDefaultVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.SHEEP, 12, 4, 4));
		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.PIG, 10, 4, 4));
		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.CHICKEN, 10, 4, 4));
		this.addSpawn(EntityCategory.CREATURE, new Biome.SpawnEntry(EntityType.COW, 8, 4, 4));
		this.addSpawn(EntityCategory.AMBIENT, new Biome.SpawnEntry(EntityType.BAT, 10, 8, 8));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SPIDER, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE, 95, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ZOMBIE_VILLAGER, 5, 1, 1));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SKELETON, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.CREEPER, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.SLIME, 100, 4, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 1, 4));
		this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.WITCH, 5, 1, 1));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getGrassColorAt(BlockPos blockPos) {
		int i = super.getGrassColorAt(blockPos);
		return (i & 16711422) + 2634762 >> 1;
	}
}
