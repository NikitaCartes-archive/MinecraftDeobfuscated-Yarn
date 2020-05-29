/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class EndCrystalItem
extends Item {
    public EndCrystalItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        double f;
        double e;
        BlockPos blockPos;
        World world = context.getWorld();
        BlockState blockState = world.getBlockState(blockPos = context.getBlockPos());
        if (!blockState.isOf(Blocks.OBSIDIAN) && !blockState.isOf(Blocks.BEDROCK)) {
            return ActionResult.FAIL;
        }
        BlockPos blockPos2 = blockPos.up();
        if (!world.isAir(blockPos2)) {
            return ActionResult.FAIL;
        }
        double d = blockPos2.getX();
        List<Entity> list = world.getEntities(null, new Box(d, e = (double)blockPos2.getY(), f = (double)blockPos2.getZ(), d + 1.0, e + 2.0, f + 1.0));
        if (!list.isEmpty()) {
            return ActionResult.FAIL;
        }
        if (world instanceof ServerWorld) {
            EndCrystalEntity endCrystalEntity = new EndCrystalEntity(world, d + 0.5, e, f + 0.5);
            endCrystalEntity.setShowBottom(false);
            world.spawnEntity(endCrystalEntity);
            EnderDragonFight enderDragonFight = ((ServerWorld)world).method_29198();
            if (enderDragonFight != null) {
                enderDragonFight.respawnDragon();
            }
        }
        context.getStack().decrement(1);
        return ActionResult.method_29236(world.isClient);
    }

    @Override
    public boolean hasEnchantmentGlint(ItemStack stack) {
        return true;
    }
}

