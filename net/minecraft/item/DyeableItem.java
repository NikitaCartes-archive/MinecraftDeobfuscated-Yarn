/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public interface DyeableItem {
    default public boolean hasColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getSubTag("display");
        return compoundTag != null && compoundTag.containsKey("color", 99);
    }

    default public int getColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getSubTag("display");
        if (compoundTag != null && compoundTag.containsKey("color", 99)) {
            return compoundTag.getInt("color");
        }
        return 10511680;
    }

    default public void removeColor(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getSubTag("display");
        if (compoundTag != null && compoundTag.containsKey("color")) {
            compoundTag.remove("color");
        }
    }

    default public void setColor(ItemStack itemStack, int i) {
        itemStack.getOrCreateSubTag("display").putInt("color", i);
    }

    public static ItemStack blendAndSetColor(ItemStack itemStack, List<DyeItem> list) {
        int n;
        float h;
        ItemStack itemStack2 = ItemStack.EMPTY;
        int[] is = new int[3];
        int i = 0;
        int j = 0;
        DyeableItem dyeableItem = null;
        Item item = itemStack.getItem();
        if (item instanceof DyeableItem) {
            dyeableItem = (DyeableItem)((Object)item);
            itemStack2 = itemStack.copy();
            itemStack2.setCount(1);
            if (dyeableItem.hasColor(itemStack)) {
                int k = dyeableItem.getColor(itemStack2);
                float f = (float)(k >> 16 & 0xFF) / 255.0f;
                float g = (float)(k >> 8 & 0xFF) / 255.0f;
                h = (float)(k & 0xFF) / 255.0f;
                i = (int)((float)i + Math.max(f, Math.max(g, h)) * 255.0f);
                is[0] = (int)((float)is[0] + f * 255.0f);
                is[1] = (int)((float)is[1] + g * 255.0f);
                is[2] = (int)((float)is[2] + h * 255.0f);
                ++j;
            }
            for (DyeItem dyeItem : list) {
                float[] fs = dyeItem.getColor().getColorComponents();
                int l = (int)(fs[0] * 255.0f);
                int m = (int)(fs[1] * 255.0f);
                n = (int)(fs[2] * 255.0f);
                i += Math.max(l, Math.max(m, n));
                is[0] = is[0] + l;
                is[1] = is[1] + m;
                is[2] = is[2] + n;
                ++j;
            }
        }
        if (dyeableItem == null) {
            return ItemStack.EMPTY;
        }
        int k = is[0] / j;
        int o = is[1] / j;
        int p = is[2] / j;
        h = (float)i / (float)j;
        float q = Math.max(k, Math.max(o, p));
        k = (int)((float)k * h / q);
        o = (int)((float)o * h / q);
        p = (int)((float)p * h / q);
        n = k;
        n = (n << 8) + o;
        n = (n << 8) + p;
        dyeableItem.setColor(itemStack2, n);
        return itemStack2;
    }
}

