package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9064;
import net.minecraft.class_9066;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
	protected static final float field_32921 = 0.025F;
	protected final EntityRenderDispatcher dispatcher;
	private final TextRenderer textRenderer;
	protected float shadowRadius;
	protected float shadowOpacity = 1.0F;

	protected EntityRenderer(EntityRendererFactory.Context ctx) {
		this.dispatcher = ctx.getRenderDispatcher();
		this.textRenderer = ctx.getTextRenderer();
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
		} else if (entity.ignoreCameraFrustum) {
			return true;
		} else {
			Box box = entity.getVisibilityBoundingBox().expand(0.5);
			if (box.isNaN() || box.getAverageSideLength() == 0.0) {
				box = new Box(entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0, entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0);
			}

			return frustum.isVisible(box);
		}
	}

	public Vec3d getPositionOffset(T entity, float tickDelta) {
		return Vec3d.ZERO;
	}

	public void render(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		if (this.hasLabel(entity)) {
			this.renderLabelIfPresent(entity, entity.getDisplayName(), matrices, vertexConsumers, light, tickDelta);
		}
	}

	/**
	 * Determines whether the passed entity should render with a nameplate above its head.
	 * 
	 * <p>Checks for a custom nametag on living entities, and for teams/team visibilities for players.
	 */
	protected boolean hasLabel(T entity) {
		return entity.shouldRenderName() || entity.hasCustomName() && entity == this.dispatcher.targetedEntity;
	}

	public abstract Identifier getTexture(T entity);

	public TextRenderer getTextRenderer() {
		return this.textRenderer;
	}

	protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float f) {
		double d = this.dispatcher.getSquaredDistanceToCamera(entity);
		if (!(d > 4096.0)) {
			class_9066 lv = entity.getDimensions(entity.getPose()).attachments();
			Vec3d vec3d = lv.method_55675(class_9064.NAME_TAG, 0, entity.getYaw(f));
			if (vec3d != null) {
				boolean bl = !entity.isSneaky();
				int i = "deadmau5".equals(text.getString()) ? -10 : 0;
				matrices.push();
				matrices.translate(vec3d.x, vec3d.y + 0.5, vec3d.z);
				matrices.multiply(this.dispatcher.getRotation());
				matrices.scale(-0.025F, -0.025F, 0.025F);
				Matrix4f matrix4f = matrices.peek().getPositionMatrix();
				float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
				int j = (int)(g * 255.0F) << 24;
				TextRenderer textRenderer = this.getTextRenderer();
				float h = (float)(-textRenderer.getWidth(text) / 2);
				textRenderer.draw(
					text, h, (float)i, 553648127, false, matrix4f, vertexConsumers, bl ? TextRenderer.TextLayerType.SEE_THROUGH : TextRenderer.TextLayerType.NORMAL, j, light
				);
				if (bl) {
					textRenderer.draw(text, h, (float)i, Colors.WHITE, false, matrix4f, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
				}

				matrices.pop();
			}
		}
	}

	protected float method_55831(T entity) {
		return this.shadowRadius;
	}
}
