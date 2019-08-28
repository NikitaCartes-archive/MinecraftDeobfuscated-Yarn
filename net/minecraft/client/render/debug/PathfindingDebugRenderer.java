/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PathfindingDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private final Map<Integer, Path> paths = Maps.newHashMap();
    private final Map<Integer, Float> field_4617 = Maps.newHashMap();
    private final Map<Integer, Long> pathTimes = Maps.newHashMap();

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
        long m = SystemUtil.getMeasuringTimeMs();
        for (Integer integer : this.paths.keySet()) {
            Path path = this.paths.get(integer);
            float f = this.field_4617.get(integer).floatValue();
            PathfindingDebugRenderer.method_20556(this.method_20557(), path, f, true, true);
        }
        for (Integer integer2 : this.pathTimes.keySet().toArray(new Integer[0])) {
            if (m - this.pathTimes.get(integer2) <= 20000L) continue;
            this.paths.remove(integer2);
            this.pathTimes.remove(integer2);
        }
    }

    public static void method_20556(Camera camera, Path path, float f, boolean bl, boolean bl2) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
        RenderSystem.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6.0f);
        PathfindingDebugRenderer.method_20558(camera, path, f, bl, bl2);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private static void method_20558(Camera camera, Path path, float f, boolean bl, boolean bl2) {
        int i;
        PathfindingDebugRenderer.method_20555(camera, path);
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double g = camera.getPos().z;
        BlockPos blockPos = path.method_48();
        if (PathfindingDebugRenderer.method_20554(camera, blockPos) <= 40.0f) {
            DebugRenderer.method_19695(new Box((float)blockPos.getX() + 0.25f, (float)blockPos.getY() + 0.25f, (double)blockPos.getZ() + 0.25, (float)blockPos.getX() + 0.75f, (float)blockPos.getY() + 0.75f, (float)blockPos.getZ() + 0.75f).offset(-d, -e, -g), 0.0f, 1.0f, 0.0f, 0.5f);
            for (i = 0; i < path.getLength(); ++i) {
                PathNode pathNode = path.getNode(i);
                if (!(PathfindingDebugRenderer.method_20554(camera, pathNode.method_21652()) <= 40.0f)) continue;
                float h = i == path.getCurrentNodeIndex() ? 1.0f : 0.0f;
                float j = i == path.getCurrentNodeIndex() ? 0.0f : 1.0f;
                DebugRenderer.method_19695(new Box((float)pathNode.x + 0.5f - f, (float)pathNode.y + 0.01f * (float)i, (float)pathNode.z + 0.5f - f, (float)pathNode.x + 0.5f + f, (float)pathNode.y + 0.25f + 0.01f * (float)i, (float)pathNode.z + 0.5f + f).offset(-d, -e, -g), h, 0.0f, j, 0.5f);
            }
        }
        if (bl) {
            for (PathNode pathNode2 : path.method_37()) {
                if (!(PathfindingDebugRenderer.method_20554(camera, pathNode2.method_21652()) <= 40.0f)) continue;
                DebugRenderer.method_3714(String.format("%s", new Object[]{pathNode2.type}), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.75, (double)pathNode2.z + 0.5, -65536);
                DebugRenderer.method_3714(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode2.field_43)), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.25, (double)pathNode2.z + 0.5, -65536);
            }
            for (PathNode pathNode2 : path.method_43()) {
                if (!(PathfindingDebugRenderer.method_20554(camera, pathNode2.method_21652()) <= 40.0f)) continue;
                DebugRenderer.method_3714(String.format("%s", new Object[]{pathNode2.type}), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.75, (double)pathNode2.z + 0.5, -16776961);
                DebugRenderer.method_3714(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode2.field_43)), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.25, (double)pathNode2.z + 0.5, -16776961);
            }
        }
        if (bl2) {
            for (i = 0; i < path.getLength(); ++i) {
                PathNode pathNode = path.getNode(i);
                if (!(PathfindingDebugRenderer.method_20554(camera, pathNode.method_21652()) <= 40.0f)) continue;
                DebugRenderer.method_3714(String.format("%s", new Object[]{pathNode.type}), (double)pathNode.x + 0.5, (double)pathNode.y + 0.75, (double)pathNode.z + 0.5, -1);
                DebugRenderer.method_3714(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode.field_43)), (double)pathNode.x + 0.5, (double)pathNode.y + 0.25, (double)pathNode.z + 0.5, -1);
            }
        }
    }

    public static void method_20555(Camera camera, Path path) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < path.getLength(); ++i) {
            PathNode pathNode = path.getNode(i);
            if (PathfindingDebugRenderer.method_20554(camera, pathNode.method_21652()) > 40.0f) continue;
            float g = (float)i / (float)path.getLength() * 0.33f;
            int j = i == 0 ? 0 : MathHelper.hsvToRgb(g, 0.9f, 0.9f);
            int k = j >> 16 & 0xFF;
            int l = j >> 8 & 0xFF;
            int m = j & 0xFF;
            bufferBuilder.vertex((double)pathNode.x - d + 0.5, (double)pathNode.y - e + 0.5, (double)pathNode.z - f + 0.5).color(k, l, m, 255).next();
        }
        tessellator.draw();
    }

    private static float method_20554(Camera camera, BlockPos blockPos) {
        return (float)(Math.abs((double)blockPos.getX() - camera.getPos().x) + Math.abs((double)blockPos.getY() - camera.getPos().y) + Math.abs((double)blockPos.getZ() - camera.getPos().z));
    }

    private Camera method_20557() {
        return this.client.gameRenderer.getCamera();
    }
}

