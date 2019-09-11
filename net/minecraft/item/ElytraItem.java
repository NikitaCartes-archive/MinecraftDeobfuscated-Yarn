/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ElytraItem
extends Item {
    public ElytraItem(Item.Settings settings) {
        super(settings);
        this.addPropertyGetter(new Identifier("broken"), (itemStack, world, livingEntity) -> ElytraItem.isUsable(itemStack) ? 0.0f : 1.0f);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSER_BEHAVIOR);
    }

    public static boolean isUsable(ItemStack itemStack) {
        return itemStack.getDamage() < itemStack.getMaxDamage() - 1;
    }

    @Override
    public boolean canRepair(ItemStack itemStack, ItemStack itemStack2) {
        return itemStack2.getItem() == Items.PHANTOM_MEMBRANE;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        EquipmentSlot equipmentSlot = MobEntity.getPreferredEquipmentSlot(itemStack);
        ItemStack itemStack2 = playerEntity.getEquippedStack(equipmentSlot);
        if (itemStack2.isEmpty()) {
            playerEntity.equipStack(equipmentSlot, itemStack.copy());
            itemStack.setCount(0);
            return TypedActionResult.method_22427(itemStack);
        }
        return TypedActionResult.method_22431(itemStack);
    }
}

