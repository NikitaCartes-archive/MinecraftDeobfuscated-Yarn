package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class FishingBobberEntityRenderer extends EntityRenderer<FishingBobberEntity> {
	private static final Identifier TEXTURE = new Identifier("textures/entity/fishing_hook.png");
	private static final RenderLayer LAYER = RenderLayer.getEntityCutout(TEXTURE);
	private static final double field_33632 = 960.0;

	public FishingBobberEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public void render(FishingBobberEntity fishingBobberEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		PlayerEntity playerEntity = fishingBobberEntity.getPlayerOwner();
		if (playerEntity != null) {
			matrixStack.push();
			matrixStack.push();
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			matrixStack.multiply(this.dispatcher.getRotation());
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
			MatrixStack.Entry entry = matrixStack.peek();
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
			vertex(vertexConsumer, entry, i, 0.0F, 0, 0, 1);
			vertex(vertexConsumer, entry, i, 1.0F, 0, 1, 1);
			vertex(vertexConsumer, entry, i, 1.0F, 1, 1, 0);
			vertex(vertexConsumer, entry, i, 0.0F, 1, 0, 0);
			matrixStack.pop();
			Vec3d vec3d = getPlayerHandPos(playerEntity, g, Items.FISHING_ROD, this.dispatcher);
			double d = MathHelper.lerp((double)g, fishingBobberEntity.prevX, fishingBobberEntity.getX());
			double e = MathHelper.lerp((double)g, fishingBobberEntity.prevY, fishingBobberEntity.getY()) + 0.25;
			double h = MathHelper.lerp((double)g, fishingBobberEntity.prevZ, fishingBobberEntity.getZ());
			float j = (float)(vec3d.x - d);
			float k = (float)(vec3d.y - e);
			float l = (float)(vec3d.z - h);
			VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip());
			MatrixStack.Entry entry2 = matrixStack.peek();
			int m = 16;

			for (int n = 0; n <= 16; n++) {
				renderFishingLine(j, k, l, vertexConsumer2, entry2, percentage(n, 16), percentage(n + 1, 16));
			}

			matrixStack.pop();
			super.render(fishingBobberEntity, f, g, matrixStack, vertexConsumerProvider, i);
		}
	}

	public static Vec3d getPlayerHandPos(PlayerEntity player, float tickDelta, Item item, EntityRenderDispatcher dispatcher) {
		int i = player.getMainArm() == Arm.RIGHT ? 1 : -1;
		ItemStack itemStack = player.getMainHandStack();
		if (!itemStack.isOf(item)) {
			i = -i;
		}

		float f = player.getHandSwingProgress(tickDelta);
		float g = MathHelper.sin(MathHelper.sqrt(f) * (float) Math.PI);
		float h = MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) * (float) (Math.PI / 180.0);
		double d = (double)MathHelper.sin(h);
		double e = (double)MathHelper.cos(h);
		double j = (double)i * 0.35;
		double k = 0.8;
		if ((dispatcher.gameOptions == null || dispatcher.gameOptions.getPerspective().isFirstPerson()) && player == MinecraftClient.getInstance().player) {
			double m = 960.0 / (double)dispatcher.gameOptions.getFov().getValue().intValue();
			Vec3d vec3d = dispatcher.camera.getProjection().getPosition((float)i * 0.525F, -0.1F);
			vec3d = vec3d.multiply(m);
			vec3d = vec3d.rotateY(g * 0.5F);
			vec3d = vec3d.rotateX(-g * 0.7F);
			return new Vec3d(
				MathHelper.lerp((double)tickDelta, player.prevX, player.getX()) + vec3d.x,
				MathHelper.lerp((double)tickDelta, player.prevY, player.getY()) + vec3d.y + (double)player.getStandingEyeHeight(),
				MathHelper.lerp((double)tickDelta, player.prevZ, player.getZ()) + vec3d.z
			);
		} else {
			float l = player.isInSneakingPose() ? -0.1875F : 0.0F;
			return new Vec3d(
				MathHelper.lerp((double)tickDelta, player.prevX, player.getX()) - e * j - d * 0.8,
				player.prevY + (double)player.getStandingEyeHeight() + (player.getY() - player.prevY) * (double)tickDelta - 0.45 + (double)l,
				MathHelper.lerp((double)tickDelta, player.prevZ, player.getZ()) - d * j + e * 0.8
			);
		}
	}

	private static float percentage(int value, int max) {
		return (float)value / (float)max;
	}

	private static void vertex(VertexConsumer buffer, MatrixStack.Entry matrix, int light, float x, int y, int u, int v) {
		buffer.vertex(matrix, x - 0.5F, (float)y - 0.5F, 0.0F)
			.color(255, 255, 255, 255)
			.texture((float)u, (float)v)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(light)
			.normal(matrix, 0.0F, 1.0F, 0.0F)
			.next();
	}

	private static void renderFishingLine(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
		float f = x * segmentStart;
		float g = y * (segmentStart * segmentStart + segmentStart) * 0.5F + 0.25F;
		float h = z * segmentStart;
		float i = x * segmentEnd - f;
		float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5F + 0.25F - g;
		float k = z * segmentEnd - h;
		float l = MathHelper.sqrt(i * i + j * j + k * k);
		i /= l;
		j /= l;
		k /= l;
		buffer.vertex(matrices, f, g, h).color(0, 0, 0, 255).normal(matrices, i, j, k).next();
	}

	public Identifier getTexture(FishingBobberEntity fishingBobberEntity) {
		return TEXTURE;
	}
}
