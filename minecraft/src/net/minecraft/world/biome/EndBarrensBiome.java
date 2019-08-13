package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class EndBarrensBiome extends Biome {
	public EndBarrensBiome() {
		super(
			new Biome.Settings()
				.configureSurfaceBuilder(SurfaceBuilder.field_15701, SurfaceBuilder.END_CONFIG)
				.precipitation(Biome.Precipitation.NONE)
				.category(Biome.Category.THEEND)
				.depth(0.1F)
				.scale(0.2F)
				.temperature(0.5F)
				.downfall(0.5F)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent(null)
		);
		DefaultBiomeFeatures.method_20826(this);
		this.addSpawn(EntityCategory.field_6302, new Biome.SpawnEntry(EntityType.field_6091, 10, 4, 4));
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getSkyColor(float f) {
		return 0;
	}
}
