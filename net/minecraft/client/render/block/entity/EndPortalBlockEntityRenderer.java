/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class EndPortalBlockEntityRenderer
extends BlockEntityRenderer<EndPortalBlockEntity> {
    private static final Identifier SKY_TEX = new Identifier("textures/environment/end_sky.png");
    private static final Identifier PORTAL_TEX = new Identifier("textures/entity/end_portal.png");
    private static final Random RANDOM = new Random(31100L);
    private static final FloatBuffer field_4408 = GlAllocationUtils.allocateFloatBuffer(16);
    private static final FloatBuffer field_4404 = GlAllocationUtils.allocateFloatBuffer(16);
    private final FloatBuffer field_4403 = GlAllocationUtils.allocateFloatBuffer(16);

    public void method_3591(EndPortalBlockEntity endPortalBlockEntity, double d, double e, double f, float g, int i) {
        RenderSystem.disableLighting();
        RANDOM.setSeed(31100L);
        RenderSystem.getMatrix(2982, field_4408);
        RenderSystem.getMatrix(2983, field_4404);
        double h = d * d + e * e + f * f;
        int j = this.method_3592(h);
        float k = this.method_3594();
        boolean bl = false;
        GameRenderer gameRenderer = MinecraftClient.getInstance().gameRenderer;
        for (int l = 0; l < j; ++l) {
            RenderSystem.pushMatrix();
            float m = 2.0f / (float)(18 - l);
            if (l == 0) {
                this.bindTexture(SKY_TEX);
                m = 0.15f;
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
            }
            if (l >= 1) {
                this.bindTexture(PORTAL_TEX);
                bl = true;
                gameRenderer.setFogBlack(true);
            }
            if (l == 1) {
                RenderSystem.enableBlend();
                RenderSystem.blendFunc(GlStateManager.class_4535.ONE, GlStateManager.class_4534.ONE);
            }
            RenderSystem.texGenMode(GlStateManager.TexCoord.S, 9216);
            RenderSystem.texGenMode(GlStateManager.TexCoord.T, 9216);
            RenderSystem.texGenMode(GlStateManager.TexCoord.R, 9216);
            RenderSystem.texGenParam(GlStateManager.TexCoord.S, 9474, this.method_3593(1.0f, 0.0f, 0.0f, 0.0f));
            RenderSystem.texGenParam(GlStateManager.TexCoord.T, 9474, this.method_3593(0.0f, 1.0f, 0.0f, 0.0f));
            RenderSystem.texGenParam(GlStateManager.TexCoord.R, 9474, this.method_3593(0.0f, 0.0f, 1.0f, 0.0f));
            RenderSystem.enableTexGen(GlStateManager.TexCoord.S);
            RenderSystem.enableTexGen(GlStateManager.TexCoord.T);
            RenderSystem.enableTexGen(GlStateManager.TexCoord.R);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5890);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.5f, 0.5f, 0.0f);
            RenderSystem.scalef(0.5f, 0.5f, 1.0f);
            float n = l + 1;
            RenderSystem.translatef(17.0f / n, (2.0f + n / 1.5f) * ((float)(SystemUtil.getMeasuringTimeMs() % 800000L) / 800000.0f), 0.0f);
            RenderSystem.rotatef((n * n * 4321.0f + n * 9.0f) * 2.0f, 0.0f, 0.0f, 1.0f);
            RenderSystem.scalef(4.5f - n / 4.0f, 4.5f - n / 4.0f, 1.0f);
            RenderSystem.multMatrix(field_4404);
            RenderSystem.multMatrix(field_4408);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
            bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
            float o = (RANDOM.nextFloat() * 0.5f + 0.1f) * m;
            float p = (RANDOM.nextFloat() * 0.5f + 0.4f) * m;
            float q = (RANDOM.nextFloat() * 0.5f + 0.5f) * m;
            if (endPortalBlockEntity.shouldDrawSide(Direction.SOUTH)) {
                bufferBuilder.vertex(d, e, f + 1.0).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d + 1.0, e + 1.0, f + 1.0).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d, e + 1.0, f + 1.0).color(o, p, q, 1.0f).next();
            }
            if (endPortalBlockEntity.shouldDrawSide(Direction.NORTH)) {
                bufferBuilder.vertex(d, e + 1.0, f).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d + 1.0, e + 1.0, f).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d + 1.0, e, f).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d, e, f).color(o, p, q, 1.0f).next();
            }
            if (endPortalBlockEntity.shouldDrawSide(Direction.EAST)) {
                bufferBuilder.vertex(d + 1.0, e + 1.0, f).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d + 1.0, e + 1.0, f + 1.0).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d + 1.0, e, f).color(o, p, q, 1.0f).next();
            }
            if (endPortalBlockEntity.shouldDrawSide(Direction.WEST)) {
                bufferBuilder.vertex(d, e, f).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d, e, f + 1.0).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d, e + 1.0, f + 1.0).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d, e + 1.0, f).color(o, p, q, 1.0f).next();
            }
            if (endPortalBlockEntity.shouldDrawSide(Direction.DOWN)) {
                bufferBuilder.vertex(d, e, f).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d + 1.0, e, f).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d + 1.0, e, f + 1.0).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d, e, f + 1.0).color(o, p, q, 1.0f).next();
            }
            if (endPortalBlockEntity.shouldDrawSide(Direction.UP)) {
                bufferBuilder.vertex(d, e + (double)k, f + 1.0).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d + 1.0, e + (double)k, f + 1.0).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d + 1.0, e + (double)k, f).color(o, p, q, 1.0f).next();
                bufferBuilder.vertex(d, e + (double)k, f).color(o, p, q, 1.0f).next();
            }
            tessellator.draw();
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(5888);
            this.bindTexture(SKY_TEX);
        }
        RenderSystem.disableBlend();
        RenderSystem.disableTexGen(GlStateManager.TexCoord.S);
        RenderSystem.disableTexGen(GlStateManager.TexCoord.T);
        RenderSystem.disableTexGen(GlStateManager.TexCoord.R);
        RenderSystem.enableLighting();
        if (bl) {
            gameRenderer.setFogBlack(false);
        }
    }

    protected int method_3592(double d) {
        int i = d > 36864.0 ? 1 : (d > 25600.0 ? 3 : (d > 16384.0 ? 5 : (d > 9216.0 ? 7 : (d > 4096.0 ? 9 : (d > 1024.0 ? 11 : (d > 576.0 ? 13 : (d > 256.0 ? 14 : 15)))))));
        return i;
    }

    protected float method_3594() {
        return 0.75f;
    }

    private FloatBuffer method_3593(float f, float g, float h, float i) {
        this.field_4403.clear();
        this.field_4403.put(f).put(g).put(h).put(i);
        this.field_4403.flip();
        return this.field_4403;
    }
}

