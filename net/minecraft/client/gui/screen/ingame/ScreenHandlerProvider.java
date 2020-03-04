/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.ingame;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.screen.ScreenHandler;

@Environment(value=EnvType.CLIENT)
public interface ScreenHandlerProvider<T extends ScreenHandler> {
    public T getScreenHandler();
}

