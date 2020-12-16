/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.block.entity;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class EndPortalBlockEntityRenderer<T extends EndPortalBlockEntity>
implements BlockEntityRenderer<T> {
    public static final Identifier SKY_TEXTURE = new Identifier("textures/environment/end_sky.png");
    public static final Identifier PORTAL_TEXTURE = new Identifier("textures/entity/end_portal.png");
    private static final Random RANDOM = new Random(31100L);
    private static final List<RenderLayer> PORTAL_RENDER_LAYERS = IntStream.range(0, 16).mapToObj(i -> RenderLayer.getEndPortal(i + 1)).collect(ImmutableList.toImmutableList());
    private final BlockEntityRenderDispatcher dispatcher;

    public EndPortalBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.dispatcher = ctx.getRenderDispatcher();
    }

    @Override
    public void render(T endPortalBlockEntity, float f, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j) {
        RANDOM.setSeed(31100L);
        double d = ((BlockEntity)endPortalBlockEntity).getPos().getSquaredDistance(this.dispatcher.camera.getPos(), true);
        int k = this.getDetailLevel(d);
        float g = this.getTopYOffset();
        Matrix4f matrix4f = matrixStack.peek().getModel();
        this.renderSides(endPortalBlockEntity, g, 0.15f, matrix4f, vertexConsumerProvider.getBuffer(PORTAL_RENDER_LAYERS.get(0)));
        for (int l = 1; l < k; ++l) {
            this.renderSides(endPortalBlockEntity, g, 2.0f / (float)(18 - l), matrix4f, vertexConsumerProvider.getBuffer(PORTAL_RENDER_LAYERS.get(l)));
        }
    }

    private void renderSides(T entity, float topYOffset, float brightness, Matrix4f model, VertexConsumer vertices) {
        float f = (RANDOM.nextFloat() * 0.5f + 0.1f) * brightness;
        float g = (RANDOM.nextFloat() * 0.5f + 0.4f) * brightness;
        float h = (RANDOM.nextFloat() * 0.5f + 0.5f) * brightness;
        this.renderSide(entity, model, vertices, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, f, g, h, Direction.SOUTH);
        this.renderSide(entity, model, vertices, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, f, g, h, Direction.NORTH);
        this.renderSide(entity, model, vertices, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, f, g, h, Direction.EAST);
        this.renderSide(entity, model, vertices, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, f, g, h, Direction.WEST);
        this.renderSide(entity, model, vertices, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, f, g, h, Direction.DOWN);
        this.renderSide(entity, model, vertices, 0.0f, 1.0f, topYOffset, topYOffset, 1.0f, 1.0f, 0.0f, 0.0f, f, g, h, Direction.UP);
    }

    private void renderSide(T entity, Matrix4f model, VertexConsumer vertices, float x1, float x2, float y1, float y2, float z1, float z2, float z3, float z4, float red, float green, float blue, Direction direction) {
        if (((EndPortalBlockEntity)entity).shouldDrawSide(direction)) {
            vertices.vertex(model, x1, y1, z1).color(red, green, blue, 1.0f).next();
            vertices.vertex(model, x2, y1, z2).color(red, green, blue, 1.0f).next();
            vertices.vertex(model, x2, y2, z3).color(red, green, blue, 1.0f).next();
            vertices.vertex(model, x1, y2, z4).color(red, green, blue, 1.0f).next();
        }
    }

    /**
     * Decides how many layers of texture to show on a portal block based on its distance from the camera.
     */
    protected int getDetailLevel(double distance) {
        if (distance > 36864.0) {
            return 1;
        }
        if (distance > 25600.0) {
            return 3;
        }
        if (distance > 16384.0) {
            return 5;
        }
        if (distance > 9216.0) {
            return 7;
        }
        if (distance > 4096.0) {
            return 9;
        }
        if (distance > 1024.0) {
            return 11;
        }
        if (distance > 576.0) {
            return 13;
        }
        if (distance > 256.0) {
            return 14;
        }
        return 15;
    }

    protected float getTopYOffset() {
        return 0.75f;
    }
}

