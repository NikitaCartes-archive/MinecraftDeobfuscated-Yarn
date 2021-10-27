/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;

@Environment(value=EnvType.CLIENT)
public class WorldGenAttemptDebugRenderer
implements DebugRenderer.Renderer {
    private final List<BlockPos> positions = Lists.newArrayList();
    private final List<Float> sizes = Lists.newArrayList();
    private final List<Float> alphas = Lists.newArrayList();
    private final List<Float> reds = Lists.newArrayList();
    private final List<Float> greens = Lists.newArrayList();
    private final List<Float> blues = Lists.newArrayList();

    public void addBox(BlockPos pos, float size, float red, float green, float blue, float alpha) {
        this.positions.add(pos);
        this.sizes.add(Float.valueOf(size));
        this.alphas.add(Float.valueOf(alpha));
        this.reds.add(Float.valueOf(red));
        this.greens.add(Float.valueOf(green));
        this.blues.add(Float.valueOf(blue));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < this.positions.size(); ++i) {
            BlockPos blockPos = this.positions.get(i);
            Float float_ = this.sizes.get(i);
            float f = float_.floatValue() / 2.0f;
            WorldRenderer.drawBox(bufferBuilder, (double)((float)blockPos.getX() + 0.5f - f) - cameraX, (double)((float)blockPos.getY() + 0.5f - f) - cameraY, (double)((float)blockPos.getZ() + 0.5f - f) - cameraZ, (double)((float)blockPos.getX() + 0.5f + f) - cameraX, (double)((float)blockPos.getY() + 0.5f + f) - cameraY, (double)((float)blockPos.getZ() + 0.5f + f) - cameraZ, this.reds.get(i).floatValue(), this.greens.get(i).floatValue(), this.blues.get(i).floatValue(), this.alphas.get(i).floatValue());
        }
        tessellator.draw();
        RenderSystem.enableTexture();
    }
}

