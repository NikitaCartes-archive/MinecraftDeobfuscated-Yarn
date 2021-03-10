/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class PathfindingDebugRenderer
implements DebugRenderer.Renderer {
    private final Map<Integer, Path> paths = Maps.newHashMap();
    private final Map<Integer, Float> field_4617 = Maps.newHashMap();
    private final Map<Integer, Long> pathTimes = Maps.newHashMap();

    public void addPath(int id, Path path, float f) {
        this.paths.put(id, path);
        this.pathTimes.put(id, Util.getMeasuringTimeMs());
        this.field_4617.put(id, Float.valueOf(f));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        if (this.paths.isEmpty()) {
            return;
        }
        long l = Util.getMeasuringTimeMs();
        for (Integer integer : this.paths.keySet()) {
            Path path = this.paths.get(integer);
            float f = this.field_4617.get(integer).floatValue();
            PathfindingDebugRenderer.drawPath(path, f, true, true, cameraX, cameraY, cameraZ);
        }
        for (Integer integer2 : this.pathTimes.keySet().toArray(new Integer[0])) {
            if (l - this.pathTimes.get(integer2) <= 5000L) continue;
            this.paths.remove(integer2);
            this.pathTimes.remove(integer2);
        }
    }

    public static void drawPath(Path path, float nodeSize, boolean bl, boolean drawLabels, double cameraX, double cameraY, double cameraZ) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6.0f);
        PathfindingDebugRenderer.drawPathInternal(path, nodeSize, bl, drawLabels, cameraX, cameraY, cameraZ);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    private static void drawPathInternal(Path path, float nodeSize, boolean bl, boolean drawLabels, double cameraX, double cameraY, double cameraZ) {
        int i;
        PathfindingDebugRenderer.drawPathLines(path, cameraX, cameraY, cameraZ);
        BlockPos blockPos = path.getTarget();
        if (PathfindingDebugRenderer.getManhattanDistance(blockPos, cameraX, cameraY, cameraZ) <= 80.0f) {
            DebugRenderer.drawBox(new Box((float)blockPos.getX() + 0.25f, (float)blockPos.getY() + 0.25f, (double)blockPos.getZ() + 0.25, (float)blockPos.getX() + 0.75f, (float)blockPos.getY() + 0.75f, (float)blockPos.getZ() + 0.75f).offset(-cameraX, -cameraY, -cameraZ), 0.0f, 1.0f, 0.0f, 0.5f);
            for (i = 0; i < path.getLength(); ++i) {
                PathNode pathNode = path.getNode(i);
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode.getPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                float f = i == path.getCurrentNodeIndex() ? 1.0f : 0.0f;
                float g = i == path.getCurrentNodeIndex() ? 0.0f : 1.0f;
                DebugRenderer.drawBox(new Box((float)pathNode.x + 0.5f - nodeSize, (float)pathNode.y + 0.01f * (float)i, (float)pathNode.z + 0.5f - nodeSize, (float)pathNode.x + 0.5f + nodeSize, (float)pathNode.y + 0.25f + 0.01f * (float)i, (float)pathNode.z + 0.5f + nodeSize).offset(-cameraX, -cameraY, -cameraZ), f, 0.0f, g, 0.5f);
            }
        }
        if (bl) {
            for (PathNode pathNode2 : path.method_22881()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode2.getPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                DebugRenderer.drawBox(new Box((float)pathNode2.x + 0.5f - nodeSize / 2.0f, (float)pathNode2.y + 0.01f, (float)pathNode2.z + 0.5f - nodeSize / 2.0f, (float)pathNode2.x + 0.5f + nodeSize / 2.0f, (double)pathNode2.y + 0.1, (float)pathNode2.z + 0.5f + nodeSize / 2.0f).offset(-cameraX, -cameraY, -cameraZ), 1.0f, 0.8f, 0.8f, 0.5f);
            }
            for (PathNode pathNode2 : path.method_22880()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode2.getPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                DebugRenderer.drawBox(new Box((float)pathNode2.x + 0.5f - nodeSize / 2.0f, (float)pathNode2.y + 0.01f, (float)pathNode2.z + 0.5f - nodeSize / 2.0f, (float)pathNode2.x + 0.5f + nodeSize / 2.0f, (double)pathNode2.y + 0.1, (float)pathNode2.z + 0.5f + nodeSize / 2.0f).offset(-cameraX, -cameraY, -cameraZ), 0.8f, 1.0f, 1.0f, 0.5f);
            }
        }
        if (drawLabels) {
            for (i = 0; i < path.getLength(); ++i) {
                PathNode pathNode = path.getNode(i);
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode.getPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                DebugRenderer.drawString(String.format("%s", new Object[]{pathNode.type}), (double)pathNode.x + 0.5, (double)pathNode.y + 0.75, (double)pathNode.z + 0.5, -1, 0.02f, true, 0.0f, true);
                DebugRenderer.drawString(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode.penalty)), (double)pathNode.x + 0.5, (double)pathNode.y + 0.25, (double)pathNode.z + 0.5, -1, 0.02f, true, 0.0f, true);
            }
        }
    }

    public static void drawPathLines(Path path, double cameraX, double cameraY, double cameraZ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.LINE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < path.getLength(); ++i) {
            PathNode pathNode = path.getNode(i);
            if (PathfindingDebugRenderer.getManhattanDistance(pathNode.getPos(), cameraX, cameraY, cameraZ) > 80.0f) continue;
            float f = (float)i / (float)path.getLength() * 0.33f;
            int j = i == 0 ? 0 : MathHelper.hsvToRgb(f, 0.9f, 0.9f);
            int k = j >> 16 & 0xFF;
            int l = j >> 8 & 0xFF;
            int m = j & 0xFF;
            bufferBuilder.vertex((double)pathNode.x - cameraX + 0.5, (double)pathNode.y - cameraY + 0.5, (double)pathNode.z - cameraZ + 0.5).color(k, l, m, 255).next();
        }
        tessellator.draw();
    }

    private static float getManhattanDistance(BlockPos pos, double x, double y, double z) {
        return (float)(Math.abs((double)pos.getX() - x) + Math.abs((double)pos.getY() - y) + Math.abs((double)pos.getZ() - z));
    }
}

