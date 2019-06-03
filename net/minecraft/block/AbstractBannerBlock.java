/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBannerBlock
extends BlockWithEntity {
    private final DyeColor color;

    protected AbstractBannerBlock(DyeColor dyeColor, Block.Settings settings) {
        super(settings);
        this.color = dyeColor;
    }

    @Override
    public boolean canMobSpawnInside() {
        return true;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new BannerBlockEntity(this.color);
    }

    @Override
    public void onPlaced(World world, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
        BlockEntity blockEntity;
        if (itemStack.hasCustomName() && (blockEntity = world.getBlockEntity(blockPos)) instanceof BannerBlockEntity) {
            ((BannerBlockEntity)blockEntity).setCustomName(itemStack.getName());
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
        BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
        if (blockEntity instanceof BannerBlockEntity) {
            return ((BannerBlockEntity)blockEntity).getPickStack(blockState);
        }
        return super.getPickStack(blockView, blockPos, blockState);
    }

    public DyeColor getColor() {
        return this.color;
    }
}

