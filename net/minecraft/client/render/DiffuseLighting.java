/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Environment(value=EnvType.CLIENT)
public class DiffuseLighting {
    private static final Vector3f field_24426 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    private static final Vector3f field_24427 = new Vector3f(-0.2f, 1.0f, 0.7f).normalize();
    private static final Vector3f field_24428 = new Vector3f(0.2f, 1.0f, -0.7f).normalize();
    private static final Vector3f field_24429 = new Vector3f(-0.2f, -1.0f, 0.7f).normalize();
    private static final Vector3f field_29567 = new Vector3f(0.2f, -1.0f, -1.0f).normalize();
    private static final Vector3f field_29568 = new Vector3f(-0.2f, -1.0f, 0.0f).normalize();

    public static void enableForLevel(Matrix4f positionMatrix) {
        RenderSystem.setupLevelDiffuseLighting(field_24428, field_24429, positionMatrix);
    }

    public static void disableForLevel(Matrix4f positionMatrix) {
        RenderSystem.setupLevelDiffuseLighting(field_24426, field_24427, positionMatrix);
    }

    public static void disableGuiDepthLighting() {
        RenderSystem.setupGuiFlatDiffuseLighting(field_24426, field_24427);
    }

    public static void enableGuiDepthLighting() {
        RenderSystem.setupGui3DDiffuseLighting(field_24426, field_24427);
    }

    public static void method_34742() {
        RenderSystem.setShaderLights(field_29567, field_29568);
    }
}

