/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class GuiLighting {
    public static void enable() {
        RenderSystem.enableLighting();
        RenderSystem.enableColorMaterial();
    }

    public static void disable() {
        RenderSystem.disableLighting();
        RenderSystem.disableColorMaterial();
    }

    public static void enable(Matrix4f matrix4f) {
        RenderSystem.setupLevelDiffuseLighting(matrix4f);
    }

    public static void enableForItems(Matrix4f matrix4f) {
        RenderSystem.setupGuiDiffuseLighting(matrix4f);
    }
}

