/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
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
    private final Map<Integer, Float> nodeSizes = Maps.newHashMap();
    private final Map<Integer, Long> pathTimes = Maps.newHashMap();
    private static final long MAX_PATH_AGE = 5000L;
    private static final float RANGE = 80.0f;
    private static final boolean field_32908 = true;
    private static final boolean field_32909 = false;
    private static final boolean field_32910 = false;
    private static final boolean field_32911 = true;
    private static final boolean field_32912 = true;
    private static final float DRAWN_STRING_SIZE = 0.02f;

    public void addPath(int id, Path path, float nodeSize) {
        this.paths.put(id, path);
        this.pathTimes.put(id, Util.getMeasuringTimeMs());
        this.nodeSizes.put(id, Float.valueOf(nodeSize));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        if (this.paths.isEmpty()) {
            return;
        }
        long l = Util.getMeasuringTimeMs();
        for (Integer integer : this.paths.keySet()) {
            Path path = this.paths.get(integer);
            float f = this.nodeSizes.get(integer).floatValue();
            PathfindingDebugRenderer.drawPath(matrices, vertexConsumers, path, f, true, true, cameraX, cameraY, cameraZ);
        }
        for (Integer integer2 : this.pathTimes.keySet().toArray(new Integer[0])) {
            if (l - this.pathTimes.get(integer2) <= 5000L) continue;
            this.paths.remove(integer2);
            this.pathTimes.remove(integer2);
        }
    }

    public static void drawPath(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Path path, float nodeSize, boolean drawDebugNodes, boolean drawLabels, double cameraX, double cameraY, double cameraZ) {
        int i;
        PathfindingDebugRenderer.drawPathLines(matrices, vertexConsumers.getBuffer(RenderLayer.getDebugLineStrip(6.0)), path, cameraX, cameraY, cameraZ);
        BlockPos blockPos = path.getTarget();
        if (PathfindingDebugRenderer.getManhattanDistance(blockPos, cameraX, cameraY, cameraZ) <= 80.0f) {
            DebugRenderer.drawBox(matrices, vertexConsumers, new Box((float)blockPos.getX() + 0.25f, (float)blockPos.getY() + 0.25f, (double)blockPos.getZ() + 0.25, (float)blockPos.getX() + 0.75f, (float)blockPos.getY() + 0.75f, (float)blockPos.getZ() + 0.75f).offset(-cameraX, -cameraY, -cameraZ), 0.0f, 1.0f, 0.0f, 0.5f);
            for (i = 0; i < path.getLength(); ++i) {
                PathNode pathNode = path.getNode(i);
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                float f = i == path.getCurrentNodeIndex() ? 1.0f : 0.0f;
                float g = i == path.getCurrentNodeIndex() ? 0.0f : 1.0f;
                DebugRenderer.drawBox(matrices, vertexConsumers, new Box((float)pathNode.x + 0.5f - nodeSize, (float)pathNode.y + 0.01f * (float)i, (float)pathNode.z + 0.5f - nodeSize, (float)pathNode.x + 0.5f + nodeSize, (float)pathNode.y + 0.25f + 0.01f * (float)i, (float)pathNode.z + 0.5f + nodeSize).offset(-cameraX, -cameraY, -cameraZ), f, 0.0f, g, 0.5f);
            }
        }
        if (drawDebugNodes) {
            for (PathNode pathNode2 : path.getDebugSecondNodes()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode2.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                DebugRenderer.drawBox(matrices, vertexConsumers, new Box((float)pathNode2.x + 0.5f - nodeSize / 2.0f, (float)pathNode2.y + 0.01f, (float)pathNode2.z + 0.5f - nodeSize / 2.0f, (float)pathNode2.x + 0.5f + nodeSize / 2.0f, (double)pathNode2.y + 0.1, (float)pathNode2.z + 0.5f + nodeSize / 2.0f).offset(-cameraX, -cameraY, -cameraZ), 1.0f, 0.8f, 0.8f, 0.5f);
            }
            for (PathNode pathNode2 : path.getDebugNodes()) {
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode2.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                DebugRenderer.drawBox(matrices, vertexConsumers, new Box((float)pathNode2.x + 0.5f - nodeSize / 2.0f, (float)pathNode2.y + 0.01f, (float)pathNode2.z + 0.5f - nodeSize / 2.0f, (float)pathNode2.x + 0.5f + nodeSize / 2.0f, (double)pathNode2.y + 0.1, (float)pathNode2.z + 0.5f + nodeSize / 2.0f).offset(-cameraX, -cameraY, -cameraZ), 0.8f, 1.0f, 1.0f, 0.5f);
            }
        }
        if (drawLabels) {
            for (i = 0; i < path.getLength(); ++i) {
                PathNode pathNode = path.getNode(i);
                if (!(PathfindingDebugRenderer.getManhattanDistance(pathNode.getBlockPos(), cameraX, cameraY, cameraZ) <= 80.0f)) continue;
                DebugRenderer.drawString(matrices, vertexConsumers, String.valueOf((Object)pathNode.type), (double)pathNode.x + 0.5, (double)pathNode.y + 0.75, (double)pathNode.z + 0.5, -1, 0.02f, true, 0.0f, true);
                DebugRenderer.drawString(matrices, vertexConsumers, String.format(Locale.ROOT, "%.2f", Float.valueOf(pathNode.penalty)), (double)pathNode.x + 0.5, (double)pathNode.y + 0.25, (double)pathNode.z + 0.5, -1, 0.02f, true, 0.0f, true);
            }
        }
    }

    public static void drawPathLines(MatrixStack matrices, VertexConsumer vertexConsumers, Path path, double cameraX, double cameraY, double cameraZ) {
        for (int i = 0; i < path.getLength(); ++i) {
            PathNode pathNode = path.getNode(i);
            if (PathfindingDebugRenderer.getManhattanDistance(pathNode.getBlockPos(), cameraX, cameraY, cameraZ) > 80.0f) continue;
            float f = (float)i / (float)path.getLength() * 0.33f;
            int j = i == 0 ? 0 : MathHelper.hsvToRgb(f, 0.9f, 0.9f);
            int k = j >> 16 & 0xFF;
            int l = j >> 8 & 0xFF;
            int m = j & 0xFF;
            vertexConsumers.vertex(matrices.peek().getPositionMatrix(), (float)((double)pathNode.x - cameraX + 0.5), (float)((double)pathNode.y - cameraY + 0.5), (float)((double)pathNode.z - cameraZ + 0.5)).color(k, l, m, 255).next();
        }
    }

    private static float getManhattanDistance(BlockPos pos, double x, double y, double z) {
        return (float)(Math.abs((double)pos.getX() - x) + Math.abs((double)pos.getY() - y) + Math.abs((double)pos.getZ() - z));
    }
}

