/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

@Environment(value=EnvType.CLIENT)
public enum PlayerModelPart {
    CAPE(0, "cape"),
    JACKET(1, "jacket"),
    LEFT_SLEEVE(2, "left_sleeve"),
    RIGHT_SLEEVE(3, "right_sleeve"),
    LEFT_PANTS_LEG(4, "left_pants_leg"),
    RIGHT_PANTS_LEG(5, "right_pants_leg"),
    HAT(6, "hat");

    private final int id;
    private final int bitFlag;
    private final String name;
    private final Component localizedName;

    private PlayerModelPart(int j, String string2) {
        this.id = j;
        this.bitFlag = 1 << j;
        this.name = string2;
        this.localizedName = new TranslatableComponent("options.modelPart." + string2, new Object[0]);
    }

    public int getBitFlag() {
        return this.bitFlag;
    }

    public String getName() {
        return this.name;
    }

    public Component getLocalizedName() {
        return this.localizedName;
    }
}

