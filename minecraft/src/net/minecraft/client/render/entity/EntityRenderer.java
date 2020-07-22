package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LightType;

@Environment(EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
	protected final EntityRenderDispatcher dispatcher;
	protected float shadowRadius;
	protected float shadowOpacity = 1.0F;

	protected EntityRenderer(EntityRenderDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public final int getLight(T entity, float tickDelta) {
		BlockPos blockPos = new BlockPos(entity.getCameraPosVec(tickDelta));
		return LightmapTextureManager.pack(this.getBlockLight(entity, blockPos), this.method_27950(entity, blockPos));
	}

	protected int method_27950(T entity, BlockPos blockPos) {
		return entity.world.getLightLevel(LightType.SKY, blockPos);
	}

	protected int getBlockLight(T entity, BlockPos blockPos) {
		return entity.isOnFire() ? 15 : entity.world.getLightLevel(LightType.BLOCK, blockPos);
	}

	public boolean shouldRender(T entity, Frustum frustum, double x, double y, double z) {
		if (!entity.shouldRender(x, y, z)) {
			return false;
		} else if (entity.ignoreCameraFrustum) {
			return true;
		} else {
			Box box = entity.getVisibilityBoundingBox().expand(0.5);
			if (box.isValid() || box.getAverageSideLength() == 0.0) {
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
			this.renderLabelIfPresent(entity, entity.getDisplayName(), matrices, vertexConsumers, light);
		}
	}

	/**
	 * Determines whether the passed entity should render with a nameplate above its head.
	 * 
	 * <p>Checks for a custom nametag on living entities, and for teams/team visibilities for players.</p>
	 */
	protected boolean hasLabel(T entity) {
		return entity.shouldRenderName() && entity.hasCustomName();
	}

	public abstract Identifier getTexture(T entity);

	public TextRenderer getFontRenderer() {
		return this.dispatcher.getTextRenderer();
	}

	protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		double d = this.dispatcher.getSquaredDistanceToCamera(entity);
		if (!(d > 4096.0)) {
			boolean bl = !entity.isSneaky();
			float f = entity.getHeight() + 0.5F;
			int i = "deadmau5".equals(text.getString()) ? -10 : 0;
			matrices.push();
			matrices.translate(0.0, (double)f, 0.0);
			matrices.multiply(this.dispatcher.getRotation());
			matrices.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = matrices.peek().getModel();
			float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
			int j = (int)(g * 255.0F) << 24;
			TextRenderer textRenderer = this.getFontRenderer();
			float h = (float)(-textRenderer.getWidth(text) / 2);
			textRenderer.method_30882(text, h, (float)i, 553648127, false, matrix4f, vertexConsumers, bl, j, light);
			if (bl) {
				textRenderer.method_30882(text, h, (float)i, -1, false, matrix4f, vertexConsumers, false, 0, light);
			}

			matrices.pop();
		}
	}

	public EntityRenderDispatcher getRenderManager() {
		return this.dispatcher;
	}
}
