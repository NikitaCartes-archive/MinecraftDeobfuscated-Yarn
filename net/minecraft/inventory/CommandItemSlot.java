/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

/**
 * Represents an item slot for command access. Usually obtained from entities.
 * 
 * <p>Some dispenser behaviors also use this to simulate commands' behavior
 * of equipping items.
 * 
 * @see net.minecraft.entity.Entity#getCommandItemSlot(int)
 */
public interface CommandItemSlot {
    /**
     * An immutable empty slot.
     */
    public static final CommandItemSlot EMPTY = new CommandItemSlot(){

        @Override
        public ItemStack get() {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean set(ItemStack stack) {
            return false;
        }
    };

    /**
     * Creates a slot backed by an index within an inventory and guarded by
     * a condition for setting stacks into the inventory.
     * 
     * @param stackFilter the condition to guard stack setting
     */
    public static CommandItemSlot of(final Inventory inventory, final int index, final Predicate<ItemStack> stackFilter) {
        return new CommandItemSlot(){

            @Override
            public ItemStack get() {
                return inventory.getStack(index);
            }

            @Override
            public boolean set(ItemStack stack) {
                if (!stackFilter.test(stack)) {
                    return false;
                }
                inventory.setStack(index, stack);
                return true;
            }
        };
    }

    /**
     * Creates a slot backed by an index within an inventory.
     */
    public static CommandItemSlot of(Inventory inventory, int index) {
        return CommandItemSlot.of(inventory, index, (ItemStack stack) -> true);
    }

    /**
     * Creates a slot backed by an equipment slot of an living entity and guarded by
     * a condition for setting stacks into the inventory.
     * 
     * @param stackFilter the condition to guard stack setting
     */
    public static CommandItemSlot of(final LivingEntity entity, final EquipmentSlot equipmentSlot, final Predicate<ItemStack> stackFilter) {
        return new CommandItemSlot(){

            @Override
            public ItemStack get() {
                return entity.getEquippedStack(equipmentSlot);
            }

            @Override
            public boolean set(ItemStack stack) {
                if (!stackFilter.test(stack)) {
                    return false;
                }
                entity.equipStack(equipmentSlot, stack);
                return true;
            }
        };
    }

    /**
     * Creates a slot backed by an equipment slot of an living entity.
     */
    public static CommandItemSlot of(LivingEntity entity, EquipmentSlot equipmentSlot) {
        return CommandItemSlot.of(entity, equipmentSlot, (ItemStack stack) -> true);
    }

    /**
     * Gets the current item stack in this slot.
     */
    public ItemStack get();

    /**
     * Sets the {@code stack} to this slot.
     * 
     * @return {@code true} if the setting is successful, {@code false} if rejected
     * 
     * @param stack the item stack to set
     */
    public boolean set(ItemStack var1);
}

