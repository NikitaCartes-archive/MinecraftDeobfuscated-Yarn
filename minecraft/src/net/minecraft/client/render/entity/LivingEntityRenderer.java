package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final float field_32939 = 0.1F;
	protected M model;
	protected final List<FeatureRenderer<T, M>> features = Lists.<FeatureRenderer<T, M>>newArrayList();

	public LivingEntityRenderer(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
		super(ctx);
		this.model = model;
		this.shadowRadius = shadowRadius;
	}

	protected final boolean addFeature(FeatureRenderer<T, M> feature) {
		return this.features.add(feature);
	}

	@Override
	public M getModel() {
		return this.model;
	}

	public void render(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, g);
		this.model.riding = livingEntity.hasVehicle();
		this.model.child = livingEntity.isBaby();
		float h = MathHelper.lerpAngleDegrees(g, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
		float j = MathHelper.lerpAngleDegrees(g, livingEntity.prevHeadYaw, livingEntity.headYaw);
		float k = j - h;
		if (livingEntity.hasVehicle()) {
			Entity l = livingEntity.getVehicle();
			if (l instanceof LivingEntity livingEntity2) {
				h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
				k = j - h;
				float lx = MathHelper.wrapDegrees(k);
				if (lx < -85.0F) {
					lx = -85.0F;
				}

				if (lx >= 85.0F) {
					lx = 85.0F;
				}

				h = j - lx;
				if (lx * lx > 2500.0F) {
					h += lx * 0.2F;
				}

				k = j - h;
			}
		}

		float m = MathHelper.lerp(g, livingEntity.prevPitch, livingEntity.getPitch());
		if (shouldFlipUpsideDown(livingEntity)) {
			m *= -1.0F;
			k *= -1.0F;
		}

		k = MathHelper.wrapDegrees(k);
		if (livingEntity.isInPose(EntityPose.SLEEPING)) {
			Direction direction = livingEntity.getSleepingDirection();
			if (direction != null) {
				float n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
				matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0F, (float)(-direction.getOffsetZ()) * n);
			}
		}

		float l = livingEntity.getScale();
		matrixStack.scale(l, l, l);
		float n = this.getAnimationProgress(livingEntity, g);
		this.setupTransforms(livingEntity, matrixStack, n, h, g, l);
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		this.scale(livingEntity, matrixStack, g);
		matrixStack.translate(0.0F, -1.501F, 0.0F);
		float o = 0.0F;
		float p = 0.0F;
		if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
			o = livingEntity.limbAnimator.getSpeed(g);
			p = livingEntity.limbAnimator.getPos(g);
			if (livingEntity.isBaby()) {
				p *= 3.0F;
			}

			if (o > 1.0F) {
				o = 1.0F;
			}
		}

		this.model.animateModel(livingEntity, p, o, g);
		this.model.setAngles(livingEntity, p, o, n, k, m);
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		boolean bl = this.isVisible(livingEntity);
		boolean bl2 = !bl && !livingEntity.isInvisibleTo(minecraftClient.player);
		boolean bl3 = minecraftClient.hasOutline(livingEntity);
		RenderLayer renderLayer = this.getRenderLayer(livingEntity, bl, bl2, bl3);
		if (renderLayer != null) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
			int q = getOverlay(livingEntity, this.getAnimationCounter(livingEntity, g));
			this.model.render(matrixStack, vertexConsumer, i, q, 1.0F, 1.0F, 1.0F, bl2 ? 0.15F : 1.0F);
		}

		if (!livingEntity.isSpectator()) {
			for(FeatureRenderer<T, M> featureRenderer : this.features) {
				featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntity, p, o, g, n, k, m);
			}
		}

		matrixStack.pop();
		super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	/**
	 * Gets the render layer appropriate for rendering the passed entity. Returns null if the entity should not be rendered.
	 */
	@Nullable
	protected RenderLayer getRenderLayer(T entity, boolean showBody, boolean translucent, boolean showOutline) {
		Identifier identifier = this.getTexture(entity);
		if (translucent) {
			return RenderLayer.getItemEntityTranslucentCull(identifier);
		} else if (showBody) {
			return this.model.getLayer(identifier);
		} else {
			return showOutline ? RenderLayer.getOutline(identifier) : null;
		}
	}

	/**
	 * {@return the packed overlay color for an entity} It is determined by the entity's death progress and whether the entity is flashing.
	 */
	public static int getOverlay(LivingEntity entity, float whiteOverlayProgress) {
		return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
	}

	protected boolean isVisible(T entity) {
		return !entity.isInvisible();
	}

	private static float getYaw(Direction direction) {
		switch(direction) {
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

	/**
	 * {@return if this entity is shaking} Specifically, in the way a zombie villager,
	 * zombie, husk, or piglin undergoing conversion shakes.
	 */
	protected boolean isShaking(T entity) {
		return entity.isFrozen();
	}

	protected void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, float scale) {
		if (this.isShaking(entity)) {
			bodyYaw += (float)(Math.cos((double)entity.age * 3.25) * Math.PI * 0.4F);
		}

		if (!entity.isInPose(EntityPose.SLEEPING)) {
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - bodyYaw));
		}

		if (entity.deathTime > 0) {
			float f = ((float)entity.deathTime + tickDelta - 1.0F) / 20.0F * 1.6F;
			f = MathHelper.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * this.getLyingAngle(entity)));
		} else if (entity.isUsingRiptide()) {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F - entity.getPitch()));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((float)entity.age + tickDelta) * -75.0F));
		} else if (entity.isInPose(EntityPose.SLEEPING)) {
			Direction direction = entity.getSleepingDirection();
			float g = direction != null ? getYaw(direction) : bodyYaw;
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.getLyingAngle(entity)));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0F));
		} else if (shouldFlipUpsideDown(entity)) {
			matrices.translate(0.0F, (entity.getHeight() + 0.1F) / scale, 0.0F);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
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

	protected float getLyingAngle(T entity) {
		return 90.0F;
	}

	protected float getAnimationCounter(T entity, float tickDelta) {
		return 0.0F;
	}

	protected void scale(T entity, MatrixStack matrices, float amount) {
	}

	protected boolean hasLabel(T livingEntity) {
		double d = this.dispatcher.getSquaredDistanceToCamera(livingEntity);
		float f = livingEntity.isSneaky() ? 32.0F : 64.0F;
		if (d >= (double)(f * f)) {
			return false;
		} else {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
			boolean bl = !livingEntity.isInvisibleTo(clientPlayerEntity);
			if (livingEntity != clientPlayerEntity) {
				AbstractTeam abstractTeam = livingEntity.getScoreboardTeam();
				AbstractTeam abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
				if (abstractTeam != null) {
					AbstractTeam.VisibilityRule visibilityRule = abstractTeam.getNameTagVisibilityRule();
					switch(visibilityRule) {
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

			return MinecraftClient.isHudEnabled() && livingEntity != minecraftClient.getCameraEntity() && bl && !livingEntity.hasPassengers();
		}
	}

	public static boolean shouldFlipUpsideDown(LivingEntity entity) {
		if (entity instanceof PlayerEntity || entity.hasCustomName()) {
			String string = Formatting.strip(entity.getName().getString());
			if ("Dinnerbone".equals(string) || "Grumm".equals(string)) {
				return !(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(PlayerModelPart.CAPE);
			}
		}

		return false;
	}

	protected float getShadowRadius(T livingEntity) {
		return super.getShadowRadius(livingEntity) * livingEntity.getScale();
	}
}
