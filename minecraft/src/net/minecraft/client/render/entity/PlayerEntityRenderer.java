package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.number.StyledNumberFormat;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class PlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityRenderState, PlayerEntityModel> {
	public PlayerEntityRenderer(EntityRendererFactory.Context ctx, boolean slim) {
		super(ctx, new PlayerEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim), 0.5F);
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this,
				new ArmorEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)),
				new ArmorEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR)),
				ctx.getModelManager()
			)
		);
		this.addFeature(new PlayerHeldItemFeatureRenderer<>(this, ctx.getItemRenderer()));
		this.addFeature(new StuckArrowsFeatureRenderer<>(this, ctx));
		this.addFeature(new Deadmau5FeatureRenderer(this, ctx.getModelLoader()));
		this.addFeature(new CapeFeatureRenderer(this, ctx.getModelLoader()));
		this.addFeature(new HeadFeatureRenderer<>(this, ctx.getModelLoader(), ctx.getItemRenderer()));
		this.addFeature(new ElytraFeatureRenderer<>(this, ctx.getModelLoader()));
		this.addFeature(new ShoulderParrotFeatureRenderer(this, ctx.getModelLoader()));
		this.addFeature(new TridentRiptideFeatureRenderer(this, ctx.getModelLoader()));
		this.addFeature(new StuckStingersFeatureRenderer<>(this, ctx));
	}

	protected boolean shouldRenderFeatures(PlayerEntityRenderState playerEntityRenderState) {
		return !playerEntityRenderState.spectator;
	}

	public Vec3d getPositionOffset(PlayerEntityRenderState playerEntityRenderState) {
		Vec3d vec3d = super.getPositionOffset(playerEntityRenderState);
		return playerEntityRenderState.isInSneakingPose ? vec3d.add(0.0, (double)(playerEntityRenderState.baseScale * -2.0F) / 16.0, 0.0) : vec3d;
	}

	public static BipedEntityModel.ArmPose getArmPose(PlayerEntityRenderState state, PlayerEntityRenderState.HandState handState, Hand hand) {
		if (handState.empty) {
			return BipedEntityModel.ArmPose.EMPTY;
		} else {
			if (state.activeHand == hand && state.itemUseTimeLeft > 0) {
				UseAction useAction = handState.itemUseAction;
				if (useAction == UseAction.BLOCK) {
					return BipedEntityModel.ArmPose.BLOCK;
				}

				if (useAction == UseAction.BOW) {
					return BipedEntityModel.ArmPose.BOW_AND_ARROW;
				}

				if (useAction == UseAction.SPEAR) {
					return BipedEntityModel.ArmPose.THROW_SPEAR;
				}

				if (useAction == UseAction.CROSSBOW) {
					return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
				}

				if (useAction == UseAction.SPYGLASS) {
					return BipedEntityModel.ArmPose.SPYGLASS;
				}

				if (useAction == UseAction.TOOT_HORN) {
					return BipedEntityModel.ArmPose.TOOT_HORN;
				}

				if (useAction == UseAction.BRUSH) {
					return BipedEntityModel.ArmPose.BRUSH;
				}
			} else if (!state.handSwinging && handState.hasChargedCrossbow) {
				return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
			}

			return BipedEntityModel.ArmPose.ITEM;
		}
	}

	public Identifier getTexture(PlayerEntityRenderState playerEntityRenderState) {
		return playerEntityRenderState.skinTextures.texture();
	}

	protected void scale(PlayerEntityRenderState playerEntityRenderState, MatrixStack matrixStack) {
		float f = 0.9375F;
		matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
	}

	protected void renderLabelIfPresent(
		PlayerEntityRenderState playerEntityRenderState, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		if (playerEntityRenderState.playerName != null) {
			matrixStack.push();
			super.renderLabelIfPresent(playerEntityRenderState, playerEntityRenderState.playerName, matrixStack, vertexConsumerProvider, i);
			matrixStack.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
			matrixStack.pop();
		}

		super.renderLabelIfPresent(playerEntityRenderState, text, matrixStack, vertexConsumerProvider, i);
	}

	public PlayerEntityRenderState getRenderState() {
		return new PlayerEntityRenderState();
	}

	public void updateRenderState(AbstractClientPlayerEntity abstractClientPlayerEntity, PlayerEntityRenderState playerEntityRenderState, float f) {
		super.updateRenderState(abstractClientPlayerEntity, playerEntityRenderState, f);
		BipedEntityRenderer.updateBipedRenderState(abstractClientPlayerEntity, playerEntityRenderState, f);
		playerEntityRenderState.skinTextures = abstractClientPlayerEntity.getSkinTextures();
		playerEntityRenderState.stuckArrowCount = abstractClientPlayerEntity.getStuckArrowCount();
		playerEntityRenderState.stingerCount = abstractClientPlayerEntity.getStingerCount();
		playerEntityRenderState.itemUseTimeLeft = abstractClientPlayerEntity.getItemUseTimeLeft();
		playerEntityRenderState.handSwinging = abstractClientPlayerEntity.handSwinging;
		playerEntityRenderState.inSwimmingPose = abstractClientPlayerEntity.isInSwimmingPose();
		playerEntityRenderState.spectator = abstractClientPlayerEntity.isSpectator();
		playerEntityRenderState.hatVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.HAT);
		playerEntityRenderState.jacketVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.JACKET);
		playerEntityRenderState.leftPantsLegVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
		playerEntityRenderState.rightPantsLegVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
		playerEntityRenderState.leftSleeveVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
		playerEntityRenderState.rightSleeveVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
		playerEntityRenderState.capeVisible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE);
		updateFallFlying(abstractClientPlayerEntity, playerEntityRenderState, f);
		this.updateHandState(abstractClientPlayerEntity, playerEntityRenderState.mainHandState, Hand.MAIN_HAND);
		this.updateHandState(abstractClientPlayerEntity, playerEntityRenderState.offHandState, Hand.OFF_HAND);
		updateCape(abstractClientPlayerEntity, playerEntityRenderState, f);
		if (playerEntityRenderState.squaredDistanceToCamera < 100.0) {
			Scoreboard scoreboard = abstractClientPlayerEntity.getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);
			if (scoreboardObjective != null) {
				ReadableScoreboardScore readableScoreboardScore = scoreboard.getScore(abstractClientPlayerEntity, scoreboardObjective);
				Text text = ReadableScoreboardScore.getFormattedScore(readableScoreboardScore, scoreboardObjective.getNumberFormatOr(StyledNumberFormat.EMPTY));
				playerEntityRenderState.playerName = Text.empty().append(text).append(ScreenTexts.SPACE).append(scoreboardObjective.getDisplayName());
			} else {
				playerEntityRenderState.playerName = null;
			}
		} else {
			playerEntityRenderState.playerName = null;
		}

		playerEntityRenderState.leftShoulderParrotVariant = getShoulderParrotVariant(abstractClientPlayerEntity, true);
		playerEntityRenderState.rightShoulderParrotVariant = getShoulderParrotVariant(abstractClientPlayerEntity, false);
		playerEntityRenderState.id = abstractClientPlayerEntity.getId();
		playerEntityRenderState.name = abstractClientPlayerEntity.getGameProfile().getName();
	}

	private static void updateFallFlying(AbstractClientPlayerEntity player, PlayerEntityRenderState state, float tickDelta) {
		state.fallFlyingTicks = (float)player.getFallFlyingTicks() + tickDelta;
		Vec3d vec3d = player.getRotationVec(tickDelta);
		Vec3d vec3d2 = player.lerpVelocity(tickDelta);
		double d = vec3d2.horizontalLengthSquared();
		double e = vec3d.horizontalLengthSquared();
		if (d > 0.0 && e > 0.0) {
			state.applyFlyingRotation = true;
			double f = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
			double g = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
			state.flyingRotation = (float)(Math.signum(g) * Math.acos(f));
		} else {
			state.applyFlyingRotation = false;
			state.flyingRotation = 0.0F;
		}
	}

	private void updateHandState(AbstractClientPlayerEntity player, PlayerEntityRenderState.HandState handState, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		handState.empty = itemStack.isEmpty();
		handState.itemUseAction = !itemStack.isEmpty() ? itemStack.getUseAction() : null;
		handState.hasChargedCrossbow = itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack);
	}

	private static void updateCape(AbstractClientPlayerEntity player, PlayerEntityRenderState state, float tickDelta) {
		double d = MathHelper.lerp((double)tickDelta, player.prevCapeX, player.capeX) - MathHelper.lerp((double)tickDelta, player.prevX, player.getX());
		double e = MathHelper.lerp((double)tickDelta, player.prevCapeY, player.capeY) - MathHelper.lerp((double)tickDelta, player.prevY, player.getY());
		double f = MathHelper.lerp((double)tickDelta, player.prevCapeZ, player.capeZ) - MathHelper.lerp((double)tickDelta, player.prevZ, player.getZ());
		float g = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
		double h = (double)MathHelper.sin(g * (float) (Math.PI / 180.0));
		double i = (double)(-MathHelper.cos(g * (float) (Math.PI / 180.0)));
		state.field_53536 = (float)e * 10.0F;
		state.field_53536 = MathHelper.clamp(state.field_53536, -6.0F, 32.0F);
		state.field_53537 = (float)(d * h + f * i) * 100.0F;
		state.field_53537 = MathHelper.clamp(state.field_53537, 0.0F, 150.0F);
		state.field_53538 = (float)(d * i - f * h) * 100.0F;
		state.field_53538 = MathHelper.clamp(state.field_53538, -20.0F, 20.0F);
		float j = MathHelper.lerp(tickDelta, player.prevStrideDistance, player.strideDistance);
		float k = MathHelper.lerp(tickDelta, player.field_53038, player.field_53039);
		state.field_53536 = state.field_53536 + MathHelper.sin(k * 6.0F) * 32.0F * j;
	}

	@Nullable
	private static ParrotEntity.Variant getShoulderParrotVariant(AbstractClientPlayerEntity player, boolean left) {
		NbtCompound nbtCompound = left ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
		return EntityType.get(nbtCompound.getString("id")).filter(type -> type == EntityType.PARROT).isPresent()
			? ParrotEntity.Variant.byIndex(nbtCompound.getInt("Variant"))
			: null;
	}

	public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Identifier skinTexture) {
		this.renderArm(matrices, vertexConsumers, light, skinTexture, this.model.rightArm);
	}

	public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Identifier skinTexture) {
		this.renderArm(matrices, vertexConsumers, light, skinTexture, this.model.leftArm);
	}

	private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Identifier skinTexture, ModelPart arm) {
		PlayerEntityModel playerEntityModel = this.getModel();
		playerEntityModel.leftArm.resetTransform();
		playerEntityModel.rightArm.resetTransform();
		playerEntityModel.leftArm.roll = -0.1F;
		playerEntityModel.rightArm.roll = 0.1F;
		arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(skinTexture)), light, OverlayTexture.DEFAULT_UV);
	}

	protected void setupTransforms(PlayerEntityRenderState playerEntityRenderState, MatrixStack matrixStack, float f, float g) {
		float h = playerEntityRenderState.leaningPitch;
		float i = playerEntityRenderState.pitch;
		if (playerEntityRenderState.isFallFlying) {
			super.setupTransforms(playerEntityRenderState, matrixStack, f, g);
			float j = MathHelper.clamp(playerEntityRenderState.fallFlyingTicks * playerEntityRenderState.fallFlyingTicks / 100.0F, 0.0F, 1.0F);
			if (!playerEntityRenderState.usingRiptide) {
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j * (-90.0F - i)));
			}

			if (playerEntityRenderState.applyFlyingRotation) {
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(playerEntityRenderState.flyingRotation));
			}
		} else if (h > 0.0F) {
			super.setupTransforms(playerEntityRenderState, matrixStack, f, g);
			float jx = playerEntityRenderState.touchingWater ? -90.0F - i : -90.0F;
			float k = MathHelper.lerp(h, 0.0F, jx);
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(k));
			if (playerEntityRenderState.inSwimmingPose) {
				matrixStack.translate(0.0F, -1.0F, 0.3F);
			}
		} else {
			super.setupTransforms(playerEntityRenderState, matrixStack, f, g);
		}
	}
}
