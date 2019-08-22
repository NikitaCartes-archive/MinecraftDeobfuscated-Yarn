package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
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
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final NativeImageBackedTexture colorOverlayTexture = SystemUtil.consume(
		new NativeImageBackedTexture(16, 16, false), nativeImageBackedTexture -> {
			nativeImageBackedTexture.getImage().untrack();

			for (int i = 0; i < 16; i++) {
				for (int j = 0; j < 16; j++) {
					nativeImageBackedTexture.getImage().setPixelRGBA(j, i, -1);
				}
			}

			nativeImageBackedTexture.upload();
		}
	);
	protected M model;
	protected final FloatBuffer colorOverlayBuffer = GlAllocationUtils.allocateFloatBuffer(4);
	protected final List<FeatureRenderer<T, M>> features = Lists.<FeatureRenderer<T, M>>newArrayList();
	protected boolean disableOutlineRender;

	public LivingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M entityModel, float f) {
		super(entityRenderDispatcher);
		this.model = entityModel;
		this.field_4673 = f;
	}

	protected final boolean addFeature(FeatureRenderer<T, M> featureRenderer) {
		return this.features.add(featureRenderer);
	}

	@Override
	public M getModel() {
		return this.model;
	}

	public void method_4054(T livingEntity, double d, double e, double f, float g, float h) {
		RenderSystem.pushMatrix();
		RenderSystem.disableCull();
		this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, h);
		this.model.isRiding = livingEntity.hasVehicle();
		this.model.isChild = livingEntity.isBaby();

		try {
			float i = MathHelper.lerpAngleDegrees(h, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
			float j = MathHelper.lerpAngleDegrees(h, livingEntity.prevHeadYaw, livingEntity.headYaw);
			float k = j - i;
			if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity) {
				LivingEntity livingEntity2 = (LivingEntity)livingEntity.getVehicle();
				i = MathHelper.lerpAngleDegrees(h, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
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
			float lx = this.getAge(livingEntity, h);
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

			RenderSystem.enableAlphaTest();
			this.model.animateModel(livingEntity, p, o, h);
			this.model.setAngles(livingEntity, p, o, lx, k, m, n);
			if (this.renderOutlines) {
				boolean bl = this.beforeOutlineRender(livingEntity);
				RenderSystem.enableColorMaterial();
				RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(livingEntity));
				if (!this.disableOutlineRender) {
					this.render(livingEntity, p, o, lx, k, m, n);
				}

				if (!livingEntity.isSpectator()) {
					this.renderFeatures(livingEntity, p, o, h, lx, k, m, n);
				}

				RenderSystem.tearDownSolidRenderingTextureCombine();
				RenderSystem.disableColorMaterial();
				if (bl) {
					this.afterOutlineRender();
				}
			} else {
				boolean blx = this.applyOverlayColor(livingEntity, h);
				this.render(livingEntity, p, o, lx, k, m, n);
				if (blx) {
					this.disableOverlayColor();
				}

				RenderSystem.depthMask(true);
				if (!livingEntity.isSpectator()) {
					this.renderFeatures(livingEntity, p, o, h, lx, k, m, n);
				}
			}

			RenderSystem.disableRescaleNormal();
		} catch (Exception var19) {
			LOGGER.error("Couldn't render entity", (Throwable)var19);
		}

		RenderSystem.activeTexture(33985);
		RenderSystem.enableTexture();
		RenderSystem.activeTexture(33984);
		RenderSystem.enableCull();
		RenderSystem.popMatrix();
		super.render(livingEntity, d, e, f, g, h);
	}

	public float scaleAndTranslate(T livingEntity, float f) {
		RenderSystem.enableRescaleNormal();
		RenderSystem.scalef(-1.0F, -1.0F, 1.0F);
		this.scale(livingEntity, f);
		float g = 0.0625F;
		RenderSystem.translatef(0.0F, -1.501F, 0.0F);
		return 0.0625F;
	}

	protected boolean beforeOutlineRender(T livingEntity) {
		RenderSystem.disableLighting();
		RenderSystem.activeTexture(33985);
		RenderSystem.disableTexture();
		RenderSystem.activeTexture(33984);
		return true;
	}

	protected void afterOutlineRender() {
		RenderSystem.enableLighting();
		RenderSystem.activeTexture(33985);
		RenderSystem.enableTexture();
		RenderSystem.activeTexture(33984);
	}

	protected void render(T livingEntity, float f, float g, float h, float i, float j, float k) {
		boolean bl = this.method_4056(livingEntity);
		boolean bl2 = !bl && !livingEntity.canSeePlayer(MinecraftClient.getInstance().player);
		if (bl || bl2) {
			if (!this.bindEntityTexture(livingEntity)) {
				return;
			}

			if (bl2) {
				class_4493.method_21967(class_4493.RenderMode.TRANSPARENT_MODEL);
			}

			this.model.render(livingEntity, f, g, h, i, j, k);
			if (bl2) {
				class_4493.method_21994(class_4493.RenderMode.TRANSPARENT_MODEL);
			}
		}
	}

	protected boolean method_4056(T livingEntity) {
		return !livingEntity.isInvisible() || this.renderOutlines;
	}

	protected boolean applyOverlayColor(T livingEntity, float f) {
		return this.applyOverlayColor(livingEntity, f, true);
	}

	protected boolean applyOverlayColor(T livingEntity, float f, boolean bl) {
		float g = livingEntity.getBrightnessAtEyes();
		int i = this.getOverlayColor(livingEntity, g, f);
		boolean bl2 = (i >> 24 & 0xFF) > 0;
		boolean bl3 = livingEntity.hurtTime > 0 || livingEntity.deathTime > 0;
		if (!bl2 && !bl3) {
			return false;
		} else if (!bl2 && !bl) {
			return false;
		} else {
			RenderSystem.activeTexture(33984);
			RenderSystem.enableTexture();
			RenderSystem.texEnv(8960, 8704, 34160);
			RenderSystem.texEnv(8960, 34161, 8448);
			RenderSystem.texEnv(8960, 34176, 33984);
			RenderSystem.texEnv(8960, 34177, 34167);
			RenderSystem.texEnv(8960, 34192, 768);
			RenderSystem.texEnv(8960, 34193, 768);
			RenderSystem.texEnv(8960, 34162, 7681);
			RenderSystem.texEnv(8960, 34184, 33984);
			RenderSystem.texEnv(8960, 34200, 770);
			RenderSystem.activeTexture(33985);
			RenderSystem.enableTexture();
			RenderSystem.texEnv(8960, 8704, 34160);
			RenderSystem.texEnv(8960, 34161, 34165);
			RenderSystem.texEnv(8960, 34176, 34166);
			RenderSystem.texEnv(8960, 34177, 34168);
			RenderSystem.texEnv(8960, 34178, 34166);
			RenderSystem.texEnv(8960, 34192, 768);
			RenderSystem.texEnv(8960, 34193, 768);
			RenderSystem.texEnv(8960, 34194, 770);
			RenderSystem.texEnv(8960, 34162, 7681);
			RenderSystem.texEnv(8960, 34184, 34168);
			RenderSystem.texEnv(8960, 34200, 770);
			this.colorOverlayBuffer.position(0);
			if (bl3) {
				this.colorOverlayBuffer.put(1.0F);
				this.colorOverlayBuffer.put(0.0F);
				this.colorOverlayBuffer.put(0.0F);
				this.colorOverlayBuffer.put(0.3F);
			} else {
				float h = (float)(i >> 24 & 0xFF) / 255.0F;
				float j = (float)(i >> 16 & 0xFF) / 255.0F;
				float k = (float)(i >> 8 & 0xFF) / 255.0F;
				float l = (float)(i & 0xFF) / 255.0F;
				this.colorOverlayBuffer.put(j);
				this.colorOverlayBuffer.put(k);
				this.colorOverlayBuffer.put(l);
				this.colorOverlayBuffer.put(1.0F - h);
			}

			this.colorOverlayBuffer.flip();
			RenderSystem.texEnv(8960, 8705, this.colorOverlayBuffer);
			RenderSystem.activeTexture(33986);
			RenderSystem.enableTexture();
			RenderSystem.bindTexture(colorOverlayTexture.getGlId());
			RenderSystem.texEnv(8960, 8704, 34160);
			RenderSystem.texEnv(8960, 34161, 8448);
			RenderSystem.texEnv(8960, 34176, 34168);
			RenderSystem.texEnv(8960, 34177, 33985);
			RenderSystem.texEnv(8960, 34192, 768);
			RenderSystem.texEnv(8960, 34193, 768);
			RenderSystem.texEnv(8960, 34162, 7681);
			RenderSystem.texEnv(8960, 34184, 34168);
			RenderSystem.texEnv(8960, 34200, 770);
			RenderSystem.activeTexture(33984);
			return true;
		}
	}

	protected void disableOverlayColor() {
		RenderSystem.activeTexture(33984);
		RenderSystem.enableTexture();
		RenderSystem.texEnv(8960, 8704, 34160);
		RenderSystem.texEnv(8960, 34161, 8448);
		RenderSystem.texEnv(8960, 34176, 33984);
		RenderSystem.texEnv(8960, 34177, 34167);
		RenderSystem.texEnv(8960, 34192, 768);
		RenderSystem.texEnv(8960, 34193, 768);
		RenderSystem.texEnv(8960, 34162, 8448);
		RenderSystem.texEnv(8960, 34184, 33984);
		RenderSystem.texEnv(8960, 34185, 34167);
		RenderSystem.texEnv(8960, 34200, 770);
		RenderSystem.texEnv(8960, 34201, 770);
		RenderSystem.activeTexture(33985);
		RenderSystem.texEnv(8960, 8704, 34160);
		RenderSystem.texEnv(8960, 34161, 8448);
		RenderSystem.texEnv(8960, 34192, 768);
		RenderSystem.texEnv(8960, 34193, 768);
		RenderSystem.texEnv(8960, 34176, 5890);
		RenderSystem.texEnv(8960, 34177, 34168);
		RenderSystem.texEnv(8960, 34162, 8448);
		RenderSystem.texEnv(8960, 34200, 770);
		RenderSystem.texEnv(8960, 34184, 5890);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.activeTexture(33986);
		RenderSystem.disableTexture();
		RenderSystem.bindTexture(0);
		RenderSystem.texEnv(8960, 8704, 34160);
		RenderSystem.texEnv(8960, 34161, 8448);
		RenderSystem.texEnv(8960, 34192, 768);
		RenderSystem.texEnv(8960, 34193, 768);
		RenderSystem.texEnv(8960, 34176, 5890);
		RenderSystem.texEnv(8960, 34177, 34168);
		RenderSystem.texEnv(8960, 34162, 8448);
		RenderSystem.texEnv(8960, 34200, 770);
		RenderSystem.texEnv(8960, 34184, 5890);
		RenderSystem.activeTexture(33984);
	}

	protected void method_4048(T livingEntity, double d, double e, double f) {
		if (livingEntity.getPose() == EntityPose.SLEEPING) {
			Direction direction = livingEntity.getSleepingDirection();
			if (direction != null) {
				float g = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
				RenderSystem.translatef((float)d - (float)direction.getOffsetX() * g, (float)e, (float)f - (float)direction.getOffsetZ() * g);
				return;
			}
		}

		RenderSystem.translatef((float)d, (float)e, (float)f);
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

	protected void setupTransforms(T livingEntity, float f, float g, float h) {
		EntityPose entityPose = livingEntity.getPose();
		if (entityPose != EntityPose.SLEEPING) {
			RenderSystem.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		}

		if (livingEntity.deathTime > 0) {
			float i = ((float)livingEntity.deathTime + h - 1.0F) / 20.0F * 1.6F;
			i = MathHelper.sqrt(i);
			if (i > 1.0F) {
				i = 1.0F;
			}

			RenderSystem.rotatef(i * this.getLyingAngle(livingEntity), 0.0F, 0.0F, 1.0F);
		} else if (livingEntity.isUsingRiptide()) {
			RenderSystem.rotatef(-90.0F - livingEntity.pitch, 1.0F, 0.0F, 0.0F);
			RenderSystem.rotatef(((float)livingEntity.age + h) * -75.0F, 0.0F, 1.0F, 0.0F);
		} else if (entityPose == EntityPose.SLEEPING) {
			Direction direction = livingEntity.getSleepingDirection();
			RenderSystem.rotatef(direction != null ? method_18656(direction) : g, 0.0F, 1.0F, 0.0F);
			RenderSystem.rotatef(this.getLyingAngle(livingEntity), 0.0F, 0.0F, 1.0F);
			RenderSystem.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
		} else if (livingEntity.hasCustomName() || livingEntity instanceof PlayerEntity) {
			String string = Formatting.strip(livingEntity.getName().getString());
			if (string != null
				&& ("Dinnerbone".equals(string) || "Grumm".equals(string))
				&& (!(livingEntity instanceof PlayerEntity) || ((PlayerEntity)livingEntity).isSkinOverlayVisible(PlayerModelPart.CAPE))) {
				RenderSystem.translatef(0.0F, livingEntity.getHeight() + 0.1F, 0.0F);
				RenderSystem.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
			}
		}
	}

	protected float getHandSwingProgress(T livingEntity, float f) {
		return livingEntity.getHandSwingProgress(f);
	}

	protected float getAge(T livingEntity, float f) {
		return (float)livingEntity.age + f;
	}

	protected void renderFeatures(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		for (FeatureRenderer<T, M> featureRenderer : this.features) {
			boolean bl = this.applyOverlayColor(livingEntity, h, featureRenderer.hasHurtOverlay());
			featureRenderer.render(livingEntity, f, g, h, i, j, k, l);
			if (bl) {
				this.disableOverlayColor();
			}
		}
	}

	protected float getLyingAngle(T livingEntity) {
		return 90.0F;
	}

	protected int getOverlayColor(T livingEntity, float f, float g) {
		return 0;
	}

	protected void scale(T livingEntity, float f) {
	}

	public void method_4041(T livingEntity, double d, double e, double f) {
		if (this.method_4055(livingEntity)) {
			double g = livingEntity.squaredDistanceTo(this.renderManager.camera.getPos());
			float h = livingEntity.method_21751() ? 32.0F : 64.0F;
			if (!(g >= (double)(h * h))) {
				String string = livingEntity.getDisplayName().asFormattedString();
				RenderSystem.alphaFunc(516, 0.1F);
				this.renderLabel(livingEntity, d, e, f, string, g);
			}
		}
	}

	protected boolean method_4055(T livingEntity) {
		ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
		boolean bl = !livingEntity.canSeePlayer(clientPlayerEntity);
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
