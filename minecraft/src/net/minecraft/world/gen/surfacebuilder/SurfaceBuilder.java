package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

public abstract class SurfaceBuilder<C extends SurfaceConfig> {
	public static final BlockState AIR = Blocks.field_10124.getDefaultState();
	public static final BlockState DIRT = Blocks.field_10566.getDefaultState();
	public static final BlockState GRASS_BLOCK = Blocks.field_10219.getDefaultState();
	public static final BlockState PODZOL = Blocks.field_10520.getDefaultState();
	public static final BlockState GRAVEL = Blocks.field_10255.getDefaultState();
	public static final BlockState STONE = Blocks.field_10340.getDefaultState();
	public static final BlockState COARSE_DIRT = Blocks.field_10253.getDefaultState();
	public static final BlockState SAND = Blocks.field_10102.getDefaultState();
	public static final BlockState RED_SAND = Blocks.field_10534.getDefaultState();
	public static final BlockState WHITE_TERRACOTTA = Blocks.field_10611.getDefaultState();
	public static final BlockState MYCELIUM = Blocks.field_10402.getDefaultState();
	public static final BlockState NETHERRACK = Blocks.field_10515.getDefaultState();
	public static final BlockState END_STONE = Blocks.field_10471.getDefaultState();
	public static final TernarySurfaceConfig AIR_CONFIG = new TernarySurfaceConfig(AIR, AIR, AIR);
	public static final TernarySurfaceConfig PODZOL_CONFIG = new TernarySurfaceConfig(PODZOL, DIRT, GRAVEL);
	public static final TernarySurfaceConfig GRAVEL_CONFIG = new TernarySurfaceConfig(GRAVEL, GRAVEL, GRAVEL);
	public static final TernarySurfaceConfig GRASS_CONFIG = new TernarySurfaceConfig(GRASS_BLOCK, DIRT, GRAVEL);
	public static final TernarySurfaceConfig DIRT_CONFIG = new TernarySurfaceConfig(DIRT, DIRT, GRAVEL);
	public static final TernarySurfaceConfig STONE_CONFIG = new TernarySurfaceConfig(STONE, STONE, GRAVEL);
	public static final TernarySurfaceConfig COARSE_DIRT_CONFIG = new TernarySurfaceConfig(COARSE_DIRT, DIRT, GRAVEL);
	public static final TernarySurfaceConfig SAND_CONFIG = new TernarySurfaceConfig(SAND, SAND, GRAVEL);
	public static final TernarySurfaceConfig GRASS_SAND_UNDERWATER_CONFIG = new TernarySurfaceConfig(GRASS_BLOCK, DIRT, SAND);
	public static final TernarySurfaceConfig SAND_SAND_UNDERWATER_CONFIG = new TernarySurfaceConfig(SAND, SAND, SAND);
	public static final TernarySurfaceConfig BADLANDS_CONFIG = new TernarySurfaceConfig(RED_SAND, WHITE_TERRACOTTA, GRAVEL);
	public static final TernarySurfaceConfig MYCELIUM_CONFIG = new TernarySurfaceConfig(MYCELIUM, DIRT, GRAVEL);
	public static final TernarySurfaceConfig NETHER_CONFIG = new TernarySurfaceConfig(NETHERRACK, NETHERRACK, NETHERRACK);
	public static final TernarySurfaceConfig END_CONFIG = new TernarySurfaceConfig(END_STONE, END_STONE, END_STONE);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15701 = register("default", new DefaultSurfaceBuilder(TernarySurfaceConfig::deserialize));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15692 = register("mountain", new MountainSurfaceBuilder(TernarySurfaceConfig::deserialize));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15680 = register(
		"shattered_savanna", new ShatteredSavannaSurfaceBuilder(TernarySurfaceConfig::deserialize)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15702 = register(
		"gravelly_mountain", new GravellyMountainSurfaceBuilder(TernarySurfaceConfig::deserialize)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15688 = register(
		"giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder(TernarySurfaceConfig::deserialize)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15681 = register("swamp", new SwampSurfaceBuilder(TernarySurfaceConfig::deserialize));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15698 = register("badlands", new BadlandsSurfaceBuilder(TernarySurfaceConfig::deserialize));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15689 = register(
		"wooded_badlands", new WoodedBadlandsSurfaceBuilder(TernarySurfaceConfig::deserialize)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15684 = register(
		"eroded_badlands", new ErodedBadlandsSurfaceBuilder(TernarySurfaceConfig::deserialize)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15699 = register(
		"frozen_ocean", new FrozenOceanSurfaceBuilder(TernarySurfaceConfig::deserialize)
	);
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15693 = register("nether", new NetherSurfaceBuilder(TernarySurfaceConfig::deserialize));
	public static final SurfaceBuilder<TernarySurfaceConfig> field_15683 = register("nope", new NopeSurfaceBuilder(TernarySurfaceConfig::deserialize));
	private final Function<Dynamic<?>, ? extends C> factory;

	private static <C extends SurfaceConfig, F extends SurfaceBuilder<C>> F register(String string, F surfaceBuilder) {
		return Registry.register(Registry.SURFACE_BUILDER, string, surfaceBuilder);
	}

	public SurfaceBuilder(Function<Dynamic<?>, ? extends C> function) {
		this.factory = function;
	}

	public abstract void generate(
		Random random, Chunk chunk, Biome biome, int i, int j, int k, double d, BlockState blockState, BlockState blockState2, int l, long m, C surfaceConfig
	);

	public void initSeed(long l) {
	}
}
