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
    private static final Identifier SKIN = new Identifier("textures/entity/fishing_hook.png");

    public FishingBobberEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_3974(FishingBobberEntity fishingBobberEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        double t;
        float s;
        double r;
        double q;
        double p;
        PlayerEntity playerEntity = fishingBobberEntity.getOwner();
        if (playerEntity == null) {
            return;
        }
        matrixStack.push();
        matrixStack.push();
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f - this.renderManager.cameraYaw));
        float h = (float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch;
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(h));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getModel();
        Matrix3f matrix3f = entry.getNormal();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(SKIN));
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
        float k = playerEntity.getHandSwingProgress(g);
        float l = MathHelper.sin(MathHelper.sqrt(k) * (float)Math.PI);
        float m = MathHelper.lerp(g, playerEntity.prevBodyYaw, playerEntity.bodyYaw) * ((float)Math.PI / 180);
        double d = MathHelper.sin(m);
        double e = MathHelper.cos(m);
        double n = (double)j * 0.35;
        double o = 0.8;
        if (this.renderManager.gameOptions != null && this.renderManager.gameOptions.perspective > 0 || playerEntity != MinecraftClient.getInstance().player) {
            p = MathHelper.lerp((double)g, playerEntity.prevX, playerEntity.getX()) - e * n - d * 0.8;
            q = playerEntity.prevY + (double)playerEntity.getStandingEyeHeight() + (playerEntity.getY() - playerEntity.prevY) * (double)g - 0.45;
            r = MathHelper.lerp((double)g, playerEntity.prevZ, playerEntity.getZ()) - d * n + e * 0.8;
            s = playerEntity.isInSneakingPose() ? -0.1875f : 0.0f;
        } else {
            t = this.renderManager.gameOptions.fov;
            Vec3d vec3d = new Vec3d((double)j * -0.36 * (t /= 100.0), -0.045 * t, 0.4);
            vec3d = vec3d.rotateX(-MathHelper.lerp(g, playerEntity.prevPitch, playerEntity.pitch) * ((float)Math.PI / 180));
            vec3d = vec3d.rotateY(-MathHelper.lerp(g, playerEntity.prevYaw, playerEntity.yaw) * ((float)Math.PI / 180));
            vec3d = vec3d.rotateY(l * 0.5f);
            vec3d = vec3d.rotateX(-l * 0.7f);
            p = MathHelper.lerp((double)g, playerEntity.prevX, playerEntity.getX()) + vec3d.x;
            q = MathHelper.lerp((double)g, playerEntity.prevY, playerEntity.getY()) + vec3d.y;
            r = MathHelper.lerp((double)g, playerEntity.prevZ, playerEntity.getZ()) + vec3d.z;
            s = playerEntity.getStandingEyeHeight();
        }
        t = MathHelper.lerp((double)g, fishingBobberEntity.prevX, fishingBobberEntity.getX());
        double u = MathHelper.lerp((double)g, fishingBobberEntity.prevY, fishingBobberEntity.getY()) + 0.25;
        double v = MathHelper.lerp((double)g, fishingBobberEntity.prevZ, fishingBobberEntity.getZ());
        float w = (float)(p - t);
        float x = (float)(q - u) + s;
        float y = (float)(r - v);
        VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
        Matrix4f matrix4f2 = matrixStack.peek().getModel();
        int z = 16;
        for (int aa = 0; aa < 16; ++aa) {
            FishingBobberEntityRenderer.method_23172(w, x, y, vertexConsumer2, matrix4f2, aa / 16);
            FishingBobberEntityRenderer.method_23172(w, x, y, vertexConsumer2, matrix4f2, (aa + 1) / 16);
        }
        matrixStack.pop();
        super.render(fishingBobberEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    private static void method_23840(VertexConsumer vertexConsumer, Matrix4f matrix4f, Matrix3f matrix3f, int i, float f, int j, int k, int l) {
        vertexConsumer.vertex(matrix4f, f - 0.5f, (float)j - 0.5f, 0.0f).color(255, 255, 255, 255).texture(k, l).overlay(OverlayTexture.DEFAULT_UV).light(i).method_23763(matrix3f, 0.0f, 1.0f, 0.0f).next();
    }

    private static void method_23172(float f, float g, float h, VertexConsumer vertexConsumer, Matrix4f matrix4f, float i) {
        vertexConsumer.vertex(matrix4f, f * i, g * (i * i + i) * 0.5f + 0.25f, h * i).color(0, 0, 0, 255).next();
    }

    public Identifier method_3975(FishingBobberEntity fishingBobberEntity) {
        return SKIN;
    }
}

