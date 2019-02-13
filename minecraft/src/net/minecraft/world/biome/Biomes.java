package net.minecraft.world.biome;

import java.util.Collections;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public abstract class Biomes {
	public static final Biome field_9423 = register(0, "ocean", new OceanBiome());
	public static final Biome DEFAULT = field_9423;
	public static final Biome field_9451 = register(1, "plains", new PlainsBiome());
	public static final Biome field_9424 = register(2, "desert", new DesertBiome());
	public static final Biome field_9472 = register(3, "mountains", new MountainsBiome());
	public static final Biome field_9409 = register(4, "forest", new ForestBiome());
	public static final Biome field_9420 = register(5, "taiga", new TaigaBiome());
	public static final Biome field_9471 = register(6, "swamp", new SwampBiome());
	public static final Biome field_9438 = register(7, "river", new RiverBiome());
	public static final Biome field_9461 = register(8, "nether", new NetherBiome());
	public static final Biome field_9411 = register(9, "the_end", new EndBiome());
	public static final Biome field_9435 = register(10, "frozen_ocean", new FrozenOceanBiome());
	public static final Biome field_9463 = register(11, "frozen_river", new FrozenRiverBiome());
	public static final Biome field_9452 = register(12, "snowy_tundra", new SnowyTundraBiome());
	public static final Biome field_9444 = register(13, "snowy_mountains", new SnowyMountainsBiome());
	public static final Biome field_9462 = register(14, "mushroom_fields", new MushroomFieldsBiome());
	public static final Biome field_9407 = register(15, "mushroom_field_shore", new MushroomFieldShoreBiome());
	public static final Biome field_9434 = register(16, "beach", new BeachBiome());
	public static final Biome field_9466 = register(17, "desert_hills", new DesertHillsBiome());
	public static final Biome field_9459 = register(18, "wooded_hills", new WoodedHillsBiome());
	public static final Biome field_9428 = register(19, "taiga_hills", new TaigaHillsBiome());
	public static final Biome field_9464 = register(20, "mountain_edge", new MountainEdgeBiome());
	public static final Biome field_9417 = register(21, "jungle", new JungleBiome());
	public static final Biome field_9432 = register(22, "jungle_hills", new JungleHillsBiome());
	public static final Biome field_9474 = register(23, "jungle_edge", new JungleEdgeBiome());
	public static final Biome field_9446 = register(24, "deep_ocean", new DeepOceanBiome());
	public static final Biome field_9419 = register(25, "stone_shore", new StoneShoreBiome());
	public static final Biome field_9478 = register(26, "snowy_beach", new SnowyBeachBiome());
	public static final Biome field_9412 = register(27, "birch_forest", new BirchForestBiome());
	public static final Biome field_9421 = register(28, "birch_forest_hills", new BirchForestHillsBiome());
	public static final Biome field_9475 = register(29, "dark_forest", new DarkForestBiome());
	public static final Biome field_9454 = register(30, "snowy_taiga", new SnowyTaigaBiome());
	public static final Biome field_9425 = register(31, "snowy_taiga_hills", new SnowyTaigaHillsBiome());
	public static final Biome field_9477 = register(32, "giant_tree_taiga", new GiantTreeTaigaBiome());
	public static final Biome field_9429 = register(33, "giant_tree_taiga_hills", new GiantTreeTaigaHillsBiome());
	public static final Biome field_9460 = register(34, "wooded_mountains", new WoodedMountainsBiome());
	public static final Biome field_9449 = register(35, "savanna", new SavannaBiome());
	public static final Biome field_9430 = register(36, "savanna_plateau", new SavannaPlateauBiome());
	public static final Biome field_9415 = register(37, "badlands", new BadlandsBiome());
	public static final Biome field_9410 = register(38, "wooded_badlands_plateau", new WoodedBadlandsPlateauBiome());
	public static final Biome field_9433 = register(39, "badlands_plateau", new BadlandsPlateauBiome());
	public static final Biome field_9457 = register(40, "small_end_islands", new EndIslandsSmallBiome());
	public static final Biome field_9447 = register(41, "end_midlands", new EndMidlandsBiome());
	public static final Biome field_9442 = register(42, "end_highlands", new EndHighlandsBiome());
	public static final Biome field_9465 = register(43, "end_barrens", new EndBarrensBiome());
	public static final Biome field_9408 = register(44, "warm_ocean", new OceanWarmBiome());
	public static final Biome field_9441 = register(45, "lukewarm_ocean", new OceanLukewarmBiome());
	public static final Biome field_9467 = register(46, "cold_ocean", new OceanColdBiome());
	public static final Biome field_9448 = register(47, "deep_warm_ocean", new OceanDeepWarmBiome());
	public static final Biome field_9439 = register(48, "deep_lukewarm_ocean", new OceanDeepLukewarmBiome());
	public static final Biome field_9470 = register(49, "deep_cold_ocean", new OceanDeepColdBiome());
	public static final Biome field_9418 = register(50, "deep_frozen_ocean", new OceanDeepFrozenBiome());
	public static final Biome field_9473 = register(127, "the_void", new VoidBiome());
	public static final Biome field_9455 = register(129, "sunflower_plains", new SunflowerPlainsBiome());
	public static final Biome field_9427 = register(130, "desert_lakes", new DesertLakesBiome());
	public static final Biome field_9476 = register(131, "gravelly_mountains", new GravellyMountainsBiome());
	public static final Biome field_9414 = register(132, "flower_forest", new FlowerForestBiome());
	public static final Biome field_9422 = register(133, "taiga_mountains", new TaigaMountainsBiome());
	public static final Biome field_9479 = register(134, "swamp_hills", new SwampHillsBiome());
	public static final Biome field_9453 = register(140, "ice_spikes", new IceSpikesBiome());
	public static final Biome field_9426 = register(149, "modified_jungle", new ModifiedJungleBiome());
	public static final Biome field_9405 = register(151, "modified_jungle_edge", new ModifiedJungleEdgeBiome());
	public static final Biome field_9431 = register(155, "tall_birch_forest", new TallBirchForestBiome());
	public static final Biome field_9458 = register(156, "tall_birch_hills", new TallBirchHillsBiome());
	public static final Biome field_9450 = register(157, "dark_forest_hills", new DarkForestHillsBiome());
	public static final Biome field_9437 = register(158, "snowy_taiga_mountains", new SnowyTaigaMountainsBiome());
	public static final Biome field_9416 = register(160, "giant_spruce_taiga", new GiantSpruceTaigaBiome());
	public static final Biome field_9404 = register(161, "giant_spruce_taiga_hills", new GiantSpruceTaigaHillsBiome());
	public static final Biome field_9436 = register(162, "modified_gravelly_mountains", new ModifiedGravellyMountainsBiome());
	public static final Biome field_9456 = register(163, "shattered_savanna", new ShatteredSavannaBiome());
	public static final Biome field_9445 = register(164, "shattered_savanna_plateau", new ShatteredSavannaPlateauBiome());
	public static final Biome field_9443 = register(165, "eroded_badlands", new ErodedBadlandsBiome());
	public static final Biome field_9413 = register(166, "modified_wooded_badlands_plateau", new ModifiedWoodedBadlandsPlateauBiome());
	public static final Biome field_9406 = register(167, "modified_badlands_plateau", new ModifiedBadlandsPlateauBiome());
	public static final Biome field_9440 = register(168, "bamboo_jungle", new BambooJungleBiome());
	public static final Biome field_9468 = register(169, "bamboo_jungle_hills", new BambooJungleHillsBiome());

	private static Biome register(int i, String string, Biome biome) {
		Registry.register(Registry.BIOME, i, string, biome);
		if (biome.hasParent()) {
			Biome.PARENT_BIOME_ID_MAP.set(biome, Registry.BIOME.getRawId(Registry.BIOME.get(new Identifier(biome.parent))));
		}

		return biome;
	}

	static {
		Collections.addAll(
			Biome.BIOMES,
			new Biome[]{
				field_9423,
				field_9451,
				field_9424,
				field_9472,
				field_9409,
				field_9420,
				field_9471,
				field_9438,
				field_9463,
				field_9452,
				field_9444,
				field_9462,
				field_9407,
				field_9434,
				field_9466,
				field_9459,
				field_9428,
				field_9417,
				field_9432,
				field_9474,
				field_9446,
				field_9419,
				field_9478,
				field_9412,
				field_9421,
				field_9475,
				field_9454,
				field_9425,
				field_9477,
				field_9429,
				field_9460,
				field_9449,
				field_9430,
				field_9415,
				field_9410,
				field_9433
			}
		);
	}
}
