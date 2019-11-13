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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
	public static final StructureFeature<DefaultFeatureConfig> PILLAGER_OUTPOST = register(
		"pillager_outpost", new PillagerOutpostFeature(DefaultFeatureConfig::deserialize)
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
	public static final Feature<DefaultFeatureConfig> NO_OP = register("no_op", new NoOpFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<BranchedTreeFeatureConfig> NORMAL_TREE = register("normal_tree", new OakTreeFeature(BranchedTreeFeatureConfig::method_23426));
	public static final Feature<BranchedTreeFeatureConfig> ACACIA_TREE = register("acacia_tree", new AcaciaTreeFeature(BranchedTreeFeatureConfig::method_23426));
	public static final Feature<BranchedTreeFeatureConfig> FANCY_TREE = register("fancy_tree", new LargeOakTreeFeature(BranchedTreeFeatureConfig::method_23426));
	public static final Feature<TreeFeatureConfig> JUNGLE_GROUND_BUSH = register("jungle_ground_bush", new JungleGroundBushFeature(TreeFeatureConfig::deserialize));
	public static final Feature<MegaTreeFeatureConfig> DARK_OAK_TREE = register("dark_oak_tree", new DarkOakTreeFeature(MegaTreeFeatureConfig::method_23408));
	public static final Feature<MegaTreeFeatureConfig> MEGA_JUNGLE_TREE = register(
		"mega_jungle_tree", new MegaJungleTreeFeature(MegaTreeFeatureConfig::method_23408)
	);
	public static final Feature<MegaTreeFeatureConfig> MEGA_SPRUCE_TREE = register(
		"mega_spruce_tree", new MegaPineTreeFeature(MegaTreeFeatureConfig::method_23408)
	);
	public static final FlowerFeature<FlowerFeatureConfig> FLOWER = register("flower", new DefaultFlowerFeature(FlowerFeatureConfig::method_23413));
	public static final Feature<FlowerFeatureConfig> RANDOM_PATCH = register("random_patch", new RandomPatchFeature(FlowerFeatureConfig::method_23413));
	public static final Feature<BlockPileFeatureConfig> BLOCK_PILE = register("block_pile", new AbstractPileFeature(BlockPileFeatureConfig::method_23406));
	public static final Feature<SpringFeatureConfig> SPRING_FEATURE = register("spring_feature", new SpringFeature(SpringFeatureConfig::method_23440));
	public static final Feature<DefaultFeatureConfig> CHORUS_PLANT = register("chorus_plant", new ChorusPlantFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<EmeraldOreFeatureConfig> EMERALD_ORE = register("emerald_ore", new EmeraldOreFeature(EmeraldOreFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> VOID_START_PLATFORM = register(
		"void_start_platform", new VoidStartPlatformFeature(DefaultFeatureConfig::deserialize)
	);
	public static final Feature<DefaultFeatureConfig> DESERT_WELL = register("desert_well", new DesertWellFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> FOSSIL = register("fossil", new FossilFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<HugeMushroomFeatureConfig> HUGE_RED_MUSHROOM = register(
		"huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfig::method_23407)
	);
	public static final Feature<HugeMushroomFeatureConfig> HUGE_BROWN_MUSHROOM = register(
		"huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfig::method_23407)
	);
	public static final Feature<DefaultFeatureConfig> ICE_SPIKE = register("ice_spike", new IceSpikeFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> GLOWSTONE_BLOB = register("glowstone_blob", new GlowstoneBlobFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> FREEZE_TOP_LAYER = register("freeze_top_layer", new FreezeTopLayerFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> VINES = register("vines", new VinesFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> MONSTER_ROOM = register("monster_room", new DungeonFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> BLUE_ICE = register("blue_ice", new BlueIceFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<BushFeatureConfig> ICEBERG = register("iceberg", new IcebergFeature(BushFeatureConfig::deserialize));
	public static final Feature<BoulderFeatureConfig> FOREST_ROCK = register("forest_rock", new ForestRockFeature(BoulderFeatureConfig::deserialize));
	public static final Feature<DiskFeatureConfig> DISK = register("disk", new DiskFeature(DiskFeatureConfig::deserialize));
	public static final Feature<IcePatchFeatureConfig> ICE_PATCH = register("ice_patch", new IcePatchFeature(IcePatchFeatureConfig::deserialize));
	public static final Feature<BushFeatureConfig> LAKE = register("lake", new LakeFeature(BushFeatureConfig::deserialize));
	public static final Feature<OreFeatureConfig> ORE = register("ore", new OreFeature(OreFeatureConfig::deserialize));
	public static final Feature<EndSpikeFeatureConfig> END_SPIKE = register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> END_ISLAND = register("end_island", new EndIslandFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<EndGatewayFeatureConfig> END_GATEWAY = register("end_gateway", new EndGatewayFeature(EndGatewayFeatureConfig::deserialize));
	public static final Feature<SeagrassFeatureConfig> SEAGRASS = register("seagrass", new SeagrassFeature(SeagrassFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> KELP = register("kelp", new KelpFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> CORAL_TREE = register("coral_tree", new CoralTreeFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> CORAL_MUSHROOM = register("coral_mushroom", new CoralMushroomFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> CORAL_CLAW = register("coral_claw", new CoralClawFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<SeaPickleFeatureConfig> SEA_PICKLE = register("sea_pickle", new SeaPickleFeature(SeaPickleFeatureConfig::deserialize));
	public static final Feature<SimpleBlockFeatureConfig> SIMPLE_BLOCK = register("simple_block", new SimpleBlockFeature(SimpleBlockFeatureConfig::deserialize));
	public static final Feature<ProbabilityConfig> BAMBOO = register("bamboo", new BambooFeature(ProbabilityConfig::deserialize));
	public static final Feature<FillLayerFeatureConfig> FILL_LAYER = register("fill_layer", new FillLayerFeature(FillLayerFeatureConfig::deserialize));
	public static final BonusChestFeature BONUS_CHEST = register("bonus_chest", new BonusChestFeature(DefaultFeatureConfig::deserialize));
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
	public static final Feature<DecoratedFeatureConfig> DECORATED = register("decorated", new DecoratedFeature(DecoratedFeatureConfig::deserialize));
	public static final Feature<DecoratedFeatureConfig> DECORATED_FLOWER = register(
		"decorated_flower", new DecoratedFlowerFeature(DecoratedFeatureConfig::deserialize)
	);
	public static final BiMap<String, StructureFeature<?>> STRUCTURES = Util.create(HashBiMap.create(), hashBiMap -> {
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

	private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
		return Registry.register(Registry.FEATURE, name, feature);
	}

	public Feature(Function<Dynamic<?>, ? extends FC> configDeserializer) {
		this.configDeserializer = configDeserializer;
	}

	public ConfiguredFeature<FC, ?> configure(FC config) {
		return new ConfiguredFeature<>(this, config);
	}

	public FC deserializeConfig(Dynamic<?> dynamic) {
		return (FC)this.configDeserializer.apply(dynamic);
	}

	protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
		world.setBlockState(pos, state, 3);
	}

	public abstract boolean generate(IWorld world, ChunkGenerator<? extends ChunkGeneratorConfig> generator, Random random, BlockPos pos, FC config);

	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return Collections.emptyList();
	}

	public List<Biome.SpawnEntry> getCreatureSpawns() {
		return Collections.emptyList();
	}

	protected static boolean method_23395(Block block) {
		return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
	}

	protected static boolean method_23396(Block block) {
		return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT || block == Blocks.MYCELIUM;
	}
}
