/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;

public class DyeableArmorItem
extends ArmorItem
implements DyeableItem {
    public DyeableArmorItem(ArmorMaterial armorMaterial, ArmorItem.Type type, Item.Settings settings) {
        super(armorMaterial, type, settings);
    }
}

