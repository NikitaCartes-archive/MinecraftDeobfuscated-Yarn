package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.FishingBobberEntityState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class FishingBobberEntityRenderer extends EntityRenderer<FishingBobberEntity, FishingBobberEntityState> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fishing_hook.png");
	private static final RenderLayer LAYER = RenderLayer.getEntityCutout(TEXTURE);
	private static final double field_33632 = 960.0;

	public FishingBobberEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
	}

	public boolean shouldRender(FishingBobberEntity fishingBobberEntity, Frustum frustum, double d, double e, double f) {
		return super.shouldRender(fishingBobberEntity, frustum, d, e, f) && fishingBobberEntity.getPlayerOwner() != null;
	}

	public void render(FishingBobberEntityState fishingBobberEntityState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		matrixStack.push();
		matrixStack.scale(0.5F, 0.5F, 0.5F);
		matrixStack.multiply(this.dispatcher.getRotation());
		MatrixStack.Entry entry = matrixStack.peek();
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
		vertex(vertexConsumer, entry, i, 0.0F, 0, 0, 1);
		vertex(vertexConsumer, entry, i, 1.0F, 0, 1, 1);
		vertex(vertexConsumer, entry, i, 1.0F, 1, 1, 0);
		vertex(vertexConsumer, entry, i, 0.0F, 1, 0, 0);
		matrixStack.pop();
		float f = (float)fishingBobberEntityState.pos.x;
		float g = (float)fishingBobberEntityState.pos.y;
		float h = (float)fishingBobberEntityState.pos.z;
		VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip());
		MatrixStack.Entry entry2 = matrixStack.peek();
		int j = 16;

		for (int k = 0; k <= 16; k++) {
			renderFishingLine(f, g, h, vertexConsumer2, entry2, percentage(k, 16), percentage(k + 1, 16));
		}

		matrixStack.pop();
		super.render(fishingBobberEntityState, matrixStack, vertexConsumerProvider, i);
	}

	private Vec3d getHandPos(PlayerEntity player, float f, float tickDelta) {
		int i = player.getMainArm() == Arm.RIGHT ? 1 : -1;
		ItemStack itemStack = player.getMainHandStack();
		if (!itemStack.isOf(Items.FISHING_ROD)) {
			i = -i;
		}

		if (this.dispatcher.gameOptions.getPerspective().isFirstPerson() && player == MinecraftClient.getInstance().player) {
			double m = 960.0 / (double)this.dispatcher.gameOptions.getFov().getValue().intValue();
			Vec3d vec3d = this.dispatcher.camera.getProjection().getPosition((float)i * 0.525F, -0.1F).multiply(m).rotateY(f * 0.5F).rotateX(-f * 0.7F);
			return player.getCameraPosVec(tickDelta).add(vec3d);
		} else {
			float g = MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) * (float) (Math.PI / 180.0);
			double d = (double)MathHelper.sin(g);
			double e = (double)MathHelper.cos(g);
			float h = player.getScale();
			double j = (double)i * 0.35 * (double)h;
			double k = 0.8 * (double)h;
			float l = player.isInSneakingPose() ? -0.1875F : 0.0F;
			return player.getCameraPosVec(tickDelta).add(-e * j - d * k, (double)l - 0.45 * (double)h, -d * j + e * k);
		}
	}

	private static float percentage(int value, int max) {
		return (float)value / (float)max;
	}

	private static void vertex(VertexConsumer buffer, MatrixStack.Entry matrix, int light, float x, int y, int u, int v) {
		buffer.vertex(matrix, x - 0.5F, (float)y - 0.5F, 0.0F)
			.color(Colors.WHITE)
			.texture((float)u, (float)v)
			.overlay(OverlayTexture.DEFAULT_UV)
			.light(light)
			.normal(matrix, 0.0F, 1.0F, 0.0F);
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
		buffer.vertex(matrices, f, g, h).color(Colors.BLACK).normal(matrices, i, j, k);
	}

	public FishingBobberEntityState createRenderState() {
		return new FishingBobberEntityState();
	}

	public void updateRenderState(FishingBobberEntity fishingBobberEntity, FishingBobberEntityState fishingBobberEntityState, float f) {
		super.updateRenderState(fishingBobberEntity, fishingBobberEntityState, f);
		PlayerEntity playerEntity = fishingBobberEntity.getPlayerOwner();
		if (playerEntity == null) {
			fishingBobberEntityState.pos = Vec3d.ZERO;
		} else {
			float g = playerEntity.getHandSwingProgress(f);
			float h = MathHelper.sin(MathHelper.sqrt(g) * (float) Math.PI);
			Vec3d vec3d = this.getHandPos(playerEntity, h, f);
			Vec3d vec3d2 = fishingBobberEntity.getLerpedPos(f).add(0.0, 0.25, 0.0);
			fishingBobberEntityState.pos = vec3d.subtract(vec3d2);
		}
	}

	protected boolean canBeCulled(FishingBobberEntity fishingBobberEntity) {
		return false;
	}
}
