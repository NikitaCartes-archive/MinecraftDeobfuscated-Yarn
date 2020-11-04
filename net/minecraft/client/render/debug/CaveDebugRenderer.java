/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class CaveDebugRenderer
implements DebugRenderer.Renderer {
    private final Map<BlockPos, BlockPos> field_4507 = Maps.newHashMap();
    private final Map<BlockPos, Float> field_4508 = Maps.newHashMap();
    private final List<BlockPos> field_4506 = Lists.newArrayList();

    public void method_3704(BlockPos blockPos, List<BlockPos> list, List<Float> list2) {
        for (int i = 0; i < list.size(); ++i) {
            this.field_4507.put(list.get(i), blockPos);
            this.field_4508.put(list.get(i), list2.get(i));
        }
        this.field_4506.add(blockPos);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        BlockPos blockPos = new BlockPos(cameraX, 0.0, cameraZ);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (Map.Entry<BlockPos, BlockPos> entry : this.field_4507.entrySet()) {
            BlockPos blockPos2 = entry.getKey();
            BlockPos blockPos3 = entry.getValue();
            float f = (float)(blockPos3.getX() * 128 % 256) / 256.0f;
            float g = (float)(blockPos3.getY() * 128 % 256) / 256.0f;
            float h = (float)(blockPos3.getZ() * 128 % 256) / 256.0f;
            float i = this.field_4508.get(blockPos2).floatValue();
            if (!blockPos.isWithinDistance(blockPos2, 160.0)) continue;
            WorldRenderer.drawBox(bufferBuilder, (double)((float)blockPos2.getX() + 0.5f) - cameraX - (double)i, (double)((float)blockPos2.getY() + 0.5f) - cameraY - (double)i, (double)((float)blockPos2.getZ() + 0.5f) - cameraZ - (double)i, (double)((float)blockPos2.getX() + 0.5f) - cameraX + (double)i, (double)((float)blockPos2.getY() + 0.5f) - cameraY + (double)i, (double)((float)blockPos2.getZ() + 0.5f) - cameraZ + (double)i, f, g, h, 0.5f);
        }
        for (BlockPos blockPos4 : this.field_4506) {
            if (!blockPos.isWithinDistance(blockPos4, 160.0)) continue;
            WorldRenderer.drawBox(bufferBuilder, (double)blockPos4.getX() - cameraX, (double)blockPos4.getY() - cameraY, (double)blockPos4.getZ() - cameraZ, (double)((float)blockPos4.getX() + 1.0f) - cameraX, (double)((float)blockPos4.getY() + 1.0f) - cameraY, (double)((float)blockPos4.getZ() + 1.0f) - cameraZ, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        tessellator.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}

