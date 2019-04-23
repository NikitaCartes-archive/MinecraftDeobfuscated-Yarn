/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

@Environment(value=EnvType.CLIENT)
public class HeightmapDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public HeightmapDebugRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
    }

    @Override
    public void render(long l) {
        Camera camera = this.client.gameRenderer.getCamera();
        ClientWorld iWorld = this.client.world;
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.disableTexture();
        BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-40, 0, -40), blockPos.add(40, 0, 40))) {
            int i;
            if (iWorld.getBlockState(blockPos2.add(0, i = iWorld.getTop(Heightmap.Type.WORLD_SURFACE_WG, blockPos2.getX(), blockPos2.getZ()), 0).down()).isAir()) {
                WorldRenderer.buildBox(bufferBuilder, (double)((float)blockPos2.getX() + 0.25f) - d, (double)i - e, (double)((float)blockPos2.getZ() + 0.25f) - f, (double)((float)blockPos2.getX() + 0.75f) - d, (double)i + 0.09375 - e, (double)((float)blockPos2.getZ() + 0.75f) - f, 0.0f, 0.0f, 1.0f, 0.5f);
                continue;
            }
            WorldRenderer.buildBox(bufferBuilder, (double)((float)blockPos2.getX() + 0.25f) - d, (double)i - e, (double)((float)blockPos2.getZ() + 0.25f) - f, (double)((float)blockPos2.getX() + 0.75f) - d, (double)i + 0.09375 - e, (double)((float)blockPos2.getZ() + 0.75f) - f, 0.0f, 1.0f, 0.0f, 0.5f);
        }
        tessellator.draw();
        GlStateManager.enableTexture();
        GlStateManager.popMatrix();
    }
}

