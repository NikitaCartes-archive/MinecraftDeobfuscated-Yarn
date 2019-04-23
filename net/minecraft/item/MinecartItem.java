/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MinecartItem
extends Item {
    private static final DispenserBehavior DISPENSER_BEHAVIOR = new ItemDispenserBehavior(){
        private final ItemDispenserBehavior field_8898 = new ItemDispenserBehavior();

        @Override
        public ItemStack dispenseStack(BlockPointer blockPointer, ItemStack itemStack) {
            double g;
            RailShape railShape;
            Direction direction = blockPointer.getBlockState().get(DispenserBlock.FACING);
            World world = blockPointer.getWorld();
            double d = blockPointer.getX() + (double)direction.getOffsetX() * 1.125;
            double e = Math.floor(blockPointer.getY()) + (double)direction.getOffsetY();
            double f = blockPointer.getZ() + (double)direction.getOffsetZ() * 1.125;
            BlockPos blockPos = blockPointer.getBlockPos().offset(direction);
            BlockState blockState = world.getBlockState(blockPos);
            RailShape railShape2 = railShape = blockState.getBlock() instanceof AbstractRailBlock ? blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            if (blockState.matches(BlockTags.RAILS)) {
                g = railShape.isAscending() ? 0.6 : 0.1;
            } else if (blockState.isAir() && world.getBlockState(blockPos.down()).matches(BlockTags.RAILS)) {
                RailShape railShape22;
                BlockState blockState2 = world.getBlockState(blockPos.down());
                RailShape railShape3 = railShape22 = blockState2.getBlock() instanceof AbstractRailBlock ? blockState2.get(((AbstractRailBlock)blockState2.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
                g = direction == Direction.DOWN || !railShape22.isAscending() ? -0.9 : -0.4;
            } else {
                return this.field_8898.dispense(blockPointer, itemStack);
            }
            AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(world, d, e + g, f, ((MinecartItem)itemStack.getItem()).type);
            if (itemStack.hasDisplayName()) {
                abstractMinecartEntity.setCustomName(itemStack.getDisplayName());
            }
            world.spawnEntity(abstractMinecartEntity);
            itemStack.subtractAmount(1);
            return itemStack;
        }

        @Override
        protected void playSound(BlockPointer blockPointer) {
            blockPointer.getWorld().playLevelEvent(1000, blockPointer.getBlockPos(), 0);
        }
    };
    private final AbstractMinecartEntity.Type type;

    public MinecartItem(AbstractMinecartEntity.Type type, Item.Settings settings) {
        super(settings);
        this.type = type;
        DispenserBlock.registerBehavior(this, DISPENSER_BEHAVIOR);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        BlockPos blockPos;
        World world = itemUsageContext.getWorld();
        BlockState blockState = world.getBlockState(blockPos = itemUsageContext.getBlockPos());
        if (!blockState.matches(BlockTags.RAILS)) {
            return ActionResult.FAIL;
        }
        ItemStack itemStack = itemUsageContext.getItemStack();
        if (!world.isClient) {
            RailShape railShape = blockState.getBlock() instanceof AbstractRailBlock ? blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty()) : RailShape.NORTH_SOUTH;
            double d = 0.0;
            if (railShape.isAscending()) {
                d = 0.5;
            }
            AbstractMinecartEntity abstractMinecartEntity = AbstractMinecartEntity.create(world, (double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.0625 + d, (double)blockPos.getZ() + 0.5, this.type);
            if (itemStack.hasDisplayName()) {
                abstractMinecartEntity.setCustomName(itemStack.getDisplayName());
            }
            world.spawnEntity(abstractMinecartEntity);
        }
        itemStack.subtractAmount(1);
        return ActionResult.SUCCESS;
    }
}

