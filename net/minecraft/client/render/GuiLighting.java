/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class GuiLighting {
    public static void method_22890() {
        RenderSystem.enableLighting();
        RenderSystem.enableColorMaterial();
    }

    public static void disable() {
        RenderSystem.disableLighting();
        RenderSystem.disableColorMaterial();
    }

    public static void enable() {
        RenderSystem.setupLevelDiffuseLighting();
    }

    public static void enableForItems() {
        RenderSystem.setupGuiDiffuseLighting();
    }
}

