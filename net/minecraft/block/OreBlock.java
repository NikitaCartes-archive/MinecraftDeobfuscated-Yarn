/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class OreBlock
extends Block {
    public OreBlock(Block.Settings settings) {
        super(settings);
    }

    protected int getExperienceWhenMined(Random random) {
        if (this == Blocks.COAL_ORE) {
            return MathHelper.nextInt(random, 0, 2);
        }
        if (this == Blocks.DIAMOND_ORE) {
            return MathHelper.nextInt(random, 3, 7);
        }
        if (this == Blocks.EMERALD_ORE) {
            return MathHelper.nextInt(random, 3, 7);
        }
        if (this == Blocks.LAPIS_ORE) {
            return MathHelper.nextInt(random, 2, 5);
        }
        if (this == Blocks.NETHER_QUARTZ_ORE) {
            return MathHelper.nextInt(random, 2, 5);
        }
        return 0;
    }

    @Override
    public void onStacksDropped(BlockState blockState, World world, BlockPos blockPos, ItemStack itemStack) {
        int i;
        super.onStacksDropped(blockState, world, blockPos, itemStack);
        if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, itemStack) == 0 && (i = this.getExperienceWhenMined(world.random)) > 0) {
            this.dropExperience(world, blockPos, i);
        }
    }
}

