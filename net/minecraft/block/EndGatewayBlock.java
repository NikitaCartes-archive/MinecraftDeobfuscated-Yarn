/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class EndGatewayBlock
extends BlockWithEntity {
    protected EndGatewayBlock(Block.Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView) {
        return new EndGatewayBlockEntity();
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void randomDisplayTick(BlockState blockState, World world, BlockPos blockPos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (!(blockEntity instanceof EndGatewayBlockEntity)) {
            return;
        }
        int i = ((EndGatewayBlockEntity)blockEntity).getDrawnSidesCount();
        for (int j = 0; j < i; ++j) {
            double d = (float)blockPos.getX() + random.nextFloat();
            double e = (float)blockPos.getY() + random.nextFloat();
            double f = (float)blockPos.getZ() + random.nextFloat();
            double g = ((double)random.nextFloat() - 0.5) * 0.5;
            double h = ((double)random.nextFloat() - 0.5) * 0.5;
            double k = ((double)random.nextFloat() - 0.5) * 0.5;
            int l = random.nextInt(2) * 2 - 1;
            if (random.nextBoolean()) {
                f = (double)blockPos.getZ() + 0.5 + 0.25 * (double)l;
                k = random.nextFloat() * 2.0f * (float)l;
            } else {
                d = (double)blockPos.getX() + 0.5 + 0.25 * (double)l;
                g = random.nextFloat() * 2.0f * (float)l;
            }
            world.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, k);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickStack(BlockView blockView, BlockPos blockPos, BlockState blockState) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean method_22358(BlockState blockState, Fluid fluid) {
        return false;
    }
}

