/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Narratable;

@Environment(value=EnvType.CLIENT)
public interface Selectable
extends Narratable {
    public SelectionType getType();

    @Environment(value=EnvType.CLIENT)
    public static enum SelectionType {
        NONE,
        HOVERED,
        FOCUSED;


        public boolean isFocused() {
            return this == FOCUSED;
        }
    }
}

