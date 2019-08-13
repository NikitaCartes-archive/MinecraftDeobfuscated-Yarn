package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public final class FrozenOceanBiome extends Biome {
	protected static final OctaveSimplexNoiseSampler field_9487 = new OctaveSimplexNoiseSampler(new Random(3456L), 3);

	public FrozenOceanBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.field_15699, SurfaceBuilder.GRASS_CONFIG)
				.precipitation(Biome.Precipitation.SNOW)
				.category(Biome.Category.field_9367)
				.depth(-1.0F)
				.scale(0.1F)
				.temperature(0.0F)
				.downfall(0.5F)
				.waterColor(3750089)
				.waterFogColor(329011)
				.parent(null)
		);
		this.addStructureFeature(Feature.OCEAN_RUIN, new OceanRuinFeatureConfig(OceanRuinFeature.BiomeType.field_14528, 0.3F, 0.9F));
		this.addStructureFeature(Feature.MINESHAFT, new MineshaftFeatureConfig(0.004, MineshaftFeature.Type.field_13692));
		this.addStructureFeature(Feature.SHIPWRECK, new ShipwreckFeatureConfig(false));
		DefaultBiomeFeatures.addOceanCarvers(this);
		DefaultBiomeFeatures.addDefaultStructures(this);
		DefaultBiomeFeatures.addDefaultLakes(this);
		DefaultBiomeFeatures.addIcebergs(this);
		DefaultBiomeFeatures.addDungeons(this);
		DefaultBiomeFeatures.addBlueIce(this);
		DefaultBiomeFeatures.addMineables(this);
		DefaultBiomeFeatures.addDefaultOres(this);
		DefaultBiomeFeatures.addDefaultDisks(this);
		DefaultBiomeFeatures.addWaterBiomeOakTrees(this);
		DefaultBiomeFeatures.addDefaultFlowers(this);
		DefaultBiomeFeatures.addDefaultGrass(this);
		DefaultBiomeFeatures.addDefaultMushrooms(this);
		DefaultBiomeFeatures.addDefaultVegetation(this);
		DefaultBiomeFeatures.addSprings(this);
		DefaultBiomeFeatures.addFrozenTopLayer(this);
		this.addSpawn(EntityCategory.field_6300, new Biome.SpawnEntry(EntityType.field_6114, 1, 1, 4));
		this.addSpawn(EntityCategory.field_6300, new Biome.SpawnEntry(EntityType.field_6073, 15, 1, 5));
		this.addSpawn(EntityCategory.field_6294, new Biome.SpawnEntry(EntityType.field_6042, 1, 1, 2));
		this.addSpawn(EntityCategory.field_6303, new Biome.SpawnEntry(EntityType.field_6108, 10, 8, 8));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6079, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6051, 95, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6123, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6054, 5, 1, 1));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6137, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6046, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6069, 100, 4, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6091, 10, 1, 4));
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6145, 5, 1, 1));
	}

	@Override
	protected float computeTemperature(BlockPos blockPos) {
		float f = this.getTemperature();
		double d = field_9487.sample((double)blockPos.getX() * 0.05, (double)blockPos.getZ() * 0.05);
		double e = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.2, (double)blockPos.getZ() * 0.2);
		double g = d + e;
		if (g < 0.3) {
			double h = FOLIAGE_NOISE.sample((double)blockPos.getX() * 0.09, (double)blockPos.getZ() * 0.09);
			if (h < 0.8) {
				f = 0.2F;
			}
		}

		if (blockPos.getY() > 64) {
			float i = (float)(TEMPERATURE_NOISE.sample((double)((float)blockPos.getX() / 8.0F), (double)((float)blockPos.getZ() / 8.0F)) * 4.0);
			return f - (i + (float)blockPos.getY() - 64.0F) * 0.05F / 30.0F;
		} else {
			return f;
		}
	}
}
