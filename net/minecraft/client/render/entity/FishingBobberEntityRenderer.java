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
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class FishingBobberEntityRenderer
extends EntityRenderer<FishingBobberEntity> {
    private static final Identifier SKIN = new Identifier("textures/entity/fishing_hook.png");

    public FishingBobberEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher);
    }

    public void method_3974(FishingBobberEntity fishingBobberEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        double y;
        float x;
        double w;
        double v;
        double u;
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
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f - this.renderManager.cameraYaw, true));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch, true));
        Matrix4f matrix4f = matrixStack.peek();
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(SKIN));
        OverlayTexture.clearDefaultOverlay(vertexConsumer);
        int l = fishingBobberEntity.getLightmapCoordinates();
        vertexConsumer.vertex(matrix4f, -0.5f, -0.5f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(l).normal(0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, 0.5f, -0.5f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(l).normal(0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, 0.5f, 0.5f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(l).normal(0.0f, 1.0f, 0.0f).next();
        vertexConsumer.vertex(matrix4f, -0.5f, 0.5f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(l).normal(0.0f, 1.0f, 0.0f).next();
        matrixStack.pop();
        vertexConsumer.clearDefaultOverlay();
        int m = playerEntity.getMainArm() == Arm.RIGHT ? 1 : -1;
        ItemStack itemStack = playerEntity.getMainHandStack();
        if (itemStack.getItem() != Items.FISHING_ROD) {
            m = -m;
        }
        float n = playerEntity.getHandSwingProgress(h);
        float o = MathHelper.sin(MathHelper.sqrt(n) * (float)Math.PI);
        float p = MathHelper.lerp(h, playerEntity.prevBodyYaw, playerEntity.bodyYaw) * ((float)Math.PI / 180);
        double q = MathHelper.sin(p);
        double r = MathHelper.cos(p);
        double s = (double)m * 0.35;
        double t = 0.8;
        if (this.renderManager.gameOptions != null && this.renderManager.gameOptions.perspective > 0 || playerEntity != MinecraftClient.getInstance().player) {
            u = MathHelper.lerp((double)h, playerEntity.prevX, playerEntity.x) - r * s - q * 0.8;
            v = playerEntity.prevY + (double)playerEntity.getStandingEyeHeight() + (playerEntity.y - playerEntity.prevY) * (double)h - 0.45;
            w = MathHelper.lerp((double)h, playerEntity.prevZ, playerEntity.z) - q * s + r * 0.8;
            x = playerEntity.isInSneakingPose() ? -0.1875f : 0.0f;
        } else {
            y = this.renderManager.gameOptions.fov;
            Vec3d vec3d = new Vec3d((double)m * -0.36 * (y /= 100.0), -0.045 * y, 0.4);
            vec3d = vec3d.rotateX(-MathHelper.lerp(h, playerEntity.prevPitch, playerEntity.pitch) * ((float)Math.PI / 180));
            vec3d = vec3d.rotateY(-MathHelper.lerp(h, playerEntity.prevYaw, playerEntity.yaw) * ((float)Math.PI / 180));
            vec3d = vec3d.rotateY(o * 0.5f);
            vec3d = vec3d.rotateX(-o * 0.7f);
            u = MathHelper.lerp((double)h, playerEntity.prevX, playerEntity.x) + vec3d.x;
            v = MathHelper.lerp((double)h, playerEntity.prevY, playerEntity.y) + vec3d.y;
            w = MathHelper.lerp((double)h, playerEntity.prevZ, playerEntity.z) + vec3d.z;
            x = playerEntity.getStandingEyeHeight();
        }
        y = MathHelper.lerp((double)h, fishingBobberEntity.prevX, fishingBobberEntity.x);
        double z = MathHelper.lerp((double)h, fishingBobberEntity.prevY, fishingBobberEntity.y) + 0.25;
        double aa = MathHelper.lerp((double)h, fishingBobberEntity.prevZ, fishingBobberEntity.z);
        float ab = (float)(u - y);
        float ac = (float)(v - z) + x;
        float ad = (float)(w - aa);
        VertexConsumer vertexConsumer2 = layeredVertexConsumerStorage.getBuffer(RenderLayer.LINES);
        Matrix4f matrix4f2 = matrixStack.peek();
        int ae = 16;
        for (int af = 0; af < 16; ++af) {
            FishingBobberEntityRenderer.method_23172(ab, ac, ad, vertexConsumer2, matrix4f2, af / 16);
            FishingBobberEntityRenderer.method_23172(ab, ac, ad, vertexConsumer2, matrix4f2, (af + 1) / 16);
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

