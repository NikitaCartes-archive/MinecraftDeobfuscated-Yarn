/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.util.function.Predicate;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

public interface class_5630 {
    public static final class_5630 field_27860 = new class_5630(){

        @Override
        public ItemStack method_32327() {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean method_32332(ItemStack itemStack) {
            return false;
        }
    };

    public static class_5630 method_32329(final Inventory inventory, final int i, final Predicate<ItemStack> predicate) {
        return new class_5630(){

            @Override
            public ItemStack method_32327() {
                return inventory.getStack(i);
            }

            @Override
            public boolean method_32332(ItemStack itemStack) {
                if (!predicate.test(itemStack)) {
                    return false;
                }
                inventory.setStack(i, itemStack);
                return true;
            }
        };
    }

    public static class_5630 method_32328(Inventory inventory, int i) {
        return class_5630.method_32329(inventory, i, itemStack -> true);
    }

    public static class_5630 method_32331(final LivingEntity livingEntity, final EquipmentSlot equipmentSlot, final Predicate<ItemStack> predicate) {
        return new class_5630(){

            @Override
            public ItemStack method_32327() {
                return livingEntity.getEquippedStack(equipmentSlot);
            }

            @Override
            public boolean method_32332(ItemStack itemStack) {
                if (!predicate.test(itemStack)) {
                    return false;
                }
                livingEntity.equipStack(equipmentSlot, itemStack);
                return true;
            }
        };
    }

    public static class_5630 method_32330(LivingEntity livingEntity, EquipmentSlot equipmentSlot) {
        return class_5630.method_32331(livingEntity, equipmentSlot, itemStack -> true);
    }

    public ItemStack method_32327();

    public boolean method_32332(ItemStack var1);
}

