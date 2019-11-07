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

    public void addPath(int i, Path path, float f) {
        this.paths.put(i, path);
        this.pathTimes.put(i, Util.getMeasuringTimeMs());
        this.field_4617.put(i, Float.valueOf(f));
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, double d, double e, double f, long l) {
        if (this.paths.isEmpty()) {
            return;
        }
        long m = Util.getMeasuringTimeMs();
        for (Integer integer : this.paths.keySet()) {
            Path path = this.paths.get(integer);
            float g = this.field_4617.get(integer).floatValue();
            PathfindingDebugRenderer.drawPath(path, g, true, true, d, e, f);
        }
        for (Integer integer2 : this.pathTimes.keySet().toArray(new Integer[0])) {
            if (m - this.pathTimes.get(integer2) <= 20000L) continue;
            this.paths.remove(integer2);
            this.pathTimes.remove(integer2);
        }
    }

    public static void drawPath(Path path, float f, boolean bl, boolean bl2, double d, double e, double g) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(0.0f, 1.0f, 0.0f, 0.75f);
        RenderSystem.disableTexture();
        RenderSystem.lineWidth(6.0f);
        PathfindingDebugRenderer.drawPathInternal(path, f, bl, bl2, d, e, g);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private static void drawPathInternal(Path path, float f, boolean bl, boolean bl2, double d, double e, double g) {
        int i;
        PathfindingDebugRenderer.drawPathLines(path, d, e, g);
        BlockPos blockPos = path.getTarget();
        if (PathfindingDebugRenderer.getManhattanDistance(blockPos, d, e, g) <= 40.0f) {
            DebugRenderer.drawBox(new Box((float)blockPos.getX() + 0.25f, (float)blockPos.getY() + 0.25f, (double)blockPos.getZ() + 0.25, (float)blockPos.getX() + 0.75f, (float)blockPos.getY() + 0.75f, (float)blockPos.getZ() + 0.75f).offset(-d, -e, -g), 0.0f, 1.0f, 0.0f, 0.5f);
            for (i = 0; i < path.getLength(); ++i) {
                PathNode pathNode = path.getNode(i);
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode.getPos(), d, e, g) <= 40.0f)) continue;
                float h = i == path.getCurrentNodeIndex() ? 1.0f : 0.0f;
                float j = i == path.getCurrentNodeIndex() ? 0.0f : 1.0f;
                DebugRenderer.drawBox(new Box((float)pathNode.x + 0.5f - f, (float)pathNode.y + 0.01f * (float)i, (float)pathNode.z + 0.5f - f, (float)pathNode.x + 0.5f + f, (float)pathNode.y + 0.25f + 0.01f * (float)i, (float)pathNode.z + 0.5f + f).offset(-d, -e, -g), h, 0.0f, j, 0.5f);
            }
        }
        if (bl) {
            for (PathNode pathNode2 : path.method_22881()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode2.getPos(), d, e, g) <= 40.0f)) continue;
                DebugRenderer.drawString(String.format("%s", new Object[]{pathNode2.type}), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.75, (double)pathNode2.z + 0.5, -65536);
                DebugRenderer.drawString(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode2.penalty)), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.25, (double)pathNode2.z + 0.5, -65536);
            }
            for (PathNode pathNode2 : path.method_22880()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode2.getPos(), d, e, g) <= 40.0f)) continue;
                DebugRenderer.drawString(String.format("%s", new Object[]{pathNode2.type}), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.75, (double)pathNode2.z + 0.5, -16776961);
                DebugRenderer.drawString(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode2.penalty)), (double)pathNode2.x + 0.5, (double)pathNode2.y + 0.25, (double)pathNode2.z + 0.5, -16776961);
            }
        }
        if (bl2) {
            for (i = 0; i < path.getLength(); ++i) {
                PathNode pathNode = path.getNode(i);
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode.getPos(), d, e, g) <= 40.0f)) continue;
                DebugRenderer.drawString(String.format("%s", new Object[]{pathNode.type}), (double)pathNode.x + 0.5, (double)pathNode.y + 0.75, (double)pathNode.z + 0.5, -1);
                DebugRenderer.drawString(String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode.penalty)), (double)pathNode.x + 0.5, (double)pathNode.y + 0.25, (double)pathNode.z + 0.5, -1);
            }
        }
    }

    public static void drawPathLines(Path path, double d, double e, double f) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(3, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < path.getLength(); ++i) {
            PathNode pathNode = path.getNode(i);
            if (PathfindingDebugRenderer.getManhattanDistance(pathNode.getPos(), d, e, f) > 40.0f) continue;
            float g = (float)i / (float)path.getLength() * 0.33f;
            int j = i == 0 ? 0 : MathHelper.hsvToRgb(g, 0.9f, 0.9f);
            int k = j >> 16 & 0xFF;
            int l = j >> 8 & 0xFF;
            int m = j & 0xFF;
            bufferBuilder.vertex((double)pathNode.x - d + 0.5, (double)pathNode.y - e + 0.5, (double)pathNode.z - f + 0.5).color(k, l, m, 255).next();
        }
        tessellator.draw();
    }

    private static float getManhattanDistance(BlockPos blockPos, double d, double e, double f) {
        return (float)(Math.abs((double)blockPos.getX() - d) + Math.abs((double)blockPos.getY() - e) + Math.abs((double)blockPos.getZ() - f));
    }
}

