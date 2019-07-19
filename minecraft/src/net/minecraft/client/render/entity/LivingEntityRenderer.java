package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final NativeImageBackedTexture colorOverlayTexture = Util.make(new NativeImageBackedTexture(16, 16, false), nativeImageBackedTexture -> {
		nativeImageBackedTexture.getImage().untrack();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				nativeImageBackedTexture.getImage().setPixelRgba(j, i, -1);
			}
		}

		nativeImageBackedTexture.upload();
	});
	protected M model;
	protected final FloatBuffer colorOverlayBuffer = GlAllocationUtils.allocateFloatBuffer(4);
	protected final List<FeatureRenderer<T, M>> features = Lists.<FeatureRenderer<T, M>>newArrayList();
	protected boolean disableOutlineRender;

	public LivingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M entityModel, float f) {
		super(entityRenderDispatcher);
		this.model = entityModel;
		this.field_4673 = f;
	}

	protected final boolean addFeature(FeatureRenderer<T, M> feature) {
		return this.features.add(feature);
	}

	@Override
	public M getModel() {
		return this.model;
	}

	public void render(T livingEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, h);
		this.model.riding = livingEntity.hasVehicle();
		this.model.child = livingEntity.isBaby();

		try {
			float i = MathHelper.lerpAngleDegrees(h, livingEntity.field_6220, livingEntity.field_6283);
			float j = MathHelper.lerpAngleDegrees(h, livingEntity.prevHeadYaw, livingEntity.headYaw);
			float k = j - i;
			if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity) {
				LivingEntity livingEntity2 = (LivingEntity)livingEntity.getVehicle();
				i = MathHelper.lerpAngleDegrees(h, livingEntity2.field_6220, livingEntity2.field_6283);
				k = j - i;
				float l = MathHelper.wrapDegrees(k);
				if (l < -85.0F) {
					l = -85.0F;
				}

				if (l >= 85.0F) {
					l = 85.0F;
				}

				i = j - l;
				if (l * l > 2500.0F) {
					i += l * 0.2F;
				}

				k = j - i;
			}

			float m = MathHelper.lerp(h, livingEntity.prevPitch, livingEntity.pitch);
			this.method_4048(livingEntity, d, e, f);
			float lx = this.getAnimationProgress(livingEntity, h);
			this.setupTransforms(livingEntity, lx, i, h);
			float n = this.scaleAndTranslate(livingEntity, h);
			float o = 0.0F;
			float p = 0.0F;
			if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
				o = MathHelper.lerp(h, livingEntity.lastLimbDistance, livingEntity.limbDistance);
				p = livingEntity.limbAngle - livingEntity.limbDistance * (1.0F - h);
				if (livingEntity.isBaby()) {
					p *= 3.0F;
				}

				if (o > 1.0F) {
					o = 1.0F;
				}
			}

			GlStateManager.enableAlphaTest();
			this.model.animateModel(livingEntity, p, o, h);
			this.model.setAngles(livingEntity, p, o, lx, k, m, n);
			if (this.renderOutlines) {
				boolean bl = this.beforeOutlineRender(livingEntity);
				GlStateManager.enableColorMaterial();
				GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(livingEntity));
				if (!this.disableOutlineRender) {
					this.render(livingEntity, p, o, lx, k, m, n);
				}

				if (!livingEntity.isSpectator()) {
					this.renderFeatures(livingEntity, p, o, h, lx, k, m, n);
				}

				GlStateManager.tearDownSolidRenderingTextureCombine();
				GlStateManager.disableColorMaterial();
				if (bl) {
					this.afterOutlineRender();
				}
			} else {
				boolean blx = this.applyOverlayColor(livingEntity, h);
				this.render(livingEntity, p, o, lx, k, m, n);
				if (blx) {
					this.disableOverlayColor();
				}

				GlStateManager.depthMask(true);
				if (!livingEntity.isSpectator()) {
					this.renderFeatures(livingEntity, p, o, h, lx, k, m, n);
				}
			}

			GlStateManager.disableRescaleNormal();
		} catch (Exception var19) {
			LOGGER.error("Couldn't render entity", (Throwable)var19);
		}

		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
		super.render(livingEntity, d, e, f, g, h);
	}

	public float scaleAndTranslate(T entity, float tickDelta) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		this.scale(entity, tickDelta);
		float f = 0.0625F;
		GlStateManager.translatef(0.0F, -1.501F, 0.0F);
		return 0.0625F;
	}

	protected boolean beforeOutlineRender(T livingEntity) {
		GlStateManager.disableLighting();
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.disableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		return true;
	}

	protected void afterOutlineRender() {
		GlStateManager.enableLighting();
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.enableTexture();
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	protected void render(T entity, float limbAngle, float limbDistance, float age, float headYaw, float headPitch, float scale) {
		boolean bl = this.method_4056(entity);
		boolean bl2 = !bl && !entity.isInvisibleTo(MinecraftClient.getInstance().player);
		if (bl || bl2) {
			if (!this.bindEntityTexture(entity)) {
				return;
			}

			if (bl2) {
				GlStateManager.setProfile(GlStateManager.RenderMode.TRANSPARENT_MODEL);
			}

			this.model.render(entity, limbAngle, limbDistance, age, headYaw, headPitch, scale);
			if (bl2) {
				GlStateManager.unsetProfile(GlStateManager.RenderMode.TRANSPARENT_MODEL);
			}
		}
	}

	protected boolean method_4056(T livingEntity) {
		return !livingEntity.isInvisible() || this.renderOutlines;
	}

	protected boolean applyOverlayColor(T livingEntity, float f) {
		return this.applyOverlayColor(livingEntity, f, true);
	}

	protected boolean applyOverlayColor(T entity, float tickDelta, boolean hasHurtOverlay) {
		float f = entity.getBrightnessAtEyes();
		int i = this.getOverlayColor(entity, f, tickDelta);
		boolean bl = (i >> 24 & 0xFF) > 0;
		boolean bl2 = entity.hurtTime > 0 || entity.deathTime > 0;
		if (!bl && !bl2) {
			return false;
		} else if (!bl && !hasHurtOverlay) {
			return false;
		} else {
			GlStateManager.activeTexture(GLX.GL_TEXTURE0);
			GlStateManager.enableTexture();
			GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
			GlStateManager.activeTexture(GLX.GL_TEXTURE1);
			GlStateManager.enableTexture();
			GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, GLX.GL_INTERPOLATE);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_CONSTANT);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE2_RGB, GLX.GL_CONSTANT);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND2_RGB, 770);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
			this.colorOverlayBuffer.position(0);
			if (bl2) {
				this.colorOverlayBuffer.put(1.0F);
				this.colorOverlayBuffer.put(0.0F);
				this.colorOverlayBuffer.put(0.0F);
				this.colorOverlayBuffer.put(0.3F);
			} else {
				float g = (float)(i >> 24 & 0xFF) / 255.0F;
				float h = (float)(i >> 16 & 0xFF) / 255.0F;
				float j = (float)(i >> 8 & 0xFF) / 255.0F;
				float k = (float)(i & 0xFF) / 255.0F;
				this.colorOverlayBuffer.put(h);
				this.colorOverlayBuffer.put(j);
				this.colorOverlayBuffer.put(k);
				this.colorOverlayBuffer.put(1.0F - g);
			}

			this.colorOverlayBuffer.flip();
			GlStateManager.texEnv(8960, 8705, this.colorOverlayBuffer);
			GlStateManager.activeTexture(GLX.GL_TEXTURE2);
			GlStateManager.enableTexture();
			GlStateManager.bindTexture(colorOverlayTexture.getGlId());
			GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_PREVIOUS);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_TEXTURE1);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
			GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
			GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
			GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
			GlStateManager.activeTexture(GLX.GL_TEXTURE0);
			return true;
		}
	}

	protected void disableOverlayColor() {
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
		GlStateManager.enableTexture();
		GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE1_ALPHA, GLX.GL_PRIMARY_COLOR);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND1_ALPHA, 770);
		GlStateManager.activeTexture(GLX.GL_TEXTURE1);
		GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.activeTexture(GLX.GL_TEXTURE2);
		GlStateManager.disableTexture();
		GlStateManager.bindTexture(0);
		GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
		GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
		GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
		GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
		GlStateManager.activeTexture(GLX.GL_TEXTURE0);
	}

	protected void method_4048(T livingEntity, double d, double e, double f) {
		if (livingEntity.getPose() == EntityPose.SLEEPING) {
			Direction direction = livingEntity.getSleepingDirection();
			if (direction != null) {
				float g = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
				GlStateManager.translatef((float)d - (float)direction.getOffsetX() * g, (float)e, (float)f - (float)direction.getOffsetZ() * g);
				return;
			}
		}

		GlStateManager.translatef((float)d, (float)e, (float)f);
	}

	private static float method_18656(Direction direction) {
		switch (direction) {
			case SOUTH:
				return 90.0F;
			case WEST:
				return 0.0F;
			case NORTH:
				return 270.0F;
			case EAST:
				return 180.0F;
			default:
				return 0.0F;
		}
	}

	protected void setupTransforms(T entity, float pitch, float yaw, float delta) {
		EntityPose entityPose = entity.getPose();
		if (entityPose != EntityPose.SLEEPING) {
			GlStateManager.rotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
		}

		if (entity.deathTime > 0) {
			float f = ((float)entity.deathTime + delta - 1.0F) / 20.0F * 1.6F;
			f = MathHelper.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			GlStateManager.rotatef(f * this.getLyingAngle(entity), 0.0F, 0.0F, 1.0F);
		} else if (entity.isUsingRiptide()) {
			GlStateManager.rotatef(-90.0F - entity.pitch, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(((float)entity.age + delta) * -75.0F, 0.0F, 1.0F, 0.0F);
		} else if (entityPose == EntityPose.SLEEPING) {
			Direction direction = entity.getSleepingDirection();
			GlStateManager.rotatef(direction != null ? method_18656(direction) : yaw, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(this.getLyingAngle(entity), 0.0F, 0.0F, 1.0F);
			GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
		} else if (entity.hasCustomName() || entity instanceof PlayerEntity) {
			String string = Formatting.strip(entity.getName().getString());
			if (string != null
				&& ("Dinnerbone".equals(string) || "Grumm".equals(string))
				&& (!(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(PlayerModelPart.CAPE))) {
				GlStateManager.translatef(0.0F, entity.getHeight() + 0.1F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
			}
		}
	}

	protected float getHandSwingProgress(T entity, float tickDelta) {
		return entity.getHandSwingProgress(tickDelta);
	}

	/**
	 * This value is passed to other methods when calculating angles for animation.
	 * It's typically just the sum of the entity's age (in ticks) and the passed in tickDelta.
	 */
	protected float getAnimationProgress(T entity, float tickDelta) {
		return (float)entity.age + tickDelta;
	}

	protected void renderFeatures(T entity, float limbAngle, float limbDistance, float tickDelta, float age, float headYaw, float headPitch, float scale) {
		for (FeatureRenderer<T, M> featureRenderer : this.features) {
			boolean bl = this.applyOverlayColor(entity, tickDelta, featureRenderer.hasHurtOverlay());
			featureRenderer.render(entity, limbAngle, limbDistance, tickDelta, age, headYaw, headPitch, scale);
			if (bl) {
				this.disableOverlayColor();
			}
		}
	}

	protected float getLyingAngle(T entity) {
		return 90.0F;
	}

	protected int getOverlayColor(T livingEntity, float f, float g) {
		return 0;
	}

	protected void scale(T entity, float tickDelta) {
	}

	public void renderLabelIfPresent(T livingEntity, double d, double e, double f) {
		if (this.hasLabel(livingEntity)) {
			double g = livingEntity.squaredDistanceTo(this.renderManager.camera.getPos());
			float h = livingEntity.isInSneakingPose() ? 32.0F : 64.0F;
			if (!(g >= (double)(h * h))) {
				String string = livingEntity.getDisplayName().asFormattedString();
				GlStateManager.alphaFunc(516, 0.1F);
				this.renderLabel(livingEntity, d, e, f, string, g);
			}
		}
	}

	protected boolean hasLabel(T livingEntity) {
		ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
		boolean bl = !livingEntity.isInvisibleTo(clientPlayerEntity);
		if (livingEntity != clientPlayerEntity) {
			AbstractTeam abstractTeam = livingEntity.getScoreboardTeam();
			AbstractTeam abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
			if (abstractTeam != null) {
				AbstractTeam.VisibilityRule visibilityRule = abstractTeam.getNameTagVisibilityRule();
				switch (visibilityRule) {
					case ALWAYS:
						return bl;
					case NEVER:
						return false;
					case HIDE_FOR_OTHER_TEAMS:
						return abstractTeam2 == null ? bl : abstractTeam.isEqual(abstractTeam2) && (abstractTeam.shouldShowFriendlyInvisibles() || bl);
					case HIDE_FOR_OWN_TEAM:
						return abstractTeam2 == null ? bl : !abstractTeam.isEqual(abstractTeam2) && bl;
					default:
						return true;
				}
			}
		}

		return MinecraftClient.isHudEnabled() && livingEntity != this.renderManager.camera.getFocusedEntity() && bl && !livingEntity.hasPassengers();
	}
}
