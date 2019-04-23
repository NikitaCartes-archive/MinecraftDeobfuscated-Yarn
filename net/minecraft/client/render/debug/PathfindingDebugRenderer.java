/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PathfindingDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private final Map<Integer, Path> paths = Maps.newHashMap();
    private final Map<Integer, Float> field_4617 = Maps.newHashMap();
    private final Map<Integer, Long> pathTimes = Maps.newHashMap();
    private Camera camera;
    private double field_4621;
    private double field_4620;
    private double field_4619;

    public PathfindingDebugRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    public void addPath(int i, Path path, float f) {
        this.paths.put(i, path);
        this.pathTimes.put(i, SystemUtil.getMeasuringTimeMs());
        this.field_4617.put(i, Float.valueOf(f));
    }

    @Override
    public void render(long l) {
        if (this.paths.isEmpty()) {
            return;
        }
        this.camera = this.client.gameRenderer.getCamera();
        this.field_4621 = this.camera.getPos().x;
        this.field_4620 = this.camera.getPos().y;
        this.field_4619 = this.camera.getPos().z;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        GlStateManager.disableTexture();
        GlStateManager.lineWidth(6.0f);
        this.method_19698();
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void method_19698() {
        Path path;
        long l = SystemUtil.getMeasuringTimeMs();
        for (Integer integer : this.paths.keySet()) {
            path = this.paths.get(integer);
            float f = this.field_4617.get(integer).floatValue();
            this.method_3868(path);
            PathNode pathNode = path.method_48();
            if (this.method_3867(pathNode) > 40.0f) continue;
            DebugRenderer.method_19695(new BoundingBox((float)pathNode.x + 0.25f, (float)pathNode.y + 0.25f, (double)pathNode.z + 0.25, (float)pathNode.x + 0.75f, (float)pathNode.y + 0.75f, (float)pathNode.z + 0.75f).offset(-this.field_4621, -this.field_4620, -this.field_4619), 0.0f, 1.0f, 0.0f, 0.5f);
            for (int i = 0; i < path.getLength(); ++i) {
                PathNode pathNode2 = path.getNode(i);
                if (this.method_3867(pathNode2) > 40.0f) continue;
                float g = i == path.getCurrentNodeIndex() ? 1.0f : 0.0f;
                float h = i == path.getCurrentNodeIndex() ? 0.0f : 1.0f;
                DebugRenderer.method_19695(new BoundingBox((float)pathNode2.x + 0.5f - f, (float)pathNode2.y + 0.01f * (float)i, (float)pathNode2.z + 0.5f - f, (float)pathNode2.x + 0.5f + f, (float)pathNode2.y + 0.25f + 0.01f * (float)i, (float)pathNode2.z + 0.5f + f).offset(-this.field_4621, -this.field_4620, -this.field_4619), g, 0.0f, h, 0.5f);
            }
        }
        for (Integer integer : this.paths.keySet()) {
            path = this.paths.get(integer);
            for (PathNode pathNode2 : path.method_37()) {
                if (this.method_3867(pathNode2) > 40.0f) continue;
                DebugRenderer.method_3714(String.format("%s", new Object[]{pathNode2.type}), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.75, (double)pathNode2.z + 0.5, -65536);
                DebugRenderer.method_3714(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode2.field_43)), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.25, (double)pathNode2.z + 0.5, -65536);
            }
            for (PathNode pathNode2 : path.method_43()) {
                if (this.method_3867(pathNode2) > 40.0f) continue;
                DebugRenderer.method_3714(String.format("%s", new Object[]{pathNode2.type}), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.75, (double)pathNode2.z + 0.5, -16776961);
                DebugRenderer.method_3714(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode2.field_43)), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.25, (double)pathNode2.z + 0.5, -16776961);
            }
            for (int j = 0; j < path.getLength(); ++j) {
                PathNode pathNode = path.getNode(j);
                if (this.method_3867(pathNode) > 40.0f) continue;
                DebugRenderer.method_3714(String.format("%s", new Object[]{pathNode.type}), (double)pathNode.x + 0.5, (double)pathNode.y + 0.75, (double)pathNode.z + 0.5, -1);
                DebugRenderer.method_3714(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode.field_43)), (double)pathNode.x + 0.5, (double)pathNode.y + 0.25, (double)pathNode.z + 0.5, -1);
            }
        }
        for (Integer integer2 : this.pathTimes.keySet().toArray(new Integer[0])) {
            if (l - this.pathTimes.get(integer2) <= 20000L) continue;
            this.paths.remove(integer2);
            this.pathTimes.remove(integer2);
        }
    }

    public void method_3868(Path path) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < path.getLength(); ++i) {
            PathNode pathNode = path.getNode(i);
            if (this.method_3867(pathNode) > 40.0f) continue;
            float f = (float)i / (float)path.getLength() * 0.33f;
            int j = i == 0 ? 0 : MathHelper.hsvToRgb(f, 0.9f, 0.9f);
            int k = j >> 16 & 0xFF;
            int l = j >> 8 & 0xFF;
            int m = j & 0xFF;
            bufferBuilder.vertex((double)pathNode.x - this.field_4621 + 0.5, (double)pathNode.y - this.field_4620 + 0.5, (double)pathNode.z - this.field_4619 + 0.5).color(k, l, m, 255).next();
        }
        tessellator.draw();
    }

    private float method_3867(PathNode pathNode) {
        return (float)(Math.abs((double)pathNode.x - this.camera.getPos().x) + Math.abs((double)pathNode.y - this.camera.getPos().y) + Math.abs((double)pathNode.z - this.camera.getPos().z));
    }
}

