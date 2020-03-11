/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class FishingBobberEntityRenderer
extends EntityRenderer<FishingBobberEntity> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/fishing_hook.png");
    private static final RenderLayer LAYER = RenderLayer.getEntityCutout(TEXTURE);

    public FishingBobberEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    @Override
    public void render(FishingBobberEntity fishingBobberEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        double s;
        float r;
        double q;
        double p;
        double o;
        PlayerEntity playerEntity = fishingBobberEntity.getOwner();
        if (playerEntity == null) {
            return;
        }
        matrixStack.push();
        matrixStack.push();
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getModel();
        Matrix3f matrix3f = entry.getNormal();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        FishingBobberEntityRenderer.method_23840(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1);
        FishingBobberEntityRenderer.method_23840(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1);
        FishingBobberEntityRenderer.method_23840(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0);
        FishingBobberEntityRenderer.method_23840(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0);
        matrixStack.pop();
        int j = playerEntity.getMainArm() == Arm.RIGHT ? 1 : -1;
        ItemStack itemStack = playerEntity.getMainHandStack();
        if (itemStack.getItem() != Items.FISHING_ROD) {
            j = -j;
        }
        float h = playerEntity.getHandSwingProgress(g);
        float k = MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
        float l = MathHelper.lerp(g, playerEntity.prevBodyYaw, playerEntity.bodyYaw) * ((float)Math.PI / 180);
        double d = MathHelper.sin(l);
        double e = MathHelper.cos(l);
        double m = (double)j * 0.35;
        double n = 0.8;
        if (this.dispatcher.gameOptions != null && this.dispatcher.gameOptions.perspective > 0 || playerEntity != MinecraftClient.getInstance().player) {
            o = MathHelper.lerp((double)g, playerEntity.prevX, playerEntity.getX()) - e * m - d * 0.8;
            p = playerEntity.prevY + (double)playerEntity.getStandingEyeHeight() + (playerEntity.getY() - playerEntity.prevY) * (double)g - 0.45;
            q = MathHelper.lerp((double)g, playerEntity.prevZ, playerEntity.getZ()) - d * m + e * 0.8;
            r = playerEntity.isInSneakingPose() ? -0.1875f : 0.0f;
        } else {
            s = this.dispatcher.gameOptions.fov;
            Vec3d vec3d = new Vec3d((double)j * -0.36 * (s /= 100.0), -0.045 * s, 0.4);
            vec3d = vec3d.rotateX(-MathHelper.lerp(g, playerEntity.prevPitch, playerEntity.pitch) * ((float)Math.PI / 180));
            vec3d = vec3d.rotateY(-MathHelper.lerp(g, playerEntity.prevYaw, playerEntity.yaw) * ((float)Math.PI / 180));
            vec3d = vec3d.rotateY(k * 0.5f);
            vec3d = vec3d.rotateX(-k * 0.7f);
            o = MathHelper.lerp((double)g, playerEntity.prevX, playerEntity.getX()) + vec3d.x;
            p = MathHelper.lerp((double)g, playerEntity.prevY, playerEntity.getY()) + vec3d.y;
            q = MathHelper.lerp((double)g, playerEntity.prevZ, playerEntity.getZ()) + vec3d.z;
            r = playerEntity.getStandingEyeHeight();
        }
        s = MathHelper.lerp((double)g, fishingBobberEntity.prevX, fishingBobberEntity.getX());
        double t = MathHelper.lerp((double)g, fishingBobberEntity.prevY, fishingBobberEntity.getY()) + 0.25;
        double u = MathHelper.lerp((double)g, fishingBobberEntity.prevZ, fishingBobberEntity.getZ());
        float v = (float)(o - s);
        float w = (float)(p - t) + r;
        float x = (float)(q - u);
        VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
        Matrix4f matrix4f2 = matrixStack.peek().getModel();
        int y = 16;
        for (int z = 0; z < 16; ++z) {
            FishingBobberEntityRenderer.method_23172(v, w, x, vertexConsumer2, matrix4f2, FishingBobberEntityRenderer.method_23954(z, 16));
            FishingBobberEntityRenderer.method_23172(v, w, x, vertexConsumer2, matrix4f2, FishingBobberEntityRenderer.method_23954(z + 1, 16));
        }
        matrixStack.pop();
        super.render(fishingBobberEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private static float method_23954(int i, int j) {
        return (float)i / (float)j;
    }

    private static void method_23840(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int i, float f, int j, int k, int l) {
        vertexConsumer.vertex(matrix4f, f - 0.5f, (float)j - 0.5f, 0.0f).color(255, 255, 255, 255).texture(k, l).overlay(OverlayTexture.DEFAULT_UV).light(i).normal(matrix3f, 0.0f, 1.0f, 0.0f).next();
    }

    private static void method_23172(float f, float g, float h, VertexConsumer vertexConsumer, Matrix4f matrix4f, float i) {
        vertexConsumer.vertex(matrix4f, f * i, g * (i * i + i) * 0.5f + 0.25f, h * i).color(0, 0, 0, 255).next();
    }

    @Override
    public Identifier getTexture(FishingBobberEntity fishingBobberEntity) {
        return TEXTURE;
    }
}

