/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
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

    public void method_3974(FishingBobberEntity fishingBobberEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        double z;
        float y;
        double x;
        double w;
        double v;
        PlayerEntity playerEntity = fishingBobberEntity.getOwner();
        if (playerEntity == null) {
            return;
        }
        matrixStack.push();
        matrixStack.push();
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        float i = 1.0f;
        float j = 0.5f;
        float k = 0.5f;
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f - this.renderManager.cameraYaw));
        float l = (float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch;
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(l));
        Matrix4f matrix4f = matrixStack.peek();
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityCutout(SKIN));
        int m = fishingBobberEntity.getLightmapCoordinates();
        vertexConsumer.vertex(matrix4f, -0.5f, -0.5f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).defaultOverlay(OverlayTexture.DEFAULT_UV).light(m).normal(0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, 0.5f, -0.5f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).defaultOverlay(OverlayTexture.DEFAULT_UV).light(m).normal(0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, 0.5f, 0.5f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).defaultOverlay(OverlayTexture.DEFAULT_UV).light(m).normal(0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, -0.5f, 0.5f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).defaultOverlay(OverlayTexture.DEFAULT_UV).light(m).normal(0.0f, 1.0f, 0.0f).next();
        matrixStack.pop();
        int n = playerEntity.getMainArm() == Arm.RIGHT ? 1 : -1;
        ItemStack itemStack = playerEntity.getMainHandStack();
        if (itemStack.getItem() != Items.FISHING_ROD) {
            n = -n;
        }
        float o = playerEntity.getHandSwingProgress(h);
        float p = MathHelper.sin(MathHelper.sqrt(o) * (float)Math.PI);
        float q = MathHelper.lerp(h, playerEntity.prevBodyYaw, playerEntity.bodyYaw) * ((float)Math.PI / 180);
        double r = MathHelper.sin(q);
        double s = MathHelper.cos(q);
        double t = (double)n * 0.35;
        double u = 0.8;
        if (this.renderManager.gameOptions != null && this.renderManager.gameOptions.perspective > 0 || playerEntity != MinecraftClient.getInstance().player) {
            v = MathHelper.lerp((double)h, playerEntity.prevX, playerEntity.getX()) - s * t - r * 0.8;
            w = playerEntity.prevY + (double)playerEntity.getStandingEyeHeight() + (playerEntity.getY() - playerEntity.prevY) * (double)h - 0.45;
            x = MathHelper.lerp((double)h, playerEntity.prevZ, playerEntity.getZ()) - r * t + s * 0.8;
            y = playerEntity.isInSneakingPose() ? -0.1875f : 0.0f;
        } else {
            z = this.renderManager.gameOptions.fov;
            Vec3d vec3d = new Vec3d((double)n * -0.36 * (z /= 100.0), -0.045 * z, 0.4);
            vec3d = vec3d.rotateX(-MathHelper.lerp(h, playerEntity.prevPitch, playerEntity.pitch) * ((float)Math.PI / 180));
            vec3d = vec3d.rotateY(-MathHelper.lerp(h, playerEntity.prevYaw, playerEntity.yaw) * ((float)Math.PI / 180));
            vec3d = vec3d.rotateY(p * 0.5f);
            vec3d = vec3d.rotateX(-p * 0.7f);
            v = MathHelper.lerp((double)h, playerEntity.prevX, playerEntity.getX()) + vec3d.x;
            w = MathHelper.lerp((double)h, playerEntity.prevY, playerEntity.getY()) + vec3d.y;
            x = MathHelper.lerp((double)h, playerEntity.prevZ, playerEntity.getZ()) + vec3d.z;
            y = playerEntity.getStandingEyeHeight();
        }
        z = MathHelper.lerp((double)h, fishingBobberEntity.prevX, fishingBobberEntity.getX());
        double aa = MathHelper.lerp((double)h, fishingBobberEntity.prevY, fishingBobberEntity.getY()) + 0.25;
        double ab = MathHelper.lerp((double)h, fishingBobberEntity.prevZ, fishingBobberEntity.getZ());
        float ac = (float)(v - z);
        float ad = (float)(w - aa) + y;
        float ae = (float)(x - ab);
        VertexConsumer vertexConsumer2 = layeredVertexConsumerStorage.getBuffer(RenderLayer.getLines());
        Matrix4f matrix4f2 = matrixStack.peek();
        int af = 16;
        for (int ag = 0; ag < 16; ++ag) {
            FishingBobberEntityRenderer.method_23172(ac, ad, ae, vertexConsumer2, matrix4f2, ag / 16);
            FishingBobberEntityRenderer.method_23172(ac, ad, ae, vertexConsumer2, matrix4f2, (ag + 1) / 16);
        }
        matrixStack.pop();
        super.render(fishingBobberEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
    }

    private static void method_23172(float f, float g, float h, VertexConsumer vertexConsumer, Matrix4f matrix4f, float i) {
        vertexConsumer.vertex(matrix4f, f * i, g * (i * i + i) * 0.5f + 0.25f, h * i).color(0, 0, 0, 255).next();
    }

    public Identifier method_3975(FishingBobberEntity fishingBobberEntity) {
        return SKIN;
    }
}

