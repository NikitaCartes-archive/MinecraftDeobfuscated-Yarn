package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
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
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Arm;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>>
	extends EntityRenderer<T, S>
	implements FeatureRendererContext<S, M> {
	private static final float field_32939 = 0.1F;
	protected M model;
	protected final ItemRenderer itemRenderer;
	protected final List<FeatureRenderer<S, M>> features = Lists.<FeatureRenderer<S, M>>newArrayList();

	public LivingEntityRenderer(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
		super(ctx);
		this.itemRenderer = ctx.getItemRenderer();
		this.model = model;
		this.shadowRadius = shadowRadius;
	}

	protected final boolean addFeature(FeatureRenderer<S, M> feature) {
		return this.features.add(feature);
	}

	@Override
	public M getModel() {
		return this.model;
	}

	protected Box getBoundingBox(T livingEntity) {
		Box box = super.getBoundingBox(livingEntity);
		if (livingEntity.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.DRAGON_HEAD)) {
			float f = 0.5F;
			return box.expand(0.5, 0.5, 0.5);
		} else {
			return box;
		}
	}

	public void render(S livingEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		if (livingEntityRenderState.isInPose(EntityPose.SLEEPING)) {
			Direction direction = livingEntityRenderState.sleepingDirection;
			if (direction != null) {
				float f = livingEntityRenderState.standingEyeHeight - 0.1F;
				matrixStack.translate((float)(-direction.getOffsetX()) * f, 0.0F, (float)(-direction.getOffsetZ()) * f);
			}
		}

		float g = livingEntityRenderState.baseScale;
		matrixStack.scale(g, g, g);
		this.setupTransforms(livingEntityRenderState, matrixStack, livingEntityRenderState.bodyYaw, g);
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		this.scale(livingEntityRenderState, matrixStack);
		matrixStack.translate(0.0F, -1.501F, 0.0F);
		this.model.setAngles(livingEntityRenderState);
		boolean bl = this.isVisible(livingEntityRenderState);
		boolean bl2 = !bl && !livingEntityRenderState.invisibleToPlayer;
		RenderLayer renderLayer = this.getRenderLayer(livingEntityRenderState, bl, bl2, livingEntityRenderState.hasOutline);
		if (renderLayer != null) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
			int j = getOverlay(livingEntityRenderState, this.getAnimationCounter(livingEntityRenderState));
			int k = bl2 ? 654311423 : Colors.WHITE;
			int l = ColorHelper.mix(k, this.getMixColor(livingEntityRenderState));
			this.model.render(matrixStack, vertexConsumer, i, j, l);
		}

		if (this.shouldRenderFeatures(livingEntityRenderState)) {
			for (FeatureRenderer<S, M> featureRenderer : this.features) {
				featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntityRenderState, livingEntityRenderState.yawDegrees, livingEntityRenderState.pitch);
			}
		}

		matrixStack.pop();
		super.render(livingEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	protected boolean shouldRenderFeatures(S state) {
		return true;
	}

	protected int getMixColor(S state) {
		return -1;
	}

	public abstract Identifier getTexture(S state);

	/**
	 * Gets the render layer appropriate for rendering the passed entity. Returns null if the entity should not be rendered.
	 */
	@Nullable
	protected RenderLayer getRenderLayer(S state, boolean showBody, boolean translucent, boolean showOutline) {
		Identifier identifier = this.getTexture(state);
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
	public static int getOverlay(LivingEntityRenderState state, float whiteOverlayProgress) {
		return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(state.hurt));
	}

	protected boolean isVisible(S state) {
		return !state.invisible;
	}

	private static float getYaw(Direction direction) {
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

	/**
	 * {@return if this entity is shaking} Specifically, in the way a zombie villager,
	 * zombie, husk, or piglin undergoing conversion shakes.
	 */
	protected boolean isShaking(S state) {
		return state.shaking;
	}

	protected void setupTransforms(S state, MatrixStack matrices, float animationProgress, float bodyYaw) {
		if (this.isShaking(state)) {
			animationProgress += (float)(Math.cos((double)((float)MathHelper.floor(state.age) * 3.25F)) * Math.PI * 0.4F);
		}

		if (!state.isInPose(EntityPose.SLEEPING)) {
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - animationProgress));
		}

		if (state.deathTime > 0.0F) {
			float f = (state.deathTime - 1.0F) / 20.0F * 1.6F;
			f = MathHelper.sqrt(f);
			if (f > 1.0F) {
				f = 1.0F;
			}

			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * this.method_3919()));
		} else if (state.usingRiptide) {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F - state.pitch));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(state.age * -75.0F));
		} else if (state.isInPose(EntityPose.SLEEPING)) {
			Direction direction = state.sleepingDirection;
			float g = direction != null ? getYaw(direction) : animationProgress;
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.method_3919()));
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0F));
		} else if (state.flipUpsideDown) {
			matrices.translate(0.0F, (state.height + 0.1F) / bodyYaw, 0.0F);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
		}
	}

	protected float method_3919() {
		return 90.0F;
	}

	protected float getAnimationCounter(S state) {
		return 0.0F;
	}

	protected void scale(S state, MatrixStack matrices) {
	}

	protected boolean hasLabel(T livingEntity, double d) {
		if (livingEntity.isSneaky()) {
			float f = 32.0F;
			if (d >= 1024.0) {
				return false;
			}
		}

		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
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

		return MinecraftClient.isHudEnabled() && livingEntity != minecraftClient.getCameraEntity() && bl && !livingEntity.hasPassengers();
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

	protected float getShadowRadius(S livingEntityRenderState) {
		return super.getShadowRadius(livingEntityRenderState) * livingEntityRenderState.baseScale;
	}

	public void updateRenderState(T livingEntity, S livingEntityRenderState, float f) {
		super.updateRenderState(livingEntity, livingEntityRenderState, f);
		float g = MathHelper.lerpAngleDegrees(f, livingEntity.prevHeadYaw, livingEntity.headYaw);
		livingEntityRenderState.bodyYaw = clampBodyYaw(livingEntity, g, f);
		livingEntityRenderState.yawDegrees = MathHelper.wrapDegrees(g - livingEntityRenderState.bodyYaw);
		livingEntityRenderState.pitch = livingEntity.getLerpedPitch(f);
		livingEntityRenderState.customName = livingEntity.getCustomName();
		livingEntityRenderState.flipUpsideDown = shouldFlipUpsideDown(livingEntity);
		if (livingEntityRenderState.flipUpsideDown) {
			livingEntityRenderState.pitch *= -1.0F;
			livingEntityRenderState.yawDegrees *= -1.0F;
		}

		livingEntityRenderState.limbFrequency = livingEntity.limbAnimator.getPos(f);
		livingEntityRenderState.limbAmplitudeMultiplier = livingEntity.limbAnimator.getSpeed(f);
		if (livingEntity.getVehicle() instanceof LivingEntity livingEntity2) {
			livingEntityRenderState.headItemAnimationProgress = livingEntity2.limbAnimator.getPos(f);
		} else {
			livingEntityRenderState.headItemAnimationProgress = livingEntityRenderState.limbFrequency;
		}

		livingEntityRenderState.baseScale = livingEntity.getScale();
		livingEntityRenderState.ageScale = livingEntity.getScaleFactor();
		livingEntityRenderState.pose = livingEntity.getPose();
		livingEntityRenderState.sleepingDirection = livingEntity.getSleepingDirection();
		if (livingEntityRenderState.sleepingDirection != null) {
			livingEntityRenderState.standingEyeHeight = livingEntity.getEyeHeight(EntityPose.STANDING);
		}

		livingEntityRenderState.shaking = livingEntity.isFrozen();
		livingEntityRenderState.baby = livingEntity.isBaby();
		livingEntityRenderState.touchingWater = livingEntity.isTouchingWater();
		livingEntityRenderState.usingRiptide = livingEntity.isUsingRiptide();
		livingEntityRenderState.hurt = livingEntity.hurtTime > 0 || livingEntity.deathTime > 0;
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
		livingEntityRenderState.equippedHeadStack = itemStack.copy();
		livingEntityRenderState.equippedHeadItemModel = this.itemRenderer.getModel(itemStack, livingEntity, ModelTransformationMode.HEAD);
		livingEntityRenderState.mainArm = livingEntity.getMainArm();
		ItemStack itemStack2 = livingEntity.getStackInArm(Arm.RIGHT);
		ItemStack itemStack3 = livingEntity.getStackInArm(Arm.LEFT);
		livingEntityRenderState.rightHandStack = itemStack2.copy();
		livingEntityRenderState.leftHandStack = itemStack3.copy();
		livingEntityRenderState.rightHandItemModel = this.itemRenderer.getModel(itemStack2, livingEntity, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND);
		livingEntityRenderState.leftHandItemModel = this.itemRenderer.getModel(itemStack3, livingEntity, ModelTransformationMode.THIRD_PERSON_LEFT_HAND);
		livingEntityRenderState.deathTime = livingEntity.deathTime > 0 ? (float)livingEntity.deathTime + f : 0.0F;
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		livingEntityRenderState.invisibleToPlayer = livingEntityRenderState.invisible && livingEntity.isInvisibleTo(minecraftClient.player);
		livingEntityRenderState.hasOutline = minecraftClient.hasOutline(livingEntity);
	}

	private static float clampBodyYaw(LivingEntity entity, float degrees, float tickDelta) {
		if (entity.getVehicle() instanceof LivingEntity livingEntity) {
			float f = MathHelper.lerpAngleDegrees(tickDelta, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
			float g = 85.0F;
			float h = MathHelper.clamp(MathHelper.wrapDegrees(degrees - f), -85.0F, 85.0F);
			f = degrees - h;
			if (Math.abs(h) > 50.0F) {
				f += h * 0.2F;
			}

			return f;
		} else {
			return MathHelper.lerpAngleDegrees(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
		}
	}
}
