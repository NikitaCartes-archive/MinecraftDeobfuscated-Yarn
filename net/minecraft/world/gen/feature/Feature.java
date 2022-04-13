/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BambooFeature;
import net.minecraft.world.gen.feature.BasaltColumnsFeature;
import net.minecraft.world.gen.feature.BasaltColumnsFeatureConfig;
import net.minecraft.world.gen.feature.BasaltPillarFeature;
import net.minecraft.world.gen.feature.BlockColumnFeature;
import net.minecraft.world.gen.feature.BlockColumnFeatureConfig;
import net.minecraft.world.gen.feature.BlockPileFeature;
import net.minecraft.world.gen.feature.BlockPileFeatureConfig;
import net.minecraft.world.gen.feature.BlueIceFeature;
import net.minecraft.world.gen.feature.BonusChestFeature;
import net.minecraft.world.gen.feature.ChorusPlantFeature;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.CoralClawFeature;
import net.minecraft.world.gen.feature.CoralMushroomFeature;
import net.minecraft.world.gen.feature.CoralTreeFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.DeltaFeature;
import net.minecraft.world.gen.feature.DeltaFeatureConfig;
import net.minecraft.world.gen.feature.DesertWellFeature;
import net.minecraft.world.gen.feature.DiskFeature;
import net.minecraft.world.gen.feature.DiskFeatureConfig;
import net.minecraft.world.gen.feature.DripstoneClusterFeature;
import net.minecraft.world.gen.feature.DripstoneClusterFeatureConfig;
import net.minecraft.world.gen.feature.DungeonFeature;
import net.minecraft.world.gen.feature.EmeraldOreFeature;
import net.minecraft.world.gen.feature.EmeraldOreFeatureConfig;
import net.minecraft.world.gen.feature.EndGatewayFeature;
import net.minecraft.world.gen.feature.EndGatewayFeatureConfig;
import net.minecraft.world.gen.feature.EndIslandFeature;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import net.minecraft.world.gen.feature.EndSpikeFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.FillLayerFeature;
import net.minecraft.world.gen.feature.FillLayerFeatureConfig;
import net.minecraft.world.gen.feature.ForestRockFeature;
import net.minecraft.world.gen.feature.FossilFeature;
import net.minecraft.world.gen.feature.FossilFeatureConfig;
import net.minecraft.world.gen.feature.FreezeTopLayerFeature;
import net.minecraft.world.gen.feature.GeodeFeature;
import net.minecraft.world.gen.feature.GeodeFeatureConfig;
import net.minecraft.world.gen.feature.GlowstoneBlobFeature;
import net.minecraft.world.gen.feature.HugeBrownMushroomFeature;
import net.minecraft.world.gen.feature.HugeFungusFeature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;
import net.minecraft.world.gen.feature.HugeMushroomFeatureConfig;
import net.minecraft.world.gen.feature.HugeRedMushroomFeature;
import net.minecraft.world.gen.feature.IceSpikeFeature;
import net.minecraft.world.gen.feature.IcebergFeature;
import net.minecraft.world.gen.feature.KelpFeature;
import net.minecraft.world.gen.feature.LakeFeature;
import net.minecraft.world.gen.feature.LargeDripstoneFeature;
import net.minecraft.world.gen.feature.LargeDripstoneFeatureConfig;
import net.minecraft.world.gen.feature.MultifaceGrowthFeature;
import net.minecraft.world.gen.feature.MultifaceGrowthFeatureConfig;
import net.minecraft.world.gen.feature.NetherForestVegetationFeature;
import net.minecraft.world.gen.feature.NetherForestVegetationFeatureConfig;
import net.minecraft.world.gen.feature.NoOpFeature;
import net.minecraft.world.gen.feature.OreFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.RandomBooleanFeature;
import net.minecraft.world.gen.feature.RandomBooleanFeatureConfig;
import net.minecraft.world.gen.feature.RandomFeature;
import net.minecraft.world.gen.feature.RandomFeatureConfig;
import net.minecraft.world.gen.feature.RandomPatchFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import net.minecraft.world.gen.feature.ReplaceBlobsFeature;
import net.minecraft.world.gen.feature.ReplaceBlobsFeatureConfig;
import net.minecraft.world.gen.feature.RootSystemFeature;
import net.minecraft.world.gen.feature.RootSystemFeatureConfig;
import net.minecraft.world.gen.feature.ScatteredOreFeature;
import net.minecraft.world.gen.feature.SculkPatchFeature;
import net.minecraft.world.gen.feature.SculkPatchFeatureConfig;
import net.minecraft.world.gen.feature.SeaPickleFeature;
import net.minecraft.world.gen.feature.SeagrassFeature;
import net.minecraft.world.gen.feature.SimpleBlockFeature;
import net.minecraft.world.gen.feature.SimpleBlockFeatureConfig;
import net.minecraft.world.gen.feature.SimpleRandomFeature;
import net.minecraft.world.gen.feature.SimpleRandomFeatureConfig;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;
import net.minecraft.world.gen.feature.SmallDripstoneFeature;
import net.minecraft.world.gen.feature.SmallDripstoneFeatureConfig;
import net.minecraft.world.gen.feature.SpringFeature;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.feature.TwistingVinesFeature;
import net.minecraft.world.gen.feature.TwistingVinesFeatureConfig;
import net.minecraft.world.gen.feature.UnderwaterMagmaFeature;
import net.minecraft.world.gen.feature.UnderwaterMagmaFeatureConfig;
import net.minecraft.world.gen.feature.VegetationPatchFeature;
import net.minecraft.world.gen.feature.VegetationPatchFeatureConfig;
import net.minecraft.world.gen.feature.VinesFeature;
import net.minecraft.world.gen.feature.VoidStartPlatformFeature;
import net.minecraft.world.gen.feature.WaterloggedVegetationPatchFeature;
import net.minecraft.world.gen.feature.WeepingVinesFeature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public abstract class Feature<FC extends FeatureConfig> {
    public static final Feature<DefaultFeatureConfig> NO_OP = Feature.register("no_op", new NoOpFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<TreeFeatureConfig> TREE = Feature.register("tree", new TreeFeature(TreeFeatureConfig.CODEC));
    public static final Feature<RandomPatchFeatureConfig> FLOWER = Feature.register("flower", new RandomPatchFeature(RandomPatchFeatureConfig.CODEC));
    public static final Feature<RandomPatchFeatureConfig> NO_BONEMEAL_FLOWER = Feature.register("no_bonemeal_flower", new RandomPatchFeature(RandomPatchFeatureConfig.CODEC));
    public static final Feature<RandomPatchFeatureConfig> RANDOM_PATCH = Feature.register("random_patch", new RandomPatchFeature(RandomPatchFeatureConfig.CODEC));
    public static final Feature<BlockPileFeatureConfig> BLOCK_PILE = Feature.register("block_pile", new BlockPileFeature(BlockPileFeatureConfig.CODEC));
    public static final Feature<SpringFeatureConfig> SPRING_FEATURE = Feature.register("spring_feature", new SpringFeature(SpringFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> CHORUS_PLANT = Feature.register("chorus_plant", new ChorusPlantFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<EmeraldOreFeatureConfig> REPLACE_SINGLE_BLOCK = Feature.register("replace_single_block", new EmeraldOreFeature(EmeraldOreFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> VOID_START_PLATFORM = Feature.register("void_start_platform", new VoidStartPlatformFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> DESERT_WELL = Feature.register("desert_well", new DesertWellFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<FossilFeatureConfig> FOSSIL = Feature.register("fossil", new FossilFeature(FossilFeatureConfig.CODEC));
    public static final Feature<HugeMushroomFeatureConfig> HUGE_RED_MUSHROOM = Feature.register("huge_red_mushroom", new HugeRedMushroomFeature(HugeMushroomFeatureConfig.CODEC));
    public static final Feature<HugeMushroomFeatureConfig> HUGE_BROWN_MUSHROOM = Feature.register("huge_brown_mushroom", new HugeBrownMushroomFeature(HugeMushroomFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> ICE_SPIKE = Feature.register("ice_spike", new IceSpikeFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> GLOWSTONE_BLOB = Feature.register("glowstone_blob", new GlowstoneBlobFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> FREEZE_TOP_LAYER = Feature.register("freeze_top_layer", new FreezeTopLayerFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> VINES = Feature.register("vines", new VinesFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<BlockColumnFeatureConfig> BLOCK_COLUMN = Feature.register("block_column", new BlockColumnFeature(BlockColumnFeatureConfig.CODEC));
    public static final Feature<VegetationPatchFeatureConfig> VEGETATION_PATCH = Feature.register("vegetation_patch", new VegetationPatchFeature(VegetationPatchFeatureConfig.CODEC));
    public static final Feature<VegetationPatchFeatureConfig> WATERLOGGED_VEGETATION_PATCH = Feature.register("waterlogged_vegetation_patch", new WaterloggedVegetationPatchFeature(VegetationPatchFeatureConfig.CODEC));
    public static final Feature<RootSystemFeatureConfig> ROOT_SYSTEM = Feature.register("root_system", new RootSystemFeature(RootSystemFeatureConfig.CODEC));
    public static final Feature<MultifaceGrowthFeatureConfig> MULTIFACE_GROWTH = Feature.register("multiface_growth", new MultifaceGrowthFeature(MultifaceGrowthFeatureConfig.CODEC));
    public static final Feature<UnderwaterMagmaFeatureConfig> UNDERWATER_MAGMA = Feature.register("underwater_magma", new UnderwaterMagmaFeature(UnderwaterMagmaFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> MONSTER_ROOM = Feature.register("monster_room", new DungeonFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> BLUE_ICE = Feature.register("blue_ice", new BlueIceFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<SingleStateFeatureConfig> ICEBERG = Feature.register("iceberg", new IcebergFeature(SingleStateFeatureConfig.CODEC));
    public static final Feature<SingleStateFeatureConfig> FOREST_ROCK = Feature.register("forest_rock", new ForestRockFeature(SingleStateFeatureConfig.CODEC));
    public static final Feature<DiskFeatureConfig> DISK = Feature.register("disk", new DiskFeature(DiskFeatureConfig.CODEC));
    public static final Feature<LakeFeature.Config> LAKE = Feature.register("lake", new LakeFeature(LakeFeature.Config.CODEC));
    public static final Feature<OreFeatureConfig> ORE = Feature.register("ore", new OreFeature(OreFeatureConfig.CODEC));
    public static final Feature<EndSpikeFeatureConfig> END_SPIKE = Feature.register("end_spike", new EndSpikeFeature(EndSpikeFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> END_ISLAND = Feature.register("end_island", new EndIslandFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<EndGatewayFeatureConfig> END_GATEWAY = Feature.register("end_gateway", new EndGatewayFeature(EndGatewayFeatureConfig.CODEC));
    public static final SeagrassFeature SEAGRASS = Feature.register("seagrass", new SeagrassFeature(ProbabilityConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> KELP = Feature.register("kelp", new KelpFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> CORAL_TREE = Feature.register("coral_tree", new CoralTreeFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> CORAL_MUSHROOM = Feature.register("coral_mushroom", new CoralMushroomFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> CORAL_CLAW = Feature.register("coral_claw", new CoralClawFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<CountConfig> SEA_PICKLE = Feature.register("sea_pickle", new SeaPickleFeature(CountConfig.CODEC));
    public static final Feature<SimpleBlockFeatureConfig> SIMPLE_BLOCK = Feature.register("simple_block", new SimpleBlockFeature(SimpleBlockFeatureConfig.CODEC));
    public static final Feature<ProbabilityConfig> BAMBOO = Feature.register("bamboo", new BambooFeature(ProbabilityConfig.CODEC));
    public static final Feature<HugeFungusFeatureConfig> HUGE_FUNGUS = Feature.register("huge_fungus", new HugeFungusFeature(HugeFungusFeatureConfig.CODEC));
    public static final Feature<NetherForestVegetationFeatureConfig> NETHER_FOREST_VEGETATION = Feature.register("nether_forest_vegetation", new NetherForestVegetationFeature(NetherForestVegetationFeatureConfig.VEGETATION_CODEC));
    public static final Feature<DefaultFeatureConfig> WEEPING_VINES = Feature.register("weeping_vines", new WeepingVinesFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<TwistingVinesFeatureConfig> TWISTING_VINES = Feature.register("twisting_vines", new TwistingVinesFeature(TwistingVinesFeatureConfig.CODEC));
    public static final Feature<BasaltColumnsFeatureConfig> BASALT_COLUMNS = Feature.register("basalt_columns", new BasaltColumnsFeature(BasaltColumnsFeatureConfig.CODEC));
    public static final Feature<DeltaFeatureConfig> DELTA_FEATURE = Feature.register("delta_feature", new DeltaFeature(DeltaFeatureConfig.CODEC));
    public static final Feature<ReplaceBlobsFeatureConfig> NETHERRACK_REPLACE_BLOBS = Feature.register("netherrack_replace_blobs", new ReplaceBlobsFeature(ReplaceBlobsFeatureConfig.CODEC));
    public static final Feature<FillLayerFeatureConfig> FILL_LAYER = Feature.register("fill_layer", new FillLayerFeature(FillLayerFeatureConfig.CODEC));
    public static final BonusChestFeature BONUS_CHEST = Feature.register("bonus_chest", new BonusChestFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<DefaultFeatureConfig> BASALT_PILLAR = Feature.register("basalt_pillar", new BasaltPillarFeature(DefaultFeatureConfig.CODEC));
    public static final Feature<OreFeatureConfig> SCATTERED_ORE = Feature.register("scattered_ore", new ScatteredOreFeature(OreFeatureConfig.CODEC));
    public static final Feature<RandomFeatureConfig> RANDOM_SELECTOR = Feature.register("random_selector", new RandomFeature(RandomFeatureConfig.CODEC));
    public static final Feature<SimpleRandomFeatureConfig> SIMPLE_RANDOM_SELECTOR = Feature.register("simple_random_selector", new SimpleRandomFeature(SimpleRandomFeatureConfig.CODEC));
    public static final Feature<RandomBooleanFeatureConfig> RANDOM_BOOLEAN_SELECTOR = Feature.register("random_boolean_selector", new RandomBooleanFeature(RandomBooleanFeatureConfig.CODEC));
    public static final Feature<GeodeFeatureConfig> GEODE = Feature.register("geode", new GeodeFeature(GeodeFeatureConfig.CODEC));
    public static final Feature<DripstoneClusterFeatureConfig> DRIPSTONE_CLUSTER = Feature.register("dripstone_cluster", new DripstoneClusterFeature(DripstoneClusterFeatureConfig.CODEC));
    public static final Feature<LargeDripstoneFeatureConfig> LARGE_DRIPSTONE = Feature.register("large_dripstone", new LargeDripstoneFeature(LargeDripstoneFeatureConfig.CODEC));
    public static final Feature<SmallDripstoneFeatureConfig> POINTED_DRIPSTONE = Feature.register("pointed_dripstone", new SmallDripstoneFeature(SmallDripstoneFeatureConfig.CODEC));
    public static final Feature<SculkPatchFeatureConfig> SCULK_PATCH = Feature.register("sculk_patch", new SculkPatchFeature(SculkPatchFeatureConfig.CODEC));
    private final Codec<ConfiguredFeature<FC, Feature<FC>>> codec;

    private static <C extends FeatureConfig, F extends Feature<C>> F register(String name, F feature) {
        return (F)Registry.register(Registry.FEATURE, name, feature);
    }

    public Feature(Codec<FC> configCodec) {
        this.codec = ((MapCodec)configCodec.fieldOf("config")).xmap(config -> new ConfiguredFeature<FeatureConfig, Feature>(this, (FeatureConfig)config), ConfiguredFeature::config).codec();
    }

    public Codec<ConfiguredFeature<FC, Feature<FC>>> getCodec() {
        return this.codec;
    }

    protected void setBlockState(ModifiableWorld world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, Block.NOTIFY_ALL);
    }

    public static Predicate<BlockState> notInBlockTagPredicate(TagKey<Block> tag) {
        return state -> !state.isIn(tag);
    }

    protected void setBlockStateIf(StructureWorldAccess world, BlockPos pos, BlockState state, Predicate<BlockState> predicate) {
        if (predicate.test(world.getBlockState(pos))) {
            world.setBlockState(pos, state, Block.NOTIFY_LISTENERS);
        }
    }

    public abstract boolean generate(FeatureContext<FC> var1);

    public boolean generateIfValid(FC config, StructureWorldAccess world, ChunkGenerator chunkGenerator, AbstractRandom random, BlockPos pos) {
        if (world.isValidForSetBlock(pos)) {
            return this.generate(new FeatureContext<FC>(Optional.empty(), world, chunkGenerator, random, pos, config));
        }
        return false;
    }

    protected static boolean isStone(BlockState state) {
        return state.isIn(BlockTags.BASE_STONE_OVERWORLD);
    }

    public static boolean isSoil(BlockState state) {
        return state.isIn(BlockTags.DIRT);
    }

    public static boolean isSoil(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, Feature::isSoil);
    }

    public static boolean testAdjacentStates(Function<BlockPos, BlockState> posToState, BlockPos pos, Predicate<BlockState> predicate) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.values()) {
            mutable.set((Vec3i)pos, direction);
            if (!predicate.test(posToState.apply(mutable))) continue;
            return true;
        }
        return false;
    }

    public static boolean isExposedToAir(Function<BlockPos, BlockState> posToState, BlockPos pos) {
        return Feature.testAdjacentStates(posToState, pos, AbstractBlock.AbstractBlockState::isAir);
    }

    protected void markBlocksAboveForPostProcessing(StructureWorldAccess world, BlockPos pos) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        for (int i = 0; i < 2; ++i) {
            mutable.move(Direction.UP);
            if (world.getBlockState(mutable).isAir()) {
                return;
            }
            world.getChunk(mutable).markBlockForPostProcessing(mutable);
        }
    }
}

