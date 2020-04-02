/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.shape.VoxelShape;

@Environment(value=EnvType.CLIENT)
public class CollisionDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private double lastUpdateTime = Double.MIN_VALUE;
    private List<VoxelShape> collisions = Collections.emptyList();

    public CollisionDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        double d = Util.getMeasuringTimeNano();
        if (d - this.lastUpdateTime > 1.0E8) {
            this.lastUpdateTime = d;
            Entity entity2 = this.client.gameRenderer.getCamera().getFocusedEntity();
            this.collisions = entity2.world.getCollisions(entity2, entity2.getBoundingBox().expand(6.0), entity -> true).collect(Collectors.toList());
        }
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLines());
        for (VoxelShape voxelShape : this.collisions) {
            WorldRenderer.method_22983(matrices, vertexConsumer, voxelShape, -cameraX, -cameraY, -cameraZ, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }
}

