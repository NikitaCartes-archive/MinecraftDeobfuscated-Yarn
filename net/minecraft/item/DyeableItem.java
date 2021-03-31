/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import java.util.List;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

public interface DyeableItem {
    public static final String COLOR_KEY = "color";
    public static final String DISPLAY_KEY = "display";
    public static final int DEFAULT_COLOR = 10511680;

    default public boolean hasColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubTag(DISPLAY_KEY);
        return nbtCompound != null && nbtCompound.contains(COLOR_KEY, 99);
    }

    default public int getColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubTag(DISPLAY_KEY);
        if (nbtCompound != null && nbtCompound.contains(COLOR_KEY, 99)) {
            return nbtCompound.getInt(COLOR_KEY);
        }
        return 10511680;
    }

    default public void removeColor(ItemStack stack) {
        NbtCompound nbtCompound = stack.getSubTag(DISPLAY_KEY);
        if (nbtCompound != null && nbtCompound.contains(COLOR_KEY)) {
            nbtCompound.remove(COLOR_KEY);
        }
    }

    default public void setColor(ItemStack stack, int color) {
        stack.getOrCreateSubTag(DISPLAY_KEY).putInt(COLOR_KEY, color);
    }

    public static ItemStack blendAndSetColor(ItemStack stack, List<DyeItem> colors) {
        int n;
        float h;
        ItemStack itemStack = ItemStack.EMPTY;
        int[] is = new int[3];
        int i = 0;
        int j = 0;
        DyeableItem dyeableItem = null;
        Item item = stack.getItem();
        if (item instanceof DyeableItem) {
            dyeableItem = (DyeableItem)((Object)item);
            itemStack = stack.copy();
            itemStack.setCount(1);
            if (dyeableItem.hasColor(stack)) {
                int k = dyeableItem.getColor(itemStack);
                float f = (float)(k >> 16 & 0xFF) / 255.0f;
                float g = (float)(k >> 8 & 0xFF) / 255.0f;
                h = (float)(k & 0xFF) / 255.0f;
                i = (int)((float)i + Math.max(f, Math.max(g, h)) * 255.0f);
                is[0] = (int)((float)is[0] + f * 255.0f);
                is[1] = (int)((float)is[1] + g * 255.0f);
                is[2] = (int)((float)is[2] + h * 255.0f);
                ++j;
            }
            for (DyeItem dyeItem : colors) {
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
        dyeableItem.setColor(itemStack, n);
        return itemStack;
    }
}

