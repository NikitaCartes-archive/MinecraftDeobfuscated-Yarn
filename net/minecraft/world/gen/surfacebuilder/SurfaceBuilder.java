/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.surfacebuilder;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.surfacebuilder.BadlandsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.DefaultSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ErodedBadlandsSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.FrozenOceanSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.GiantTreeTaigaSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.GravellyMountainSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.MountainSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.NetherSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.NopeSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.ShatteredSavannaSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.SurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.SwampSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilder.TernarySurfaceConfig;
import net.minecraft.world.gen.surfacebuilder.WoodedBadlandsSurfaceBuilder;

public abstract class SurfaceBuilder<C extends SurfaceConfig> {
    public static final BlockState AIR = Blocks.AIR.getDefaultState();
    public static final BlockState DIRT = Blocks.DIRT.getDefaultState();
    public static final BlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.getDefaultState();
    public static final BlockState PODZOL = Blocks.PODZOL.getDefaultState();
    public static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
    public static final BlockState STONE = Blocks.STONE.getDefaultState();
    public static final BlockState COARSE_DIRT = Blocks.COARSE_DIRT.getDefaultState();
    public static final BlockState SAND = Blocks.SAND.getDefaultState();
    public static final BlockState RED_SAND = Blocks.RED_SAND.getDefaultState();
    public static final BlockState WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getDefaultState();
    public static final BlockState MYCELIUM = Blocks.MYCELIUM.getDefaultState();
    public static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
    public static final BlockState END_STONE = Blocks.END_STONE.getDefaultState();
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
    public static final SurfaceBuilder<TernarySurfaceConfig> DEFAULT = SurfaceBuilder.register("default", new DefaultSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    public static final SurfaceBuilder<TernarySurfaceConfig> MOUNTAIN = SurfaceBuilder.register("mountain", new MountainSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    public static final SurfaceBuilder<TernarySurfaceConfig> SHATTERED_SAVANNA = SurfaceBuilder.register("shattered_savanna", new ShatteredSavannaSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    public static final SurfaceBuilder<TernarySurfaceConfig> GRAVELLY_MOUNTAIN = SurfaceBuilder.register("gravelly_mountain", new GravellyMountainSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    public static final SurfaceBuilder<TernarySurfaceConfig> GIANT_TREE_TAIGA = SurfaceBuilder.register("giant_tree_taiga", new GiantTreeTaigaSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    public static final SurfaceBuilder<TernarySurfaceConfig> SWAMP = SurfaceBuilder.register("swamp", new SwampSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    public static final SurfaceBuilder<TernarySurfaceConfig> BADLANDS = SurfaceBuilder.register("badlands", new BadlandsSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    public static final SurfaceBuilder<TernarySurfaceConfig> WOODED_BADLANDS = SurfaceBuilder.register("wooded_badlands", new WoodedBadlandsSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    public static final SurfaceBuilder<TernarySurfaceConfig> ERODED_BADLANDS = SurfaceBuilder.register("eroded_badlands", new ErodedBadlandsSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    public static final SurfaceBuilder<TernarySurfaceConfig> FROZEN_OCEAN = SurfaceBuilder.register("frozen_ocean", new FrozenOceanSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    public static final SurfaceBuilder<TernarySurfaceConfig> NETHER = SurfaceBuilder.register("nether", new NetherSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    public static final SurfaceBuilder<TernarySurfaceConfig> NOPE = SurfaceBuilder.register("nope", new NopeSurfaceBuilder((Function<Dynamic<?>, ? extends TernarySurfaceConfig>)((Function<Dynamic<?>, TernarySurfaceConfig>)TernarySurfaceConfig::deserialize)));
    private final Function<Dynamic<?>, ? extends C> factory;

    private static <C extends SurfaceConfig, F extends SurfaceBuilder<C>> F register(String string, F surfaceBuilder) {
        return (F)Registry.register(Registry.SURFACE_BUILDER, string, surfaceBuilder);
    }

    public SurfaceBuilder(Function<Dynamic<?>, ? extends C> function) {
        this.factory = function;
    }

    public abstract void generate(Random var1, Chunk var2, Biome var3, int var4, int var5, int var6, double var7, BlockState var9, BlockState var10, int var11, long var12, C var14);

    public void initSeed(long l) {
    }
}

