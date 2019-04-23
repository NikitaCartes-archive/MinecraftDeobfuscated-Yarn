/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;

@Environment(value=EnvType.CLIENT)
public abstract class Overlay
extends DrawableHelper
implements Drawable {
    public boolean pausesGame() {
        return true;
    }
}

