/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class DiffuseLighting {
    public static void enable() {
        RenderSystem.enableLighting();
        RenderSystem.enableColorMaterial();
        RenderSystem.colorMaterial(1032, 5634);
    }

    public static void disable() {
        RenderSystem.disableLighting();
        RenderSystem.disableColorMaterial();
    }

    public static void enableForLevel(Matrix4f modelMatrix) {
        RenderSystem.setupLevelDiffuseLighting(modelMatrix);
    }

    public static void disableGuiDepthLighting() {
        RenderSystem.setupGuiFlatDiffuseLighting();
    }

    public static void enableGuiDepthLighting() {
        RenderSystem.setupGui3DDiffuseLighting();
    }
}

