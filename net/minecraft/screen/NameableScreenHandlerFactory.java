/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.text.Text;

public interface NameableScreenHandlerFactory
extends ScreenHandlerFactory {
    public Text getDisplayName();
}

