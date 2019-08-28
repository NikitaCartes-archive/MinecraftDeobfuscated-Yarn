/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class CaveDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient field_4505;
    private final Map<BlockPos, BlockPos> field_4507 = Maps.newHashMap();
    private final Map<BlockPos, Float> field_4508 = Maps.newHashMap();
    private final List<BlockPos> field_4506 = Lists.newArrayList();

    public CaveDebugRenderer(MinecraftClient minecraftClient) {
        this.field_4505 = minecraftClient;
    }

    public void method_3704(BlockPos blockPos, List<BlockPos> list, List<Float> list2) {
        for (int i = 0; i < list.size(); ++i) {
            this.field_4507.put(list.get(i), blockPos);
            this.field_4508.put(list.get(i), list2.get(i));
        }
        this.field_4506.add(blockPos);
    }

    @Override
    public void render(long l) {
        Camera camera = this.field_4505.gameRenderer.getCamera();
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
        RenderSystem.disableTexture();
        BlockPos blockPos = new BlockPos(camera.getPos().x, 0.0, camera.getPos().z);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        for (Map.Entry<BlockPos, BlockPos> entry : this.field_4507.entrySet()) {
            BlockPos blockPos2 = entry.getKey();
            BlockPos blockPos3 = entry.getValue();
            float g = (float)(blockPos3.getX() * 128 % 256) / 256.0f;
            float h = (float)(blockPos3.getY() * 128 % 256) / 256.0f;
            float i = (float)(blockPos3.getZ() * 128 % 256) / 256.0f;
            float j = this.field_4508.get(blockPos2).floatValue();
            if (!blockPos.isWithinDistance(blockPos2, 160.0)) continue;
            WorldRenderer.buildBox(bufferBuilder, (double)((float)blockPos2.getX() + 0.5f) - d - (double)j, (double)((float)blockPos2.getY() + 0.5f) - e - (double)j, (double)((float)blockPos2.getZ() + 0.5f) - f - (double)j, (double)((float)blockPos2.getX() + 0.5f) - d + (double)j, (double)((float)blockPos2.getY() + 0.5f) - e + (double)j, (double)((float)blockPos2.getZ() + 0.5f) - f + (double)j, g, h, i, 0.5f);
        }
        for (BlockPos blockPos4 : this.field_4506) {
            if (!blockPos.isWithinDistance(blockPos4, 160.0)) continue;
            WorldRenderer.buildBox(bufferBuilder, (double)blockPos4.getX() - d, (double)blockPos4.getY() - e, (double)blockPos4.getZ() - f, (double)((float)blockPos4.getX() + 1.0f) - d, (double)((float)blockPos4.getY() + 1.0f) - e, (double)((float)blockPos4.getZ() + 1.0f) - f, 1.0f, 1.0f, 1.0f, 1.0f);
        }
        tessellator.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}

