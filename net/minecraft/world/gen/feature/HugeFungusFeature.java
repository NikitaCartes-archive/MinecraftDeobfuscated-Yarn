/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.HugeFungusFeatureConfig;
import net.minecraft.world.gen.feature.WeepingVinesFeature;

public class HugeFungusFeature
extends Feature<HugeFungusFeatureConfig> {
    public HugeFungusFeature(Codec<HugeFungusFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, HugeFungusFeatureConfig hugeFungusFeatureConfig) {
        Block block = hugeFungusFeatureConfig.validBaseBlock.getBlock();
        BlockPos blockPos2 = null;
        Block block2 = structureWorldAccess.getBlockState(blockPos.down()).getBlock();
        if (block2 == block) {
            blockPos2 = blockPos;
        }
        if (blockPos2 == null) {
            return false;
        }
        int i = MathHelper.nextInt(random, 4, 13);
        if (random.nextInt(12) == 0) {
            i *= 2;
        }
        if (!hugeFungusFeatureConfig.planted) {
            int j = chunkGenerator.getMaxY();
            if (blockPos2.getY() + i + 1 >= j) {
                return false;
            }
        }
        boolean bl = !hugeFungusFeatureConfig.planted && random.nextFloat() < 0.06f;
        structureWorldAccess.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 4);
        this.generateStem(structureWorldAccess, random, hugeFungusFeatureConfig, blockPos2, i, bl);
        this.generateHat(structureWorldAccess, random, hugeFungusFeatureConfig, blockPos2, i, bl);
        return true;
    }

    private static boolean method_24866(WorldAccess worldAccess, BlockPos blockPos, boolean bl) {
        return worldAccess.testBlockState(blockPos, blockState -> {
            Material material = blockState.getMaterial();
            return blockState.getMaterial().isReplaceable() || bl && material == Material.PLANT;
        });
    }

    private void generateStem(WorldAccess world, Random random, HugeFungusFeatureConfig config, BlockPos blockPos, int stemHeight, boolean thickStem) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockState blockState = config.stemState;
        int i = thickStem ? 1 : 0;
        for (int j = -i; j <= i; ++j) {
            for (int k = -i; k <= i; ++k) {
                boolean bl = thickStem && MathHelper.abs(j) == i && MathHelper.abs(k) == i;
                for (int l = 0; l < stemHeight; ++l) {
                    mutable.set(blockPos, j, l, k);
                    if (!HugeFungusFeature.method_24866(world, mutable, true)) continue;
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

    private void generateHat(WorldAccess world, Random random, HugeFungusFeatureConfig config, BlockPos blockPos, int hatHeight, boolean thickStem) {
        int j;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        boolean bl = config.hatState.isOf(Blocks.NETHER_WART_BLOCK);
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
                    mutable.set(blockPos, m, k, n2);
                    if (!HugeFungusFeature.method_24866(world, mutable, false)) continue;
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

    private void generateHatBlock(WorldAccess world, Random random, HugeFungusFeatureConfig config, BlockPos.Mutable pos, float decorationChance, float generationChance, float vineChance) {
        if (random.nextFloat() < decorationChance) {
            this.setBlockState(world, pos, config.decorationState);
        } else if (random.nextFloat() < generationChance) {
            this.setBlockState(world, pos, config.hatState);
            if (random.nextFloat() < vineChance) {
                HugeFungusFeature.generateVines(pos, world, random);
            }
        }
    }

    private void tryGenerateVines(WorldAccess world, Random random, BlockPos pos, BlockState state, boolean bl) {
        if (world.getBlockState(pos.down()).isOf(state.getBlock())) {
            this.setBlockState(world, pos, state);
        } else if ((double)random.nextFloat() < 0.15) {
            this.setBlockState(world, pos, state);
            if (bl && random.nextInt(11) == 0) {
                HugeFungusFeature.generateVines(pos, world, random);
            }
        }
    }

    private static void generateVines(BlockPos blockPos, WorldAccess worldAccess, Random random) {
        BlockPos.Mutable mutable = blockPos.mutableCopy().move(Direction.DOWN);
        if (!worldAccess.isAir(mutable)) {
            return;
        }
        int i = MathHelper.nextInt(random, 1, 5);
        if (random.nextInt(7) == 0) {
            i *= 2;
        }
        int j = 23;
        int k = 25;
        WeepingVinesFeature.generateVineColumn(worldAccess, random, mutable, i, 23, 25);
    }
}

