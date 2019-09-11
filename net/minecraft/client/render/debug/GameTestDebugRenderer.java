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
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class GameTestDebugRenderer
implements DebugRenderer.Renderer {
    private final Map<BlockPos, Marker> markers = Maps.newHashMap();

    public void addMarker(BlockPos blockPos, int i, String string, int j) {
        this.markers.put(blockPos, new Marker(i, string, SystemUtil.getMeasuringTimeMs() + (long)j));
    }

    @Override
    public void clear() {
        this.markers.clear();
    }

    @Override
    public void render(long l) {
        long m = SystemUtil.getMeasuringTimeMs();
        this.markers.entrySet().removeIf(entry -> m > ((Marker)entry.getValue()).removalTime);
        this.markers.forEach(this::renderMarker);
    }

    private void renderMarker(BlockPos blockPos, Marker marker) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
        RenderSystem.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        DebugRenderer.method_19696(blockPos, 0.02f, marker.getRed(), marker.getGreen(), marker.getBlue(), marker.getAlpha());
        if (!marker.message.isEmpty()) {
            double d = (double)blockPos.getX() + 0.5;
            double e = (double)blockPos.getY() + 1.2;
            double f = (double)blockPos.getZ() + 0.5;
            DebugRenderer.drawFloatingText(marker.message, d, e, f, -1, 0.01f, true, 0.0f, true);
        }
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    @Environment(value=EnvType.CLIENT)
    static class Marker {
        public int color;
        public String message;
        public long removalTime;

        public Marker(int i, String string, long l) {
            this.color = i;
            this.message = string;
            this.removalTime = l;
        }

        public float getRed() {
            return (float)(this.color >> 16 & 0xFF) / 255.0f;
        }

        public float getGreen() {
            return (float)(this.color >> 8 & 0xFF) / 255.0f;
        }

        public float getBlue() {
            return (float)(this.color & 0xFF) / 255.0f;
        }

        public float getAlpha() {
            return (float)(this.color >> 24 & 0xFF) / 255.0f;
        }
    }
}

