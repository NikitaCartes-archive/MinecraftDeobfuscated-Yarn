/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerRotation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ArmorStandItem
extends Item {
    public ArmorStandItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext itemUsageContext) {
        double f;
        double e;
        Direction direction = itemUsageContext.getSide();
        if (direction == Direction.DOWN) {
            return ActionResult.FAIL;
        }
        World world = itemUsageContext.getWorld();
        ItemPlacementContext itemPlacementContext = new ItemPlacementContext(itemUsageContext);
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        BlockPos blockPos2 = blockPos.up();
        if (!itemPlacementContext.canPlace() || !world.getBlockState(blockPos2).canReplace(itemPlacementContext)) {
            return ActionResult.FAIL;
        }
        double d = blockPos.getX();
        List<Entity> list = world.getEntities((Entity)null, new Box(d, e = (double)blockPos.getY(), f = (double)blockPos.getZ(), d + 1.0, e + 2.0, f + 1.0));
        if (!list.isEmpty()) {
            return ActionResult.FAIL;
        }
        ItemStack itemStack = itemUsageContext.getStack();
        if (!world.isClient) {
            world.clearBlockState(blockPos, false);
            world.clearBlockState(blockPos2, false);
            ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, d + 0.5, e, f + 0.5);
            float g = (float)MathHelper.floor((MathHelper.wrapDegrees(itemUsageContext.getPlayerYaw() - 180.0f) + 22.5f) / 45.0f) * 45.0f;
            armorStandEntity.setPositionAndAngles(d + 0.5, e, f + 0.5, g, 0.0f);
            this.setRotations(armorStandEntity, world.random);
            EntityType.loadFromEntityTag(world, itemUsageContext.getPlayer(), armorStandEntity, itemStack.getTag());
            world.spawnEntity(armorStandEntity);
            world.playSound(null, armorStandEntity.x, armorStandEntity.y, armorStandEntity.z, SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75f, 0.8f);
        }
        itemStack.decrement(1);
        return ActionResult.SUCCESS;
    }

    private void setRotations(ArmorStandEntity armorStandEntity, Random random) {
        EulerRotation eulerRotation = armorStandEntity.getHeadRotation();
        float f = random.nextFloat() * 5.0f;
        float g = random.nextFloat() * 20.0f - 10.0f;
        EulerRotation eulerRotation2 = new EulerRotation(eulerRotation.getX() + f, eulerRotation.getY() + g, eulerRotation.getZ());
        armorStandEntity.setHeadRotation(eulerRotation2);
        eulerRotation = armorStandEntity.getBodyRotation();
        f = random.nextFloat() * 10.0f - 5.0f;
        eulerRotation2 = new EulerRotation(eulerRotation.getX(), eulerRotation.getY() + f, eulerRotation.getZ());
        armorStandEntity.setBodyRotation(eulerRotation2);
    }
}

