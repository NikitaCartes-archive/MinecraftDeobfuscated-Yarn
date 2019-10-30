/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class EndPortalBlockEntityRenderer<T extends EndPortalBlockEntity>
extends BlockEntityRenderer<T> {
    public static final Identifier SKY_TEX = new Identifier("textures/environment/end_sky.png");
    public static final Identifier PORTAL_TEX = new Identifier("textures/entity/end_portal.png");
    private static final Random RANDOM = new Random(31100L);

    public EndPortalBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
        super(blockEntityRenderDispatcher);
    }

    public void method_3591(T endPortalBlockEntity, double d, double e, double f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        RANDOM.setSeed(31100L);
        double h = d * d + e * e + f * f;
        int k = this.method_3592(h);
        float l = this.method_3594();
        Matrix4f matrix4f = matrixStack.peekModel();
        this.method_23084(endPortalBlockEntity, l, 0.15f, matrix4f, vertexConsumerProvider.getBuffer(RenderLayer.getEndPortal(1)));
        for (int m = 1; m < k; ++m) {
            this.method_23084(endPortalBlockEntity, l, 2.0f / (float)(18 - m), matrix4f, vertexConsumerProvider.getBuffer(RenderLayer.getEndPortal(m + 1)));
        }
    }

    private void method_23084(T endPortalBlockEntity, float f, float g, Matrix4f matrix4f, VertexConsumer vertexConsumer) {
        float h = (RANDOM.nextFloat() * 0.5f + 0.1f) * g;
        float i = (RANDOM.nextFloat() * 0.5f + 0.4f) * g;
        float j = (RANDOM.nextFloat() * 0.5f + 0.5f) * g;
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, h, i, j, Direction.SOUTH);
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, h, i, j, Direction.NORTH);
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, h, i, j, Direction.EAST);
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, h, i, j, Direction.WEST);
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, h, i, j, Direction.DOWN);
        this.method_23085(endPortalBlockEntity, matrix4f, vertexConsumer, 0.0f, 1.0f, f, f, 1.0f, 1.0f, 0.0f, 0.0f, h, i, j, Direction.UP);
    }

    private void method_23085(T endPortalBlockEntity, Matrix4f matrix4f, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p, Direction direction) {
        if (((EndPortalBlockEntity)endPortalBlockEntity).shouldDrawSide(direction)) {
            vertexConsumer.vertex(matrix4f, f, h, j).color(n, o, p, 1.0f).next();
            vertexConsumer.vertex(matrix4f, g, h, k).color(n, o, p, 1.0f).next();
            vertexConsumer.vertex(matrix4f, g, i, l).color(n, o, p, 1.0f).next();
            vertexConsumer.vertex(matrix4f, f, i, m).color(n, o, p, 1.0f).next();
        }
    }

    protected int method_3592(double d) {
        int i = d > 36864.0 ? 1 : (d > 25600.0 ? 3 : (d > 16384.0 ? 5 : (d > 9216.0 ? 7 : (d > 4096.0 ? 9 : (d > 1024.0 ? 11 : (d > 576.0 ? 13 : (d > 256.0 ? 14 : 15)))))));
        return i;
    }

    protected float method_3594() {
        return 0.75f;
    }
}

