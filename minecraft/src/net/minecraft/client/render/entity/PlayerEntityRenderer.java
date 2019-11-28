package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class PlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this(entityRenderDispatcher, false);
	}

	public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, boolean bl) {
		super(entityRenderDispatcher, new PlayerEntityModel<>(0.0F, bl), 0.5F);
		this.addFeature(new ArmorBipedFeatureRenderer<>(this, new BipedEntityModel(0.5F), new BipedEntityModel(1.0F)));
		this.addFeature(new HeldItemFeatureRenderer<>(this));
		this.addFeature(new StuckArrowsFeatureRenderer<>(this));
		this.addFeature(new Deadmau5FeatureRenderer(this));
		this.addFeature(new CapeFeatureRenderer(this));
		this.addFeature(new HeadFeatureRenderer<>(this));
		this.addFeature(new ElytraFeatureRenderer<>(this));
		this.addFeature(new ShoulderParrotFeatureRenderer<>(this));
		this.addFeature(new TridentRiptideFeatureRenderer<>(this));
		this.addFeature(new StuckStingersFeatureRenderer<>(this));
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

	private void setModelPose(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
		if (abstractClientPlayerEntity.isSpectator()) {
			playerEntityModel.setVisible(false);
			playerEntityModel.head.visible = true;
			playerEntityModel.helmet.visible = true;
		} else {
			ItemStack itemStack = abstractClientPlayerEntity.getMainHandStack();
			ItemStack itemStack2 = abstractClientPlayerEntity.getOffHandStack();
			playerEntityModel.setVisible(true);
			playerEntityModel.helmet.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.HAT);
			playerEntityModel.jacket.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.JACKET);
			playerEntityModel.leftPantLeg.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
			playerEntityModel.rightPantLeg.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
			playerEntityModel.leftSleeve.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
			playerEntityModel.rightSleeve.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
			playerEntityModel.isSneaking = abstractClientPlayerEntity.isInSneakingPose();
			BipedEntityModel.ArmPose armPose = this.getArmPose(abstractClientPlayerEntity, itemStack, itemStack2, Hand.MAIN_HAND);
			BipedEntityModel.ArmPose armPose2 = this.getArmPose(abstractClientPlayerEntity, itemStack, itemStack2, Hand.OFF_HAND);
			if (abstractClientPlayerEntity.getMainArm() == Arm.RIGHT) {
				playerEntityModel.rightArmPose = armPose;
				playerEntityModel.leftArmPose = armPose2;
			} else {
				playerEntityModel.rightArmPose = armPose2;
				playerEntityModel.leftArmPose = armPose;
			}
		}
	}

	private BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity abstractClientPlayerEntity, ItemStack itemStack, ItemStack itemStack2, Hand hand) {
		BipedEntityModel.ArmPose armPose = BipedEntityModel.ArmPose.EMPTY;
		ItemStack itemStack3 = hand == Hand.MAIN_HAND ? itemStack : itemStack2;
		if (!itemStack3.isEmpty()) {
			armPose = BipedEntityModel.ArmPose.ITEM;
			if (abstractClientPlayerEntity.getItemUseTimeLeft() > 0) {
				UseAction useAction = itemStack3.getUseAction();
				if (useAction == UseAction.BLOCK) {
					armPose = BipedEntityModel.ArmPose.BLOCK;
				} else if (useAction == UseAction.BOW) {
					armPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
				} else if (useAction == UseAction.SPEAR) {
					armPose = BipedEntityModel.ArmPose.THROW_SPEAR;
				} else if (useAction == UseAction.CROSSBOW && hand == abstractClientPlayerEntity.getActiveHand()) {
					armPose = BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
				}
			} else {
				boolean bl = itemStack.getItem() == Items.CROSSBOW;
				boolean bl2 = CrossbowItem.isCharged(itemStack);
				boolean bl3 = itemStack2.getItem() == Items.CROSSBOW;
				boolean bl4 = CrossbowItem.isCharged(itemStack2);
				if (bl && bl2) {
					armPose = BipedEntityModel.ArmPose.CROSSBOW_HOLD;
				}

				if (bl3 && bl4 && itemStack.getItem().getUseAction(itemStack) == UseAction.NONE) {
					armPose = BipedEntityModel.ArmPose.CROSSBOW_HOLD;
				}
			}
		}

		return armPose;
	}

	public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		return abstractClientPlayerEntity.getSkinTexture();
	}

	protected void scale(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f) {
		float g = 0.9375F;
		matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
	}

	protected void renderLabelIfPresent(
		AbstractClientPlayerEntity abstractClientPlayerEntity, String string, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i
	) {
		double d = this.renderManager.getSquaredDistanceToCamera(abstractClientPlayerEntity);
		matrixStack.push();
		if (d < 100.0) {
			Scoreboard scoreboard = abstractClientPlayerEntity.getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(2);
			if (scoreboardObjective != null) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(abstractClientPlayerEntity.getEntityName(), scoreboardObjective);
				super.renderLabelIfPresent(
					abstractClientPlayerEntity,
					scoreboardPlayerScore.getScore() + " " + scoreboardObjective.getDisplayName().asFormattedString(),
					matrixStack,
					vertexConsumerProvider,
					i
				);
				matrixStack.translate(0.0, (double)(9.0F * 1.15F * 0.025F), 0.0);
			}
		}

		super.renderLabelIfPresent(abstractClientPlayerEntity, string, matrixStack, vertexConsumerProvider, i);
		matrixStack.pop();
	}

	public void renderRightArm(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity
	) {
		this.method_23205(matrixStack, vertexConsumerProvider, i, abstractClientPlayerEntity, this.model.rightArm, this.model.rightSleeve);
	}

	public void renderLeftArm(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity) {
		this.method_23205(matrixStack, vertexConsumerProvider, i, abstractClientPlayerEntity, this.model.leftArm, this.model.leftSleeve);
	}

	private void method_23205(
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		AbstractClientPlayerEntity abstractClientPlayerEntity,
		ModelPart modelPart,
		ModelPart modelPart2
	) {
		PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
		this.setModelPose(abstractClientPlayerEntity);
		playerEntityModel.handSwingProgress = 0.0F;
		playerEntityModel.isSneaking = false;
		playerEntityModel.field_3396 = 0.0F;
		playerEntityModel.setAngles(abstractClientPlayerEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		modelPart.pitch = 0.0F;
		modelPart.render(
			matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(abstractClientPlayerEntity.getSkinTexture())), i, OverlayTexture.DEFAULT_UV
		);
		modelPart2.pitch = 0.0F;
		modelPart2.render(
			matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(abstractClientPlayerEntity.getSkinTexture())), i, OverlayTexture.DEFAULT_UV
		);
	}

	protected void setupTransforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h) {
		float i = abstractClientPlayerEntity.getLeaningPitch(h);
		if (abstractClientPlayerEntity.isFallFlying()) {
			super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
			float j = (float)abstractClientPlayerEntity.getRoll() + h;
			float k = MathHelper.clamp(j * j / 100.0F, 0.0F, 1.0F);
			if (!abstractClientPlayerEntity.isUsingRiptide()) {
				matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(k * (-90.0F - abstractClientPlayerEntity.pitch)));
			}

			Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
			Vec3d vec3d2 = abstractClientPlayerEntity.getVelocity();
			double d = Entity.squaredHorizontalLength(vec3d2);
			double e = Entity.squaredHorizontalLength(vec3d);
			if (d > 0.0 && e > 0.0) {
				double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / (Math.sqrt(d) * Math.sqrt(e));
				double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float)(Math.signum(m) * Math.acos(l))));
			}
		} else if (i > 0.0F) {
			super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
			float jx = abstractClientPlayerEntity.isInsideWater() ? -90.0F - abstractClientPlayerEntity.pitch : -90.0F;
			float kx = MathHelper.lerp(i, 0.0F, jx);
			matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(kx));
			if (abstractClientPlayerEntity.isInSwimmingPose()) {
				matrixStack.translate(0.0, -1.0, 0.3F);
			}
		} else {
			super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
		}
	}
}
