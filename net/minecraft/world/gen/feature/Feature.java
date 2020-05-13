/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.AbstractPileFeature;
import net.minecraft.world.gen.feature.BambooFeature;
import net.minecraft.world.gen.feature.BasaltColumnsFeature;
import net.minecraft.world.gen.feature.BasaltColumnsFeatureConfig;
import net.minecraft.world.gen.feature.BasaltPillarFeature;
import net.minecraft.world.gen.feature.BastionRemnantFeature;
import net.minecraft.world.gen.feature.BastionRemnantFeatureConfig;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.BlueIceFeature;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.feature.BuriedTreasureFeature;
import net.minecraft.world.gen.feature.BuriedTreasureFeatureConfig;
import net.minecraft.world.gen.feature.ChorusPlantFeature;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.CoralClawFeature;
import net.minecraft.world.gen.feature.CoralMushroomFeature;
import net.minecraft.world.gen.feature.CoralTreeFeature;
import net.minecraft.world.gen.feature.DecoratedFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.DecoratedFlowerFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFlowerFeature;
import net.minecraft.world.gen.feature.DeltaFeature;
import net.minecraft.world.gen.feature.DeltaFeatureConfig;
import net.minecraft.world.gen.feature.DesertPyramidFeature;
import net.minecraft.world.gen.feature.DesertWellFeature;
import net.minecraft.world.gen.feature.DiskFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.DungeonFeature;
import net.minecraft.world.gen.feature.EmeraldOreFeature;
import net.minecraft.world.gen.feature.EmeraldOreFeatureConfig;
import net.minecraft.world.gen.feature.EndCityFeature;
import net.minecraft.world.gen.feature.EndGatewayFeature;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.EndIslandFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FillLayerFeature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;
import net.minecraft.world.gen.feature.ForestRockFeature;
import net.minecraft.world.gen.feature.FossilFeature;
import net.minecraft.world.gen.feature.FreezeTopLayerFeature;
import net.minecraft.world.gen.feature.GlowstoneBlobFeature;
import net.minecraft.world.gen.feature.HugeBrownMushroomFeature;
import net.minecraft.world.gen.feature.HugeFungusFeature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.gen.feature.HugeRedMushroomFeature;
import net.minecraft.world.gen.feature.IcePatchFeature;
import net.minecraft.world.gen.feature.IcePatchFeatureConfig;
import net.minecraft.world.gen.feature.IceSpikeFeature;
import net.minecraft.world.gen.feature.IcebergFeature;
import net.minecraft.world.gen.feature.IglooFeature;
import net.minecraft.world.gen.feature.JungleTempleFeature;
import net.minecraft.world.gen.feature.KelpFeature;
import net.minecraft.world.gen.feature.LakeFeature;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.NetherForestVegetationFeature;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.NetherFossilFeature;
import net.minecraft.world.gen.feature.NetherrackReplaceBlobsFeature;
import net.minecraft.world.gen.feature.NetherrackReplaceBlobsFeatureConfig;
import net.minecraft.world.gen.feature.NoOpFeature;
import net.minecraft.world.gen.feature.NoSurfaceOreFeature;
import net.minecraft.world.gen.feature.OceanMonumentFeature;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PillagerOutpostFeature;
import net.minecraft.world.gen.feature.RandomBooleanFeature;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeature;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.RandomRandomFeature;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
import net.minecraft.world.gen.feature.RuinedPortalFeature;
import net.minecraft.world.gen.feature.RuinedPortalFeatureConfig;
import net.minecraft.world.gen.feature.SeaPickleFeature;
import net.minecraft.world.gen.feature.SeaPickleFeatureConfig;
import net.minecraft.world.gen.feature.SeagrassFeature;
import net.minecraft.world.gen.feature.SeagrassFeatureConfig;
import net.minecraft.world.gen.feature.ShipwreckFeature;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.gen.feature.SimpleBlockFeature;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.SimpleRandomFeature;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.SpringFeature;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.world.gen.feature.StrongholdFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;
import net.minecraft.world.gen.feature.SwampHutFeature;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.TwistingVinesFeature;
import net.minecraft.world.gen.feature.VillageFeature;
import net.minecraft.world.gen.feature.VinesFeature;
import net.minecraft.world.gen.feature.VoidStartPlatformFeature;
import net.minecraft.world.gen.feature.WeepingVinesFeature;
import net.minecraft.world.gen.feature.WoodlandMansionFeature;

public abstract class Feature<FC extends FeatureConfig> {
    public static final StructureFeature<DefaultFeatureConfig> PILLAGER_OUTPOST = Feature.register("pillager_outpost", new PillagerOutpostFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<MineshaftFeatureConfig> MINESHAFT = Feature.register("mineshaft", new MineshaftFeature((Function<Dynamic<?>, ? extends MineshaftFeatureConfig>)((Function<Dynamic<?>, MineshaftFeatureConfig>)MineshaftFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> WOODLAND_MANSION = Feature.register("woodland_mansion", new WoodlandMansionFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> JUNGLE_TEMPLE = Feature.register("jungle_temple", new JungleTempleFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> DESERT_PYRAMID = Feature.register("desert_pyramid", new DesertPyramidFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> IGLOO = Feature.register("igloo", new IglooFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<RuinedPortalFeatureConfig> RUINED_PORTAL = Feature.register("ruined_portal", new RuinedPortalFeature((Function<Dynamic<?>, ? extends RuinedPortalFeatureConfig>)((Function<Dynamic<?>, RuinedPortalFeatureConfig>)RuinedPortalFeatureConfig::deserialize)));
    public static final StructureFeature<ShipwreckFeatureConfig> SHIPWRECK = Feature.register("shipwreck", new ShipwreckFeature((Function<Dynamic<?>, ? extends ShipwreckFeatureConfig>)((Function<Dynamic<?>, ShipwreckFeatureConfig>)ShipwreckFeatureConfig::deserialize)));
    public static final SwampHutFeature SWAMP_HUT = Feature.register("swamp_hut", new SwampHutFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> STRONGHOLD = Feature.register("stronghold", new StrongholdFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> OCEAN_MONUMENT = Feature.register("ocean_monument", new OceanMonumentFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<OceanRuinFeatureConfig> OCEAN_RUIN = Feature.register("ocean_ruin", new OceanRuinFeature((Function<Dynamic<?>, ? extends OceanRuinFeatureConfig>)((Function<Dynamic<?>, OceanRuinFeatureConfig>)OceanRuinFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> NETHER_BRIDGE = Feature.register("nether_bridge", new NetherFortressFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> END_CITY = Feature.register("end_city", new EndCityFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<BuriedTreasureFeatureConfig> BURIED_TREASURE = Feature.register("buried_treasure", new BuriedTreasureFeature((Function<Dynamic<?>, ? extends BuriedTreasureFeatureConfig>)((Function<Dynamic<?>, BuriedTreasureFeatureConfig>)BuriedTreasureFeatureConfig::deserialize)));
    public static final StructureFeature<StructurePoolFeatureConfig> VILLAGE = Feature.register("village", new VillageFeature((Function<Dynamic<?>, ? extends StructurePoolFeatureConfig>)((Function<Dynamic<?>, StructurePoolFeatureConfig>)StructurePoolFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> NETHER_FOSSIL = Feature.register("nether_fossil", new NetherFossilFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<BastionRemnantFeatureConfig> BASTION_REMNANT = Feature.register("bastion_remnant", new BastionRemnantFeature((Function<Dynamic<?>, ? extends BastionRemnantFeatureConfig>)((Function<Dynamic<?>, BastionRemnantFeatureConfig>)BastionRemnantFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> NO_OP = Feature.register("no_op", new NoOpFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<TreeFeatureConfig> TREE = Feature.register("tree", new TreeFeature((Function<Dynamic<?>, ? extends TreeFeatureConfig>)((Function<Dynamic<?>, TreeFeatureConfig>)TreeFeatureConfig::deserialize)));
    public static final FlowerFeature<RandomPatchFeatureConfig> FLOWER = Feature.register("flower", new DefaultFlowerFeature((Function<Dynamic<?>, ? extends RandomPatchFeatureConfig>)((Function<Dynamic<?>, RandomPatchFeatureConfig>)RandomPatchFeatureConfig::deserialize)));
    public static final Feature<RandomPatchFeatureConfig> RANDOM_PATCH = Feature.register("random_patch", new RandomPatchFeature((Function<Dynamic<?>, ? extends RandomPatchFeatureConfig>)((Function<Dynamic<?>, RandomPatchFeatureConfig>)RandomPatchFeatureConfig::deserialize)));
    public static final Feature<BlockPileFeatureConfig> BLOCK_PILE = Feature.register("block_pile", new AbstractPileFeature((Function<Dynamic<?>, ? extends BlockPileFeatureConfig>)((Function<Dynamic<?>, BlockPileFeatureConfig>)BlockPileFeatureConfig::deserialize)));
    public static final Feature<SpringFeatureConfig> SPRING_FEATURE = Feature.register("spring_feature", new SpringFeature((Function<Dynamic<?>, ? extends SpringFeatureConfig>)((Function<Dynamic<?>, SpringFeatureConfig>)SpringFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> CHORUS_PLANT = Feature.register("chorus_plant", new ChorusPlantFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<EmeraldOreFeatureConfig> EMERALD_ORE = Feature.register("emerald_ore", new EmeraldOreFeature((Function<Dynamic<?>, ? extends EmeraldOreFeatureConfig>)((Function<Dynamic<?>, EmeraldOreFeatureConfig>)EmeraldOreFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> VOID_START_PLATFORM = Feature.register("void_start_platform", new VoidStartPlatformFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> DESERT_WELL = Feature.register("desert_well", new DesertWellFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> FOSSIL = Feature.register("fossil", new FossilFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<HugeMushroomFeatureConfig> HUGE_RED_MUSHROOM = Feature.register("huge_red_mushroom", new HugeRedMushroomFeature((Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig>)((Function<Dynamic<?>, HugeMushroomFeatureConfig>)HugeMushroomFeatureConfig::deserialize)));
    public static final Feature<HugeMushroomFeatureConfig> HUGE_BROWN_MUSHROOM = Feature.register("huge_brown_mushroom", new HugeBrownMushroomFeature((Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig>)((Function<Dynamic<?>, HugeMushroomFeatureConfig>)HugeMushroomFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> ICE_SPIKE = Feature.register("ice_spike", new IceSpikeFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> GLOWSTONE_BLOB = Feature.register("glowstone_blob", new GlowstoneBlobFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> FREEZE_TOP_LAYER = Feature.register("freeze_top_layer", new FreezeTopLayerFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> VINES = Feature.register("vines", new VinesFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> MONSTER_ROOM = Feature.register("monster_room", new DungeonFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> BLUE_ICE = Feature.register("blue_ice", new BlueIceFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<SingleStateFeatureConfig> ICEBERG = Feature.register("iceberg", new IcebergFeature((Function<Dynamic<?>, ? extends SingleStateFeatureConfig>)((Function<Dynamic<?>, SingleStateFeatureConfig>)SingleStateFeatureConfig::deserialize)));
    public static final Feature<BoulderFeatureConfig> FOREST_ROCK = Feature.register("forest_rock", new ForestRockFeature((Function<Dynamic<?>, ? extends BoulderFeatureConfig>)((Function<Dynamic<?>, BoulderFeatureConfig>)BoulderFeatureConfig::deserialize)));
    public static final Feature<DiskFeatureConfig> DISK = Feature.register("disk", new DiskFeature((Function<Dynamic<?>, ? extends DiskFeatureConfig>)((Function<Dynamic<?>, DiskFeatureConfig>)DiskFeatureConfig::deserialize)));
    public static final Feature<IcePatchFeatureConfig> ICE_PATCH = Feature.register("ice_patch", new IcePatchFeature((Function<Dynamic<?>, ? extends IcePatchFeatureConfig>)((Function<Dynamic<?>, IcePatchFeatureConfig>)IcePatchFeatureConfig::deserialize)));
    public static final Feature<SingleStateFeatureConfig> LAKE = Feature.register("lake", new LakeFeature((Function<Dynamic<?>, ? extends SingleStateFeatureConfig>)((Function<Dynamic<?>, SingleStateFeatureConfig>)SingleStateFeatureConfig::deserialize)));
    public static final Feature<OreFeatureConfig> ORE = Feature.register("ore", new OreFeature((Function<Dynamic<?>, ? extends OreFeatureConfig>)((Function<Dynamic<?>, OreFeatureConfig>)OreFeatureConfig::deserialize)));
    public static final Feature<EndSpikeFeatureConfig> END_SPIKE = Feature.register("end_spike", new EndSpikeFeature((Function<Dynamic<?>, ? extends EndSpikeFeatureConfig>)((Function<Dynamic<?>, EndSpikeFeatureConfig>)EndSpikeFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> END_ISLAND = Feature.register("end_island", new EndIslandFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<EndGatewayFeatureConfig> END_GATEWAY = Feature.register("end_gateway", new EndGatewayFeature((Function<Dynamic<?>, ? extends EndGatewayFeatureConfig>)((Function<Dynamic<?>, EndGatewayFeatureConfig>)EndGatewayFeatureConfig::deserialize)));
    public static final Feature<SeagrassFeatureConfig> SEAGRASS = Feature.register("seagrass", new SeagrassFeature((Function<Dynamic<?>, ? extends SeagrassFeatureConfig>)((Function<Dynamic<?>, SeagrassFeatureConfig>)SeagrassFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> KELP = Feature.register("kelp", new KelpFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> CORAL_TREE = Feature.register("coral_tree", new CoralTreeFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> CORAL_MUSHROOM = Feature.register("coral_mushroom", new CoralMushroomFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> CORAL_CLAW = Feature.register("coral_claw", new CoralClawFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<SeaPickleFeatureConfig> SEA_PICKLE = Feature.register("sea_pickle", new SeaPickleFeature((Function<Dynamic<?>, ? extends SeaPickleFeatureConfig>)((Function<Dynamic<?>, SeaPickleFeatureConfig>)SeaPickleFeatureConfig::deserialize)));
    public static final Feature<SimpleBlockFeatureConfig> SIMPLE_BLOCK = Feature.register("simple_block", new SimpleBlockFeature((Function<Dynamic<?>, ? extends SimpleBlockFeatureConfig>)((Function<Dynamic<?>, SimpleBlockFeatureConfig>)SimpleBlockFeatureConfig::deserialize)));
    public static final Feature<ProbabilityConfig> BAMBOO = Feature.register("bamboo", new BambooFeature((Function<Dynamic<?>, ? extends ProbabilityConfig>)((Function<Dynamic<?>, ProbabilityConfig>)ProbabilityConfig::deserialize)));
    public static final Feature<HugeFungusFeatureConfig> HUGE_FUNGUS = Feature.register("huge_fungus", new HugeFungusFeature((Function<Dynamic<?>, ? extends HugeFungusFeatureConfig>)((Function<Dynamic<?>, HugeFungusFeatureConfig>)HugeFungusFeatureConfig::deserialize)));
    public static final Feature<BlockPileFeatureConfig> NETHER_FOREST_VEGETATION = Feature.register("nether_forest_vegetation", new NetherForestVegetationFeature((Function<Dynamic<?>, ? extends BlockPileFeatureConfig>)((Function<Dynamic<?>, BlockPileFeatureConfig>)BlockPileFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> WEEPING_VINES = Feature.register("weeping_vines", new WeepingVinesFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> TWISTING_VINES = Feature.register("twisting_vines", new TwistingVinesFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<BasaltColumnsFeatureConfig> BASALT_COLUMNS = Feature.register("basalt_columns", new BasaltColumnsFeature((Function<Dynamic<?>, ? extends BasaltColumnsFeatureConfig>)((Function<Dynamic<?>, BasaltColumnsFeatureConfig>)BasaltColumnsFeatureConfig::deserialize)));
    public static final Feature<DeltaFeatureConfig> DELTA_FEATURE = Feature.register("delta_feature", new DeltaFeature((Function<Dynamic<?>, ? extends DeltaFeatureConfig>)((Function<Dynamic<?>, DeltaFeatureConfig>)DeltaFeatureConfig::deserialize)));
    public static final Feature<NetherrackReplaceBlobsFeatureConfig> NETHERRACK_REPLACE_BLOBS = Feature.register("netherrack_replace_blobs", new NetherrackReplaceBlobsFeature((Function<Dynamic<?>, ? extends NetherrackReplaceBlobsFeatureConfig>)((Function<Dynamic<?>, NetherrackReplaceBlobsFeatureConfig>)NetherrackReplaceBlobsFeatureConfig::deserialize)));
    public static final Feature<FillLayerFeatureConfig> FILL_LAYER = Feature.register("fill_layer", new FillLayerFeature((Function<Dynamic<?>, ? extends FillLayerFeatureConfig>)((Function<Dynamic<?>, FillLayerFeatureConfig>)FillLayerFeatureConfig::deserialize)));
    public static final BonusChestFeature BONUS_CHEST = Feature.register("bonus_chest", new BonusChestFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> BASALT_PILLAR = Feature.register("basalt_pillar", new BasaltPillarFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<OreFeatureConfig> NO_SURFACE_ORE = Feature.register("no_surface_ore", new NoSurfaceOreFeature((Function<Dynamic<?>, ? extends OreFeatureConfig>)((Function<Dynamic<?>, OreFeatureConfig>)OreFeatureConfig::deserialize)));
    public static final Feature<RandomRandomFeatureConfig> RANDOM_RANDOM_SELECTOR = Feature.register("random_random_selector", new RandomRandomFeature((Function<Dynamic<?>, ? extends RandomRandomFeatureConfig>)((Function<Dynamic<?>, RandomRandomFeatureConfig>)RandomRandomFeatureConfig::deserialize)));
    public static final Feature<RandomFeatureConfig> RANDOM_SELECTOR = Feature.register("random_selector", new RandomFeature((Function<Dynamic<?>, ? extends RandomFeatureConfig>)((Function<Dynamic<?>, RandomFeatureConfig>)RandomFeatureConfig::deserialize)));
    public static final Feature<SimpleRandomFeatureConfig> SIMPLE_RANDOM_SELECTOR = Feature.register("simple_random_selector", new SimpleRandomFeature((Function<Dynamic<?>, ? extends SimpleRandomFeatureConfig>)((Function<Dynamic<?>, SimpleRandomFeatureConfig>)SimpleRandomFeatureConfig::deserialize)));
    public static final Feature<RandomBooleanFeatureConfig> RANDOM_BOOLEAN_SELECTOR = Feature.register("random_boolean_selector", new RandomBooleanFeature((Function<Dynamic<?>, ? extends RandomBooleanFeatureConfig>)((Function<Dynamic<?>, RandomBooleanFeatureConfig>)RandomBooleanFeatureConfig::deserialize)));
    public static final Feature<DecoratedFeatureConfig> DECORATED = Feature.register("decorated", new DecoratedFeature((Function<Dynamic<?>, ? extends DecoratedFeatureConfig>)((Function<Dynamic<?>, DecoratedFeatureConfig>)DecoratedFeatureConfig::deserialize)));
    public static final Feature<DecoratedFeatureConfig> DECORATED_FLOWER = Feature.register("decorated_flower", new DecoratedFlowerFeature((Function<Dynamic<?>, ? extends DecoratedFeatureConfig>)((Function<Dynamic<?>, DecoratedFeatureConfig>)DecoratedFeatureConfig::deserialize)));
    public static final BiMap<String, StructureFeature<?>> STRUCTURES = Util.make(HashBiMap.create(), map -> {
        map.put("Pillager_Outpost".toLowerCase(Locale.ROOT), PILLAGER_OUTPOST);
        map.put("Mineshaft".toLowerCase(Locale.ROOT), MINESHAFT);
        map.put("Mansion".toLowerCase(Locale.ROOT), WOODLAND_MANSION);
        map.put("Jungle_Pyramid".toLowerCase(Locale.ROOT), JUNGLE_TEMPLE);
        map.put("Desert_Pyramid".toLowerCase(Locale.ROOT), DESERT_PYRAMID);
        map.put("Igloo".toLowerCase(Locale.ROOT), IGLOO);
        map.put("Ruined_Portal".toLowerCase(Locale.ROOT), RUINED_PORTAL);
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
        map.put("Bastion_Remnant".toLowerCase(Locale.ROOT), BASTION_REMNANT);
    });
    public static final List<StructureFeature<?>> JIGSAW_STRUCTURES = ImmutableList.of(PILLAGER_OUTPOST, VILLAGE, NETHER_FOSSIL);
    private final Function<Dynamic<?>, ? extends FC> configDeserializer;

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return (F)Registry.register(Registry.FEATURE, name, feature);
    }

    public Feature(Function<Dynamic<?>, ? extends FC> configDeserializer) {
        this.configDeserializer = configDeserializer;
    }

    public ConfiguredFeature<FC, ?> configure(FC config) {
        return new ConfiguredFeature<FC, Feature>(this, config);
    }

    public FC deserializeConfig(Dynamic<?> dynamic) {
        return (FC)((FeatureConfig)this.configDeserializer.apply(dynamic));
    }

    protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, 3);
    }

    public abstract boolean generate(ServerWorldAccess var1, StructureAccessor var2, ChunkGenerator var3, Random var4, BlockPos var5, FC var6);

    public List<Biome.SpawnEntry> getMonsterSpawns() {
        return Collections.emptyList();
    }

    public List<Biome.SpawnEntry> getCreatureSpawns() {
        return Collections.emptyList();
    }

    protected static boolean isStone(Block block) {
        return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
    }

    public static boolean isDirt(Block block) {
        return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.PODZOL || block == Blocks.COARSE_DIRT || block == Blocks.MYCELIUM;
    }

    public static boolean method_27368(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, blockState -> Feature.isDirt(blockState.getBlock()));
    }

    public static boolean method_27370(TestableWorld testableWorld, BlockPos blockPos) {
        return testableWorld.testBlockState(blockPos, AbstractBlock.AbstractBlockState::isAir);
    }
}

