/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.inventory;

import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DefaultedList;

public class Inventories {
    public static ItemStack splitStack(List<ItemStack> list, int i, int j) {
        if (i < 0 || i >= list.size() || list.get(i).isEmpty() || j <= 0) {
            return ItemStack.EMPTY;
        }
        return list.get(i).split(j);
    }

    public static ItemStack removeStack(List<ItemStack> list, int i) {
        if (i < 0 || i >= list.size()) {
            return ItemStack.EMPTY;
        }
        return list.set(i, ItemStack.EMPTY);
    }

    public static CompoundTag toTag(CompoundTag compoundTag, DefaultedList<ItemStack> defaultedList) {
        return Inventories.toTag(compoundTag, defaultedList, true);
    }

    public static CompoundTag toTag(CompoundTag compoundTag, DefaultedList<ItemStack> defaultedList, boolean bl) {
        ListTag listTag = new ListTag();
        for (int i = 0; i < defaultedList.size(); ++i) {
            ItemStack itemStack = defaultedList.get(i);
            if (itemStack.isEmpty()) continue;
            CompoundTag compoundTag2 = new CompoundTag();
            compoundTag2.putByte("Slot", (byte)i);
            itemStack.toTag(compoundTag2);
            listTag.add(compoundTag2);
        }
        if (!listTag.isEmpty() || bl) {
            compoundTag.put("Items", listTag);
        }
        return compoundTag;
    }

    public static void fromTag(CompoundTag compoundTag, DefaultedList<ItemStack> defaultedList) {
        ListTag listTag = compoundTag.getList("Items", 10);
        for (int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag2 = listTag.getCompoundTag(i);
            int j = compoundTag2.getByte("Slot") & 0xFF;
            if (j < 0 || j >= defaultedList.size()) continue;
            defaultedList.set(j, ItemStack.fromTag(compoundTag2));
        }
    }
}

