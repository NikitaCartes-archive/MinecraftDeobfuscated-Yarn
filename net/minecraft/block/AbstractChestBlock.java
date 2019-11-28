/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.DoubleBlockProperties;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class AbstractChestBlock<E extends BlockEntity>
extends BlockWithEntity {
    protected final Supplier<BlockEntityType<? extends E>> entityTypeRetriever;

    protected AbstractChestBlock(Block.Settings settings, Supplier<BlockEntityType<? extends E>> supplier) {
        super(settings);
        this.entityTypeRetriever = supplier;
    }

    @Environment(value=EnvType.CLIENT)
    public abstract DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(BlockState var1, World var2, BlockPos var3, boolean var4);
}

