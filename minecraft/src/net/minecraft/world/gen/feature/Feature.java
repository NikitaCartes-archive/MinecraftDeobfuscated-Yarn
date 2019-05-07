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
import net.minecraft.util.SystemUtil;
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
	public static final Feature<DefaultFeatureConfig> field_13529 = register("fancy_tree", new LargeOakTreeFeature(DefaultFeatureConfig::deserialize, false));
	public static final Feature<DefaultFeatureConfig> field_13566 = register("birch_tree", new BirchTreeFeature(DefaultFeatureConfig::deserialize, false, false));
	public static final Feature<DefaultFeatureConfig> field_13578 = register(
		"super_birch_tree", new BirchTreeFeature(DefaultFeatureConfig::deserialize, false, true)
	);
	public static final Feature<DefaultFeatureConfig> field_13537 = register(
		"jungle_ground_bush",
		new JungleGroundBushFeature(DefaultFeatureConfig::deserialize, Blocks.field_10306.getDefaultState(), Blocks.field_10503.getDefaultState())
	);
	public static final Feature<DefaultFeatureConfig> field_13508 = register(
		"jungle_tree",
		new JungleTreeFeature(DefaultFeatureConfig::deserialize, false, 4, Blocks.field_10306.getDefaultState(), Blocks.field_10335.getDefaultState(), true)
	);
	public static final Feature<DefaultFeatureConfig> field_13581 = register("pine_tree", new PineTreeFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13532 = register("dark_oak_tree", new DarkOakTreeFeature(DefaultFeatureConfig::deserialize, false));
	public static final Feature<DefaultFeatureConfig> field_13545 = register("savanna_tree", new SavannaTreeFeature(DefaultFeatureConfig::deserialize, false));
	public static final Feature<DefaultFeatureConfig> field_13577 = register("spruce_tree", new SpruceTreeFeature(DefaultFeatureConfig::deserialize, false));
	public static final Feature<DefaultFeatureConfig> field_13530 = register("swamp_tree", new SwampTreeFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13510 = register("normal_tree", new OakTreeFeature(DefaultFeatureConfig::deserialize, false));
	public static final Feature<DefaultFeatureConfig> field_13558 = register(
		"mega_jungle_tree",
		new MegaJungleTreeFeature(DefaultFeatureConfig::deserialize, false, 10, 20, Blocks.field_10306.getDefaultState(), Blocks.field_10335.getDefaultState())
	);
	public static final Feature<DefaultFeatureConfig> field_13543 = register(
		"mega_pine_tree", new MegaPineTreeFeature(DefaultFeatureConfig::deserialize, false, false)
	);
	public static final Feature<DefaultFeatureConfig> field_13580 = register(
		"mega_spruce_tree", new MegaPineTreeFeature(DefaultFeatureConfig::deserialize, false, true)
	);
	public static final FlowerFeature DEFAULT_FLOWER = register("default_flower", new DefaultFlowerFeature(DefaultFeatureConfig::deserialize));
	public static final FlowerFeature FOREST_FLOWER = register("forest_flower", new ForestFlowerFeature(DefaultFeatureConfig::deserialize));
	public static final FlowerFeature PLAIN_FLOWER = register("plain_flower", new PlainFlowerFeature(DefaultFeatureConfig::deserialize));
	public static final FlowerFeature SWAMP_FLOWER = register("swamp_flower", new SwampFlowerFeature(DefaultFeatureConfig::deserialize));
	public static final FlowerFeature GENERAL_FOREST_FLOWER = register("general_forest_flower", new GeneralForestFlowerFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13590 = register("jungle_grass", new JungleGrassFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13521 = register("taiga_grass", new TaigaGrassFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<GrassFeatureConfig> field_13511 = register("grass", new GrassFeature(GrassFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13591 = register(
		"void_start_platform", new VoidStartPlatformFeature(DefaultFeatureConfig::deserialize)
	);
	public static final Feature<DefaultFeatureConfig> field_13554 = register("cactus", new CactusFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13548 = register("dead_bush", new DeadBushFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13592 = register("desert_well", new DesertWellFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13516 = register("fossil", new FossilFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13523 = register("hell_fire", new NetherFireFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<PlantedFeatureConfig> field_13571 = register("huge_red_mushroom", new HugeRedMushroomFeature(PlantedFeatureConfig::deserialize));
	public static final Feature<PlantedFeatureConfig> field_13531 = register(
		"huge_brown_mushroom", new HugeBrownMushroomFeature(PlantedFeatureConfig::deserialize)
	);
	public static final Feature<DefaultFeatureConfig> field_13562 = register("ice_spike", new IceSpikeFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13568 = register("glowstone_blob", new GlowstoneBlobFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13534 = register("melon", new MelonFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13524 = register(
		"pumpkin", new WildCropFeature(DefaultFeatureConfig::deserialize, Blocks.field_10261.getDefaultState())
	);
	public static final Feature<DefaultFeatureConfig> field_13583 = register("reed", new ReedFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13539 = register("freeze_top_layer", new FreezeTopLayerFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13559 = register("vines", new VinesFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13542 = register("waterlily", new WaterlilyFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13579 = register("monster_room", new DungeonFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13560 = register("blue_ice", new BlueIceFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<IcebergFeatureConfig> field_13544 = register("iceberg", new IcebergFeature(IcebergFeatureConfig::deserialize));
	public static final Feature<BoulderFeatureConfig> field_13584 = register("forest_rock", new ForestRockFeature(BoulderFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_16797 = register("hay_pile", new HayPileFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_17005 = register("snow_pile", new SnowPileFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_17006 = register("ice_pile", new IcePileFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_17007 = register("melon_pile", new MelonPileFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_17106 = register("pumpkin_pile", new PumpkinPileFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<BushFeatureConfig> field_13519 = register("bush", new BushFeature(BushFeatureConfig::deserialize));
	public static final Feature<DiskFeatureConfig> field_13509 = register("disk", new DiskFeature(DiskFeatureConfig::deserialize));
	public static final Feature<DoublePlantFeatureConfig> field_13576 = register("double_plant", new DoublePlantFeature(DoublePlantFeatureConfig::deserialize));
	public static final Feature<NetherSpringFeatureConfig> field_13563 = register("nether_spring", new NetherSpringFeature(NetherSpringFeatureConfig::deserialize));
	public static final Feature<IcePatchFeatureConfig> field_13551 = register("ice_patch", new IcePatchFeature(IcePatchFeatureConfig::deserialize));
	public static final Feature<LakeFeatureConfig> field_13573 = register("lake", new LakeFeature(LakeFeatureConfig::deserialize));
	public static final Feature<OreFeatureConfig> field_13517 = register("ore", new OreFeature(OreFeatureConfig::deserialize));
	public static final Feature<RandomRandomFeatureConfig> field_13512 = register(
		"random_random_selector", new RandomRandomFeature(RandomRandomFeatureConfig::deserialize)
	);
	public static final Feature<RandomFeatureConfig> field_13593 = register("random_selector", new RandomFeature(RandomFeatureConfig::deserialize));
	public static final Feature<SimpleRandomFeatureConfig> field_13555 = register(
		"simple_random_selector", new SimpleRandomFeature(SimpleRandomFeatureConfig::deserialize)
	);
	public static final Feature<RandomBooleanFeatureConfig> field_13550 = register(
		"random_boolean_selector", new RandomBooleanFeature(RandomBooleanFeatureConfig::deserialize)
	);
	public static final Feature<EmeraldOreFeatureConfig> field_13594 = register("emerald_ore", new EmeraldOreFeature(EmeraldOreFeatureConfig::deserialize));
	public static final Feature<SpringFeatureConfig> field_13513 = register("spring_feature", new SpringFeature(SpringFeatureConfig::deserialize));
	public static final Feature<EndSpikeFeatureConfig> field_13522 = register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13574 = register("end_island", new EndIslandFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13552 = register("chorus_plant", new ChorusPlantFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<EndGatewayFeatureConfig> field_13564 = register("end_gateway", new EndGatewayFeature(EndGatewayFeatureConfig::deserialize));
	public static final Feature<SeagrassFeatureConfig> field_13567 = register("seagrass", new SeagrassFeature(SeagrassFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13535 = register("kelp", new KelpFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13525 = register("coral_tree", new CoralTreeFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13585 = register("coral_mushroom", new CoralMushroomFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_13546 = register("coral_claw", new CoralClawFeature(DefaultFeatureConfig::deserialize));
	public static final Feature<SeaPickleFeatureConfig> field_13575 = register("sea_pickle", new SeaPickleFeature(SeaPickleFeatureConfig::deserialize));
	public static final Feature<SimpleBlockFeatureConfig> field_13518 = register("simple_block", new SimpleBlockFeature(SimpleBlockFeatureConfig::deserialize));
	public static final Feature<ProbabilityConfig> field_13540 = register("bamboo", new BambooFeature(ProbabilityConfig::deserialize));
	public static final Feature<DecoratedFeatureConfig> field_13572 = register("decorated", new DecoratedFeature(DecoratedFeatureConfig::deserialize));
	public static final Feature<DecoratedFeatureConfig> field_13561 = register("decorated_flower", new DecoratedFlowerFeature(DecoratedFeatureConfig::deserialize));
	public static final Feature<DefaultFeatureConfig> field_17004 = register(
		"sweet_berry_bush",
		new WildCropFeature(DefaultFeatureConfig::deserialize, Blocks.field_16999.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3)))
	);
	public static final Feature<FillLayerFeatureConfig> field_19201 = register("fill_layer", new FillLayerFeature(FillLayerFeatureConfig::deserialize));
	public static final BonusChestFeature BONUS_CHEST = register("bonus_chest", new BonusChestFeature(DefaultFeatureConfig::deserialize));
	public static final BiMap<String, StructureFeature<?>> STRUCTURES = SystemUtil.consume(HashBiMap.create(), hashBiMap -> {
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

	private static <C extends FeatureConfig, F extends Feature<C>> F register(String string, F feature) {
		return Registry.register(Registry.FEATURE, string, feature);
	}

	public Feature(Function<Dynamic<?>, ? extends FC> function) {
		this.configDeserializer = function;
		this.emitNeighborBlockUpdates = false;
	}

	public Feature(Function<Dynamic<?>, ? extends FC> function, boolean bl) {
		this.configDeserializer = function;
		this.emitNeighborBlockUpdates = bl;
	}

	public FC deserializeConfig(Dynamic<?> dynamic) {
		return (FC)this.configDeserializer.apply(dynamic);
	}

	protected void setBlockState(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
		if (this.emitNeighborBlockUpdates) {
			modifiableWorld.setBlockState(blockPos, blockState, 3);
		} else {
			modifiableWorld.setBlockState(blockPos, blockState, 2);
		}
	}

	public abstract boolean generate(
		IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, FC featureConfig
	);

	public List<Biome.SpawnEntry> getMonsterSpawns() {
		return Collections.emptyList();
	}

	public List<Biome.SpawnEntry> getCreatureSpawns() {
		return Collections.emptyList();
	}
}
