/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.Vector3f;

@Environment(value=EnvType.CLIENT)
public class GuiLighting {
    private static final FloatBuffer buffer = GlAllocationUtils.allocateFloatBuffer(4);
    private static final Vector3f towardLight = GuiLighting.createNormalVector(0.2f, 1.0f, -0.7f);
    private static final Vector3f oppositeLight = GuiLighting.createNormalVector(-0.2f, 1.0f, 0.7f);

    private static Vector3f createNormalVector(float f, float g, float h) {
        Vector3f vector3f = new Vector3f(f, g, h);
        vector3f.reciprocal();
        return vector3f;
    }

    public static void disable() {
        RenderSystem.disableLighting();
        RenderSystem.disableLight(0);
        RenderSystem.disableLight(1);
        RenderSystem.disableColorMaterial();
    }

    public static void enable() {
        RenderSystem.enableLighting();
        RenderSystem.enableLight(0);
        RenderSystem.enableLight(1);
        RenderSystem.enableColorMaterial();
        RenderSystem.colorMaterial(1032, 5634);
        RenderSystem.light(16384, 4611, GuiLighting.singletonBuffer(towardLight.getX(), towardLight.getY(), towardLight.getZ(), 0.0f));
        float f = 0.6f;
        RenderSystem.light(16384, 4609, GuiLighting.singletonBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        RenderSystem.light(16384, 4608, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        RenderSystem.light(16384, 4610, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        RenderSystem.light(16385, 4611, GuiLighting.singletonBuffer(oppositeLight.getX(), oppositeLight.getY(), oppositeLight.getZ(), 0.0f));
        RenderSystem.light(16385, 4609, GuiLighting.singletonBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        RenderSystem.light(16385, 4608, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        RenderSystem.light(16385, 4610, GuiLighting.singletonBuffer(0.0f, 0.0f, 0.0f, 1.0f));
        RenderSystem.shadeModel(7424);
        float g = 0.4f;
        RenderSystem.lightModel(2899, GuiLighting.singletonBuffer(0.4f, 0.4f, 0.4f, 1.0f));
    }

    public static FloatBuffer singletonBuffer(float f, float g, float h, float i) {
        buffer.clear();
        buffer.put(f).put(g).put(h).put(i);
        buffer.flip();
        return buffer;
    }

    public static void enableForItems() {
        RenderSystem.pushMatrix();
        RenderSystem.rotatef(-30.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(165.0f, 1.0f, 0.0f, 0.0f);
        GuiLighting.enable();
        RenderSystem.popMatrix();
    }
}

