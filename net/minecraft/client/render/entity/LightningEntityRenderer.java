/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class LightningEntityRenderer
extends EntityRenderer<LightningEntity> {
    public LightningEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_4034(LightningEntity lightningEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        float[] fs = new float[8];
        float[] gs = new float[8];
        float i = 0.0f;
        float j = 0.0f;
        Random random = new Random(lightningEntity.seed);
        for (int k = 7; k >= 0; --k) {
            fs[k] = i;
            gs[k] = j;
            i += (float)(random.nextInt(11) - 5);
            j += (float)(random.nextInt(11) - 5);
        }
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getLightning());
        Matrix4f matrix4f = matrixStack.peek();
        for (int l = 0; l < 4; ++l) {
            Random random2 = new Random(lightningEntity.seed);
            for (int m = 0; m < 3; ++m) {
                int n = 7;
                int o = 0;
                if (m > 0) {
                    n = 7 - m;
                }
                if (m > 0) {
                    o = n - 2;
                }
                float p = fs[n] - i;
                float q = gs[n] - j;
                for (int r = n; r >= o; --r) {
                    float s = p;
                    float t = q;
                    if (m == 0) {
                        p += (float)(random2.nextInt(11) - 5);
                        q += (float)(random2.nextInt(11) - 5);
                    } else {
                        p += (float)(random2.nextInt(31) - 15);
                        q += (float)(random2.nextInt(31) - 15);
                    }
                    float u = 0.5f;
                    float v = 0.45f;
                    float w = 0.45f;
                    float x = 0.5f;
                    float y = 0.1f + (float)l * 0.2f;
                    if (m == 0) {
                        y = (float)((double)y * ((double)r * 0.1 + 1.0));
                    }
                    float z = 0.1f + (float)l * 0.2f;
                    if (m == 0) {
                        z *= (float)(r - 1) * 0.1f + 1.0f;
                    }
                    LightningEntityRenderer.method_23183(matrix4f, vertexConsumer, p, q, r, s, t, 0.45f, 0.45f, 0.5f, y, z, false, false, true, false);
                    LightningEntityRenderer.method_23183(matrix4f, vertexConsumer, p, q, r, s, t, 0.45f, 0.45f, 0.5f, y, z, true, false, true, true);
                    LightningEntityRenderer.method_23183(matrix4f, vertexConsumer, p, q, r, s, t, 0.45f, 0.45f, 0.5f, y, z, true, true, false, true);
                    LightningEntityRenderer.method_23183(matrix4f, vertexConsumer, p, q, r, s, t, 0.45f, 0.45f, 0.5f, y, z, false, true, false, false);
                }
            }
        }
    }

    private static void method_23183(Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, int i, float h, float j, float k, float l, float m, float n, float o, boolean bl, boolean bl2, boolean bl3, boolean bl4) {
        vertexConsumer.vertex(matrix4f, f + (bl ? o : -o), i * 16, g + (bl2 ? o : -o)).color(k, l, m, 0.3f).next();
        vertexConsumer.vertex(matrix4f, h + (bl ? n : -n), (i + 1) * 16, j + (bl2 ? n : -n)).color(k, l, m, 0.3f).next();
        vertexConsumer.vertex(matrix4f, h + (bl3 ? n : -n), (i + 1) * 16, j + (bl4 ? n : -n)).color(k, l, m, 0.3f).next();
        vertexConsumer.vertex(matrix4f, f + (bl3 ? o : -o), i * 16, g + (bl4 ? o : -o)).color(k, l, m, 0.3f).next();
    }

    public Identifier method_4033(LightningEntity lightningEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

