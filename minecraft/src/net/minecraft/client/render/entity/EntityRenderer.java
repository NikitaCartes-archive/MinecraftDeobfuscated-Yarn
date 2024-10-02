package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.Leashable;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.ExperimentalMinecartController;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity, S extends EntityRenderState> {
	protected static final float field_32921 = 0.025F;
	public static final int field_52257 = 24;
	protected final EntityRenderDispatcher dispatcher;
	private final TextRenderer textRenderer;
	protected float shadowRadius;
	protected float shadowOpacity = 1.0F;
	private final S state = this.getRenderState();

	protected EntityRenderer(EntityRendererFactory.Context context) {
		this.dispatcher = context.getRenderDispatcher();
		this.textRenderer = context.getTextRenderer();
	}

	public final int getLight(T entity, float tickDelta) {
		BlockPos blockPos = BlockPos.ofFloored(entity.getClientCameraPosVec(tickDelta));
		return LightmapTextureManager.pack(this.getBlockLight(entity, blockPos), this.getSkyLight(entity, blockPos));
	}

	protected int getSkyLight(T entity, BlockPos pos) {
		return entity.getWorld().getLightLevel(LightType.SKY, pos);
	}

	protected int getBlockLight(T entity, BlockPos pos) {
		return entity.isOnFire() ? 15 : entity.getWorld().getLightLevel(LightType.BLOCK, pos);
	}

	public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
		if (!entity.shouldRender(x, y, z)) {
			return false;
		} else if (!this.canBeCulled(entity)) {
			return true;
		} else {
			Box box = this.getBoundingBox(entity).expand(0.5);
			if (box.isNaN() || box.getAverageSideLength() == 0.0) {
				box = new Box(entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0, entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0);
			}

			if (frustum.isVisible(box)) {
				return true;
			} else {
				if (entity instanceof Leashable leashable) {
					Entity entity2 = leashable.getLeashHolder();
					if (entity2 != null) {
						return frustum.isVisible(this.dispatcher.getRenderer(entity2).getBoundingBox(entity2));
					}
				}

				return false;
			}
		}
	}

	protected Box getBoundingBox(T entity) {
		return entity.getBoundingBox();
	}

	protected boolean canBeCulled(T entity) {
		return true;
	}

	public Vec3d getPositionOffset(S state) {
		return state.positionOffset != null ? state.positionOffset : Vec3d.ZERO;
	}

	public void render(S state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		EntityRenderState.LeashData leashData = state.leashData;
		if (leashData != null) {
			renderLeash(matrices, vertexConsumers, leashData);
		}

		if (state.displayName != null) {
			this.renderLabelIfPresent(state, state.displayName, matrices, vertexConsumers, light);
		}
	}

	private static void renderLeash(MatrixStack matrices, VertexConsumerProvider vertexConsumers, EntityRenderState.LeashData leashData) {
		float f = 0.025F;
		float g = (float)(leashData.endPos.x - leashData.startPos.x);
		float h = (float)(leashData.endPos.y - leashData.startPos.y);
		float i = (float)(leashData.endPos.z - leashData.startPos.z);
		float j = MathHelper.inverseSqrt(g * g + i * i) * 0.025F / 2.0F;
		float k = i * j;
		float l = g * j;
		matrices.push();
		matrices.translate(leashData.offset);
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getLeash());
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();

		for (int m = 0; m <= 24; m++) {
			renderLeashSegment(
				vertexConsumer,
				matrix4f,
				g,
				h,
				i,
				leashData.leashedEntityBlockLight,
				leashData.leashHolderBlockLight,
				leashData.leashedEntitySkyLight,
				leashData.leashHolderSkyLight,
				0.025F,
				0.025F,
				k,
				l,
				m,
				false
			);
		}

		for (int m = 24; m >= 0; m--) {
			renderLeashSegment(
				vertexConsumer,
				matrix4f,
				g,
				h,
				i,
				leashData.leashedEntityBlockLight,
				leashData.leashHolderBlockLight,
				leashData.leashedEntitySkyLight,
				leashData.leashHolderSkyLight,
				0.025F,
				0.0F,
				k,
				l,
				m,
				true
			);
		}

		matrices.pop();
	}

	private static void renderLeashSegment(
		VertexConsumer vertexConsumer,
		Matrix4f matrix,
		float leashedEntityX,
		float leashedEntityY,
		float leashedEntityZ,
		int leashedEntityBlockLight,
		int leashHolderBlockLight,
		int leashedEntitySkyLight,
		int leashHolderSkyLight,
		float f,
		float g,
		float h,
		float i,
		int segmentIndex,
		boolean isLeashKnot
	) {
		float j = (float)segmentIndex / 24.0F;
		int k = (int)MathHelper.lerp(j, (float)leashedEntityBlockLight, (float)leashHolderBlockLight);
		int l = (int)MathHelper.lerp(j, (float)leashedEntitySkyLight, (float)leashHolderSkyLight);
		int m = LightmapTextureManager.pack(k, l);
		float n = segmentIndex % 2 == (isLeashKnot ? 1 : 0) ? 0.7F : 1.0F;
		float o = 0.5F * n;
		float p = 0.4F * n;
		float q = 0.3F * n;
		float r = leashedEntityX * j;
		float s = leashedEntityY > 0.0F ? leashedEntityY * j * j : leashedEntityY - leashedEntityY * (1.0F - j) * (1.0F - j);
		float t = leashedEntityZ * j;
		vertexConsumer.vertex(matrix, r - h, s + g, t + i).color(o, p, q, 1.0F).light(m);
		vertexConsumer.vertex(matrix, r + h, s + f - g, t - i).color(o, p, q, 1.0F).light(m);
	}

	/**
	 * Determines whether the passed entity should render with a nameplate above its head.
	 * 
	 * <p>Checks for a custom nametag on living entities, and for teams/team visibilities for players.
	 */
	protected boolean hasLabel(T entity, double squaredDistanceToCamera) {
		return entity.shouldRenderName() || entity.hasCustomName() && entity == this.dispatcher.targetedEntity;
	}

	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}

	protected void renderLabelIfPresent(S state, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		Vec3d vec3d = state.nameLabelPos;
		if (vec3d != null) {
			boolean bl = !state.sneaking;
			int i = "deadmau5".equals(text.getString()) ? -10 : 0;
			matrices.push();
			matrices.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
			matrices.multiply(this.dispatcher.getRotation());
			matrices.scale(0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = matrices.peek().getPositionMatrix();
			TextRenderer textRenderer = this.getTextRenderer();
			float f = (float)(-textRenderer.getWidth(text)) / 2.0F;
			int j = (int)(MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F) * 255.0F) << 24;
			textRenderer.draw(
				text, f, (float)i, -2130706433, false, matrix4f, vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, light
			);
			if (bl) {
				textRenderer.draw(
					text, f, (float)i, Colors.WHITE, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, LightmapTextureManager.applyEmission(light, 2)
				);
			}

			matrices.pop();
		}
	}

	@Nullable
	protected Text getDisplayName(T entity) {
		return entity.getDisplayName();
	}

	protected float getShadowRadius(S state) {
		return this.shadowRadius;
	}

	public abstract S getRenderState();

	public final S getAndUpdateRenderState(T entity, float tickDelta) {
		S entityRenderState = this.state;
		this.updateRenderState(entity, entityRenderState, tickDelta);
		return entityRenderState;
	}

	public void updateRenderState(T entity, S state, float tickDelta) {
		state.x = MathHelper.lerp((double)tickDelta, entity.lastRenderX, entity.getX());
		state.y = MathHelper.lerp((double)tickDelta, entity.lastRenderY, entity.getY());
		state.z = MathHelper.lerp((double)tickDelta, entity.lastRenderZ, entity.getZ());
		state.invisible = entity.isInvisible();
		state.age = (float)entity.age + tickDelta;
		state.width = entity.getWidth();
		state.height = entity.getHeight();
		state.standingEyeHeight = entity.getStandingEyeHeight();
		if (entity.hasVehicle()
			&& entity.getVehicle() instanceof AbstractMinecartEntity abstractMinecartEntity
			&& abstractMinecartEntity.getController() instanceof ExperimentalMinecartController experimentalMinecartController
			&& experimentalMinecartController.hasCurrentLerpSteps()) {
			double d = MathHelper.lerp((double)tickDelta, abstractMinecartEntity.lastRenderX, abstractMinecartEntity.getX());
			double e = MathHelper.lerp((double)tickDelta, abstractMinecartEntity.lastRenderY, abstractMinecartEntity.getY());
			double f = MathHelper.lerp((double)tickDelta, abstractMinecartEntity.lastRenderZ, abstractMinecartEntity.getZ());
			state.positionOffset = experimentalMinecartController.getLerpedPosition(tickDelta).subtract(new Vec3d(d, e, f));
		} else {
			state.positionOffset = null;
		}

		state.squaredDistanceToCamera = this.dispatcher.getSquaredDistanceToCamera(entity);
		boolean bl = state.squaredDistanceToCamera < 4096.0 && this.hasLabel(entity, state.squaredDistanceToCamera);
		if (bl) {
			state.displayName = this.getDisplayName(entity);
			state.nameLabelPos = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getLerpedYaw(tickDelta));
		} else {
			state.displayName = null;
		}

		state.sneaking = entity.isSneaky();
		Entity entity2 = entity instanceof Leashable leashable ? leashable.getLeashHolder() : null;
		if (entity2 != null) {
			float g = entity.lerpYaw(tickDelta) * (float) (Math.PI / 180.0);
			Vec3d vec3d = entity.getLeashOffset(tickDelta).rotateY(-g);
			BlockPos blockPos = BlockPos.ofFloored(entity.getCameraPosVec(tickDelta));
			BlockPos blockPos2 = BlockPos.ofFloored(entity2.getCameraPosVec(tickDelta));
			if (state.leashData == null) {
				state.leashData = new EntityRenderState.LeashData();
			}

			EntityRenderState.LeashData leashData = state.leashData;
			leashData.offset = vec3d;
			leashData.startPos = entity.getLerpedPos(tickDelta).add(vec3d);
			leashData.endPos = entity2.getLeashPos(tickDelta);
			leashData.leashedEntityBlockLight = this.getBlockLight(entity, blockPos);
			leashData.leashHolderBlockLight = this.dispatcher.getRenderer(entity2).getBlockLight(entity2, blockPos2);
			leashData.leashedEntitySkyLight = entity.getWorld().getLightLevel(LightType.SKY, blockPos);
			leashData.leashHolderSkyLight = entity.getWorld().getLightLevel(LightType.SKY, blockPos2);
		} else {
			state.leashData = null;
		}

		state.onFire = entity.doesRenderOnFire();
	}
}
