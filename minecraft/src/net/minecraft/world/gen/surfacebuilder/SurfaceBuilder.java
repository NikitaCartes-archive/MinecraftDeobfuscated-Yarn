package net.minecraft.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public abstract class SurfaceBuilder<C extends SurfaceConfig> {
	private static final BlockState DIRT = Blocks.DIRT.getDefaultState();
	private static final BlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.getDefaultState();
	private static final BlockState PODZOL = Blocks.PODZOL.getDefaultState();
	private static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
	private static final BlockState STONE = Blocks.STONE.getDefaultState();
	private static final BlockState COARSE_DIRT = Blocks.COARSE_DIRT.getDefaultState();
	private static final BlockState SAND = Blocks.SAND.getDefaultState();
	private static final BlockState RED_SAND = Blocks.RED_SAND.getDefaultState();
	private static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
	private static final BlockState MYCELIUM = Blocks.MYCELIUM.getDefaultState();
	private static final BlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();
	private static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
	private static final BlockState END_STONE = Blocks.END_STONE.getDefaultState();
	private static final BlockState CRIMSON_NYLIUM = Blocks.CRIMSON_NYLIUM.getDefaultState();
	private static final BlockState WARPED_NYLIUM = Blocks.WARPED_NYLIUM.getDefaultState();
	private static final BlockState NETHER_WART_BLOCK = Blocks.NETHER_WART_BLOCK.getDefaultState();
	private static final BlockState WARPED_WART_BLOCK = Blocks.WARPED_WART_BLOCK.getDefaultState();
	private static final BlockState BLACKSTONE = Blocks.BLACKSTONE.getDefaultState();
	private static final BlockState BASALT = Blocks.BASALT.getDefaultState();
	private static final BlockState MAGMA_BLOCK = Blocks.MAGMA_BLOCK.getDefaultState();
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
	public static final SurfaceBuilder<TernarySurfaceConfig> DEFAULT = register("default", new DefaultSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> MOUNTAIN = register("mountain", new MountainSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> SHATTERED_SAVANNA = register(
		"shattered_savanna", new ShatteredSavannaSurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> GRAVELLY_MOUNTAIN = register(
		"gravelly_mountain", new GravellyMountainSurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> GIANT_TREE_TAIGA = register(
		"giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> SWAMP = register("swamp", new SwampSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> BADLANDS = register("badlands", new BadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> WOODED_BADLANDS = register(
		"wooded_badlands", new WoodedBadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> ERODED_BADLANDS = register(
		"eroded_badlands", new ErodedBadlandsSurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> FROZEN_OCEAN = register("frozen_ocean", new FrozenOceanSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> NETHER = register("nether", new NetherSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> NETHER_FOREST = register("nether_forest", new NetherForestSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> SOUL_SAND_VALLEY = register(
		"soul_sand_valley", new SoulSandValleySurfaceBuilder(TernarySurfaceConfig.CODEC)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> BASALT_DELTAS = register("basalt_deltas", new BasaltDeltasSurfaceBuilder(TernarySurfaceConfig.CODEC));
	public static final SurfaceBuilder<TernarySurfaceConfig> NOPE = register("nope", new NopeSurfaceBuilder(TernarySurfaceConfig.CODEC));
	private final Codec<ConfiguredSurfaceBuilder<C>> codec;

	private static <C extends SurfaceConfig, F extends SurfaceBuilder<C>> F register(String id, F surfaceBuilder) {
		return Registry.register(Registry.SURFACE_BUILDER, id, surfaceBuilder);
	}

	public SurfaceBuilder(Codec<C> codec) {
		this.codec = codec.fieldOf("config").<ConfiguredSurfaceBuilder<C>>xmap(this::method_30478, ConfiguredSurfaceBuilder::getConfig).codec();
	}

	public Codec<ConfiguredSurfaceBuilder<C>> getCodec() {
		return this.codec;
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
