/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ModifiableTestableWorld;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.MegaTreeFeature;

public class MegaJungleTreeFeature
extends MegaTreeFeature<DefaultFeatureConfig> {
    public MegaJungleTreeFeature(Function<Dynamic<?>, ? extends DefaultFeatureConfig> function, boolean bl, int i, int j, BlockState blockState, BlockState blockState2) {
        super(function, bl, i, j, blockState, blockState2);
    }

    @Override
    public boolean generate(Set<BlockPos> set, ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, BlockBox blockBox) {
        int i = this.getHeight(random);
        if (!this.checkTreeFitsAndReplaceGround(modifiableTestableWorld, blockPos, i)) {
            return false;
        }
        this.makeLeaves(modifiableTestableWorld, blockPos.up(i), 2, blockBox, set);
        for (int j = blockPos.getY() + i - 2 - random.nextInt(4); j > blockPos.getY() + i / 2; j -= 2 + random.nextInt(4)) {
            int m;
            float f = random.nextFloat() * ((float)Math.PI * 2);
            int k = blockPos.getX() + (int)(0.5f + MathHelper.cos(f) * 4.0f);
            int l = blockPos.getZ() + (int)(0.5f + MathHelper.sin(f) * 4.0f);
            for (m = 0; m < 5; ++m) {
                k = blockPos.getX() + (int)(1.5f + MathHelper.cos(f) * (float)m);
                l = blockPos.getZ() + (int)(1.5f + MathHelper.sin(f) * (float)m);
                this.setBlockState(set, modifiableTestableWorld, new BlockPos(k, j - 3 + m / 2, l), this.log, blockBox);
            }
            m = 1 + random.nextInt(2);
            int n = j;
            for (int o = n - m; o <= n; ++o) {
                int p = o - n;
                this.makeRoundLeafLayer(modifiableTestableWorld, new BlockPos(k, o, l), 1 - p, blockBox, set);
            }
        }
        for (int q = 0; q < i; ++q) {
            BlockPos blockPos5;
            BlockPos blockPos4;
            BlockPos blockPos2 = blockPos.up(q);
            if (MegaJungleTreeFeature.canTreeReplace(modifiableTestableWorld, blockPos2)) {
                this.setBlockState(set, modifiableTestableWorld, blockPos2, this.log, blockBox);
                if (q > 0) {
                    this.tryMakingVine(modifiableTestableWorld, random, blockPos2.west(), VineBlock.EAST);
                    this.tryMakingVine(modifiableTestableWorld, random, blockPos2.north(), VineBlock.SOUTH);
                }
            }
            if (q >= i - 1) continue;
            BlockPos blockPos3 = blockPos2.east();
            if (MegaJungleTreeFeature.canTreeReplace(modifiableTestableWorld, blockPos3)) {
                this.setBlockState(set, modifiableTestableWorld, blockPos3, this.log, blockBox);
                if (q > 0) {
                    this.tryMakingVine(modifiableTestableWorld, random, blockPos3.east(), VineBlock.WEST);
                    this.tryMakingVine(modifiableTestableWorld, random, blockPos3.north(), VineBlock.SOUTH);
                }
            }
            if (MegaJungleTreeFeature.canTreeReplace(modifiableTestableWorld, blockPos4 = blockPos2.south().east())) {
                this.setBlockState(set, modifiableTestableWorld, blockPos4, this.log, blockBox);
                if (q > 0) {
                    this.tryMakingVine(modifiableTestableWorld, random, blockPos4.east(), VineBlock.WEST);
                    this.tryMakingVine(modifiableTestableWorld, random, blockPos4.south(), VineBlock.NORTH);
                }
            }
            if (!MegaJungleTreeFeature.canTreeReplace(modifiableTestableWorld, blockPos5 = blockPos2.south())) continue;
            this.setBlockState(set, modifiableTestableWorld, blockPos5, this.log, blockBox);
            if (q <= 0) continue;
            this.tryMakingVine(modifiableTestableWorld, random, blockPos5.west(), VineBlock.EAST);
            this.tryMakingVine(modifiableTestableWorld, random, blockPos5.south(), VineBlock.NORTH);
        }
        return true;
    }

    private void tryMakingVine(ModifiableTestableWorld modifiableTestableWorld, Random random, BlockPos blockPos, BooleanProperty booleanProperty) {
        if (random.nextInt(3) > 0 && MegaJungleTreeFeature.isAir(modifiableTestableWorld, blockPos)) {
            this.setBlockState(modifiableTestableWorld, blockPos, (BlockState)Blocks.VINE.getDefaultState().with(booleanProperty, true));
        }
    }

    private void makeLeaves(ModifiableTestableWorld modifiableTestableWorld, BlockPos blockPos, int i, BlockBox blockBox, Set<BlockPos> set) {
        int j = 2;
        for (int k = -2; k <= 0; ++k) {
            this.makeSquaredLeafLayer(modifiableTestableWorld, blockPos.up(k), i + 1 - k, blockBox, set);
        }
    }
}

