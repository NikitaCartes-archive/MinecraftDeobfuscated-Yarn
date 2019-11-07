package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public abstract class EntityRenderer<T extends Entity> {
	protected final EntityRenderDispatcher renderManager;
	protected float field_4673;
	protected float field_4672 = 1.0F;

	protected EntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this.renderManager = entityRenderDispatcher;
	}

	public boolean isVisible(T entity, Frustum visibleRegion, double d, double e, double f) {
		if (!entity.shouldRenderFrom(d, e, f)) {
			return false;
		} else if (entity.ignoreCameraFrustum) {
			return true;
		} else {
			Box box = entity.getVisibilityBoundingBox().expand(0.5);
			if (box.isValid() || box.getAverageSideLength() == 0.0) {
				box = new Box(entity.getX() - 2.0, entity.getY() - 2.0, entity.getZ() - 2.0, entity.getX() + 2.0, entity.getY() + 2.0, entity.getZ() + 2.0);
			}

			return visibleRegion.isVisible(box);
		}
	}

	public Vec3d getPositionOffset(T entity, float f) {
		return Vec3d.ZERO;
	}

	public void render(T entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		if (this.hasLabel(entity)) {
			this.renderLabelIfPresent(entity, entity.getDisplayName().asFormattedString(), matrixStack, vertexConsumerProvider, i);
		}
	}

	protected boolean hasLabel(T entity) {
		return entity.shouldRenderName() && entity.hasCustomName();
	}

	public abstract Identifier getTexture(T entity);

	public TextRenderer getFontRenderer() {
		return this.renderManager.getTextRenderer();
	}

	protected void renderLabelIfPresent(T entity, String string, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		double d = this.renderManager.getSquaredDistanceToCamera(entity);
		if (!(d > 4096.0)) {
			boolean bl = !entity.method_21751();
			float f = entity.getHeight() + 0.5F;
			int j = "deadmau5".equals(string) ? -10 : 0;
			matrixStack.push();
			matrixStack.translate(0.0, (double)f, 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-this.renderManager.cameraYaw));
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(this.renderManager.cameraPitch));
			matrixStack.scale(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = matrixStack.method_23760().method_23761();
			float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
			int k = (int)(g * 255.0F) << 24;
			TextRenderer textRenderer = this.getFontRenderer();
			float h = (float)(-textRenderer.getStringWidth(string) / 2);
			textRenderer.draw(string, h, (float)j, 553648127, false, matrix4f, vertexConsumerProvider, bl, k, i);
			if (bl) {
				textRenderer.draw(string, h, (float)j, -1, false, matrix4f, vertexConsumerProvider, false, 0, i);
			}

			matrixStack.pop();
		}
	}

	public EntityRenderDispatcher getRenderManager() {
		return this.renderManager;
	}
}
