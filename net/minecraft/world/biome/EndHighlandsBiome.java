/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.surfacebuilder.SurfaceBuilder;

public class EndHighlandsBiome
extends Biome {
    public EndHighlandsBiome() {
        super(new Biome.Settings().configureSurfaceBuilder(SurfaceBuilder.DEFAULT, SurfaceBuilder.END_CONFIG).precipitation(Biome.Precipitation.NONE).category(Biome.Category.THEEND).depth(0.1f).scale(0.2f).temperature(0.5f).downfall(0.5f).waterColor(4159204).waterFogColor(329011).parent(null));
        this.addStructureFeature(Feature.END_CITY, FeatureConfig.DEFAULT);
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, EndHighlandsBiome.configureFeature(Feature.END_GATEWAY, EndGatewayFeatureConfig.createConfig(TheEndDimension.SPAWN_POINT, true), Decorator.END_GATEWAY, DecoratorConfig.DEFAULT));
        this.addFeature(GenerationStep.Feature.SURFACE_STRUCTURES, EndHighlandsBiome.configureFeature(Feature.END_CITY, FeatureConfig.DEFAULT, Decorator.NOPE, DecoratorConfig.DEFAULT));
        this.addFeature(GenerationStep.Feature.VEGETAL_DECORATION, EndHighlandsBiome.configureFeature(Feature.CHORUS_PLANT, FeatureConfig.DEFAULT, Decorator.CHORUS_PLANT, DecoratorConfig.DEFAULT));
        this.addSpawn(EntityCategory.MONSTER, new Biome.SpawnEntry(EntityType.ENDERMAN, 10, 4, 4));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public int getSkyColor(float f) {
        return 0;
    }
}

