/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.advancement;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public enum AdvancementObtainedStatus {
    OBTAINED(0),
    UNOBTAINED(1);

    private final int spriteIndex;

    private AdvancementObtainedStatus(int j) {
        this.spriteIndex = j;
    }

    public int getSpriteIndex() {
        return this.spriteIndex;
    }
}

