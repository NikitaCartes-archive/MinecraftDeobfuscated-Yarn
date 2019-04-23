/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.function.Predicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Hand;

public abstract class BaseBowItem
extends Item {
    public static final Predicate<ItemStack> IS_BOW_PROJECTILE = itemStack -> itemStack.getItem().matches(ItemTags.ARROWS);
    public static final Predicate<ItemStack> IS_CROSSBOW_PROJECTILE = IS_BOW_PROJECTILE.or(itemStack -> itemStack.getItem() == Items.FIREWORK_ROCKET);

    public BaseBowItem(Item.Settings settings) {
        super(settings);
    }

    public Predicate<ItemStack> getHeldProjectilePredicate() {
        return this.getInventoryProjectilePredicate();
    }

    public abstract Predicate<ItemStack> getInventoryProjectilePredicate();

    public static ItemStack getItemHeld(LivingEntity livingEntity, Predicate<ItemStack> predicate) {
        if (predicate.test(livingEntity.getStackInHand(Hand.OFF_HAND))) {
            return livingEntity.getStackInHand(Hand.OFF_HAND);
        }
        if (predicate.test(livingEntity.getStackInHand(Hand.MAIN_HAND))) {
            return livingEntity.getStackInHand(Hand.MAIN_HAND);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getEnchantability() {
        return 1;
    }
}

