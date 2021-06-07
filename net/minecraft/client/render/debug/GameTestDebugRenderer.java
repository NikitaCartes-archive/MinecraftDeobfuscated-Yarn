/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class GameTestDebugRenderer
implements DebugRenderer.Renderer {
    private static final float field_32901 = 0.02f;
    private final Map<BlockPos, Marker> markers = Maps.newHashMap();

    public void addMarker(BlockPos pos, int color, String message, int duration) {
        this.markers.put(pos, new Marker(color, message, Util.getMeasuringTimeMs() + (long)duration));
    }

    @Override
    public void clear() {
        this.markers.clear();
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        long l = Util.getMeasuringTimeMs();
        this.markers.entrySet().removeIf(entry -> l > ((Marker)entry.getValue()).removalTime);
        this.markers.forEach(this::method_23111);
    }

    private void method_23111(BlockPos blockPos, Marker marker) {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
        RenderSystem.setShaderColor(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        DebugRenderer.drawBox(blockPos, 0.02f, marker.getBlue(), marker.getGreen(), marker.getAlpha(), marker.getRed());
        if (!marker.message.isEmpty()) {
            double d = (double)blockPos.getX() + 0.5;
            double e = (double)blockPos.getY() + 1.2;
            double f = (double)blockPos.getZ() + 0.5;
            DebugRenderer.drawString(marker.message, d, e, f, -1, 0.01f, true, 0.0f, true);
        }
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    @Environment(value=EnvType.CLIENT)
    static class Marker {
        public int color;
        public String message;
        public long removalTime;

        public Marker(int color, String message, long removalTime) {
            this.color = color;
            this.message = message;
            this.removalTime = removalTime;
        }

        public float getBlue() {
            return (float)(this.color >> 16 & 0xFF) / 255.0f;
        }

        public float getGreen() {
            return (float)(this.color >> 8 & 0xFF) / 255.0f;
        }

        public float getAlpha() {
            return (float)(this.color & 0xFF) / 255.0f;
        }

        public float getRed() {
            return (float)(this.color >> 24 & 0xFF) / 255.0f;
        }
    }
}

