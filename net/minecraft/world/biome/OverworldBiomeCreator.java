/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.biome;

import net.minecraft.client.sound.MusicType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.sound.BiomeMoodSound;
import net.minecraft.sound.MusicSound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeEffects;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.MiscPlacedFeatures;
import net.minecraft.world.gen.feature.OceanPlacedFeatures;
import net.minecraft.world.gen.feature.VegetationPlacedFeatures;
import org.jetbrains.annotations.Nullable;

public class OverworldBiomeCreator {
    protected static final int field_35340 = 4159204;
    protected static final int field_35341 = 329011;
    private static final int field_35342 = 12638463;
    @Nullable
    private static final MusicSound field_35436 = null;

    protected static int getSkyColor(float temperature) {
        float f = temperature;
        f /= 3.0f;
        f = MathHelper.clamp(f, -1.0f, 1.0f);
        return MathHelper.hsvToRgb(0.62222224f - f * 0.05f, 0.5f + f * 0.1f, 1.0f);
    }

    private static Biome method_39152(Biome.Precipitation precipitation, Biome.Category category, float f, float g, SpawnSettings.Builder builder, GenerationSettings.Builder builder2, @Nullable MusicSound musicSound) {
        return OverworldBiomeCreator.method_39151(precipitation, category, f, g, 4159204, 329011, builder, builder2, musicSound);
    }

    private static Biome method_39151(Biome.Precipitation precipitation, Biome.Category category, float f, float g, int i, int j, SpawnSettings.Builder builder, GenerationSettings.Builder builder2, @Nullable MusicSound musicSound) {
        return new Biome.Builder().precipitation(precipitation).category(category).temperature(f).downfall(g).effects(new BiomeEffects.Builder().waterColor(i).waterFogColor(j).fogColor(12638463).skyColor(OverworldBiomeCreator.getSkyColor(f)).moodSound(BiomeMoodSound.CAVE).music(musicSound).build()).spawnSettings(builder.build()).generationSettings(builder2.build()).build();
    }

    private static void method_39153(GenerationSettings.Builder builder) {
        DefaultBiomeFeatures.addLandCarvers(builder);
        DefaultBiomeFeatures.addAmethystGeodes(builder);
        DefaultBiomeFeatures.addDungeons(builder);
        DefaultBiomeFeatures.addMineables(builder);
        DefaultBiomeFeatures.addSprings(builder);
        DefaultBiomeFeatures.addFrozenTopLayer(builder);
    }

    public static Biome createOldGrowthPineTaiga(boolean bl) {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(builder);
        builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4));
        builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
        builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 8, 2, 4));
        if (bl) {
            DefaultBiomeFeatures.addBatsAndMonsters(builder);
        } else {
            DefaultBiomeFeatures.addCaveMobs(builder);
            DefaultBiomeFeatures.addMonsters(builder, 100, 25, 100, false);
        }
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addMossyRocks(builder2);
        DefaultBiomeFeatures.addLargeFerns(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, bl ? VegetationPlacedFeatures.TREES_OLD_GROWTH_SPRUCE_TAIGA : VegetationPlacedFeatures.TREES_OLD_GROWTH_PINE_TAIGA);
        DefaultBiomeFeatures.addDefaultFlowers(builder2);
        DefaultBiomeFeatures.addGiantTaigaGrass(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        DefaultBiomeFeatures.addSweetBerryBushes(builder2);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.RAIN, Biome.Category.TAIGA, bl ? 0.25f : 0.3f, 0.8f, builder, builder2, field_35436);
    }

    public static Biome createSparseJungle() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addJungleMobs(builder);
        return OverworldBiomeCreator.createJungleFeatures(0.8f, false, true, false, builder);
    }

    public static Biome createJungle() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addJungleMobs(builder);
        builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PARROT, 40, 1, 2)).spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.OCELOT, 2, 1, 3)).spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PANDA, 1, 1, 2));
        return OverworldBiomeCreator.createJungleFeatures(0.9f, false, false, true, builder);
    }

    public static Biome createNormalBambooJungle() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addJungleMobs(builder);
        builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PARROT, 40, 1, 2)).spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.PANDA, 80, 1, 2)).spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.OCELOT, 2, 1, 1));
        return OverworldBiomeCreator.createJungleFeatures(0.9f, true, false, true, builder);
    }

    private static Biome createJungleFeatures(float depth, boolean bamboo, boolean sparse, boolean unmodified, SpawnSettings.Builder builder) {
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        if (bamboo) {
            DefaultBiomeFeatures.addBambooJungleTrees(builder2);
        } else {
            if (unmodified) {
                DefaultBiomeFeatures.addBamboo(builder2);
            }
            if (sparse) {
                DefaultBiomeFeatures.addSparseJungleTrees(builder2);
            } else {
                DefaultBiomeFeatures.addJungleTrees(builder2);
            }
        }
        DefaultBiomeFeatures.addExtraDefaultFlowers(builder2);
        DefaultBiomeFeatures.addJungleGrass(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        DefaultBiomeFeatures.addJungleVegetation(builder2);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.RAIN, Biome.Category.JUNGLE, 0.95f, depth, builder, builder2, field_35436);
    }

    public static Biome createWindsweptHills(boolean bl) {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(builder);
        builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.LLAMA, 5, 4, 6));
        DefaultBiomeFeatures.addBatsAndMonsters(builder);
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        if (bl) {
            DefaultBiomeFeatures.addWindsweptForestTrees(builder2);
        } else {
            DefaultBiomeFeatures.addWindsweptHillsTrees(builder2);
        }
        DefaultBiomeFeatures.addDefaultFlowers(builder2);
        DefaultBiomeFeatures.addDefaultGrass(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        DefaultBiomeFeatures.addEmeraldOre(builder2);
        DefaultBiomeFeatures.addInfestedStone(builder2);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.RAIN, Biome.Category.EXTREME_HILLS, 0.2f, 0.3f, builder, builder2, field_35436);
    }

    public static Biome createDesert() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addDesertMobs(builder);
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        DefaultBiomeFeatures.addFossils(builder2);
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        DefaultBiomeFeatures.addDefaultFlowers(builder2);
        DefaultBiomeFeatures.addDefaultGrass(builder2);
        DefaultBiomeFeatures.addDesertDeadBushes(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addDesertVegetation(builder2);
        DefaultBiomeFeatures.addDesertFeatures(builder2);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.NONE, Biome.Category.DESERT, 2.0f, 0.0f, builder, builder2, field_35436);
    }

    public static Biome createPlains(boolean bl, boolean bl2, boolean bl3) {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        if (bl2) {
            builder.creatureSpawnProbability(0.07f);
            DefaultBiomeFeatures.addSnowyMobs(builder);
            if (bl3) {
                builder2.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.ICE_SPIKE);
                builder2.feature(GenerationStep.Feature.SURFACE_STRUCTURES, MiscPlacedFeatures.ICE_PATCH);
            }
        } else {
            DefaultBiomeFeatures.addPlainsMobs(builder);
            DefaultBiomeFeatures.addPlainsTallGrass(builder2);
            if (bl) {
                builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_SUNFLOWER);
            }
        }
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        if (bl2) {
            DefaultBiomeFeatures.addSnowySpruceTrees(builder2);
            DefaultBiomeFeatures.addDefaultFlowers(builder2);
            DefaultBiomeFeatures.addDefaultGrass(builder2);
        } else {
            DefaultBiomeFeatures.addPlainsFeatures(builder2);
        }
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        if (bl) {
            builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_SUGAR_CANE);
            builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.PATCH_PUMPKIN);
        } else {
            DefaultBiomeFeatures.addDefaultVegetation(builder2);
        }
        float f = bl2 ? 0.0f : 0.8f;
        return OverworldBiomeCreator.method_39152(bl2 ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, bl2 ? Biome.Category.ICY : Biome.Category.PLAINS, f, bl2 ? 0.5f : 0.4f, builder, builder2, field_35436);
    }

    public static Biome createMushroomFields() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addMushroomMobs(builder);
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        DefaultBiomeFeatures.addMushroomFieldsFeatures(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.RAIN, Biome.Category.MUSHROOM, 0.9f, 1.0f, builder, builder2, field_35436);
    }

    public static Biome createSavanna(boolean bl, boolean windswept) {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder);
        if (!bl) {
            DefaultBiomeFeatures.addSavannaTallGrass(builder);
        }
        DefaultBiomeFeatures.addDefaultOres(builder);
        DefaultBiomeFeatures.addDefaultDisks(builder);
        if (bl) {
            DefaultBiomeFeatures.addExtraSavannaTrees(builder);
            DefaultBiomeFeatures.addDefaultFlowers(builder);
            DefaultBiomeFeatures.addWindsweptSavannaGrass(builder);
        } else {
            DefaultBiomeFeatures.addSavannaTrees(builder);
            DefaultBiomeFeatures.addExtraDefaultFlowers(builder);
            DefaultBiomeFeatures.addSavannaGrass(builder);
        }
        DefaultBiomeFeatures.addDefaultMushrooms(builder);
        DefaultBiomeFeatures.addDefaultVegetation(builder);
        SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(builder2);
        builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.HORSE, 1, 2, 6)).spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.DONKEY, 1, 1, 1));
        DefaultBiomeFeatures.addBatsAndMonsters(builder2);
        if (windswept) {
            builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.LLAMA, 8, 4, 4));
        }
        float f = bl ? 1.1f : (windswept ? 1.0f : 1.2f);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.NONE, Biome.Category.SAVANNA, f, 0.0f, builder2, builder, field_35436);
    }

    public static Biome createNormalBadlands(boolean bl) {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addBatsAndMonsters(builder);
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addExtraGoldOre(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        if (bl) {
            DefaultBiomeFeatures.addBadlandsPlateauTrees(builder2);
        }
        DefaultBiomeFeatures.addBadlandsGrass(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addBadlandsVegetation(builder2);
        return new Biome.Builder().precipitation(Biome.Precipitation.NONE).category(Biome.Category.MESA).temperature(2.0f).downfall(0.0f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(OverworldBiomeCreator.getSkyColor(2.0f)).foliageColor(10387789).grassColor(9470285).moodSound(BiomeMoodSound.CAVE).build()).spawnSettings(builder.build()).generationSettings(builder2.build()).build();
    }

    private static Biome createOcean(SpawnSettings.Builder spawnSettings, int waterColor, int waterFogColor, GenerationSettings.Builder builder) {
        return OverworldBiomeCreator.method_39151(Biome.Precipitation.RAIN, Biome.Category.OCEAN, 0.5f, 0.5f, waterColor, waterFogColor, spawnSettings, builder, field_35436);
    }

    private static GenerationSettings.Builder createOceanGenerationSettings() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder);
        DefaultBiomeFeatures.addDefaultOres(builder);
        DefaultBiomeFeatures.addDefaultDisks(builder);
        DefaultBiomeFeatures.addWaterBiomeOakTrees(builder);
        DefaultBiomeFeatures.addDefaultFlowers(builder);
        DefaultBiomeFeatures.addDefaultGrass(builder);
        DefaultBiomeFeatures.addDefaultMushrooms(builder);
        DefaultBiomeFeatures.addDefaultVegetation(builder);
        return builder;
    }

    public static Biome createColdOcean(boolean deep) {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addOceanMobs(builder, 3, 4, 15);
        builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 15, 1, 5));
        GenerationSettings.Builder builder2 = OverworldBiomeCreator.createOceanGenerationSettings();
        builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? OceanPlacedFeatures.SEAGRASS_DEEP_COLD : OceanPlacedFeatures.SEAGRASS_COLD);
        DefaultBiomeFeatures.addSeagrassOnStone(builder2);
        DefaultBiomeFeatures.addKelp(builder2);
        return OverworldBiomeCreator.createOcean(builder, 4020182, 329011, builder2);
    }

    public static Biome createNormalOcean(boolean deep) {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addOceanMobs(builder, 1, 4, 10);
        builder.spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.DOLPHIN, 1, 1, 2));
        GenerationSettings.Builder builder2 = OverworldBiomeCreator.createOceanGenerationSettings();
        builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? OceanPlacedFeatures.SEAGRASS_DEEP : OceanPlacedFeatures.SEAGRASS_NORMAL);
        DefaultBiomeFeatures.addSeagrassOnStone(builder2);
        DefaultBiomeFeatures.addKelp(builder2);
        return OverworldBiomeCreator.createOcean(builder, 4159204, 329011, builder2);
    }

    public static Biome createLukewarmOcean(boolean deep) {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        if (deep) {
            DefaultBiomeFeatures.addOceanMobs(builder, 8, 4, 8);
        } else {
            DefaultBiomeFeatures.addOceanMobs(builder, 10, 2, 15);
        }
        builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.PUFFERFISH, 5, 1, 3)).spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8)).spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.DOLPHIN, 2, 1, 2));
        GenerationSettings.Builder builder2 = OverworldBiomeCreator.createOceanGenerationSettings();
        builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, deep ? OceanPlacedFeatures.SEAGRASS_DEEP_WARM : OceanPlacedFeatures.SEAGRASS_WARM);
        if (deep) {
            DefaultBiomeFeatures.addSeagrassOnStone(builder2);
        }
        DefaultBiomeFeatures.addLessKelp(builder2);
        return OverworldBiomeCreator.createOcean(builder, 4566514, 267827, builder2);
    }

    public static Biome createWarmOcean() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder().spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.PUFFERFISH, 15, 1, 3));
        DefaultBiomeFeatures.addWarmOceanMobs(builder, 10, 4);
        GenerationSettings.Builder builder2 = OverworldBiomeCreator.createOceanGenerationSettings().feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.WARM_OCEAN_VEGETATION).feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_WARM).feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEA_PICKLE);
        return OverworldBiomeCreator.createOcean(builder, 4445678, 270131, builder2);
    }

    public static Biome createFrozenOcean(boolean monument) {
        SpawnSettings.Builder builder = new SpawnSettings.Builder().spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, 1, 1, 4)).spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 15, 1, 5)).spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.POLAR_BEAR, 1, 1, 2));
        DefaultBiomeFeatures.addBatsAndMonsters(builder);
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, 5, 1, 1));
        float f = monument ? 0.5f : 0.0f;
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        DefaultBiomeFeatures.addIcebergs(builder2);
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addBlueIce(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        DefaultBiomeFeatures.addWaterBiomeOakTrees(builder2);
        DefaultBiomeFeatures.addDefaultFlowers(builder2);
        DefaultBiomeFeatures.addDefaultGrass(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        return new Biome.Builder().precipitation(monument ? Biome.Precipitation.RAIN : Biome.Precipitation.SNOW).category(Biome.Category.OCEAN).temperature(f).temperatureModifier(Biome.TemperatureModifier.FROZEN).downfall(0.5f).effects(new BiomeEffects.Builder().waterColor(3750089).waterFogColor(329011).fogColor(12638463).skyColor(OverworldBiomeCreator.getSkyColor(f)).moodSound(BiomeMoodSound.CAVE).build()).spawnSettings(builder.build()).generationSettings(builder2.build()).build();
    }

    public static Biome createNormalForest(boolean bl, boolean bl2, boolean bl3) {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder);
        if (bl3) {
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_FOREST_FLOWERS);
        } else {
            DefaultBiomeFeatures.addForestFlowers(builder);
        }
        DefaultBiomeFeatures.addDefaultOres(builder);
        DefaultBiomeFeatures.addDefaultDisks(builder);
        if (bl3) {
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.TREES_FLOWER_FOREST);
            builder.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.FLOWER_FLOWER_FOREST);
            DefaultBiomeFeatures.addDefaultGrass(builder);
        } else {
            if (bl) {
                if (bl2) {
                    DefaultBiomeFeatures.addTallBirchTrees(builder);
                } else {
                    DefaultBiomeFeatures.addBirchTrees(builder);
                }
            } else {
                DefaultBiomeFeatures.addForestTrees(builder);
            }
            DefaultBiomeFeatures.addDefaultFlowers(builder);
            DefaultBiomeFeatures.addForestGrass(builder);
        }
        DefaultBiomeFeatures.addDefaultMushrooms(builder);
        DefaultBiomeFeatures.addDefaultVegetation(builder);
        SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(builder2);
        DefaultBiomeFeatures.addBatsAndMonsters(builder2);
        if (bl3) {
            builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3));
        } else if (!bl) {
            builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 5, 4, 4));
        }
        float f = bl ? 0.6f : 0.7f;
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.RAIN, Biome.Category.FOREST, f, bl ? 0.6f : 0.8f, builder2, builder, field_35436);
    }

    public static Biome createTaiga(boolean cold) {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(builder);
        builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4)).spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3)).spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 8, 2, 4));
        DefaultBiomeFeatures.addBatsAndMonsters(builder);
        float f = cold ? -0.5f : 0.25f;
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addLargeFerns(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        DefaultBiomeFeatures.addTaigaTrees(builder2);
        DefaultBiomeFeatures.addDefaultFlowers(builder2);
        DefaultBiomeFeatures.addTaigaGrass(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        if (cold) {
            DefaultBiomeFeatures.addSweetBerryBushesSnowy(builder2);
        } else {
            DefaultBiomeFeatures.addSweetBerryBushes(builder2);
        }
        return OverworldBiomeCreator.method_39151(cold ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, Biome.Category.TAIGA, f, cold ? 0.4f : 0.8f, cold ? 4020182 : 4159204, 329011, builder, builder2, field_35436);
    }

    public static Biome createDarkForest() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(builder);
        DefaultBiomeFeatures.addBatsAndMonsters(builder);
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, VegetationPlacedFeatures.DARK_FOREST_VEGETATION);
        DefaultBiomeFeatures.addForestFlowers(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        DefaultBiomeFeatures.addDefaultFlowers(builder2);
        DefaultBiomeFeatures.addForestGrass(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        return new Biome.Builder().precipitation(Biome.Precipitation.RAIN).category(Biome.Category.FOREST).temperature(0.7f).downfall(0.8f).effects(new BiomeEffects.Builder().waterColor(4159204).waterFogColor(329011).fogColor(12638463).skyColor(OverworldBiomeCreator.getSkyColor(0.7f)).grassColorModifier(BiomeEffects.GrassColorModifier.DARK_FOREST).moodSound(BiomeMoodSound.CAVE).build()).spawnSettings(builder.build()).generationSettings(builder2.build()).build();
    }

    public static Biome createSwamp() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(builder);
        DefaultBiomeFeatures.addBatsAndMonsters(builder);
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.SLIME, 1, 1, 1));
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        DefaultBiomeFeatures.addFossils(builder2);
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addClayDisk(builder2);
        DefaultBiomeFeatures.addSwampFeatures(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addSwampVegetation(builder2);
        builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_SWAMP);
        return new Biome.Builder().precipitation(Biome.Precipitation.RAIN).category(Biome.Category.SWAMP).temperature(0.8f).downfall(0.9f).effects(new BiomeEffects.Builder().waterColor(6388580).waterFogColor(2302743).fogColor(12638463).skyColor(OverworldBiomeCreator.getSkyColor(0.8f)).foliageColor(6975545).grassColorModifier(BiomeEffects.GrassColorModifier.SWAMP).moodSound(BiomeMoodSound.CAVE).build()).spawnSettings(builder.build()).generationSettings(builder2.build()).build();
    }

    public static Biome createRiver(boolean bl) {
        SpawnSettings.Builder builder = new SpawnSettings.Builder().spawn(SpawnGroup.WATER_CREATURE, new SpawnSettings.SpawnEntry(EntityType.SQUID, 2, 1, 4)).spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.SALMON, 5, 1, 5));
        DefaultBiomeFeatures.addBatsAndMonsters(builder);
        builder.spawn(SpawnGroup.MONSTER, new SpawnSettings.SpawnEntry(EntityType.DROWNED, bl ? 1 : 100, 1, 1));
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        DefaultBiomeFeatures.addWaterBiomeOakTrees(builder2);
        DefaultBiomeFeatures.addDefaultFlowers(builder2);
        DefaultBiomeFeatures.addDefaultGrass(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        if (!bl) {
            builder2.feature(GenerationStep.Feature.VEGETAL_DECORATION, OceanPlacedFeatures.SEAGRASS_RIVER);
        }
        float f = bl ? 0.0f : 0.5f;
        return OverworldBiomeCreator.method_39151(bl ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, Biome.Category.RIVER, f, 0.5f, bl ? 3750089 : 4159204, 329011, builder, builder2, field_35436);
    }

    public static Biome createBeach(boolean bl, boolean bl2) {
        boolean bl3;
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        boolean bl4 = bl3 = !bl2 && !bl;
        if (bl3) {
            builder.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.TURTLE, 5, 2, 5));
        }
        DefaultBiomeFeatures.addBatsAndMonsters(builder);
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        DefaultBiomeFeatures.addDefaultFlowers(builder2);
        DefaultBiomeFeatures.addDefaultGrass(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        float f = bl ? 0.05f : (bl2 ? 0.2f : 0.8f);
        return OverworldBiomeCreator.method_39151(bl ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN, Biome.Category.BEACH, f, bl3 ? 0.4f : 0.3f, bl ? 4020182 : 4159204, 329011, builder, builder2, field_35436);
    }

    public static Biome createTheVoid() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        builder.feature(GenerationStep.Feature.TOP_LAYER_MODIFICATION, MiscPlacedFeatures.VOID_START_PLATFORM);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.NONE, Biome.Category.NONE, 0.5f, 0.5f, new SpawnSettings.Builder(), builder, field_35436);
    }

    public static Biome createMeadow() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
        builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.DONKEY, 1, 1, 2)).spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 2, 2, 6)).spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.SHEEP, 2, 2, 4));
        DefaultBiomeFeatures.addBatsAndMonsters(builder2);
        OverworldBiomeCreator.method_39153(builder);
        DefaultBiomeFeatures.addPlainsTallGrass(builder);
        DefaultBiomeFeatures.addDefaultOres(builder);
        DefaultBiomeFeatures.addDefaultDisks(builder);
        DefaultBiomeFeatures.addMeadowFlowers(builder);
        DefaultBiomeFeatures.addEmeraldOre(builder);
        DefaultBiomeFeatures.addInfestedStone(builder);
        MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_MEADOW);
        return OverworldBiomeCreator.method_39151(Biome.Precipitation.RAIN, Biome.Category.MOUNTAIN, 0.5f, 0.8f, 937679, 329011, builder2, builder, musicSound);
    }

    public static Biome createFrozenPeaks() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
        builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.GOAT, 5, 1, 3));
        DefaultBiomeFeatures.addBatsAndMonsters(builder2);
        OverworldBiomeCreator.method_39153(builder);
        DefaultBiomeFeatures.method_39420(builder);
        DefaultBiomeFeatures.addDefaultOres(builder);
        DefaultBiomeFeatures.addDefaultDisks(builder);
        DefaultBiomeFeatures.addEmeraldOre(builder);
        DefaultBiomeFeatures.addInfestedStone(builder);
        MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_FROZEN_PEAKS);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.SNOW, Biome.Category.MOUNTAIN, -0.7f, 0.9f, builder2, builder, musicSound);
    }

    public static Biome createJaggedPeaks() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
        builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.GOAT, 5, 1, 3));
        DefaultBiomeFeatures.addBatsAndMonsters(builder2);
        OverworldBiomeCreator.method_39153(builder);
        DefaultBiomeFeatures.method_39420(builder);
        DefaultBiomeFeatures.addDefaultOres(builder);
        DefaultBiomeFeatures.addDefaultDisks(builder);
        DefaultBiomeFeatures.addEmeraldOre(builder);
        DefaultBiomeFeatures.addInfestedStone(builder);
        MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_JAGGED_PEAKS);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.SNOW, Biome.Category.MOUNTAIN, -0.7f, 0.9f, builder2, builder, musicSound);
    }

    public static Biome createStonyPeaks() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addBatsAndMonsters(builder2);
        OverworldBiomeCreator.method_39153(builder);
        DefaultBiomeFeatures.addDefaultOres(builder);
        DefaultBiomeFeatures.addDefaultDisks(builder);
        DefaultBiomeFeatures.addEmeraldOre(builder);
        DefaultBiomeFeatures.addInfestedStone(builder);
        MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_STONY_PEAKS);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.RAIN, Biome.Category.MOUNTAIN, 1.0f, 0.3f, builder2, builder, musicSound);
    }

    public static Biome createSnowySlopes() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
        builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3)).spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.GOAT, 5, 1, 3));
        DefaultBiomeFeatures.addBatsAndMonsters(builder2);
        OverworldBiomeCreator.method_39153(builder);
        DefaultBiomeFeatures.method_39420(builder);
        DefaultBiomeFeatures.addDefaultOres(builder);
        DefaultBiomeFeatures.addDefaultDisks(builder);
        DefaultBiomeFeatures.addDefaultVegetation(builder);
        DefaultBiomeFeatures.addEmeraldOre(builder);
        DefaultBiomeFeatures.addInfestedStone(builder);
        MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_SNOWY_SLOPES);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.SNOW, Biome.Category.MOUNTAIN, -0.3f, 0.9f, builder2, builder, musicSound);
    }

    public static Biome createGrove() {
        GenerationSettings.Builder builder = new GenerationSettings.Builder();
        SpawnSettings.Builder builder2 = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addFarmAnimals(builder2);
        builder2.spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.WOLF, 8, 4, 4)).spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.RABBIT, 4, 2, 3)).spawn(SpawnGroup.CREATURE, new SpawnSettings.SpawnEntry(EntityType.FOX, 8, 2, 4));
        DefaultBiomeFeatures.addBatsAndMonsters(builder2);
        OverworldBiomeCreator.method_39153(builder);
        DefaultBiomeFeatures.method_39420(builder);
        DefaultBiomeFeatures.addDefaultOres(builder);
        DefaultBiomeFeatures.addDefaultDisks(builder);
        DefaultBiomeFeatures.addGroveTrees(builder);
        DefaultBiomeFeatures.addDefaultVegetation(builder);
        DefaultBiomeFeatures.addEmeraldOre(builder);
        DefaultBiomeFeatures.addInfestedStone(builder);
        MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_GROVE);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.SNOW, Biome.Category.FOREST, -0.2f, 0.8f, builder2, builder, musicSound);
    }

    public static Biome createLushCaves() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        builder.spawn(SpawnGroup.AXOLOTLS, new SpawnSettings.SpawnEntry(EntityType.AXOLOTL, 10, 4, 6));
        builder.spawn(SpawnGroup.WATER_AMBIENT, new SpawnSettings.SpawnEntry(EntityType.TROPICAL_FISH, 25, 8, 8));
        DefaultBiomeFeatures.addBatsAndMonsters(builder);
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addPlainsTallGrass(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2);
        DefaultBiomeFeatures.addClayOre(builder2);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        DefaultBiomeFeatures.addLushCavesDecoration(builder2);
        MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_LUSH_CAVES);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.RAIN, Biome.Category.UNDERGROUND, 0.5f, 0.5f, builder, builder2, musicSound);
    }

    public static Biome createDripstoneCaves() {
        SpawnSettings.Builder builder = new SpawnSettings.Builder();
        DefaultBiomeFeatures.addDripstoneCaveMobs(builder);
        GenerationSettings.Builder builder2 = new GenerationSettings.Builder();
        OverworldBiomeCreator.method_39153(builder2);
        DefaultBiomeFeatures.addPlainsTallGrass(builder2);
        DefaultBiomeFeatures.addDefaultOres(builder2, true);
        DefaultBiomeFeatures.addDefaultDisks(builder2);
        DefaultBiomeFeatures.addPlainsFeatures(builder2);
        DefaultBiomeFeatures.addDefaultMushrooms(builder2);
        DefaultBiomeFeatures.addDefaultVegetation(builder2);
        DefaultBiomeFeatures.addDripstone(builder2);
        MusicSound musicSound = MusicType.createIngameMusic(SoundEvents.MUSIC_OVERWORLD_DRIPSTONE_CAVES);
        return OverworldBiomeCreator.method_39152(Biome.Precipitation.RAIN, Biome.Category.UNDERGROUND, 0.8f, 0.4f, builder, builder2, musicSound);
    }
}

