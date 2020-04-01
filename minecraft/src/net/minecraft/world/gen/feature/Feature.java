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
import net.minecraft.class_5021;
import net.minecraft.class_5102;
import net.minecraft.class_5103;
import net.minecraft.class_5104;
import net.minecraft.class_5105;
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
		"pillager_outpost", new PillagerOutpostFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final StructureFeature<MineshaftFeatureConfig> MINESHAFT = register(
		"mineshaft", new MineshaftFeature(MineshaftFeatureConfig::deserialize, MineshaftFeatureConfig::method_26616)
	);
	public static final StructureFeature<DefaultFeatureConfig> WOODLAND_MANSION = register(
		"woodland_mansion", new WoodlandMansionFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final StructureFeature<DefaultFeatureConfig> JUNGLE_TEMPLE = register(
		"jungle_temple", new JungleTempleFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final StructureFeature<DefaultFeatureConfig> DESERT_PYRAMID = register(
		"desert_pyramid", new DesertPyramidFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final StructureFeature<DefaultFeatureConfig> IGLOO = register(
		"igloo", new IglooFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final StructureFeature<ShipwreckFeatureConfig> SHIPWRECK = register(
		"shipwreck", new ShipwreckFeature(ShipwreckFeatureConfig::deserialize, ShipwreckFeatureConfig::method_26641)
	);
	public static final SwampHutFeature SWAMP_HUT = register(
		"swamp_hut", new SwampHutFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final StructureFeature<DefaultFeatureConfig> STRONGHOLD = register(
		"stronghold", new StrongholdFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final StructureFeature<DefaultFeatureConfig> OCEAN_MONUMENT = register(
		"ocean_monument", new OceanMonumentFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final StructureFeature<OceanRuinFeatureConfig> OCEAN_RUIN = register(
		"ocean_ruin", new OceanRuinFeature(OceanRuinFeatureConfig::deserialize, OceanRuinFeatureConfig::method_26620)
	);
	public static final StructureFeature<DefaultFeatureConfig> NETHER_BRIDGE = register(
		"nether_bridge", new NetherFortressFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final StructureFeature<DefaultFeatureConfig> END_CITY = register(
		"end_city", new EndCityFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final StructureFeature<BuriedTreasureFeatureConfig> BURIED_TREASURE = register(
		"buried_treasure", new BuriedTreasureFeature(BuriedTreasureFeatureConfig::deserialize, BuriedTreasureFeatureConfig::method_26598)
	);
	public static final StructureFeature<VillageFeatureConfig> VILLAGE = register(
		"village", new VillageFeature(VillageFeatureConfig::deserialize, VillageFeatureConfig::method_26651)
	);
	public static final StructureFeature<DefaultFeatureConfig> NETHER_FOSSIL = register(
		"nether_fossil", new NetherFossilFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final StructureFeature<DefaultFeatureConfig> field_23572 = register(
		"ship", new class_5021.class_5024(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> NO_OP = register(
		"no_op", new NoOpFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<BranchedTreeFeatureConfig> NORMAL_TREE = register(
		"normal_tree", new OakTreeFeature(BranchedTreeFeatureConfig::deserialize, BranchedTreeFeatureConfig::method_26645)
	);
	public static final Feature<BranchedTreeFeatureConfig> ACACIA_TREE = register(
		"acacia_tree", new AcaciaTreeFeature(BranchedTreeFeatureConfig::deserialize, BranchedTreeFeatureConfig::method_26645)
	);
	public static final Feature<BranchedTreeFeatureConfig> FANCY_TREE = register(
		"fancy_tree", new LargeOakTreeFeature(BranchedTreeFeatureConfig::deserialize, BranchedTreeFeatureConfig::method_26646)
	);
	public static final Feature<TreeFeatureConfig> JUNGLE_GROUND_BUSH = register(
		"jungle_ground_bush", new JungleGroundBushFeature(TreeFeatureConfig::deserialize, TreeFeatureConfig::method_26650)
	);
	public static final Feature<MegaTreeFeatureConfig> DARK_OAK_TREE = register(
		"dark_oak_tree", new DarkOakTreeFeature(MegaTreeFeatureConfig::deserialize, MegaTreeFeatureConfig::method_26615)
	);
	public static final Feature<MegaTreeFeatureConfig> MEGA_JUNGLE_TREE = register(
		"mega_jungle_tree", new MegaJungleTreeFeature(MegaTreeFeatureConfig::deserialize, MegaTreeFeatureConfig::method_26615)
	);
	public static final Feature<MegaTreeFeatureConfig> MEGA_SPRUCE_TREE = register(
		"mega_spruce_tree", new MegaPineTreeFeature(MegaTreeFeatureConfig::deserialize, MegaTreeFeatureConfig::method_26615)
	);
	public static final FlowerFeature<RandomPatchFeatureConfig> FLOWER = register(
		"flower", new DefaultFlowerFeature(RandomPatchFeatureConfig::deserialize, RandomPatchFeatureConfig::method_26627)
	);
	public static final Feature<RandomPatchFeatureConfig> RANDOM_PATCH = register(
		"random_patch", new RandomPatchFeature(RandomPatchFeatureConfig::deserialize, RandomPatchFeatureConfig::method_26627)
	);
	public static final Feature<BlockPileFeatureConfig> BLOCK_PILE = register(
		"block_pile", new AbstractPileFeature(BlockPileFeatureConfig::deserialize, BlockPileFeatureConfig::method_26596)
	);
	public static final Feature<SpringFeatureConfig> SPRING_FEATURE = register(
		"spring_feature", new SpringFeature(SpringFeatureConfig::deserialize, SpringFeatureConfig::method_26648)
	);
	public static final Feature<DefaultFeatureConfig> CHORUS_PLANT = register(
		"chorus_plant", new ChorusPlantFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<EmeraldOreFeatureConfig> EMERALD_ORE = register(
		"emerald_ore", new EmeraldOreFeature(EmeraldOreFeatureConfig::deserialize, EmeraldOreFeatureConfig::method_26630)
	);
	public static final Feature<DefaultFeatureConfig> VOID_START_PLATFORM = register(
		"void_start_platform", new VoidStartPlatformFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> DESERT_WELL = register(
		"desert_well", new DesertWellFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> FOSSIL = register(
		"fossil", new FossilFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<HugeMushroomFeatureConfig> HUGE_RED_MUSHROOM = register(
		"huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfig::deserialize, HugeMushroomFeatureConfig::method_26613)
	);
	public static final Feature<HugeMushroomFeatureConfig> HUGE_BROWN_MUSHROOM = register(
		"huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfig::deserialize, HugeMushroomFeatureConfig::method_26613)
	);
	public static final Feature<DefaultFeatureConfig> ICE_SPIKE = register(
		"ice_spike", new IceSpikeFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> GLOWSTONE_BLOB = register(
		"glowstone_blob", new GlowstoneBlobFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> FREEZE_TOP_LAYER = register(
		"freeze_top_layer", new FreezeTopLayerFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> VINES = register(
		"vines", new VinesFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> MONSTER_ROOM = register(
		"monster_room", new DungeonFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> BLUE_ICE = register(
		"blue_ice", new BlueIceFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<SingleStateFeatureConfig> ICEBERG = register(
		"iceberg", new IcebergFeature(SingleStateFeatureConfig::deserialize, SingleStateFeatureConfig::method_26597)
	);
	public static final Feature<BoulderFeatureConfig> FOREST_ROCK = register(
		"forest_rock", new ForestRockFeature(BoulderFeatureConfig::deserialize, BoulderFeatureConfig::method_26595)
	);
	public static final Feature<DiskFeatureConfig> DISK = register("disk", new DiskFeature(DiskFeatureConfig::deserialize, DiskFeatureConfig::method_26609));
	public static final Feature<IcePatchFeatureConfig> ICE_PATCH = register(
		"ice_patch", new IcePatchFeature(IcePatchFeatureConfig::deserialize, IcePatchFeatureConfig::method_26611)
	);
	public static final Feature<SingleStateFeatureConfig> LAKE = register(
		"lake", new LakeFeature(SingleStateFeatureConfig::deserialize, SingleStateFeatureConfig::method_26597)
	);
	public static final Feature<OreFeatureConfig> ORE = register("ore", new OreFeature(OreFeatureConfig::deserialize, OreFeatureConfig::method_26621));
	public static final Feature<EndSpikeFeatureConfig> END_SPIKE = register(
		"end_spike", new EndSpikeFeature(EndSpikeFeatureConfig::deserialize, EndSpikeFeatureConfig::method_26647)
	);
	public static final Feature<SingleStateFeatureConfig> END_ISLAND = register(
		"end_island", new EndIslandFeature(SingleStateFeatureConfig::deserialize, SingleStateFeatureConfig::method_26597)
	);
	public static final Feature<EndGatewayFeatureConfig> END_GATEWAY = register(
		"end_gateway", new EndGatewayFeature(EndGatewayFeatureConfig::deserialize, EndGatewayFeatureConfig::method_26610)
	);
	public static final Feature<SeagrassFeatureConfig> SEAGRASS = register(
		"seagrass", new SeagrassFeature(SeagrassFeatureConfig::deserialize, SeagrassFeatureConfig::method_26631)
	);
	public static final Feature<DefaultFeatureConfig> KELP = register(
		"kelp", new KelpFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> CORAL_TREE = register(
		"coral_tree", new CoralTreeFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> CORAL_MUSHROOM = register(
		"coral_mushroom", new CoralMushroomFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> CORAL_CLAW = register(
		"coral_claw", new CoralClawFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<SeaPickleFeatureConfig> SEA_PICKLE = register(
		"sea_pickle", new SeaPickleFeature(SeaPickleFeatureConfig::deserialize, SeaPickleFeatureConfig::method_26606)
	);
	public static final Feature<SimpleBlockFeatureConfig> SIMPLE_BLOCK = register(
		"simple_block", new SimpleBlockFeature(SimpleBlockFeatureConfig::deserialize, SimpleBlockFeatureConfig::method_26642)
	);
	public static final Feature<ProbabilityConfig> BAMBOO = register("bamboo", new BambooFeature(ProbabilityConfig::deserialize, ProbabilityConfig::method_26623));
	public static final Feature<HugeFungusFeatureConfig> HUGE_FUNGUS = register(
		"huge_fungus", new HugeFungusFeature(HugeFungusFeatureConfig::deserialize, HugeFungusFeatureConfig::method_26589)
	);
	public static final Feature<BlockPileFeatureConfig> NETHER_FOREST_VEGETATION = register(
		"nether_forest_vegetation", new NetherForestVegetationFeature(BlockPileFeatureConfig::deserialize, BlockPileFeatureConfig::method_26596)
	);
	public static final Feature<DefaultFeatureConfig> WEEPING_VINES = register(
		"weeping_vines", new WeepingVinesFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> TWISTING_VINES = register(
		"twisting_vines", new TwistingVinesFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<FillLayerFeatureConfig> FILL_LAYER = register(
		"fill_layer", new FillLayerFeature(FillLayerFeatureConfig::deserialize, FillLayerFeatureConfig::method_26614)
	);
	public static final BonusChestFeature BONUS_CHEST = register(
		"bonus_chest", new BonusChestFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<DefaultFeatureConfig> BASALT_PILLAR = register(
		"basalt_pillar", new BasaltPillarFeature(DefaultFeatureConfig::deserialize, DefaultFeatureConfig::method_26619)
	);
	public static final Feature<OreFeatureConfig> NO_SURFACE_ORE = register(
		"no_surface_ore", new NoSurfaceOreFeature(OreFeatureConfig::deserialize, OreFeatureConfig::method_26621)
	);
	public static final Feature<RandomRandomFeatureConfig> RANDOM_RANDOM_SELECTOR = register(
		"random_random_selector", new RandomRandomFeature(RandomRandomFeatureConfig::deserialize, RandomRandomFeatureConfig::method_26628)
	);
	public static final Feature<RandomFeatureConfig> RANDOM_SELECTOR = register(
		"random_selector", new RandomFeature(RandomFeatureConfig::deserialize, RandomFeatureConfig::method_26625)
	);
	public static final Feature<SimpleRandomFeatureConfig> SIMPLE_RANDOM_SELECTOR = register(
		"simple_random_selector", new SimpleRandomFeature(SimpleRandomFeatureConfig::deserialize, SimpleRandomFeatureConfig::method_26643)
	);
	public static final Feature<RandomBooleanFeatureConfig> RANDOM_BOOLEAN_SELECTOR = register(
		"random_boolean_selector", new RandomBooleanFeature(RandomBooleanFeatureConfig::deserialize, RandomBooleanFeatureConfig::method_26624)
	);
	public static final Feature<DecoratedFeatureConfig> DECORATED = register(
		"decorated", new DecoratedFeature(DecoratedFeatureConfig::deserialize, DecoratedFeatureConfig::method_26608)
	);
	public static final Feature<DecoratedFeatureConfig> DECORATED_FLOWER = register(
		"decorated_flower", new DecoratedFlowerFeature(DecoratedFeatureConfig::deserialize, DecoratedFeatureConfig::method_26608)
	);
	public static final Feature<class_5105> SHAPE = register("shape", new class_5103(class_5105::method_26632, class_5105::method_26633));
	public static final Feature<class_5104> CHAR = register("char", new class_5102(class_5104::method_26601, class_5104::method_26602));
	public static final BiMap<String, StructureFeature<?>> STRUCTURES = Util.make(HashBiMap.create(), map -> {
		map.put("Pillager_Outpost".toLowerCase(Locale.ROOT), PILLAGER_OUTPOST);
		map.put("Mineshaft".toLowerCase(Locale.ROOT), MINESHAFT);
		map.put("Mansion".toLowerCase(Locale.ROOT), WOODLAND_MANSION);
		map.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), JUNGLE_TEMPLE);
		map.put("Desert_Pyramid".toLowerCase(Locale.ROOT), DESERT_PYRAMID);
		map.put("Igloo".toLowerCase(Locale.ROOT), IGLOO);
		map.put("Shipwreck".toLowerCase(Locale.ROOT), SHIPWRECK);
		map.put("Swamp_Hut".toLowerCase(Locale.ROOT), SWAMP_HUT);
		map.put("Stronghold".toLowerCase(Locale.ROOT), STRONGHOLD);
		map.put("Monument".toLowerCase(Locale.ROOT), OCEAN_MONUMENT);
		map.put("Ocean_Ruin".toLowerCase(Locale.ROOT), OCEAN_RUIN);
		map.put("Fortress".toLowerCase(Locale.ROOT), NETHER_BRIDGE);
		map.put("EndCity".toLowerCase(Locale.ROOT), END_CITY);
		map.put("Buried_Treasure".toLowerCase(Locale.ROOT), BURIED_TREASURE);
		map.put("Village".toLowerCase(Locale.ROOT), VILLAGE);
		map.put("Nether_Fossil".toLowerCase(Locale.ROOT), NETHER_FOSSIL);
		map.put("Ship".toLowerCase(Locale.ROOT), field_23572);
	});
	public static final List<StructureFeature<?>> JIGSAW_STRUCTURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL);
	private final Function<Dynamic<?>, ? extends FC> configDeserializer;
	protected final Function<Random, ? extends FC> field_23569;

	private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
		return Registry.register(Registry.FEATURE, name, feature);
	}

	public Feature(Function<Dynamic<?>, ? extends FC> configDeserializer, Function<Random, ? extends FC> function) {
		this.configDeserializer = configDeserializer;
		this.field_23569 = function;
	}

	public ConfiguredFeature<FC, ?> configure(FC config) {
		return new ConfiguredFeature<>(this, config);
	}

	public ConfiguredFeature<FC, ?> method_26588(Random random) {
		return new ConfiguredFeature<>(this, (FC)this.field_23569.apply(random));
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

	protected static boolean isStone(Block block) {
		return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
	}

	protected static boolean isDirt(Block block) {
		return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT || block == Blocks.MYCELIUM;
	}
}
