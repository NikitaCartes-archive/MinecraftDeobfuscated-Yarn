package net.minecraft;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.DefaultBiomeCreator;
import net.minecraft.world.gen.surfacebuilder.ConfiguredSurfaceBuilders;

public abstract class class_5504 {
	private static final Int2ObjectMap<RegistryKey<Biome>> field_26736 = new Int2ObjectArrayMap<>();
	public static final Biome field_26734 = method_31145(1, Biomes.field_9451, DefaultBiomeCreator.createPlains(false));
	public static final Biome field_26735 = method_31145(127, Biomes.field_9473, DefaultBiomeCreator.createTheVoid());

	private static Biome method_31145(int i, RegistryKey<Biome> registryKey, Biome biome) {
		field_26736.put(i, registryKey);
		return BuiltinRegistries.set(BuiltinRegistries.BIOME, i, registryKey, biome);
	}

	public static RegistryKey<Biome> method_31144(int i) {
		return field_26736.get(i);
	}

	static {
		method_31145(0, Biomes.field_9423, DefaultBiomeCreator.createNormalOcean(false));
		method_31145(2, Biomes.field_9424, DefaultBiomeCreator.createDesert(0.125F, 0.05F, true, true, true));
		method_31145(3, Biomes.field_9472, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.field_26330, false));
		method_31145(4, Biomes.field_9409, DefaultBiomeCreator.createNormalForest(0.1F, 0.2F));
		method_31145(5, Biomes.field_9420, DefaultBiomeCreator.createTaiga(0.2F, 0.2F, false, false, true, false));
		method_31145(6, Biomes.field_9471, DefaultBiomeCreator.createSwamp(-0.2F, 0.1F, false));
		method_31145(7, Biomes.field_9438, DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.5F, 4159204, false));
		method_31145(8, Biomes.field_9461, DefaultBiomeCreator.createNetherWastes());
		method_31145(9, Biomes.field_9411, DefaultBiomeCreator.createTheEnd());
		method_31145(10, Biomes.field_9435, DefaultBiomeCreator.createFrozenOcean(false));
		method_31145(11, Biomes.field_9463, DefaultBiomeCreator.createRiver(-0.5F, 0.0F, 0.0F, 3750089, true));
		method_31145(12, Biomes.field_9452, DefaultBiomeCreator.createSnowyTundra(0.125F, 0.05F, false, false));
		method_31145(13, Biomes.field_9444, DefaultBiomeCreator.createSnowyTundra(0.45F, 0.3F, false, true));
		method_31145(14, Biomes.field_9462, DefaultBiomeCreator.createMushroomFields(0.2F, 0.3F));
		method_31145(15, Biomes.field_9407, DefaultBiomeCreator.createMushroomFields(0.0F, 0.025F));
		method_31145(16, Biomes.field_9434, DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.8F, 0.4F, 4159204, false, false));
		method_31145(17, Biomes.field_9466, DefaultBiomeCreator.createDesert(0.45F, 0.3F, false, true, false));
		method_31145(18, Biomes.field_9459, DefaultBiomeCreator.createNormalForest(0.45F, 0.3F));
		method_31145(19, Biomes.field_9428, DefaultBiomeCreator.createTaiga(0.45F, 0.3F, false, false, false, false));
		method_31145(20, Biomes.field_9464, DefaultBiomeCreator.createMountains(0.8F, 0.3F, ConfiguredSurfaceBuilders.field_26327, true));
		method_31145(21, Biomes.field_9417, DefaultBiomeCreator.createJungle());
		method_31145(22, Biomes.field_9432, DefaultBiomeCreator.createJungleHills());
		method_31145(23, Biomes.field_9474, DefaultBiomeCreator.createJungleEdge());
		method_31145(24, Biomes.field_9446, DefaultBiomeCreator.createNormalOcean(true));
		method_31145(25, Biomes.field_9419, DefaultBiomeCreator.createBeach(0.1F, 0.8F, 0.2F, 0.3F, 4159204, false, true));
		method_31145(26, Biomes.field_9478, DefaultBiomeCreator.createBeach(0.0F, 0.025F, 0.05F, 0.3F, 4020182, true, false));
		method_31145(27, Biomes.field_9412, DefaultBiomeCreator.createBirchForest(0.1F, 0.2F, false));
		method_31145(28, Biomes.field_9421, DefaultBiomeCreator.createBirchForest(0.45F, 0.3F, false));
		method_31145(29, Biomes.field_9475, DefaultBiomeCreator.createDarkForest(0.1F, 0.2F, false));
		method_31145(30, Biomes.field_9454, DefaultBiomeCreator.createTaiga(0.2F, 0.2F, true, false, false, true));
		method_31145(31, Biomes.field_9425, DefaultBiomeCreator.createTaiga(0.45F, 0.3F, true, false, false, false));
		method_31145(32, Biomes.field_9477, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.3F, false));
		method_31145(33, Biomes.field_9429, DefaultBiomeCreator.createGiantTreeTaiga(0.45F, 0.3F, 0.3F, false));
		method_31145(34, Biomes.field_9460, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.field_26327, true));
		method_31145(35, Biomes.field_9449, DefaultBiomeCreator.createSavanna(0.125F, 0.05F, 1.2F, false, false));
		method_31145(36, Biomes.field_9430, DefaultBiomeCreator.createSavannaPlateau());
		method_31145(37, Biomes.field_9415, DefaultBiomeCreator.createNormalBadlands(0.1F, 0.2F, false));
		method_31145(38, Biomes.field_9410, DefaultBiomeCreator.createWoodedBadlandsPlateau(1.5F, 0.025F));
		method_31145(39, Biomes.field_9433, DefaultBiomeCreator.createNormalBadlands(1.5F, 0.025F, true));
		method_31145(40, Biomes.field_9457, DefaultBiomeCreator.createSmallEndIslands());
		method_31145(41, Biomes.field_9447, DefaultBiomeCreator.createEndMidlands());
		method_31145(42, Biomes.field_9442, DefaultBiomeCreator.createEndHighlands());
		method_31145(43, Biomes.field_9465, DefaultBiomeCreator.createEndBarrens());
		method_31145(44, Biomes.field_9408, DefaultBiomeCreator.createWarmOcean());
		method_31145(45, Biomes.field_9441, DefaultBiomeCreator.createLukewarmOcean(false));
		method_31145(46, Biomes.field_9467, DefaultBiomeCreator.createColdOcean(false));
		method_31145(47, Biomes.field_9448, DefaultBiomeCreator.createDeepWarmOcean());
		method_31145(48, Biomes.field_9439, DefaultBiomeCreator.createLukewarmOcean(true));
		method_31145(49, Biomes.field_9470, DefaultBiomeCreator.createColdOcean(true));
		method_31145(50, Biomes.field_9418, DefaultBiomeCreator.createFrozenOcean(true));
		method_31145(129, Biomes.field_9455, DefaultBiomeCreator.createPlains(true));
		method_31145(130, Biomes.field_9427, DefaultBiomeCreator.createDesert(0.225F, 0.25F, false, false, false));
		method_31145(131, Biomes.field_9476, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.field_26328, false));
		method_31145(132, Biomes.field_9414, DefaultBiomeCreator.createFlowerForest());
		method_31145(133, Biomes.field_9422, DefaultBiomeCreator.createTaiga(0.3F, 0.4F, false, true, false, false));
		method_31145(134, Biomes.field_9479, DefaultBiomeCreator.createSwamp(-0.1F, 0.3F, true));
		method_31145(140, Biomes.field_9453, DefaultBiomeCreator.createSnowyTundra(0.425F, 0.45000002F, true, false));
		method_31145(149, Biomes.field_9426, DefaultBiomeCreator.createModifiedJungle());
		method_31145(151, Biomes.field_9405, DefaultBiomeCreator.createModifiedJungleEdge());
		method_31145(155, Biomes.field_9431, DefaultBiomeCreator.createBirchForest(0.2F, 0.4F, true));
		method_31145(156, Biomes.field_9458, DefaultBiomeCreator.createBirchForest(0.55F, 0.5F, true));
		method_31145(157, Biomes.field_9450, DefaultBiomeCreator.createDarkForest(0.2F, 0.4F, true));
		method_31145(158, Biomes.field_9437, DefaultBiomeCreator.createTaiga(0.3F, 0.4F, true, true, false, false));
		method_31145(160, Biomes.field_9416, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true));
		method_31145(161, Biomes.field_9404, DefaultBiomeCreator.createGiantTreeTaiga(0.2F, 0.2F, 0.25F, true));
		method_31145(162, Biomes.field_9436, DefaultBiomeCreator.createMountains(1.0F, 0.5F, ConfiguredSurfaceBuilders.field_26328, false));
		method_31145(163, Biomes.field_9456, DefaultBiomeCreator.createSavanna(0.3625F, 1.225F, 1.1F, true, true));
		method_31145(164, Biomes.field_9445, DefaultBiomeCreator.createSavanna(1.05F, 1.2125001F, 1.0F, true, true));
		method_31145(165, Biomes.field_9443, DefaultBiomeCreator.createErodedBadlands());
		method_31145(166, Biomes.field_9413, DefaultBiomeCreator.createWoodedBadlandsPlateau(0.45F, 0.3F));
		method_31145(167, Biomes.field_9406, DefaultBiomeCreator.createNormalBadlands(0.45F, 0.3F, true));
		method_31145(168, Biomes.field_9440, DefaultBiomeCreator.createNormalBambooJungle());
		method_31145(169, Biomes.field_9468, DefaultBiomeCreator.createBambooJungleHills());
		method_31145(170, Biomes.field_22076, DefaultBiomeCreator.createSoulSandValley());
		method_31145(171, Biomes.field_22077, DefaultBiomeCreator.createCrimsonForest());
		method_31145(172, Biomes.field_22075, DefaultBiomeCreator.createWarpedForest());
		method_31145(173, Biomes.field_23859, DefaultBiomeCreator.createBasaltDeltas());
	}
}
