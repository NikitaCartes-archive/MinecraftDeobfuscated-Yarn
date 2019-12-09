/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

@Environment(value=EnvType.CLIENT)
public class HeightmapDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public HeightmapDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        ClientWorld iWorld = this.client.world;
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        BlockPos blockPos = new BlockPos(cameraX, 0.0, cameraZ);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-40, 0, -40), blockPos.add(40, 0, 40))) {
            int i;
            if (iWorld.getBlockState(blockPos2.add(0, i = iWorld.getTopY(Heightmap.Type.WORLD_SURFACE_WG, blockPos2.getX(), blockPos2.getZ()), 0).down()).isAir()) {
                WorldRenderer.drawBox(bufferBuilder, (double)((float)blockPos2.getX() + 0.25f) - cameraX, (double)i - cameraY, (double)((float)blockPos2.getZ() + 0.25f) - cameraZ, (double)((float)blockPos2.getX() + 0.75f) - cameraX, (double)i + 0.09375 - cameraY, (double)((float)blockPos2.getZ() + 0.75f) - cameraZ, 0.0f, 0.0f, 1.0f, 0.5f);
                continue;
            }
            WorldRenderer.drawBox(bufferBuilder, (double)((float)blockPos2.getX() + 0.25f) - cameraX, (double)i - cameraY, (double)((float)blockPos2.getZ() + 0.25f) - cameraZ, (double)((float)blockPos2.getX() + 0.75f) - cameraX, (double)i + 0.09375 - cameraY, (double)((float)blockPos2.getZ() + 0.75f) - cameraZ, 0.0f, 1.0f, 0.0f, 0.5f);
        }
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}

