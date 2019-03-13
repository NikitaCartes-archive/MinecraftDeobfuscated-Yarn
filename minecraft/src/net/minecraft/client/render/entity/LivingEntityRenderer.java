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
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.text.TextFormat;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final NativeImageBackedTexture field_4742 = SystemUtil.consume(new NativeImageBackedTexture(16, 16, false), nativeImageBackedTexture -> {
		nativeImageBackedTexture.getImage().method_4302();

		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				nativeImageBackedTexture.getImage().setPixelRGBA(j, i, -1);
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

	protected final boolean method_4046(FeatureRenderer<T, M> featureRenderer) {
		return this.features.add(featureRenderer);
	}

	@Override
	public M getModel() {
		return this.model;
	}

	public void method_4054(T livingEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		this.model.swingProgress = this.method_4044(livingEntity, h);
		this.model.isRiding = livingEntity.hasVehicle();
		this.model.isChild = livingEntity.isChild();

		try {
			float i = MathHelper.method_17821(h, livingEntity.field_6220, livingEntity.field_6283);
			float j = MathHelper.method_17821(h, livingEntity.prevHeadYaw, livingEntity.headYaw);
			float k = j - i;
			if (livingEntity.hasVehicle() && livingEntity.getRiddenEntity() instanceof LivingEntity) {
				LivingEntity livingEntity2 = (LivingEntity)livingEntity.getRiddenEntity();
				i = MathHelper.method_17821(h, livingEntity2.field_6220, livingEntity2.field_6283);
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
			float lx = this.method_4045(livingEntity, h);
			this.setupTransforms(livingEntity, lx, i, h);
			float n = this.method_4060(livingEntity, h);
			float o = 0.0F;
			float p = 0.0F;
			if (!livingEntity.hasVehicle() && livingEntity.isValid()) {
				o = MathHelper.lerp(h, livingEntity.field_6211, livingEntity.field_6225);
				p = livingEntity.field_6249 - livingEntity.field_6225 * (1.0F - h);
				if (livingEntity.isChild()) {
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
				boolean blx = this.tryApplyOverlayColor(livingEntity, h);
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

	public float method_4060(T livingEntity, float f) {
		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		this.method_4042(livingEntity, f);
		float g = 0.0625F;
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

	protected void render(T livingEntity, float f, float g, float h, float i, float j, float k) {
		boolean bl = this.method_4056(livingEntity);
		boolean bl2 = !bl && !livingEntity.method_5756(MinecraftClient.getInstance().field_1724);
		if (bl || bl2) {
			if (!this.bindEntityTexture(livingEntity)) {
				return;
			}

			if (bl2) {
				GlStateManager.setProfile(GlStateManager.RenderMode.TRANSPARENT_MODEL);
			}

			this.model.render(livingEntity, f, g, h, i, j, k);
			if (bl2) {
				GlStateManager.unsetProfile(GlStateManager.RenderMode.TRANSPARENT_MODEL);
			}
		}
	}

	protected boolean method_4056(T livingEntity) {
		return !livingEntity.isInvisible() || this.renderOutlines;
	}

	protected boolean tryApplyOverlayColor(T livingEntity, float f) {
		return this.tryApplyOverlayColor(livingEntity, f, true);
	}

	protected boolean tryApplyOverlayColor(T livingEntity, float f, boolean bl) {
		float g = livingEntity.method_5718();
		int i = this.getOverlayColor(livingEntity, g, f);
		boolean bl2 = (i >> 24 & 0xFF) > 0;
		boolean bl3 = livingEntity.hurtTime > 0 || livingEntity.deathCounter > 0;
		if (!bl2 && !bl3) {
			return false;
		} else if (!bl2 && !bl) {
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
			GlStateManager.texEnv(8960, 8705, this.colorOverlayBuffer);
			GlStateManager.activeTexture(GLX.GL_TEXTURE2);
			GlStateManager.enableTexture();
			GlStateManager.bindTexture(field_4742.getGlId());
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
		if (livingEntity.method_18376() == EntityPose.field_18078) {
			Direction direction = livingEntity.method_18401();
			if (direction != null) {
				float g = livingEntity.method_18381(EntityPose.field_18076) - 0.1F;
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

	protected void setupTransforms(T livingEntity, float f, float g, float h) {
		EntityPose entityPose = livingEntity.method_18376();
		if (entityPose != EntityPose.field_18078) {
			GlStateManager.rotatef(180.0F - g, 0.0F, 1.0F, 0.0F);
		}

		if (livingEntity.deathCounter > 0) {
			float i = ((float)livingEntity.deathCounter + h - 1.0F) / 20.0F * 1.6F;
			i = MathHelper.sqrt(i);
			if (i > 1.0F) {
				i = 1.0F;
			}

			GlStateManager.rotatef(i * this.getLyingAngle(livingEntity), 0.0F, 0.0F, 1.0F);
		} else if (livingEntity.isUsingRiptide()) {
			GlStateManager.rotatef(-90.0F - livingEntity.pitch, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(((float)livingEntity.age + h) * -75.0F, 0.0F, 1.0F, 0.0F);
		} else if (entityPose == EntityPose.field_18078) {
			Direction direction = livingEntity.method_18401();
			GlStateManager.rotatef(direction != null ? method_18656(direction) : g, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(this.getLyingAngle(livingEntity), 0.0F, 0.0F, 1.0F);
			GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
		} else if (livingEntity.hasCustomName() || livingEntity instanceof PlayerEntity) {
			String string = TextFormat.stripFormatting(livingEntity.method_5477().getString());
			if (string != null
				&& ("Dinnerbone".equals(string) || "Grumm".equals(string))
				&& (!(livingEntity instanceof PlayerEntity) || ((PlayerEntity)livingEntity).method_7348(PlayerModelPart.CAPE))) {
				GlStateManager.translatef(0.0F, livingEntity.getHeight() + 0.1F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
			}
		}
	}

	protected float method_4044(T livingEntity, float f) {
		return livingEntity.method_6055(f);
	}

	protected float method_4045(T livingEntity, float f) {
		return (float)livingEntity.age + f;
	}

	protected void renderFeatures(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		for (FeatureRenderer<T, M> featureRenderer : this.features) {
			boolean bl = this.tryApplyOverlayColor(livingEntity, h, featureRenderer.method_4200());
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

	protected void method_4042(T livingEntity, float f) {
	}

	public void method_4041(T livingEntity, double d, double e, double f) {
		if (this.method_4055(livingEntity)) {
			double g = livingEntity.method_5707(this.renderManager.field_4686.method_19326());
			float h = livingEntity.isSneaking() ? 32.0F : 64.0F;
			if (!(g >= (double)(h * h))) {
				String string = livingEntity.method_5476().getFormattedText();
				GlStateManager.alphaFunc(516, 0.1F);
				this.method_3930(livingEntity, d, e, f, string, g);
			}
		}
	}

	protected boolean method_4055(T livingEntity) {
		ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().field_1724;
		boolean bl = !livingEntity.method_5756(clientPlayerEntity);
		if (livingEntity != clientPlayerEntity) {
			AbstractScoreboardTeam abstractScoreboardTeam = livingEntity.method_5781();
			AbstractScoreboardTeam abstractScoreboardTeam2 = clientPlayerEntity.method_5781();
			if (abstractScoreboardTeam != null) {
				AbstractScoreboardTeam.VisibilityRule visibilityRule = abstractScoreboardTeam.getNameTagVisibilityRule();
				switch (visibilityRule) {
					case ALWAYS:
						return bl;
					case NEVER:
						return false;
					case HIDDEN_FOR_OTHER_TEAMS:
						return abstractScoreboardTeam2 == null
							? bl
							: abstractScoreboardTeam.isEqual(abstractScoreboardTeam2) && (abstractScoreboardTeam.shouldShowFriendlyInvisibles() || bl);
					case HIDDEN_FOR_TEAM:
						return abstractScoreboardTeam2 == null ? bl : !abstractScoreboardTeam.isEqual(abstractScoreboardTeam2) && bl;
					default:
						return true;
				}
			}
		}

		return MinecraftClient.isHudEnabled() && livingEntity != this.renderManager.field_4686.method_19331() && bl && !livingEntity.hasPassengers();
	}
}
