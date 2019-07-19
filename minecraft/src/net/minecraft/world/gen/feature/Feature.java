package net.minecraft.world.gen.feature;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;

public abstract class Feature<FC extends FeatureConfig> {
	public static final StructureFeature<PillagerOutpostFeatureConfig> PILLAGER_OUTPOST = register(
		"pillager_outpost", new PillagerOutpostFeature(PillagerOutpostFeatureConfig::deserialize)
	);
	public static final StructureFeature<MineshaftFeatureConfig> MINESHAFT = register("mineshaft", new MineshaftFeature(MineshaftFeatureConfig::deserialize));
	public static final StructureFeature<DefaultFeatureConfig> WOODLAND_MANSION = register(
		"woodland_mansion", new WoodlandMansionFeature(DefaultFeatureConfig::deserialize)
	);
	public static final StructureFeature<DefaultFeatureConfig> JUNGLE_TEMPLE = register(
		"jungle_temple", new JungleTempleFeature(DefaultFeatureConfig::deserialize)
	);
	public static final StructureFeature<DefaultFeatureConfig> DESERT_PYRAMID = register(
		"desert_pyramid", new DesertPyramidFeature(DefaultFeatureConfig::deserialize)
	);
	public static final StructureFeature<DefaultFeatureConfig> IGLOO = register("igloo", new IglooFeature(DefaultFeatureConfig::deserialize));
	public static final StructureFeature<ShipwreckFeatureConfig> SHIPWRECK = register("shipwreck", new ShipwreckFeature(ShipwreckFeatureConfig::deserialize));
	public static final SwampHutFeature SWAMP_HUT = register("swamp_hut", new SwampHutFeature(DefaultFeatureConfig::deserialize));
	public static final StructureFeature<DefaultFeatureConfig> STRONGHOLD = register("stronghold", new StrongholdFeature(DefaultFeatureConfig::deserialize));
	public static final StructureFeature<DefaultFeatureConfig> OCEAN_MONUMENT = register(
		"ocean_monument", new OceanMonumentFeature(DefaultFeatureConfig::deserialize)
	);
	public static final StructureFeature<OceanRuinFeatureConfig> OCEAN_RUIN = register("ocean_ruin", new OceanRuinFeature(OceanRuinFeatureConfig::deserialize));
	public static final StructureFeature<DefaultFeatureConfig> NETHER_BRIDGE = register(
		"nether_bridge", new NetherFortressFeature(DefaultFeatureConfig::deserialize)
	);
	public static final StructureFeature<DefaultFeatureConfig> END_CITY = register("end_city", new EndCityFeature(DefaultFeatureConfig::deserialize));
	public static final StructureFeature<BuriedTreasureFeatureConfig> BURIED_TREASURE = register(
		"buried_treasure", new BuriedTreasureFeature(BuriedTreasureFeatureConfig::deserialize)
	);
	public static final StructureFeature<VillageFeatureConfig> VILLAGE = register("village", new VillageFeature(VillageFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> FANCY_TREE = register("fancy_tree", new LargeOakTreeFeature(DefaultFeatureConfig::deserialize, false));
	public static final Feature<DefaultFeatureConfig> BIRCH_TREE = register("birch_tree", new BirchTreeFeature(DefaultFeatureConfig::deserialize, false, false));
	public static final Feature<DefaultFeatureConfig> SUPER_BIRCH_TREE = register(
		"super_birch_tree", new BirchTreeFeature(DefaultFeatureConfig::deserialize, false, true)
	);
	public static final Feature<DefaultFeatureConfig> JUNGLE_GROUND_BUSH = register(
		"jungle_ground_bush",
		new JungleGroundBushFeature(DefaultFeatureConfig::deserialize, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState())
	);
	public static final Feature<DefaultFeatureConfig> JUNGLE_TREE = register(
		"jungle_tree",
		new JungleTreeFeature(DefaultFeatureConfig::deserialize, false, 4, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState(), true)
	);
	public static final Feature<DefaultFeatureConfig> PINE_TREE = register("pine_tree", new PineTreeFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreeFeature(DefaultFeatureConfig::deserialize, false));
	public static final Feature<DefaultFeatureConfig> SAVANNA_TREE = register("savanna_tree", new SavannaTreeFeature(DefaultFeatureConfig::deserialize, false));
	public static final Feature<DefaultFeatureConfig> SPRUCE_TREE = register("spruce_tree", new SpruceTreeFeature(DefaultFeatureConfig::deserialize, false));
	public static final Feature<DefaultFeatureConfig> SWAMP_TREE = register("swamp_tree", new SwampTreeFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> NORMAL_TREE = register("normal_tree", new OakTreeFeature(DefaultFeatureConfig::deserialize, false));
	public static final Feature<DefaultFeatureConfig> MEGA_JUNGLE_TREE = register(
		"mega_jungle_tree",
		new MegaJungleTreeFeature(DefaultFeatureConfig::deserialize, false, 10, 20, Blocks.JUNGLE_LOG.getDefaultState(), Blocks.JUNGLE_LEAVES.getDefaultState())
	);
	public static final Feature<DefaultFeatureConfig> MEGA_PINE_TREE = register(
		"mega_pine_tree", new MegaPineTreeFeature(DefaultFeatureConfig::deserialize, false, false)
	);
	public static final Feature<DefaultFeatureConfig> MEGA_SPRUCE_TREE = register(
		"mega_spruce_tree", new MegaPineTreeFeature(DefaultFeatureConfig::deserialize, false, true)
	);
	public static final FlowerFeature DEFAULT_FLOWER = register("default_flower", new DefaultFlowerFeature(DefaultFeatureConfig::deserialize));
	public static final FlowerFeature FOREST_FLOWER = register("forest_flower", new ForestFlowerFeature(DefaultFeatureConfig::deserialize));
	public static final FlowerFeature PLAIN_FLOWER = register("plain_flower", new PlainFlowerFeature(DefaultFeatureConfig::deserialize));
	public static final FlowerFeature SWAMP_FLOWER = register("swamp_flower", new SwampFlowerFeature(DefaultFeatureConfig::deserialize));
	public static final FlowerFeature GENERAL_FOREST_FLOWER = register("general_forest_flower", new GeneralForestFlowerFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> JUNGLE_GRASS = register("jungle_grass", new JungleGrassFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> TAIGA_GRASS = register("taiga_grass", new TaigaGrassFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<GrassFeatureConfig> GRASS = register("grass", new GrassFeature(GrassFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> VOID_START_PLATFORM = register(
		"void_start_platform", new VoidStartPlatformFeature(DefaultFeatureConfig::deserialize)
	);
	public static final Feature<DefaultFeatureConfig> CACTUS = register("cactus", new CactusFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> DEAD_BUSH = register("dead_bush", new DeadBushFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> DESERT_WELL = register("desert_well", new DesertWellFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> FOSSIL = register("fossil", new FossilFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> HELL_FIRE = register("hell_fire", new NetherFireFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<PlantedFeatureConfig> HUGE_RED_MUSHROOM = register(
		"huge_red_mushroom", new HugeRedMushroomFeature(PlantedFeatureConfig::deserialize)
	);
	public static final Feature<PlantedFeatureConfig> HUGE_BROWN_MUSHROOM = register(
		"huge_brown_mushroom", new HugeBrownMushroomFeature(PlantedFeatureConfig::deserialize)
	);
	public static final Feature<DefaultFeatureConfig> ICE_SPIKE = register("ice_spike", new IceSpikeFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneBlobFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> MELON = register("melon", new MelonFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> PUMPKIN = register(
		"pumpkin", new WildCropFeature(DefaultFeatureConfig::deserialize, Blocks.PUMPKIN.getDefaultState())
	);
	public static final Feature<DefaultFeatureConfig> REED = register("reed", new ReedFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> FREEZE_TOP_LAYER = register("freeze_top_layer", new FreezeTopLayerFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> VINES = register("vines", new VinesFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> WATERLILY = register("waterlily", new WaterlilyFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> MONSTER_ROOM = register("monster_room", new DungeonFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> BLUE_ICE = register("blue_ice", new BlueIceFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<IcebergFeatureConfig> ICEBERG = register("iceberg", new IcebergFeature(IcebergFeatureConfig::deserialize));
	public static final Feature<BoulderFeatureConfig> FOREST_ROCK = register("forest_rock", new ForestRockFeature(BoulderFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> HAY_PILE = register("hay_pile", new HayPileFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> SNOW_PILE = register("snow_pile", new SnowPileFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> ICE_PILE = register("ice_pile", new IcePileFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> MELON_PILE = register("melon_pile", new MelonPileFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> PUMPKIN_PILE = register("pumpkin_pile", new PumpkinPileFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<SingleStateFeatureConfig> BUSH = register("bush", new BushFeature(SingleStateFeatureConfig::deserialize));
	public static final Feature<DiskFeatureConfig> DISK = register("disk", new DiskFeature(DiskFeatureConfig::deserialize));
	public static final Feature<DoublePlantFeatureConfig> DOUBLE_PLANT = register("double_plant", new DoublePlantFeature(DoublePlantFeatureConfig::deserialize));
	public static final Feature<NetherSpringFeatureConfig> NETHER_SPRING = register(
		"nether_spring", new NetherSpringFeature(NetherSpringFeatureConfig::deserialize)
	);
	public static final Feature<IcePatchFeatureConfig> ICE_PATCH = register("ice_patch", new IcePatchFeature(IcePatchFeatureConfig::deserialize));
	public static final Feature<LakeFeatureConfig> LAKE = register("lake", new LakeFeature(LakeFeatureConfig::deserialize));
	public static final Feature<OreFeatureConfig> ORE = register("ore", new OreFeature(OreFeatureConfig::deserialize));
	public static final Feature<RandomRandomFeatureConfig> RANDOM_RANDOM_SELECTOR = register(
		"random_random_selector", new RandomRandomFeature(RandomRandomFeatureConfig::deserialize)
	);
	public static final Feature<RandomFeatureConfig> RANDOM_SELECTOR = register("random_selector", new RandomFeature(RandomFeatureConfig::deserialize));
	public static final Feature<SimpleRandomFeatureConfig> SIMPLE_RANDOM_SELECTOR = register(
		"simple_random_selector", new SimpleRandomFeature(SimpleRandomFeatureConfig::deserialize)
	);
	public static final Feature<RandomBooleanFeatureConfig> RANDOM_BOOLEAN_SELECTOR = register(
		"random_boolean_selector", new RandomBooleanFeature(RandomBooleanFeatureConfig::deserialize)
	);
	public static final Feature<EmeraldOreFeatureConfig> EMERALD_ORE = register("emerald_ore", new EmeraldOreFeature(EmeraldOreFeatureConfig::deserialize));
	public static final Feature<SpringFeatureConfig> SPRING_FEATURE = register("spring_feature", new SpringFeature(SpringFeatureConfig::deserialize));
	public static final Feature<EndSpikeFeatureConfig> END_SPIKE = register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> END_ISLAND = register("end_island", new EndIslandFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<EndGatewayFeatureConfig> END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayFeatureConfig::deserialize));
	public static final Feature<SeagrassFeatureConfig> SEAGRASS = register("seagrass", new SeagrassFeature(SeagrassFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> KELP = register("kelp", new KelpFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> CORAL_TREE = register("coral_tree", new CoralTreeFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> CORAL_CLAW = register("coral_claw", new CoralClawFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<SeaPickleFeatureConfig> SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(SeaPickleFeatureConfig::deserialize));
	public static final Feature<SimpleBlockFeatureConfig> SIMPLE_BLOCK = register("simple_block", new SimpleBlockFeature(SimpleBlockFeatureConfig::deserialize));
	public static final Feature<ProbabilityConfig> BAMBOO = register("bamboo", new BambooFeature(ProbabilityConfig::deserialize));
	public static final Feature<DecoratedFeatureConfig> DECORATED = register("decorated", new DecoratedFeature(DecoratedFeatureConfig::deserialize));
	public static final Feature<DecoratedFeatureConfig> DECORATED_FLOWER = register(
		"decorated_flower", new DecoratedFlowerFeature(DecoratedFeatureConfig::deserialize)
	);
	public static final Feature<DefaultFeatureConfig> SWEET_BERRY_BUSH = register(
		"sweet_berry_bush",
		new WildCropFeature(DefaultFeatureConfig::deserialize, Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3)))
	);
	public static final Feature<FillLayerFeatureConfig> FILL_LAYER = register("fill_layer", new FillLayerFeature(FillLayerFeatureConfig::deserialize));
	public static final BonusChestFeature BONUS_CHEST = register("bonus_chest", new BonusChestFeature(DefaultFeatureConfig::deserialize));
	public static final BiMap<String, StructureFeature<?>> STRUCTURES = Util.make(HashBiMap.create(), hashBiMap -> {
		hashBiMap.put("Pillager_Outpost".toLowerCase(Locale.ROOT), PILLAGER_OUTPOST);
		hashBiMap.put("Mineshaft".toLowerCase(Locale.ROOT), MINESHAFT);
		hashBiMap.put("Mansion".toLowerCase(Locale.ROOT), WOODLAND_MANSION);
		hashBiMap.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), JUNGLE_TEMPLE);
		hashBiMap.put("Desert_Pyramid".toLowerCase(Locale.ROOT), DESERT_PYRAMID);
		hashBiMap.put("Igloo".toLowerCase(Locale.ROOT), IGLOO);
		hashBiMap.put("Shipwreck".toLowerCase(Locale.ROOT), SHIPWRECK);
		hashBiMap.put("Swamp_Hut".toLowerCase(Locale.ROOT), SWAMP_HUT);
		hashBiMap.put("Stronghold".toLowerCase(Locale.ROOT), STRONGHOLD);
		hashBiMap.put("Monument".toLowerCase(Locale.ROOT), OCEAN_MONUMENT);
		hashBiMap.put("Ocean_Ruin".toLowerCase(Locale.ROOT), OCEAN_RUIN);
		hashBiMap.put("Fortress".toLowerCase(Locale.ROOT), NETHER_BRIDGE);
		hashBiMap.put("EndCity".toLowerCase(Locale.ROOT), END_CITY);
		hashBiMap.put("Buried_Treasure".toLowerCase(Locale.ROOT), BURIED_TREASURE);
		hashBiMap.put("Village".toLowerCase(Locale.ROOT), VILLAGE);
	});
	public static final List<StructureFeature<?>> JIGSAW_STRUCTURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE);
	private final Function<Dynamic<?>, ? extends FC> configDeserializer;
	protected final boolean emitNeighborBlockUpdates;

	private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
		return Registry.register(Registry.FEATURE, name, feature);
	}

	public Feature(Function<Dynamic<?>, ? extends FC> configDeserializer) {
		this.configDeserializer = configDeserializer;
		this.emitNeighborBlockUpdates = false;
	}

	public Feature(Function<Dynamic<?>, ? extends FC> configDeserializer, boolean emitNeighborBlockUpdates) {
		this.configDeserializer = configDeserializer;
		this.emitNeighborBlockUpdates = emitNeighborBlockUpdates;
	}

	public FC deserializeConfig(Dynamic<?> dynamic) {
		return (FC)this.configDeserializer.apply(dynamic);
	}

	protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
		if (this.emitNeighborBlockUpdates) {
			world.setBlockState(pos, state, 3);
		} else {
			world.setBlockState(pos, state, 2);
		}
	}

	public abstract boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos pos, FC config);

	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return Collections.emptyList();
	}

	public List<Biome.SpawnEntry> getCreatureSpawns() {
		return Collections.emptyList();
	}
}
