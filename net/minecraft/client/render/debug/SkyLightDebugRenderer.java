/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

@Environment(value=EnvType.CLIENT)
public class SkyLightDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;
    private static final int RANGE = 10;

    public SkyLightDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        ClientWorld world = this.client.world;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        BlockPos blockPos = new BlockPos(cameraX, cameraY, cameraZ);
        LongOpenHashSet longSet = new LongOpenHashSet();
        for (BlockPos blockPos2 : BlockPos.iterate(blockPos.add(-10, -10, -10), blockPos.add(10, 10, 10))) {
            int i = world.getLightLevel(LightType.SKY, blockPos2);
            float f = (float)(15 - i) / 15.0f * 0.5f + 0.16f;
            int j = MathHelper.hsvToRgb(f, 0.9f, 0.9f);
            long l = ChunkSectionPos.fromBlockPos(blockPos2.asLong());
            if (longSet.add(l)) {
                DebugRenderer.drawString(world.getChunkManager().getLightingProvider().displaySectionLevel(LightType.SKY, ChunkSectionPos.from(l)), ChunkSectionPos.getOffsetPos(ChunkSectionPos.unpackX(l), 8), ChunkSectionPos.getOffsetPos(ChunkSectionPos.unpackY(l), 8), ChunkSectionPos.getOffsetPos(ChunkSectionPos.unpackZ(l), 8), 0xFF0000, 0.3f);
            }
            if (i == 15) continue;
            DebugRenderer.drawString(String.valueOf(i), (double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.25, (double)blockPos2.getZ() + 0.5, j);
        }
        RenderSystem.enableTexture();
    }
}

