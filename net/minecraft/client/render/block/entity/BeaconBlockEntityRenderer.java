/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

@Environment(value=EnvType.CLIENT)
public class BeaconBlockEntityRenderer
extends BlockEntityRenderer<BeaconBlockEntity> {
    public static final Identifier BEAM_TEXTURE = new Identifier("textures/entity/beacon_beam.png");

    public BeaconBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    @Override
    public void render(BeaconBlockEntity beaconBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        long l = beaconBlockEntity.getWorld().getTime();
        List<BeaconBlockEntity.BeamSegment> list = beaconBlockEntity.getBeamSegments();
        int k = 0;
        for (int m = 0; m < list.size(); ++m) {
            BeaconBlockEntity.BeamSegment beamSegment = list.get(m);
            BeaconBlockEntityRenderer.renderBeam(matrixStack, vertexConsumerProvider, f, l, k, m == list.size() - 1 ? 1024 : beamSegment.getHeight(), beamSegment.getColor());
            k += beamSegment.getHeight();
        }
    }

    private static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, long worldTime, int yOffset, int maxY, float[] color) {
        BeaconBlockEntityRenderer.renderBeam(matrices, vertexConsumers, BEAM_TEXTURE, tickDelta, 1.0f, worldTime, yOffset, maxY, color, 0.2f, 0.25f);
    }

    public static void renderBeam(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier textureId, float tickDelta, float heightScale, long worldTime, int yOffset, int maxY, float[] color, float innerRadius, float outerRadius) {
        int i = yOffset + maxY;
        matrices.push();
        matrices.translate(0.5, 0.0, 0.5);
        float f = (float)Math.floorMod(worldTime, 40L) + tickDelta;
        float g = maxY < 0 ? f : -f;
        float h = MathHelper.fractionalPart(g * 0.2f - (float)MathHelper.floor(g * 0.1f));
        float j = color[0];
        float k = color[1];
        float l = color[2];
        matrices.push();
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(f * 2.25f - 45.0f));
        float m = 0.0f;
        float n = innerRadius;
        float o = innerRadius;
        float p = 0.0f;
        float q = -innerRadius;
        float r = 0.0f;
        float s = 0.0f;
        float t = -innerRadius;
        float u = 0.0f;
        float v = 1.0f;
        float w = -1.0f + h;
        float x = (float)maxY * heightScale * (0.5f / innerRadius) + w;
        BeaconBlockEntityRenderer.method_22741(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, false)), j, k, l, 1.0f, yOffset, i, 0.0f, n, o, 0.0f, q, 0.0f, 0.0f, t, 0.0f, 1.0f, x, w);
        matrices.pop();
        m = -outerRadius;
        n = -outerRadius;
        o = outerRadius;
        p = -outerRadius;
        q = -outerRadius;
        r = outerRadius;
        s = outerRadius;
        t = outerRadius;
        u = 0.0f;
        v = 1.0f;
        w = -1.0f + h;
        x = (float)maxY * heightScale + w;
        BeaconBlockEntityRenderer.method_22741(matrices, vertexConsumers.getBuffer(RenderLayer.getBeaconBeam(textureId, true)), j, k, l, 0.125f, yOffset, i, m, n, o, p, q, r, s, t, 0.0f, 1.0f, x, w);
        matrices.pop();
    }

    private static void method_22741(MatrixStack matrixStack, VertexConsumer vertexConsumer, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s, float t, float u, float v, float w) {
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getModel();
        Matrix3f matrix3f = entry.getNormal();
        BeaconBlockEntityRenderer.method_22740(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, k, l, m, n, o, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, k, r, s, p, q, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, k, n, o, r, s, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, k, p, q, l, m, t, u, v, w);
    }

    private static void method_22740(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s) {
        BeaconBlockEntityRenderer.method_23076(matrix4f, matrix3f, vertexConsumer, f, g, h, i, k, l, m, q, r);
        BeaconBlockEntityRenderer.method_23076(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, l, m, q, s);
        BeaconBlockEntityRenderer.method_23076(matrix4f, matrix3f, vertexConsumer, f, g, h, i, j, n, o, p, s);
        BeaconBlockEntityRenderer.method_23076(matrix4f, matrix3f, vertexConsumer, f, g, h, i, k, n, o, p, r);
    }

    private static void method_23076(Matrix4f matrix4f, Matrix3f matrix3f, VertexConsumer vertexConsumer, float f, float g, float h, float i, int j, float k, float l, float m, float n) {
        vertexConsumer.vertex(matrix4f, k, j, l).color(f, g, h, i).texture(m, n).overlay(OverlayTexture.DEFAULT_UV).light(0xF000F0).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public boolean rendersOutsideBoundingBox(BeaconBlockEntity beaconBlockEntity) {
        return true;
    }
}

