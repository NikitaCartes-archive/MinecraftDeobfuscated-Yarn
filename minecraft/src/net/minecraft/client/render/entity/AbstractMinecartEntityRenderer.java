package net.minecraft.client.render.entity;

import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.MinecartEntityModel;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.DefaultMinecartController;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public abstract class AbstractMinecartEntityRenderer<T extends AbstractMinecartEntity, S extends MinecartEntityRenderState> extends EntityRenderer<T, S> {
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/minecart.png");
	protected final MinecartEntityModel model;
	private final BlockRenderManager blockRenderManager;

	public AbstractMinecartEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer) {
		super(ctx);
		this.shadowRadius = 0.7F;
		this.model = new MinecartEntityModel(ctx.getPart(layer));
		this.blockRenderManager = ctx.getBlockRenderManager();
	}

	public void render(S minecartEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		super.render(minecartEntityRenderState, matrixStack, vertexConsumerProvider, i);
		matrixStack.push();
		long l = minecartEntityRenderState.hash;
		float f = (((float)(l >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float g = (((float)(l >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		float h = (((float)(l >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
		matrixStack.translate(f, g, h);
		if (minecartEntityRenderState.usesExperimentalController) {
			transformExperimentalControllerMinecart(minecartEntityRenderState, matrixStack);
		} else {
			transformDefaultControllerMinecart(minecartEntityRenderState, matrixStack);
		}

		float j = minecartEntityRenderState.damageWobbleTicks;
		if (j > 0.0F) {
			matrixStack.multiply(
				RotationAxis.POSITIVE_X
					.rotationDegrees(MathHelper.sin(j) * j * minecartEntityRenderState.damageWobbleStrength / 10.0F * (float)minecartEntityRenderState.damageWobbleSide)
			);
		}

		BlockState blockState = minecartEntityRenderState.containedBlock;
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			matrixStack.push();
			float k = 0.75F;
			matrixStack.scale(0.75F, 0.75F, 0.75F);
			matrixStack.translate(-0.5F, (float)(minecartEntityRenderState.blockOffset - 8) / 16.0F, 0.5F);
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
			this.renderBlock(minecartEntityRenderState, blockState, matrixStack, vertexConsumerProvider, i);
			matrixStack.pop();
		}

		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		this.model.setAngles(minecartEntityRenderState);
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(minecartEntityRenderState)));
		this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
		matrixStack.pop();
	}

	private static <S extends MinecartEntityRenderState> void transformExperimentalControllerMinecart(S state, MatrixStack matrices) {
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.lerpedYaw));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-state.lerpedPitch));
		matrices.translate(0.0F, 0.375F, 0.0F);
	}

	private static <S extends MinecartEntityRenderState> void transformDefaultControllerMinecart(S state, MatrixStack matrices) {
		double d = state.x;
		double e = state.y;
		double f = state.z;
		float g = state.lerpedPitch;
		float h = state.lerpedYaw;
		if (state.presentPos != null && state.futurePos != null && state.pastPos != null) {
			Vec3d vec3d = state.futurePos;
			Vec3d vec3d2 = state.pastPos;
			matrices.translate(state.presentPos.x - d, (vec3d.y + vec3d2.y) / 2.0 - e, state.presentPos.z - f);
			Vec3d vec3d3 = vec3d2.add(-vec3d.x, -vec3d.y, -vec3d.z);
			if (vec3d3.length() != 0.0) {
				vec3d3 = vec3d3.normalize();
				h = (float)(Math.atan2(vec3d3.z, vec3d3.x) * 180.0 / Math.PI);
				g = (float)(Math.atan(vec3d3.y) * 73.0);
			}
		}

		matrices.translate(0.0F, 0.375F, 0.0F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - h));
		matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-g));
	}

	public Identifier getTexture(S minecartEntityRenderState) {
		return TEXTURE;
	}

	public void updateRenderState(T abstractMinecartEntity, S minecartEntityRenderState, float f) {
		super.updateRenderState(abstractMinecartEntity, minecartEntityRenderState, f);
		if (abstractMinecartEntity.getController() instanceof ExperimentalMinecartController experimentalMinecartController) {
			updateFromExperimentalController(abstractMinecartEntity, experimentalMinecartController, minecartEntityRenderState, f);
			minecartEntityRenderState.usesExperimentalController = true;
		} else if (abstractMinecartEntity.getController() instanceof DefaultMinecartController defaultMinecartController) {
			updateFromDefaultController(abstractMinecartEntity, defaultMinecartController, minecartEntityRenderState, f);
			minecartEntityRenderState.usesExperimentalController = false;
		}

		long l = (long)abstractMinecartEntity.getId() * 493286711L;
		minecartEntityRenderState.hash = l * l * 4392167121L + l * 98761L;
		minecartEntityRenderState.damageWobbleTicks = (float)abstractMinecartEntity.getDamageWobbleTicks() - f;
		minecartEntityRenderState.damageWobbleSide = abstractMinecartEntity.getDamageWobbleSide();
		minecartEntityRenderState.damageWobbleStrength = Math.max(abstractMinecartEntity.getDamageWobbleStrength() - f, 0.0F);
		minecartEntityRenderState.blockOffset = abstractMinecartEntity.getBlockOffset();
		minecartEntityRenderState.containedBlock = abstractMinecartEntity.getContainedBlock();
	}

	private static <T extends AbstractMinecartEntity, S extends MinecartEntityRenderState> void updateFromExperimentalController(
		T minecart, ExperimentalMinecartController controller, S state, float tickDelta
	) {
		if (controller.method_61614()) {
			state.lerpedPos = controller.getLerpedPosition(tickDelta);
			state.lerpedPitch = controller.getLerpedPitch(tickDelta);
			state.lerpedYaw = controller.getLerpedYaw(tickDelta);
		} else {
			state.lerpedPos = null;
			state.lerpedPitch = minecart.getPitch();
			state.lerpedYaw = minecart.getYaw();
		}
	}

	private static <T extends AbstractMinecartEntity, S extends MinecartEntityRenderState> void updateFromDefaultController(
		T minecart, DefaultMinecartController controller, S state, float tickDelta
	) {
		float f = 0.3F;
		state.lerpedPitch = minecart.getLerpedPitch(tickDelta);
		state.lerpedYaw = minecart.getLerpedYaw(tickDelta);
		double d = state.x;
		double e = state.y;
		double g = state.z;
		Vec3d vec3d = controller.snapPositionToRail(d, e, g);
		if (vec3d != null) {
			state.presentPos = vec3d;
			Vec3d vec3d2 = controller.method_61619(d, e, g, 0.3F);
			Vec3d vec3d3 = controller.method_61619(d, e, g, -0.3F);
			state.futurePos = (Vec3d)Objects.requireNonNullElse(vec3d2, vec3d);
			state.pastPos = (Vec3d)Objects.requireNonNullElse(vec3d3, vec3d);
		} else {
			state.presentPos = null;
			state.futurePos = null;
			state.pastPos = null;
		}
	}

	protected void renderBlock(S state, BlockState blockState, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		this.blockRenderManager.renderBlockAsEntity(blockState, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);
	}

	protected Box getBoundingBox(T abstractMinecartEntity) {
		Box box = super.getBoundingBox(abstractMinecartEntity);
		return abstractMinecartEntity.hasCustomBlock() ? box.expand((double)Math.abs(abstractMinecartEntity.getBlockOffset()) / 16.0) : box;
	}

	public Vec3d getPositionOffset(S minecartEntityRenderState) {
		Vec3d vec3d = super.getPositionOffset(minecartEntityRenderState);
		return minecartEntityRenderState.usesExperimentalController && minecartEntityRenderState.lerpedPos != null
			? vec3d.add(
				minecartEntityRenderState.lerpedPos.x - minecartEntityRenderState.x,
				minecartEntityRenderState.lerpedPos.y - minecartEntityRenderState.y,
				minecartEntityRenderState.lerpedPos.z - minecartEntityRenderState.z
			)
			: vec3d;
	}
}
