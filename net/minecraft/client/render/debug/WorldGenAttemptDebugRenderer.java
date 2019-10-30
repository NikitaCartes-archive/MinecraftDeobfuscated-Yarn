/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.debug;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
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
public class WorldGenAttemptDebugRenderer
implements DebugRenderer.Renderer {
    private final MinecraftClient field_4634;
    private final List<BlockPos> field_4640 = Lists.newArrayList();
    private final List<Float> field_4635 = Lists.newArrayList();
    private final List<Float> field_4637 = Lists.newArrayList();
    private final List<Float> field_4639 = Lists.newArrayList();
    private final List<Float> field_4636 = Lists.newArrayList();
    private final List<Float> field_4638 = Lists.newArrayList();

    public WorldGenAttemptDebugRenderer(MinecraftClient minecraftClient) {
        this.field_4634 = minecraftClient;
    }

    public void method_3872(BlockPos blockPos, float f, float g, float h, float i, float j) {
        this.field_4640.add(blockPos);
        this.field_4635.add(Float.valueOf(f));
        this.field_4637.add(Float.valueOf(j));
        this.field_4639.add(Float.valueOf(g));
        this.field_4636.add(Float.valueOf(h));
        this.field_4638.add(Float.valueOf(i));
    }

    @Override
    public void render(long l) {
        Camera camera = this.field_4634.gameRenderer.getCamera();
        double d = camera.getPos().x;
        double e = camera.getPos().y;
        double f = camera.getPos().z;
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(5, VertexFormats.POSITION_COLOR);
        for (int i = 0; i < this.field_4640.size(); ++i) {
            BlockPos blockPos = this.field_4640.get(i);
            Float float_ = this.field_4635.get(i);
            float g = float_.floatValue() / 2.0f;
            WorldRenderer.drawBox(bufferBuilder, (double)((float)blockPos.getX() + 0.5f - g) - d, (double)((float)blockPos.getY() + 0.5f - g) - e, (double)((float)blockPos.getZ() + 0.5f - g) - f, (double)((float)blockPos.getX() + 0.5f + g) - d, (double)((float)blockPos.getY() + 0.5f + g) - e, (double)((float)blockPos.getZ() + 0.5f + g) - f, this.field_4639.get(i).floatValue(), this.field_4636.get(i).floatValue(), this.field_4638.get(i).floatValue(), this.field_4637.get(i).floatValue());
        }
        tessellator.draw();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }
}

