/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public abstract class AbstractParentElement
extends DrawableHelper
implements ParentElement {
    @Nullable
    private Element focused;
    private boolean dragging;

    @Override
    public final boolean isDragging() {
        return this.dragging;
    }

    @Override
    public final void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    @Override
    @Nullable
    public Element getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable Element focused) {
        this.focused = focused;
    }
}

