/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class RotatingCubeMapRenderer {
    private final MinecraftClient client;
    private final CubeMapRenderer cubeMap;
    private float pitch;
    private float yaw;

    public RotatingCubeMapRenderer(CubeMapRenderer cubeMap) {
        this.cubeMap = cubeMap;
        this.client = MinecraftClient.getInstance();
    }

    public void render(float delta, float alpha) {
        float f = (float)((double)delta * this.client.options.getPanoramaSpeed().getValue());
        this.pitch = RotatingCubeMapRenderer.wrapOnce(this.pitch + f * 0.1f, 360.0f);
        this.yaw = RotatingCubeMapRenderer.wrapOnce(this.yaw + f * 0.001f, (float)Math.PI * 2);
        this.cubeMap.draw(this.client, MathHelper.sin(this.yaw) * 5.0f + 25.0f, -this.pitch, alpha);
    }

    private static float wrapOnce(float a, float b) {
        return a > b ? a - b : a;
    }
}

