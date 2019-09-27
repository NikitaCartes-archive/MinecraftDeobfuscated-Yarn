package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4604;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.Matrix4f;
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

	public boolean isVisible(T entity, class_4604 arg, double d, double e, double f) {
		if (!entity.shouldRenderFrom(d, e, f)) {
			return false;
		} else if (entity.ignoreCameraFrustum) {
			return true;
		} else {
			Box box = entity.getVisibilityBoundingBox().expand(0.5);
			if (box.isValid() || box.getAverageSideLength() == 0.0) {
				box = new Box(entity.x - 2.0, entity.y - 2.0, entity.z - 2.0, entity.x + 2.0, entity.y + 2.0, entity.z + 2.0);
			}

			return arg.method_23093(box);
		}
	}

	public Vec3d method_23169(T entity, double d, double e, double f, float g) {
		return Vec3d.ZERO;
	}

	public void render(T entity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		if (this.hasLabel(entity)) {
			this.renderLabelIfPresent(entity, entity.getDisplayName().asFormattedString(), arg, arg2);
		}
	}

	protected boolean hasLabel(T entity) {
		return entity.shouldRenderName() && entity.hasCustomName();
	}

	public abstract Identifier getTexture(T entity);

	public TextRenderer getFontRenderer() {
		return this.renderManager.getTextRenderer();
	}

	protected void renderLabelIfPresent(T entity, String string, class_4587 arg, class_4597 arg2) {
		double d = this.renderManager.method_23168(entity);
		if (!(d > 4096.0)) {
			int i = entity.getLightmapCoordinates();
			if (entity.isOnFire()) {
				i = 15728880;
			}

			boolean bl = !entity.method_21751();
			float f = entity.getHeight() + 0.5F;
			int j = "deadmau5".equals(string) ? -10 : 0;
			arg.method_22903();
			arg.method_22904(0.0, (double)f, 0.0);
			arg.method_22907(Vector3f.field_20705.method_23214(-this.renderManager.cameraYaw, true));
			arg.method_22907(Vector3f.field_20703.method_23214(this.renderManager.cameraPitch, true));
			arg.method_22905(-0.025F, -0.025F, 0.025F);
			Matrix4f matrix4f = arg.method_22910();
			float g = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
			int k = (int)(g * 255.0F) << 24;
			TextRenderer textRenderer = this.getFontRenderer();
			float h = (float)(-textRenderer.getStringWidth(string) / 2);
			textRenderer.method_22942(string, h, (float)j, 553648127, false, matrix4f, arg2, bl, k, i);
			if (bl) {
				textRenderer.method_22942(string, h, (float)j, -1, false, matrix4f, arg2, false, 0, i);
			}

			arg.method_22909();
		}
	}

	public EntityRenderDispatcher getRenderManager() {
		return this.renderManager;
	}
}
