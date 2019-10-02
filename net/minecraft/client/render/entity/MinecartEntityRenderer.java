/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class MinecartEntityRenderer<T extends AbstractMinecartEntity>
extends EntityRenderer<T> {
    private static final Identifier SKIN = new Identifier("textures/entity/minecart.png");
    protected final EntityModel<T> model = new MinecartEntityModel();

    public MinecartEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
        this.field_4673 = 0.7f;
    }

    public void method_4063(T abstractMinecartEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        super.render(abstractMinecartEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
        matrixStack.push();
        long l = (long)((Entity)abstractMinecartEntity).getEntityId() * 493286711L;
        l = l * l * 4392167121L + l * 98761L;
        float i = (((float)(l >> 16 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float j = (((float)(l >> 20 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        float k = (((float)(l >> 24 & 7L) + 0.5f) / 8.0f - 0.5f) * 0.004f;
        matrixStack.translate(i, j, k);
        double m = MathHelper.lerp((double)h, ((AbstractMinecartEntity)abstractMinecartEntity).prevRenderX, ((AbstractMinecartEntity)abstractMinecartEntity).x);
        double n = MathHelper.lerp((double)h, ((AbstractMinecartEntity)abstractMinecartEntity).prevRenderY, ((AbstractMinecartEntity)abstractMinecartEntity).y);
        double o = MathHelper.lerp((double)h, ((AbstractMinecartEntity)abstractMinecartEntity).prevRenderZ, ((AbstractMinecartEntity)abstractMinecartEntity).z);
        double p = 0.3f;
        Vec3d vec3d = ((AbstractMinecartEntity)abstractMinecartEntity).method_7508(m, n, o);
        float q = MathHelper.lerp(h, ((AbstractMinecartEntity)abstractMinecartEntity).prevPitch, ((AbstractMinecartEntity)abstractMinecartEntity).pitch);
        if (vec3d != null) {
            Vec3d vec3d2 = ((AbstractMinecartEntity)abstractMinecartEntity).method_7505(m, n, o, 0.3f);
            Vec3d vec3d3 = ((AbstractMinecartEntity)abstractMinecartEntity).method_7505(m, n, o, -0.3f);
            if (vec3d2 == null) {
                vec3d2 = vec3d;
            }
            if (vec3d3 == null) {
                vec3d3 = vec3d;
            }
            matrixStack.translate(vec3d.x - m, (vec3d2.y + vec3d3.y) / 2.0 - n, vec3d.z - o);
            Vec3d vec3d4 = vec3d3.add(-vec3d2.x, -vec3d2.y, -vec3d2.z);
            if (vec3d4.length() != 0.0) {
                vec3d4 = vec3d4.normalize();
                g = (float)(Math.atan2(vec3d4.z, vec3d4.x) * 180.0 / Math.PI);
                q = (float)(Math.atan(vec3d4.y) * 73.0);
            }
        }
        matrixStack.translate(0.0, 0.375, 0.0);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f - g, true));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(-q, true));
        float r = (float)((AbstractMinecartEntity)abstractMinecartEntity).getDamageWobbleTicks() - h;
        float s = ((AbstractMinecartEntity)abstractMinecartEntity).getDamageWobbleStrength() - h;
        if (s < 0.0f) {
            s = 0.0f;
        }
        if (r > 0.0f) {
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(MathHelper.sin(r) * r * s / 10.0f * (float)((AbstractMinecartEntity)abstractMinecartEntity).getDamageWobbleSide(), true));
        }
        int t = ((AbstractMinecartEntity)abstractMinecartEntity).getBlockOffset();
        int u = ((Entity)abstractMinecartEntity).getLightmapCoordinates();
        BlockState blockState = ((AbstractMinecartEntity)abstractMinecartEntity).getContainedBlock();
        if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            matrixStack.push();
            float v = 0.75f;
            matrixStack.scale(0.75f, 0.75f, 0.75f);
            matrixStack.translate(-0.5, (float)(t - 8) / 16.0f, 0.5);
            this.renderBlock(abstractMinecartEntity, h, blockState, matrixStack, layeredVertexConsumerStorage, u);
            matrixStack.pop();
        }
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        this.model.setAngles(abstractMinecartEntity, 0.0f, 0.0f, -0.1f, 0.0f, 0.0f, 0.0625f);
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(this.method_4065(abstractMinecartEntity)));
        OverlayTexture.clearDefaultOverlay(vertexConsumer);
        this.model.method_22957(matrixStack, vertexConsumer, u);
        vertexConsumer.clearDefaultOverlay();
        matrixStack.pop();
    }

    public Identifier method_4065(T abstractMinecartEntity) {
        return SKIN;
    }

    protected void renderBlock(T abstractMinecartEntity, float f, BlockState blockState, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i) {
        MinecraftClient.getInstance().getBlockRenderManager().renderDynamic(blockState, matrixStack, layeredVertexConsumerStorage, i, 0, 10);
    }
}

