package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_8293;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.BeretFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.MustacheFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.feature.TailFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class PlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public PlayerEntityRenderer(EntityRendererFactory.Context ctx, boolean slim) {
		super(ctx, new PlayerEntityModel<>(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), slim), 0.5F);
		this.addFeature(
			new ArmorFeatureRenderer<>(
				this,
				new PlayerEntityRenderer.PlayerArmorEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)),
				new PlayerEntityRenderer.PlayerArmorEntityModel(ctx.getPart(slim ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR)),
				ctx.getModelManager()
			)
		);
		this.addFeature(new PlayerHeldItemFeatureRenderer<>(this, ctx.getHeldItemRenderer()));
		this.addFeature(new StuckArrowsFeatureRenderer<>(ctx, this));
		this.addFeature(new Deadmau5FeatureRenderer(this));
		this.addFeature(new CapeFeatureRenderer(this));
		this.addFeature(new HeadFeatureRenderer<>(this, ctx.getModelLoader(), ctx.getHeldItemRenderer()));
		this.addFeature(new ElytraFeatureRenderer<>(this, ctx.getModelLoader()));
		this.addFeature(new ShoulderParrotFeatureRenderer<>(this, ctx.getModelLoader()));
		this.addFeature(new TridentRiptideFeatureRenderer<>(this, ctx.getModelLoader()));
		this.addFeature(new StuckStingersFeatureRenderer<>(this));
		this.addFeature(new BeretFeatureRenderer<>(this, ctx.getModelLoader()));
		this.addFeature(new MustacheFeatureRenderer<>(this, ctx.getModelLoader()));
		this.addFeature(new TailFeatureRenderer<>(this, ctx.getModelLoader()));
	}

	public void render(
		AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		this.setModelPose(abstractClientPlayerEntity);
		super.render(abstractClientPlayerEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	public Vec3d getPositionOffset(AbstractClientPlayerEntity abstractClientPlayerEntity, float f) {
		return abstractClientPlayerEntity.isInSneakingPose() ? new Vec3d(0.0, -0.125, 0.0) : super.getPositionOffset(abstractClientPlayerEntity, f);
	}

	private void setModelPose(AbstractClientPlayerEntity player) {
		PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
		playerEntityModel.setupPoseAndVisibility(player);
		if (!player.isSpectator()) {
			playerEntityModel.setVisible(true);
			playerEntityModel.hat.visible = player.isPartVisible(PlayerModelPart.HAT);
			playerEntityModel.jacket.visible = player.isPartVisible(PlayerModelPart.JACKET);
			playerEntityModel.leftPants.visible = player.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
			playerEntityModel.rightPants.visible = player.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
			playerEntityModel.leftSleeve.visible = player.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
			playerEntityModel.rightSleeve.visible = player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
		}
	}

	public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		return abstractClientPlayerEntity.getSkinTexture();
	}

	protected void scale(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f) {
		float g = 0.9375F;
		matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
	}

	protected void renderLabelIfPresent(
		AbstractClientPlayerEntity abstractClientPlayerEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		double d = this.dispatcher.getSquaredDistanceToCamera(abstractClientPlayerEntity);
		if (class_8293.field_43557.method_50415(abstractClientPlayerEntity.getUuid())) {
			text = Text.translatable("rule.president.tag", abstractClientPlayerEntity.getDisplayName());
		}

		matrixStack.push();
		if (d < 100.0) {
			Scoreboard scoreboard = abstractClientPlayerEntity.getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(2);
			if (scoreboardObjective != null) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(abstractClientPlayerEntity.getEntityName(), scoreboardObjective);
				super.renderLabelIfPresent(
					abstractClientPlayerEntity,
					Text.literal(Integer.toString(scoreboardPlayerScore.getScore())).append(ScreenTexts.SPACE).append(scoreboardObjective.getDisplayName()),
					matrixStack,
					vertexConsumerProvider,
					i
				);
				matrixStack.translate(0.0F, 9.0F * 1.15F * 0.025F, 0.0F);
			}
		}

		super.renderLabelIfPresent(abstractClientPlayerEntity, text, matrixStack, vertexConsumerProvider, i);
		matrixStack.pop();
	}

	public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player) {
		this.renderArm(matrices, vertexConsumers, light, player, this.model.rightArm, this.model.rightSleeve);
	}

	public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player) {
		this.renderArm(matrices, vertexConsumers, light, player, this.model.leftArm, this.model.leftSleeve);
	}

	private void renderArm(
		MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve
	) {
		PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
		this.setModelPose(player);
		playerEntityModel.handSwingProgress = 0.0F;
		playerEntityModel.sneaking = false;
		playerEntityModel.leaningPitch = 0.0F;
		playerEntityModel.setAngles(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		arm.pitch = 0.0F;
		arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
		sleeve.pitch = 0.0F;
		sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
	}

	protected void setupTransforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h) {
		float i = abstractClientPlayerEntity.getLeaningPitch(h);
		if (abstractClientPlayerEntity.isFallFlying()) {
			super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
			float j = (float)abstractClientPlayerEntity.getRoll() + h;
			float k = MathHelper.clamp(j * j / 100.0F, 0.0F, 1.0F);
			if (!abstractClientPlayerEntity.isUsingRiptide()) {
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(k * (-90.0F - abstractClientPlayerEntity.getPitch())));
			}

			Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
			Vec3d vec3d2 = abstractClientPlayerEntity.lerpVelocity(h);
			double d = vec3d2.horizontalLengthSquared();
			double e = vec3d.horizontalLengthSquared();
			if (d > 0.0 && e > 0.0) {
				double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
				double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation((float)(Math.signum(m) * Math.acos(l))));
			}
		} else if (i > 0.0F) {
			super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
			float jx = abstractClientPlayerEntity.isTouchingWater() ? -90.0F - abstractClientPlayerEntity.getPitch() : -90.0F;
			float kx = MathHelper.lerp(i, 0.0F, jx);
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(kx));
			if (abstractClientPlayerEntity.isInSwimmingPose()) {
				matrixStack.translate(0.0F, -1.0F, 0.3F);
			}
		} else {
			super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
		}
	}

	@Nullable
	protected VertexConsumer method_51047(
		AbstractClientPlayerEntity abstractClientPlayerEntity,
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		boolean bl,
		boolean bl2,
		boolean bl3
	) {
		if (class_8293.field_43524.method_50116()) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getGoldEntityGlint());
			VertexConsumer vertexConsumer2 = super.method_51047(abstractClientPlayerEntity, matrixStack, vertexConsumerProvider, bl, bl2, bl3);
			return vertexConsumer2 == null ? null : VertexConsumers.union(vertexConsumer, vertexConsumer2);
		} else {
			return super.method_51047(abstractClientPlayerEntity, matrixStack, vertexConsumerProvider, bl, bl2, bl3);
		}
	}

	@Environment(EnvType.CLIENT)
	static class PlayerArmorEntityModel<T extends LivingEntity> extends ArmorEntityModel<T> {
		public PlayerArmorEntityModel(ModelPart modelPart) {
			super(modelPart);
		}

		@Override
		public boolean shouldBeMini() {
			return class_8293.field_43574.method_50116();
		}
	}
}
