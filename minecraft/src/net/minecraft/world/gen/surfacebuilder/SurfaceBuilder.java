package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public abstract class SurfaceBuilder<C extends SurfaceConfig> {
	private static final BlockState DIRT = Blocks.field_10566.getDefaultState();
	private static final BlockState GRASS_BLOCK = Blocks.field_10219.getDefaultState();
	private static final BlockState PODZOL = Blocks.field_10520.getDefaultState();
	private static final BlockState GRAVEL = Blocks.field_10255.getDefaultState();
	private static final BlockState STONE = Blocks.field_10340.getDefaultState();
	private static final BlockState COARSE_DIRT = Blocks.field_10253.getDefaultState();
	private static final BlockState SAND = Blocks.field_10102.getDefaultState();
	private static final BlockState RED_SAND = Blocks.field_10534.getDefaultState();
	private static final BlockState WHITE_TERRACOTTA = Blocks.field_10611.getDefaultState();
	private static final BlockState MYCELIUM = Blocks.field_10402.getDefaultState();
	private static final BlockState SOUL_SAND = Blocks.field_10114.getDefaultState();
	private static final BlockState NETHERRACK = Blocks.field_10515.getDefaultState();
	private static final BlockState END_STONE = Blocks.field_10471.getDefaultState();
	private static final BlockState CRIMSON_NYLIUM = Blocks.field_22120.getDefaultState();
	private static final BlockState WARPED_NYLIUM = Blocks.field_22113.getDefaultState();
	private static final BlockState NETHER_WART_BLOCK = Blocks.field_10541.getDefaultState();
	private static final BlockState WARPED_WART_BLOCK = Blocks.field_22115.getDefaultState();
	private static final BlockState BLACKSTONE = Blocks.field_23869.getDefaultState();
	private static final BlockState BASALT = Blocks.field_22091.getDefaultState();
	private static final BlockState MAGMA_BLOCK = Blocks.field_10092.getDefaultState();
	public static final TernarySurfaceConfig PODZOL_CONFIG = new TernarySurfaceConfig(PODZOL, DIRT, GRAVEL);
	public static final TernarySurfaceConfig GRAVEL_CONFIG = new TernarySurfaceConfig(GRAVEL, GRAVEL, GRAVEL);
	public static final TernarySurfaceConfig GRASS_CONFIG = new TernarySurfaceConfig(GRASS_BLOCK, DIRT, GRAVEL);
	public static final TernarySurfaceConfig STONE_CONFIG = new TernarySurfaceConfig(STONE, STONE, GRAVEL);
	public static final TernarySurfaceConfig COARSE_DIRT_CONFIG = new TernarySurfaceConfig(COARSE_DIRT, DIRT, GRAVEL);
	public static final TernarySurfaceConfig SAND_CONFIG = new TernarySurfaceConfig(SAND, SAND, GRAVEL);
	public static final TernarySurfaceConfig GRASS_SAND_UNDERWATER_CONFIG = new TernarySurfaceConfig(GRASS_BLOCK, DIRT, SAND);
	public static final TernarySurfaceConfig SAND_SAND_UNDERWATER_CONFIG = new TernarySurfaceConfig(SAND, SAND, SAND);
	public static final TernarySurfaceConfig BADLANDS_CONFIG = new TernarySurfaceConfig(RED_SAND, WHITE_TERRACOTTA, GRAVEL);
	public static final TernarySurfaceConfig MYCELIUM_CONFIG = new TernarySurfaceConfig(MYCELIUM, DIRT, GRAVEL);
	public static final TernarySurfaceConfig NETHER_CONFIG = new TernarySurfaceConfig(NETHERRACK, NETHERRACK, NETHERRACK);
	public static final TernarySurfaceConfig SOUL_SAND_CONFIG = new TernarySurfaceConfig(SOUL_SAND, SOUL_SAND, SOUL_SAND);
	public static final TernarySurfaceConfig END_CONFIG = new TernarySurfaceConfig(END_STONE, END_STONE, END_STONE);
	public static final TernarySurfaceConfig CRIMSON_NYLIUM_CONFIG = new TernarySurfaceConfig(CRIMSON_NYLIUM, NETHERRACK, NETHER_WART_BLOCK);
	public static final TernarySurfaceConfig WARPED_NYLIUM_CONFIG = new TernarySurfaceConfig(WARPED_NYLIUM, NETHERRACK, WARPED_WART_BLOCK);
	public static final TernarySurfaceConfig BASALT_DELTA_CONFIG = new TernarySurfaceConfig(BLACKSTONE, BASALT, MAGMA_BLOCK);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15701 = register("default", new DefaultSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15692 = register("mountain", new MountainSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15680 = register(
		"shattered_savanna", new ShatteredSavannaSurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15702 = register(
		"gravelly_mountain", new GravellyMountainSurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15688 = register(
		"giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15681 = register("swamp", new SwampSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15698 = register("badlands", new BadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15689 = register(
		"wooded_badlands", new WoodedBadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15684 = register(
		"eroded_badlands", new ErodedBadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15699 = register("frozen_ocean", new FrozenOceanSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15693 = register("nether", new NetherSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_22216 = register("nether_forest", new NetherForestSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_22217 = register(
		"soul_sand_valley", new SoulSandValleySurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_23926 = register("basalt_deltas", new BasaltDeltasSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15683 = register("nope", new NopeSurfaceBuilder(TernarySurfaceConfig.CODEC));
	private final Codec<ConfiguredSurfaceBuilder<C>> field_25016;

	private static <C extends SurfaceConfig, F extends SurfaceBuilder<C>> F register(String string, F surfaceBuilder) {
		return Registry.register(Registry.SURFACE_BUILDER, string, surfaceBuilder);
	}

	public SurfaceBuilder(Codec<C> codec) {
		this.field_25016 = codec.fieldOf("config").<ConfiguredSurfaceBuilder<C>>xmap(this::method_30478, ConfiguredSurfaceBuilder::getConfig).codec();
	}

	public Codec<ConfiguredSurfaceBuilder<C>> method_29003() {
		return this.field_25016;
	}

	public ConfiguredSurfaceBuilder<C> method_30478(C surfaceConfig) {
		return new ConfiguredSurfaceBuilder<>(this, surfaceConfig);
	}

	public abstract void generate(
		Random random,
		Chunk chunk,
		Biome biome,
		int x,
		int z,
		int height,
		double noise,
		BlockState defaultBlock,
		BlockState defaultFluid,
		int seaLevel,
		long seed,
		C surfaceBlocks
	);

	public void initSeed(long seed) {
	}
}
