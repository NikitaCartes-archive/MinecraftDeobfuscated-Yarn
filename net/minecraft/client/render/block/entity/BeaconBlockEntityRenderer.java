/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(value=EnvType.CLIENT)
public class BeaconBlockEntityRenderer
extends BlockEntityRenderer<BeaconBlockEntity> {
    public static final Identifier BEAM_TEX = new Identifier("textures/entity/beacon_beam.png");

    public BeaconBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void method_3541(BeaconBlockEntity beaconBlockEntity, double d, double e, double f, float g, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i) {
        long l = beaconBlockEntity.getWorld().getTime();
        List<BeaconBlockEntity.BeamSegment> list = beaconBlockEntity.getBeamSegments();
        int j = 0;
        for (int k = 0; k < list.size(); ++k) {
            BeaconBlockEntity.BeamSegment beamSegment = list.get(k);
            BeaconBlockEntityRenderer.render(matrixStack, layeredVertexConsumerStorage, g, l, j, k == list.size() - 1 ? 1024 : beamSegment.getHeight(), beamSegment.getColor());
            j += beamSegment.getHeight();
        }
    }

    private static void render(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, float f, long l, int i, int j, float[] fs) {
        BeaconBlockEntityRenderer.renderLightBeam(matrixStack, layeredVertexConsumerStorage, BEAM_TEX, f, 1.0f, l, i, j, fs, 0.2f, 0.25f);
    }

    public static void renderLightBeam(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, Identifier identifier, float f, float g, long l, int i, int j, float[] fs, float h, float k) {
        int m = i + j;
        matrixStack.push();
        matrixStack.translate(0.5, 0.0, 0.5);
        float n = (float)Math.floorMod(l, 40L) + f;
        float o = j < 0 ? n : -n;
        float p = MathHelper.method_22450(o * 0.2f - (float)MathHelper.floor(o * 0.1f));
        float q = fs[0];
        float r = fs[1];
        float s = fs[2];
        matrixStack.push();
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(n * 2.25f - 45.0f, true));
        float t = 0.0f;
        float u = h;
        float v = h;
        float w = 0.0f;
        float x = -h;
        float y = 0.0f;
        float z = 0.0f;
        float aa = -h;
        float ab = 0.0f;
        float ac = 1.0f;
        float ad = -1.0f + p;
        float ae = (float)j * g * (0.5f / h) + ad;
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(identifier));
        OverlayTexture.clearDefaultOverlay(vertexConsumer);
        BeaconBlockEntityRenderer.method_22741(matrixStack, vertexConsumer, q, r, s, 1.0f, i, m, 0.0f, u, v, 0.0f, x, 0.0f, 0.0f, aa, 0.0f, 1.0f, ae, ad);
        vertexConsumer.clearDefaultOverlay();
        matrixStack.pop();
        t = -k;
        u = -k;
        v = k;
        w = -k;
        x = -k;
        y = k;
        z = k;
        aa = k;
        ab = 0.0f;
        ac = 1.0f;
        ad = -1.0f + p;
        ae = (float)j * g + ad;
        BeaconBlockEntityRenderer.method_22741(matrixStack, layeredVertexConsumerStorage.getBuffer(RenderLayer.BEACON_BEAM), q, r, s, 0.125f, i, m, t, u, v, w, x, y, z, aa, 0.0f, 1.0f, ae, ad);
        matrixStack.pop();
    }

    private static void method_22741(MatrixStack matrixStack, VertexConsumer vertexConsumer, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s, float t, float u, float v, float w) {
        Matrix4f matrix4f = matrixStack.peek();
        BeaconBlockEntityRenderer.method_22740(matrix4f, vertexConsumer, f, g, h, i, j, k, l, m, n, o, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(matrix4f, vertexConsumer, f, g, h, i, j, k, r, s, p, q, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(matrix4f, vertexConsumer, f, g, h, i, j, k, n, o, r, s, t, u, v, w);
        BeaconBlockEntityRenderer.method_22740(matrix4f, vertexConsumer, f, g, h, i, j, k, p, q, l, m, t, u, v, w);
    }

    private static void method_22740(Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s) {
        BeaconBlockEntityRenderer.method_23076(matrix4f, vertexConsumer, f, g, h, i, k, l, m, q, r);
        BeaconBlockEntityRenderer.method_23076(matrix4f, vertexConsumer, f, g, h, i, j, l, m, q, s);
        BeaconBlockEntityRenderer.method_23076(matrix4f, vertexConsumer, f, g, h, i, j, n, o, p, s);
        BeaconBlockEntityRenderer.method_23076(matrix4f, vertexConsumer, f, g, h, i, k, n, o, p, r);
    }

    private static void method_23076(Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, float h, float i, int j, float k, float l, float m, float n) {
        vertexConsumer.vertex(matrix4f, k, j, l).color(f, g, h, i).texture(m, n).light(0xF000F0).normal(0.0f, 1.0f, 0.0f).next();
    }

    public boolean method_3542(BeaconBlockEntity beaconBlockEntity) {
        return true;
    }
}

