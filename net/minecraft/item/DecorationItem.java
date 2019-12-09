/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.painting.PaintingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class DecorationItem
extends Item {
    private final EntityType<? extends AbstractDecorationEntity> entityType;

    public DecorationItem(EntityType<? extends AbstractDecorationEntity> type, Item.Settings settings) {
        super(settings);
        this.entityType = type;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        AbstractDecorationEntity abstractDecorationEntity;
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockPos blockPos2 = blockPos.offset(direction);
        PlayerEntity playerEntity = context.getPlayer();
        ItemStack itemStack = context.getStack();
        if (playerEntity != null && !this.canPlaceOn(playerEntity, direction, itemStack, blockPos2)) {
            return ActionResult.FAIL;
        }
        World world = context.getWorld();
        if (this.entityType == EntityType.PAINTING) {
            abstractDecorationEntity = new PaintingEntity(world, blockPos2, direction);
        } else if (this.entityType == EntityType.ITEM_FRAME) {
            abstractDecorationEntity = new ItemFrameEntity(world, blockPos2, direction);
        } else {
            return ActionResult.SUCCESS;
        }
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag != null) {
            EntityType.loadFromEntityTag(world, playerEntity, abstractDecorationEntity, compoundTag);
        }
        if (abstractDecorationEntity.method_6888()) {
            if (!world.isClient) {
                abstractDecorationEntity.onPlace();
                world.spawnEntity(abstractDecorationEntity);
            }
            itemStack.decrement(1);
            return ActionResult.SUCCESS;
        }
        return ActionResult.CONSUME;
    }

    protected boolean canPlaceOn(PlayerEntity player, Direction side, ItemStack stack, BlockPos pos) {
        return !side.getAxis().isVertical() && player.canPlaceOn(pos, side, stack);
    }
}

