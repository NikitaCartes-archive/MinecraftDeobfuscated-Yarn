/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.ChunkGeneratorConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;
import net.minecraft.world.gen.feature.WeepingVinesFeature;
import org.jetbrains.annotations.Nullable;

public class HugeFungusFeature
extends Feature<HugeFungusFeatureConfig> {
    public HugeFungusFeature(Function<Dynamic<?>, ? extends HugeFungusFeatureConfig> function) {
        super(function);
    }

    @Override
    public boolean generate(IWorld iWorld, ChunkGenerator<? extends ChunkGeneratorConfig> chunkGenerator, Random random, BlockPos blockPos, HugeFungusFeatureConfig hugeFungusFeatureConfig) {
        Block block = hugeFungusFeatureConfig.field_22435.getBlock();
        BlockPos blockPos2 = null;
        if (hugeFungusFeatureConfig.planted) {
            Block block2 = iWorld.getBlockState(blockPos.down()).getBlock();
            if (block2 == block) {
                blockPos2 = blockPos;
            }
        } else {
            blockPos2 = HugeFungusFeature.getStartPos(iWorld, blockPos, block);
        }
        if (blockPos2 == null) {
            return false;
        }
        int i = MathHelper.nextInt(random, 4, 13);
        if (random.nextInt(12) == 0) {
            i *= 2;
        }
        if (!hugeFungusFeatureConfig.planted) {
            int j = iWorld.method_24853();
            if (blockPos2.getY() + i + 1 >= j) {
                return false;
            }
        }
        boolean bl = !hugeFungusFeatureConfig.planted && random.nextFloat() < 0.06f;
        iWorld.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 4);
        this.generateHat(iWorld, random, hugeFungusFeatureConfig, blockPos2, i, bl);
        this.generateStem(iWorld, random, hugeFungusFeatureConfig, blockPos2, i, bl);
        return true;
    }

    public static boolean method_24866(IWorld iWorld, BlockPos blockPos) {
        return iWorld.testBlockState(blockPos, blockState -> {
            Material material = blockState.getMaterial();
            return material == Material.REPLACEABLE_PLANT;
        });
    }

    private static boolean method_24868(IWorld iWorld, BlockPos blockPos) {
        return iWorld.getBlockState(blockPos).isAir() || !iWorld.getFluidState(blockPos).isEmpty() || HugeFungusFeature.method_24866(iWorld, blockPos);
    }

    private void generateStem(IWorld world, Random random, HugeFungusFeatureConfig config, BlockPos blockPos, int stemHeight, boolean thickStem) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockState blockState = config.stemState;
        int i = thickStem ? 1 : 0;
        for (int j = -i; j <= i; ++j) {
            for (int k = -i; k <= i; ++k) {
                boolean bl = thickStem && MathHelper.abs(j) == i && MathHelper.abs(k) == i;
                for (int l = 0; l < stemHeight; ++l) {
                    mutable.set(blockPos).setOffset(j, l, k);
                    if (!HugeFungusFeature.method_24868(world, mutable)) continue;
                    if (config.planted) {
                        if (!world.getBlockState((BlockPos)mutable.down()).isAir()) {
                            world.breakBlock(mutable, true);
                        }
                        world.setBlockState(mutable, blockState, 3);
                        continue;
                    }
                    if (bl) {
                        if (!(random.nextFloat() < 0.1f)) continue;
                        this.setBlockState(world, mutable, blockState);
                        continue;
                    }
                    this.setBlockState(world, mutable, blockState);
                }
            }
        }
    }

    private void generateHat(IWorld world, Random random, HugeFungusFeatureConfig config, BlockPos blockPos, int hatHeight, boolean thickStem) {
        int j;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        boolean bl = config.hatState.getBlock() == Blocks.NETHER_WART_BLOCK;
        int i = Math.min(random.nextInt(1 + hatHeight / 3) + 5, hatHeight);
        for (int k = j = hatHeight - i; k <= hatHeight; ++k) {
            int l;
            int n = l = k < hatHeight - random.nextInt(3) ? 2 : 1;
            if (i > 8 && k < j + 4) {
                l = 3;
            }
            if (thickStem) {
                ++l;
            }
            for (int m = -l; m <= l; ++m) {
                for (int n2 = -l; n2 <= l; ++n2) {
                    boolean bl2 = m == -l || m == l;
                    boolean bl3 = n2 == -l || n2 == l;
                    boolean bl4 = !bl2 && !bl3 && k != hatHeight;
                    boolean bl5 = bl2 && bl3;
                    boolean bl6 = k < j + 3;
                    mutable.set(blockPos).setOffset(m, k, n2);
                    if (!HugeFungusFeature.method_24868(world, mutable)) continue;
                    if (config.planted && !world.getBlockState((BlockPos)mutable.down()).isAir()) {
                        world.breakBlock(mutable, true);
                    }
                    if (bl6) {
                        if (bl4) continue;
                        this.tryGenerateVines(world, random, mutable, config.hatState, bl);
                        continue;
                    }
                    if (bl4) {
                        this.generateHatBlock(world, random, config, mutable, 0.1f, 0.2f, bl ? 0.1f : 0.0f);
                        continue;
                    }
                    if (bl5) {
                        this.generateHatBlock(world, random, config, mutable, 0.01f, 0.7f, bl ? 0.083f : 0.0f);
                        continue;
                    }
                    this.generateHatBlock(world, random, config, mutable, 5.0E-4f, 0.98f, bl ? 0.07f : 0.0f);
                }
            }
        }
    }

    private void generateHatBlock(IWorld world, Random random, HugeFungusFeatureConfig config, BlockPos.Mutable pos, float decorationChance, float generationChance, float vineChance) {
        if (random.nextFloat() < decorationChance) {
            this.setBlockState(world, pos, config.decorationState);
        } else if (random.nextFloat() < generationChance) {
            this.setBlockState(world, pos, config.hatState);
            if (random.nextFloat() < vineChance) {
                HugeFungusFeature.generateVines(pos, world, random);
            }
        }
    }

    private void tryGenerateVines(IWorld world, Random random, BlockPos pos, BlockState state, boolean bl) {
        if (world.getBlockState(pos.down()).getBlock() == state.getBlock()) {
            this.setBlockState(world, pos, state);
        } else if ((double)random.nextFloat() < 0.15) {
            this.setBlockState(world, pos, state);
            if (bl && random.nextInt(11) == 0) {
                HugeFungusFeature.generateVines(pos, world, random);
            }
        }
    }

    @Nullable
    private static BlockPos.Mutable getStartPos(IWorld world, BlockPos pos, Block block) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(pos);
        for (int i = pos.getY(); i >= 1; --i) {
            mutable.setY(i);
            Block block2 = world.getBlockState((BlockPos)mutable.down()).getBlock();
            if (block2 != block) continue;
            return mutable;
        }
        return null;
    }

    private static void generateVines(BlockPos blockPos, IWorld iWorld, Random random) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos).setOffset(Direction.DOWN);
        if (!iWorld.isAir(mutable)) {
            return;
        }
        int i = MathHelper.nextInt(random, 1, 5);
        if (random.nextInt(7) == 0) {
            i *= 2;
        }
        int j = 23;
        int k = 25;
        WeepingVinesFeature.generateVines(iWorld, random, mutable, i, 23, 25);
    }
}

