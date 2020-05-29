/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class ArmorStandItem
extends Item {
    public ArmorStandItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        Direction direction = context.getSide();
        if (direction == Direction.DOWN) {
            return ActionResult.FAIL;
        }
        World world = context.getWorld();
        ItemPlacementContext itemPlacementContext = new ItemPlacementContext(context);
        BlockPos blockPos = itemPlacementContext.getBlockPos();
        ItemStack itemStack = context.getStack();
        ArmorStandEntity armorStandEntity = EntityType.ARMOR_STAND.create(world, itemStack.getTag(), null, context.getPlayer(), blockPos, SpawnReason.SPAWN_EGG, true, true);
        if (!world.doesNotCollide(armorStandEntity) || !world.getEntities(armorStandEntity, armorStandEntity.getBoundingBox()).isEmpty()) {
            return ActionResult.FAIL;
        }
        if (!world.isClient) {
            float f = (float)MathHelper.floor((MathHelper.wrapDegrees(context.getPlayerYaw() - 180.0f) + 22.5f) / 45.0f) * 45.0f;
            armorStandEntity.refreshPositionAndAngles(armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), f, 0.0f);
            this.setRotations(armorStandEntity, world.random);
            world.spawnEntity(armorStandEntity);
            world.playSound(null, armorStandEntity.getX(), armorStandEntity.getY(), armorStandEntity.getZ(), SoundEvents.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75f, 0.8f);
        }
        itemStack.decrement(1);
        return ActionResult.method_29236(world.isClient);
    }

    private void setRotations(ArmorStandEntity stand, Random random) {
        EulerAngle eulerAngle = stand.getHeadRotation();
        float f = random.nextFloat() * 5.0f;
        float g = random.nextFloat() * 20.0f - 10.0f;
        EulerAngle eulerAngle2 = new EulerAngle(eulerAngle.getPitch() + f, eulerAngle.getYaw() + g, eulerAngle.getRoll());
        stand.setHeadRotation(eulerAngle2);
        eulerAngle = stand.getBodyRotation();
        f = random.nextFloat() * 10.0f - 5.0f;
        eulerAngle2 = new EulerAngle(eulerAngle.getPitch(), eulerAngle.getYaw() + f, eulerAngle.getRoll());
        stand.setBodyRotation(eulerAngle2);
    }
}

