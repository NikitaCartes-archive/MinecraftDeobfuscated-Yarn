/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

@Environment(value=EnvType.CLIENT)
public class BlockOutlineDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public BlockOutlineDebugRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void render(long l) {
        Camera camera = this.client.gameRenderer.getCamera();
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        World blockView = this.client.player.world;
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.lineWidth(2.0f);
        GlStateManager.disableTexture();
        GlStateManager.depthMask(false);
        BlockPos blockPos = new BlockPos(camera.getPos());
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-6, -6, -6), blockPos.add(6, 6, 6))) {
            BlockState blockState = blockView.getBlockState(blockPos2);
            if (blockState.getBlock() == Blocks.AIR) continue;
            VoxelShape voxelShape = blockState.getOutlineShape(blockView, blockPos2);
            for (Box box : voxelShape.getBoundingBoxes()) {
                BufferBuilder bufferBuilder;
                Tessellator tessellator;
                Box box2 = box.offset(blockPos2).expand(0.002).offset(-d, -e, -f);
                double g = box2.minX;
                double h = box2.minY;
                double i = box2.minZ;
                double j = box2.maxX;
                double k = box2.maxY;
                double m = box2.maxZ;
                float n = 1.0f;
                float o = 0.0f;
                float p = 0.0f;
                float q = 0.5f;
                if (blockState.method_20827(blockView, blockPos2, Direction.WEST)) {
                    tessellator = Tessellator.getInstance();
                    bufferBuilder = tessellator.getBufferBuilder();
                    bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
                    bufferBuilder.vertex(g, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(g, h, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(g, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(g, k, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    tessellator.draw();
                }
                if (blockState.method_20827(blockView, blockPos2, Direction.SOUTH)) {
                    tessellator = Tessellator.getInstance();
                    bufferBuilder = tessellator.getBufferBuilder();
                    bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
                    bufferBuilder.vertex(g, k, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(g, h, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(j, k, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(j, h, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    tessellator.draw();
                }
                if (blockState.method_20827(blockView, blockPos2, Direction.EAST)) {
                    tessellator = Tessellator.getInstance();
                    bufferBuilder = tessellator.getBufferBuilder();
                    bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
                    bufferBuilder.vertex(j, h, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(j, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(j, k, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(j, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    tessellator.draw();
                }
                if (blockState.method_20827(blockView, blockPos2, Direction.NORTH)) {
                    tessellator = Tessellator.getInstance();
                    bufferBuilder = tessellator.getBufferBuilder();
                    bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
                    bufferBuilder.vertex(j, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(j, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(g, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(g, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    tessellator.draw();
                }
                if (blockState.method_20827(blockView, blockPos2, Direction.DOWN)) {
                    tessellator = Tessellator.getInstance();
                    bufferBuilder = tessellator.getBufferBuilder();
                    bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
                    bufferBuilder.vertex(g, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(j, h, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(g, h, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    bufferBuilder.vertex(j, h, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                    tessellator.draw();
                }
                if (!blockState.method_20827(blockView, blockPos2, Direction.UP)) continue;
                tessellator = Tessellator.getInstance();
                bufferBuilder = tessellator.getBufferBuilder();
                bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(g, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                bufferBuilder.vertex(g, k, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                bufferBuilder.vertex(j, k, i).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                bufferBuilder.vertex(j, k, m).color(1.0f, 0.0f, 0.0f, 0.5f).next();
                tessellator.draw();
            }
        }
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture();
        GlStateManager.disableBlend();
    }
}

