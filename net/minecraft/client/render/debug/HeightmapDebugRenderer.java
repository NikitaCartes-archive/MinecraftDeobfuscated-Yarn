/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.Chunk;

@Environment(value=EnvType.CLIENT)
public class HeightmapDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient client;

    public HeightmapDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        ClientWorld worldAccess = this.client.world;
        RenderSystem.pushMatrix();
        RenderSystem.disableBlend();
        RenderSystem.disableTexture();
        RenderSystem.enableDepthTest();
        BlockPos blockPos = new BlockPos(cameraX, 0.0, cameraZ);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        for (int i = -32; i <= 32; i += 16) {
            for (int j = -32; j <= 32; j += 16) {
                Chunk chunk = worldAccess.getChunk(blockPos.add(i, 0, j));
                for (Map.Entry<Heightmap.Type, Heightmap> entry : chunk.getHeightmaps()) {
                    Heightmap.Type type = entry.getKey();
                    ChunkPos chunkPos = chunk.getPos();
                    Vector3f vector3f = this.method_27037(type);
                    for (int k = 0; k < 16; ++k) {
                        for (int l = 0; l < 16; ++l) {
                            int m = chunkPos.x * 16 + k;
                            int n = chunkPos.z * 16 + l;
                            float f = (float)((double)((float)worldAccess.getTopY(type, m, n) + (float)type.ordinal() * 0.09375f) - cameraY);
                            WorldRenderer.drawBox(bufferBuilder, (double)((float)m + 0.25f) - cameraX, f, (double)((float)n + 0.25f) - cameraZ, (double)((float)m + 0.75f) - cameraX, f + 0.09375f, (double)((float)n + 0.75f) - cameraZ, vector3f.getX(), vector3f.getY(), vector3f.getZ(), 1.0f);
                        }
                    }
                }
            }
        }
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    private Vector3f method_27037(Heightmap.Type type) {
        switch (type) {
            case WORLD_SURFACE_WG: {
                return new Vector3f(1.0f, 1.0f, 0.0f);
            }
            case OCEAN_FLOOR_WG: {
                return new Vector3f(1.0f, 0.0f, 1.0f);
            }
            case WORLD_SURFACE: {
                return new Vector3f(0.0f, 0.7f, 0.0f);
            }
            case OCEAN_FLOOR: {
                return new Vector3f(0.0f, 0.0f, 0.5f);
            }
            case MOTION_BLOCKING: {
                return new Vector3f(0.0f, 0.3f, 0.3f);
            }
            case MOTION_BLOCKING_NO_LEAVES: {
                return new Vector3f(0.0f, 0.5f, 0.5f);
            }
        }
        return new Vector3f(0.0f, 0.0f, 0.0f);
    }
}

