/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public class HorseArmorItem
extends Item {
    private final int bonus;
    private final String texture;

    public HorseArmorItem(int i, String string, Item.Settings settings) {
        super(settings);
        this.bonus = i;
        this.texture = "textures/entity/horse/armor/horse_armor_" + string + ".png";
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getHorseArmorTexture() {
        return new Identifier(this.texture);
    }

    public int getBonus() {
        return this.bonus;
    }
}

