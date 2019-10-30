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
import net.minecraft.world.gen.feature.AbstractPileFeature;
import net.minecraft.world.gen.feature.AcaciaTreeFeature;
import net.minecraft.world.gen.feature.BambooFeature;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.BlueIceFeature;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.feature.BoulderFeatureConfig;
import net.minecraft.world.gen.feature.BranchedTreeFeatureConfig;
import net.minecraft.world.gen.feature.BuriedTreasureFeature;
import net.minecraft.world.gen.feature.BuriedTreasureFeatureConfig;
import net.minecraft.world.gen.feature.BushFeatureConfig;
import net.minecraft.world.gen.feature.ChorusPlantFeature;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.CoralClawFeature;
import net.minecraft.world.gen.feature.CoralMushroomFeature;
import net.minecraft.world.gen.feature.CoralTreeFeature;
import net.minecraft.world.gen.feature.DarkOakTreeFeature;
import net.minecraft.world.gen.feature.DecoratedFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.DecoratedFlowerFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.DefaultFlowerFeature;
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
import net.minecraft.world.gen.feature.FlowerFeatureConfig;
import net.minecraft.world.gen.feature.ForestRockFeature;
import net.minecraft.world.gen.feature.FossilFeature;
import net.minecraft.world.gen.feature.FreezeTopLayerFeature;
import net.minecraft.world.gen.feature.GlowstoneBlobFeature;
import net.minecraft.world.gen.feature.HugeBrownMushroomFeature;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.gen.feature.HugeRedMushroomFeature;
import net.minecraft.world.gen.feature.IcePatchFeature;
import net.minecraft.world.gen.feature.IcePatchFeatureConfig;
import net.minecraft.world.gen.feature.IceSpikeFeature;
import net.minecraft.world.gen.feature.IcebergFeature;
import net.minecraft.world.gen.feature.IglooFeature;
import net.minecraft.world.gen.feature.JungleGroundBushFeature;
import net.minecraft.world.gen.feature.JungleTempleFeature;
import net.minecraft.world.gen.feature.KelpFeature;
import net.minecraft.world.gen.feature.LakeFeature;
import net.minecraft.world.gen.feature.LargeOakTreeFeature;
import net.minecraft.world.gen.feature.MegaJungleTreeFeature;
import net.minecraft.world.gen.feature.MegaPineTreeFeature;
import net.minecraft.world.gen.feature.MegaTreeFeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeature;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.NetherFortressFeature;
import net.minecraft.world.gen.feature.OakTreeFeature;
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
import net.minecraft.world.gen.feature.RandomRandomFeature;
import net.minecraft.world.gen.feature.RandomRandomFeatureConfig;
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
import net.minecraft.world.gen.feature.SpringFeature;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.world.gen.feature.StrongholdFeature;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.SwampHutFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.VillageFeature;
import net.minecraft.world.gen.feature.VillageFeatureConfig;
import net.minecraft.world.gen.feature.VinesFeature;
import net.minecraft.world.gen.feature.VoidStartPlatformFeature;
import net.minecraft.world.gen.feature.WoodlandMansionFeature;

public abstract class Feature<FC extends FeatureConfig> {
    public static final StructureFeature<DefaultFeatureConfig> PILLAGER_OUTPOST = Feature.register("pillager_outpost", new PillagerOutpostFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<MineshaftFeatureConfig> MINESHAFT = Feature.register("mineshaft", new MineshaftFeature((Function<Dynamic<?>, ? extends MineshaftFeatureConfig>)((Function<Dynamic<?>, MineshaftFeatureConfig>)MineshaftFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> WOODLAND_MANSION = Feature.register("woodland_mansion", new WoodlandMansionFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> JUNGLE_TEMPLE = Feature.register("jungle_temple", new JungleTempleFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> DESERT_PYRAMID = Feature.register("desert_pyramid", new DesertPyramidFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> IGLOO = Feature.register("igloo", new IglooFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<ShipwreckFeatureConfig> SHIPWRECK = Feature.register("shipwreck", new ShipwreckFeature((Function<Dynamic<?>, ? extends ShipwreckFeatureConfig>)((Function<Dynamic<?>, ShipwreckFeatureConfig>)ShipwreckFeatureConfig::deserialize)));
    public static final SwampHutFeature SWAMP_HUT = Feature.register("swamp_hut", new SwampHutFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> STRONGHOLD = Feature.register("stronghold", new StrongholdFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> OCEAN_MONUMENT = Feature.register("ocean_monument", new OceanMonumentFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<OceanRuinFeatureConfig> OCEAN_RUIN = Feature.register("ocean_ruin", new OceanRuinFeature((Function<Dynamic<?>, ? extends OceanRuinFeatureConfig>)((Function<Dynamic<?>, OceanRuinFeatureConfig>)OceanRuinFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> NETHER_BRIDGE = Feature.register("nether_bridge", new NetherFortressFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<DefaultFeatureConfig> END_CITY = Feature.register("end_city", new EndCityFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final StructureFeature<BuriedTreasureFeatureConfig> BURIED_TREASURE = Feature.register("buried_treasure", new BuriedTreasureFeature((Function<Dynamic<?>, ? extends BuriedTreasureFeatureConfig>)((Function<Dynamic<?>, BuriedTreasureFeatureConfig>)BuriedTreasureFeatureConfig::deserialize)));
    public static final StructureFeature<VillageFeatureConfig> VILLAGE = Feature.register("village", new VillageFeature((Function<Dynamic<?>, ? extends VillageFeatureConfig>)((Function<Dynamic<?>, VillageFeatureConfig>)VillageFeatureConfig::deserialize)));
    public static final Feature<BranchedTreeFeatureConfig> NORMAL_TREE = Feature.register("normal_tree", new OakTreeFeature((Function<Dynamic<?>, ? extends BranchedTreeFeatureConfig>)((Function<Dynamic<?>, BranchedTreeFeatureConfig>)BranchedTreeFeatureConfig::method_23426)));
    public static final Feature<BranchedTreeFeatureConfig> ACACIA_TREE = Feature.register("acacia_tree", new AcaciaTreeFeature((Function<Dynamic<?>, ? extends BranchedTreeFeatureConfig>)((Function<Dynamic<?>, BranchedTreeFeatureConfig>)BranchedTreeFeatureConfig::method_23426)));
    public static final Feature<BranchedTreeFeatureConfig> FANCY_TREE = Feature.register("fancy_tree", new LargeOakTreeFeature((Function<Dynamic<?>, ? extends BranchedTreeFeatureConfig>)((Function<Dynamic<?>, BranchedTreeFeatureConfig>)BranchedTreeFeatureConfig::method_23426)));
    public static final Feature<TreeFeatureConfig> JUNGLE_GROUND_BUSH = Feature.register("jungle_ground_bush", new JungleGroundBushFeature((Function<Dynamic<?>, ? extends TreeFeatureConfig>)((Function<Dynamic<?>, TreeFeatureConfig>)TreeFeatureConfig::deserialize)));
    public static final Feature<MegaTreeFeatureConfig> DARK_OAK_TREE = Feature.register("dark_oak_tree", new DarkOakTreeFeature((Function<Dynamic<?>, ? extends MegaTreeFeatureConfig>)((Function<Dynamic<?>, MegaTreeFeatureConfig>)MegaTreeFeatureConfig::method_23408)));
    public static final Feature<MegaTreeFeatureConfig> MEGA_JUNGLE_TREE = Feature.register("mega_jungle_tree", new MegaJungleTreeFeature((Function<Dynamic<?>, ? extends MegaTreeFeatureConfig>)((Function<Dynamic<?>, MegaTreeFeatureConfig>)MegaTreeFeatureConfig::method_23408)));
    public static final Feature<MegaTreeFeatureConfig> MEGA_SPRUCE_TREE = Feature.register("mega_spruce_tree", new MegaPineTreeFeature((Function<Dynamic<?>, ? extends MegaTreeFeatureConfig>)((Function<Dynamic<?>, MegaTreeFeatureConfig>)MegaTreeFeatureConfig::method_23408)));
    public static final FlowerFeature<FlowerFeatureConfig> FLOWER = Feature.register("flower", new DefaultFlowerFeature((Function<Dynamic<?>, ? extends FlowerFeatureConfig>)((Function<Dynamic<?>, FlowerFeatureConfig>)FlowerFeatureConfig::method_23413)));
    public static final Feature<FlowerFeatureConfig> RANDOM_PATCH = Feature.register("random_patch", new RandomPatchFeature((Function<Dynamic<?>, ? extends FlowerFeatureConfig>)((Function<Dynamic<?>, FlowerFeatureConfig>)FlowerFeatureConfig::method_23413)));
    public static final Feature<BlockPileFeatureConfig> BLOCK_PILE = Feature.register("block_pile", new AbstractPileFeature((Function<Dynamic<?>, ? extends BlockPileFeatureConfig>)((Function<Dynamic<?>, BlockPileFeatureConfig>)BlockPileFeatureConfig::method_23406)));
    public static final Feature<SpringFeatureConfig> SPRING_FEATURE = Feature.register("spring_feature", new SpringFeature((Function<Dynamic<?>, ? extends SpringFeatureConfig>)((Function<Dynamic<?>, SpringFeatureConfig>)SpringFeatureConfig::method_23440)));
    public static final Feature<DefaultFeatureConfig> CHORUS_PLANT = Feature.register("chorus_plant", new ChorusPlantFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<EmeraldOreFeatureConfig> EMERALD_ORE = Feature.register("emerald_ore", new EmeraldOreFeature((Function<Dynamic<?>, ? extends EmeraldOreFeatureConfig>)((Function<Dynamic<?>, EmeraldOreFeatureConfig>)EmeraldOreFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> VOID_START_PLATFORM = Feature.register("void_start_platform", new VoidStartPlatformFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> DESERT_WELL = Feature.register("desert_well", new DesertWellFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> FOSSIL = Feature.register("fossil", new FossilFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<HugeMushroomFeatureConfig> HUGE_RED_MUSHROOM = Feature.register("huge_red_mushroom", new HugeRedMushroomFeature((Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig>)((Function<Dynamic<?>, HugeMushroomFeatureConfig>)HugeMushroomFeatureConfig::method_23407)));
    public static final Feature<HugeMushroomFeatureConfig> HUGE_BROWN_MUSHROOM = Feature.register("huge_brown_mushroom", new HugeBrownMushroomFeature((Function<Dynamic<?>, ? extends HugeMushroomFeatureConfig>)((Function<Dynamic<?>, HugeMushroomFeatureConfig>)HugeMushroomFeatureConfig::method_23407)));
    public static final Feature<DefaultFeatureConfig> ICE_SPIKE = Feature.register("ice_spike", new IceSpikeFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> GLOWSTONE_BLOB = Feature.register("glowstone_blob", new GlowstoneBlobFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> FREEZE_TOP_LAYER = Feature.register("freeze_top_layer", new FreezeTopLayerFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> VINES = Feature.register("vines", new VinesFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> MONSTER_ROOM = Feature.register("monster_room", new DungeonFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<DefaultFeatureConfig> BLUE_ICE = Feature.register("blue_ice", new BlueIceFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<BushFeatureConfig> ICEBERG = Feature.register("iceberg", new IcebergFeature((Function<Dynamic<?>, ? extends BushFeatureConfig>)((Function<Dynamic<?>, BushFeatureConfig>)BushFeatureConfig::deserialize)));
    public static final Feature<BoulderFeatureConfig> FOREST_ROCK = Feature.register("forest_rock", new ForestRockFeature((Function<Dynamic<?>, ? extends BoulderFeatureConfig>)((Function<Dynamic<?>, BoulderFeatureConfig>)BoulderFeatureConfig::deserialize)));
    public static final Feature<DiskFeatureConfig> DISK = Feature.register("disk", new DiskFeature((Function<Dynamic<?>, ? extends DiskFeatureConfig>)((Function<Dynamic<?>, DiskFeatureConfig>)DiskFeatureConfig::deserialize)));
    public static final Feature<IcePatchFeatureConfig> ICE_PATCH = Feature.register("ice_patch", new IcePatchFeature((Function<Dynamic<?>, ? extends IcePatchFeatureConfig>)((Function<Dynamic<?>, IcePatchFeatureConfig>)IcePatchFeatureConfig::deserialize)));
    public static final Feature<BushFeatureConfig> LAKE = Feature.register("lake", new LakeFeature((Function<Dynamic<?>, ? extends BushFeatureConfig>)((Function<Dynamic<?>, BushFeatureConfig>)BushFeatureConfig::deserialize)));
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
    public static final Feature<FillLayerFeatureConfig> FILL_LAYER = Feature.register("fill_layer", new FillLayerFeature((Function<Dynamic<?>, ? extends FillLayerFeatureConfig>)((Function<Dynamic<?>, FillLayerFeatureConfig>)FillLayerFeatureConfig::deserialize)));
    public static final BonusChestFeature BONUS_CHEST = Feature.register("bonus_chest", new BonusChestFeature((Function<Dynamic<?>, ? extends DefaultFeatureConfig>)((Function<Dynamic<?>, DefaultFeatureConfig>)DefaultFeatureConfig::deserialize)));
    public static final Feature<RandomRandomFeatureConfig> RANDOM_RANDOM_SELECTOR = Feature.register("random_random_selector", new RandomRandomFeature((Function<Dynamic<?>, ? extends RandomRandomFeatureConfig>)((Function<Dynamic<?>, RandomRandomFeatureConfig>)RandomRandomFeatureConfig::deserialize)));
    public static final Feature<RandomFeatureConfig> RANDOM_SELECTOR = Feature.register("random_selector", new RandomFeature((Function<Dynamic<?>, ? extends RandomFeatureConfig>)((Function<Dynamic<?>, RandomFeatureConfig>)RandomFeatureConfig::deserialize)));
    public static final Feature<SimpleRandomFeatureConfig> SIMPLE_RANDOM_SELECTOR = Feature.register("simple_random_selector", new SimpleRandomFeature((Function<Dynamic<?>, ? extends SimpleRandomFeatureConfig>)((Function<Dynamic<?>, SimpleRandomFeatureConfig>)SimpleRandomFeatureConfig::deserialize)));
    public static final Feature<RandomBooleanFeatureConfig> RANDOM_BOOLEAN_SELECTOR = Feature.register("random_boolean_selector", new RandomBooleanFeature((Function<Dynamic<?>, ? extends RandomBooleanFeatureConfig>)((Function<Dynamic<?>, RandomBooleanFeatureConfig>)RandomBooleanFeatureConfig::deserialize)));
    public static final Feature<DecoratedFeatureConfig> DECORATED = Feature.register("decorated", new DecoratedFeature((Function<Dynamic<?>, ? extends DecoratedFeatureConfig>)((Function<Dynamic<?>, DecoratedFeatureConfig>)DecoratedFeatureConfig::deserialize)));
    public static final Feature<DecoratedFeatureConfig> DECORATED_FLOWER = Feature.register("decorated_flower", new DecoratedFlowerFeature((Function<Dynamic<?>, ? extends DecoratedFeatureConfig>)((Function<Dynamic<?>, DecoratedFeatureConfig>)DecoratedFeatureConfig::deserialize)));
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

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String string, F feature) {
        return (F)Registry.register(Registry.FEATURE, string, feature);
    }

    public Feature(Function<Dynamic<?>, ? extends FC> function) {
        this.configDeserializer = function;
    }

    public ConfiguredFeature<FC, ?> configure(FC featureConfig) {
        return new ConfiguredFeature<FC, Feature>(this, featureConfig);
    }

    public FC deserializeConfig(Dynamic<?> dynamic) {
        return (FC)((FeatureConfig)this.configDeserializer.apply(dynamic));
    }

    protected void setBlockState(ModifiableWorld modifiableWorld, BlockPos blockPos, BlockState blockState) {
        modifiableWorld.setBlockState(blockPos, blockState, 3);
    }

    public abstract boolean generate(IWorld var1, ChunkGenerator<? extends ChunkGeneratorConfig> var2, Random var3, BlockPos var4, FC var5);

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

