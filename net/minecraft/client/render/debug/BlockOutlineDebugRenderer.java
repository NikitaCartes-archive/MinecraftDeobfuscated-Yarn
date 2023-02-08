/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class BlockOutlineDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public BlockOutlineDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        World blockView = this.client.player.world;
        BlockPos blockPos = new BlockPos(cameraX, cameraY, cameraZ);
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-6, -6, -6), blockPos.add(6, 6, 6))) {
            BlockState blockState = blockView.getBlockState(blockPos2);
            if (blockState.isOf(Blocks.AIR)) continue;
            VoxelShape voxelShape = blockState.getOutlineShape(blockView, blockPos2);
            for (Box box : voxelShape.getBoundingBoxes()) {
                VertexConsumer vertexConsumer;
                Box box2 = box.offset(blockPos2).expand(0.002);
                float f = (float)(box2.minX - cameraX);
                float g = (float)(box2.minY - cameraY);
                float h = (float)(box2.minZ - cameraZ);
                float i = (float)(box2.maxX - cameraX);
                float j = (float)(box2.maxY - cameraY);
                float k = (float)(box2.maxZ - cameraZ);
                float l = 1.0f;
                float m = 0.0f;
                float n = 0.0f;
                float o = 0.5f;
                if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.WEST)) {
                    vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
                    vertexConsumer.vertex(matrix4f, f, g, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, f, g, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, f, j, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, f, j, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                }
                if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.SOUTH)) {
                    vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
                    vertexConsumer.vertex(matrix4f, f, j, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, f, g, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, i, j, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, i, g, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                }
                if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.EAST)) {
                    vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
                    vertexConsumer.vertex(matrix4f, i, g, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, i, g, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, i, j, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, i, j, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                }
                if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.NORTH)) {
                    vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
                    vertexConsumer.vertex(matrix4f, i, j, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, i, g, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, f, j, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, f, g, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                }
                if (blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.DOWN)) {
                    vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
                    vertexConsumer.vertex(matrix4f, f, g, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, i, g, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, f, g, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    vertexConsumer.vertex(matrix4f, i, g, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                }
                if (!blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.UP)) continue;
                vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getDebugFilledBox());
                vertexConsumer.vertex(matrix4f, f, j, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                vertexConsumer.vertex(matrix4f, f, j, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                vertexConsumer.vertex(matrix4f, i, j, h).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                vertexConsumer.vertex(matrix4f, i, j, k).color(1.0f, 0.0f, 0.0f, 0.5f).next();
            }
        }
    }
}

