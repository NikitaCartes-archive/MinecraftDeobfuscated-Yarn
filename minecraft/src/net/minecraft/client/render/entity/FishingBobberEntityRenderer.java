package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
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

@Environment(EnvType.CLIENT)
public class FishingBobberEntityRenderer extends EntityRenderer<FishingBobberEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/fishing_hook.png");

	public FishingBobberEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_3974(
		FishingBobberEntity fishingBobberEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		PlayerEntity playerEntity = fishingBobberEntity.getOwner();
		if (playerEntity != null) {
			matrixStack.push();
			matrixStack.push();
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			float i = 1.0F;
			float j = 0.5F;
			float k = 0.5F;
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F - this.renderManager.cameraYaw));
			float l = (float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch;
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(l));
			Matrix4f matrix4f = matrixStack.peek();
			VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityCutout(SKIN));
			int m = fishingBobberEntity.getLightmapCoordinates();
			vertexConsumer.vertex(matrix4f, -0.5F, -0.5F, 0.0F)
				.color(255, 255, 255, 255)
				.texture(0.0F, 1.0F)
				.defaultOverlay(OverlayTexture.field_21444)
				.light(m)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			vertexConsumer.vertex(matrix4f, 0.5F, -0.5F, 0.0F)
				.color(255, 255, 255, 255)
				.texture(1.0F, 1.0F)
				.defaultOverlay(OverlayTexture.field_21444)
				.light(m)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			vertexConsumer.vertex(matrix4f, 0.5F, 0.5F, 0.0F)
				.color(255, 255, 255, 255)
				.texture(1.0F, 0.0F)
				.defaultOverlay(OverlayTexture.field_21444)
				.light(m)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			vertexConsumer.vertex(matrix4f, -0.5F, 0.5F, 0.0F)
				.color(255, 255, 255, 255)
				.texture(0.0F, 0.0F)
				.defaultOverlay(OverlayTexture.field_21444)
				.light(m)
				.normal(0.0F, 1.0F, 0.0F)
				.next();
			matrixStack.pop();
			int n = playerEntity.getMainArm() == Arm.RIGHT ? 1 : -1;
			ItemStack itemStack = playerEntity.getMainHandStack();
			if (itemStack.getItem() != Items.FISHING_ROD) {
				n = -n;
			}

			float o = playerEntity.getHandSwingProgress(h);
			float p = MathHelper.sin(MathHelper.sqrt(o) * (float) Math.PI);
			float q = MathHelper.lerp(h, playerEntity.prevBodyYaw, playerEntity.bodyYaw) * (float) (Math.PI / 180.0);
			double r = (double)MathHelper.sin(q);
			double s = (double)MathHelper.cos(q);
			double t = (double)n * 0.35;
			double u = 0.8;
			double v;
			double w;
			double x;
			float y;
			if ((this.renderManager.gameOptions == null || this.renderManager.gameOptions.perspective <= 0) && playerEntity == MinecraftClient.getInstance().player) {
				double z = this.renderManager.gameOptions.fov;
				z /= 100.0;
				Vec3d vec3d = new Vec3d((double)n * -0.36 * z, -0.045 * z, 0.4);
				vec3d = vec3d.rotateX(-MathHelper.lerp(h, playerEntity.prevPitch, playerEntity.pitch) * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(-MathHelper.lerp(h, playerEntity.prevYaw, playerEntity.yaw) * (float) (Math.PI / 180.0));
				vec3d = vec3d.rotateY(p * 0.5F);
				vec3d = vec3d.rotateX(-p * 0.7F);
				v = MathHelper.lerp((double)h, playerEntity.prevX, playerEntity.getX()) + vec3d.x;
				w = MathHelper.lerp((double)h, playerEntity.prevY, playerEntity.getY()) + vec3d.y;
				x = MathHelper.lerp((double)h, playerEntity.prevZ, playerEntity.getZ()) + vec3d.z;
				y = playerEntity.getStandingEyeHeight();
			} else {
				v = MathHelper.lerp((double)h, playerEntity.prevX, playerEntity.getX()) - s * t - r * 0.8;
				w = playerEntity.prevY + (double)playerEntity.getStandingEyeHeight() + (playerEntity.getY() - playerEntity.prevY) * (double)h - 0.45;
				x = MathHelper.lerp((double)h, playerEntity.prevZ, playerEntity.getZ()) - r * t + s * 0.8;
				y = playerEntity.isInSneakingPose() ? -0.1875F : 0.0F;
			}

			double z = MathHelper.lerp((double)h, fishingBobberEntity.prevX, fishingBobberEntity.getX());
			double aa = MathHelper.lerp((double)h, fishingBobberEntity.prevY, fishingBobberEntity.getY()) + 0.25;
			double ab = MathHelper.lerp((double)h, fishingBobberEntity.prevZ, fishingBobberEntity.getZ());
			float ac = (float)(v - z);
			float ad = (float)(w - aa) + y;
			float ae = (float)(x - ab);
			VertexConsumer vertexConsumer2 = layeredVertexConsumerStorage.getBuffer(RenderLayer.getLines());
			Matrix4f matrix4f2 = matrixStack.peek();
			int af = 16;

			for (int ag = 0; ag < 16; ag++) {
				method_23172(ac, ad, ae, vertexConsumer2, matrix4f2, (float)(ag / 16));
				method_23172(ac, ad, ae, vertexConsumer2, matrix4f2, (float)((ag + 1) / 16));
			}

			matrixStack.pop();
			super.render(fishingBobberEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
		}
	}

	private static void method_23172(float f, float g, float h, VertexConsumer vertexConsumer, Matrix4f matrix4f, float i) {
		vertexConsumer.vertex(matrix4f, f * i, g * (i * i + i) * 0.5F + 0.25F, h * i).color(0, 0, 0, 255).next();
	}

	public Identifier method_3975(FishingBobberEntity fishingBobberEntity) {
		return SKIN;
	}
}
