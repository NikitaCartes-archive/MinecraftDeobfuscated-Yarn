/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShearsItem
extends Item {
    public ShearsItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean onBlockBroken(ItemStack itemStack, World world, BlockState blockState, BlockPos blockPos, LivingEntity livingEntity2) {
        if (!world.isClient) {
            itemStack.applyDamage(1, livingEntity2, livingEntity -> livingEntity.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        Block block = blockState.getBlock();
        if (blockState.matches(BlockTags.LEAVES) || block == Blocks.COBWEB || block == Blocks.GRASS || block == Blocks.FERN || block == Blocks.DEAD_BUSH || block == Blocks.VINE || block == Blocks.TRIPWIRE || block.matches(BlockTags.WOOL)) {
            return true;
        }
        return super.onBlockBroken(itemStack, world, blockState, blockPos, livingEntity2);
    }

    @Override
    public boolean isEffectiveOn(BlockState blockState) {
        Block block = blockState.getBlock();
        return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
    }

    @Override
    public float getBlockBreakingSpeed(ItemStack itemStack, BlockState blockState) {
        Block block = blockState.getBlock();
        if (block == Blocks.COBWEB || blockState.matches(BlockTags.LEAVES)) {
            return 15.0f;
        }
        if (block.matches(BlockTags.WOOL)) {
            return 5.0f;
        }
        return super.getBlockBreakingSpeed(itemStack, blockState);
    }
}

