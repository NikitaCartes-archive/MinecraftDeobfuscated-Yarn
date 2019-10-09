package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StingerFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
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
import net.minecraft.util.math.MatrixStack;
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
		this.addFeature(new StingerFeatureRenderer<>(this));
	}

	public void method_4215(
		AbstractClientPlayerEntity abstractClientPlayerEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		this.setModelPose(abstractClientPlayerEntity);
		super.method_4054(abstractClientPlayerEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	public Vec3d method_23206(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, float g) {
		return abstractClientPlayerEntity.isInSneakingPose() ? new Vec3d(0.0, -0.125, 0.0) : super.getPositionOffset(abstractClientPlayerEntity, d, e, f, g);
	}

	private void setModelPose(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
		if (abstractClientPlayerEntity.isSpectator()) {
			playerEntityModel.setVisible(false);
			playerEntityModel.head.visible = true;
			playerEntityModel.headwear.visible = true;
		} else {
			ItemStack itemStack = abstractClientPlayerEntity.getMainHandStack();
			ItemStack itemStack2 = abstractClientPlayerEntity.getOffHandStack();
			playerEntityModel.setVisible(true);
			playerEntityModel.headwear.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.HAT);
			playerEntityModel.bodyOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.JACKET);
			playerEntityModel.leftLegOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.LEFT_PANTS_LEG);
			playerEntityModel.rightLegOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.RIGHT_PANTS_LEG);
			playerEntityModel.leftArmOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.LEFT_SLEEVE);
			playerEntityModel.rightArmOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.RIGHT_SLEEVE);
			playerEntityModel.isSneaking = abstractClientPlayerEntity.isInSneakingPose();
			BipedEntityModel.ArmPose armPose = this.method_4210(abstractClientPlayerEntity, itemStack, itemStack2, Hand.MAIN_HAND);
			BipedEntityModel.ArmPose armPose2 = this.method_4210(abstractClientPlayerEntity, itemStack, itemStack2, Hand.OFF_HAND);
			if (abstractClientPlayerEntity.getMainArm() == Arm.RIGHT) {
				playerEntityModel.rightArmPose = armPose;
				playerEntityModel.leftArmPose = armPose2;
			} else {
				playerEntityModel.rightArmPose = armPose2;
				playerEntityModel.leftArmPose = armPose;
			}
		}
	}

	private BipedEntityModel.ArmPose method_4210(AbstractClientPlayerEntity abstractClientPlayerEntity, ItemStack itemStack, ItemStack itemStack2, Hand hand) {
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

	public Identifier method_4216(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		return abstractClientPlayerEntity.getSkinTexture();
	}

	protected void method_4217(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f) {
		float g = 0.9375F;
		matrixStack.scale(0.9375F, 0.9375F, 0.9375F);
	}

	protected void method_4213(
		AbstractClientPlayerEntity abstractClientPlayerEntity, String string, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		double d = this.renderManager.method_23168(abstractClientPlayerEntity);
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
					layeredVertexConsumerStorage
				);
				matrixStack.translate(0.0, (double)(9.0F * 1.15F * 0.025F), 0.0);
			}
		}

		super.renderLabelIfPresent(abstractClientPlayerEntity, string, matrixStack, layeredVertexConsumerStorage);
		matrixStack.pop();
	}

	public void renderRightArm(
		MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, AbstractClientPlayerEntity abstractClientPlayerEntity
	) {
		this.method_23205(matrixStack, layeredVertexConsumerStorage, abstractClientPlayerEntity, this.model.rightArm, this.model.rightArmOverlay);
	}

	public void renderLeftArm(
		MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, AbstractClientPlayerEntity abstractClientPlayerEntity
	) {
		this.method_23205(matrixStack, layeredVertexConsumerStorage, abstractClientPlayerEntity, this.model.leftArm, this.model.leftArmOverlay);
	}

	private void method_23205(
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		AbstractClientPlayerEntity abstractClientPlayerEntity,
		ModelPart modelPart,
		ModelPart modelPart2
	) {
		float f = 0.0625F;
		PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
		this.setModelPose(abstractClientPlayerEntity);
		int i = abstractClientPlayerEntity.getLightmapCoordinates();
		playerEntityModel.handSwingProgress = 0.0F;
		playerEntityModel.isSneaking = false;
		playerEntityModel.field_3396 = 0.0F;
		playerEntityModel.method_17087(abstractClientPlayerEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		modelPart.pitch = 0.0F;
		modelPart.render(
			matrixStack,
			layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntitySolid(abstractClientPlayerEntity.getSkinTexture())),
			0.0625F,
			i,
			OverlayTexture.field_21444,
			null
		);
		modelPart2.pitch = 0.0F;
		modelPart2.render(
			matrixStack,
			layeredVertexConsumerStorage.getBuffer(RenderLayer.getEntityTranslucent(abstractClientPlayerEntity.getSkinTexture())),
			0.0625F,
			i,
			OverlayTexture.field_21444,
			null
		);
	}

	protected void method_4212(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h) {
		float i = abstractClientPlayerEntity.getLeaningPitch(h);
		if (abstractClientPlayerEntity.isFallFlying()) {
			super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
			float j = (float)abstractClientPlayerEntity.getRoll() + h;
			float k = MathHelper.clamp(j * j / 100.0F, 0.0F, 1.0F);
			if (!abstractClientPlayerEntity.isUsingRiptide()) {
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(k * (-90.0F - abstractClientPlayerEntity.pitch)));
			}

			Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
			Vec3d vec3d2 = abstractClientPlayerEntity.getVelocity();
			double d = Entity.squaredHorizontalLength(vec3d2);
			double e = Entity.squaredHorizontalLength(vec3d);
			if (d > 0.0 && e > 0.0) {
				double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / (Math.sqrt(d) * Math.sqrt(e));
				double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
				matrixStack.multiply(Vector3f.POSITIVE_Y.method_23626((float)(Math.signum(m) * Math.acos(l))));
			}
		} else if (i > 0.0F) {
			super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
			float jx = abstractClientPlayerEntity.isInsideWater() ? -90.0F - abstractClientPlayerEntity.pitch : -90.0F;
			float kx = MathHelper.lerp(i, 0.0F, jx);
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(kx));
			if (abstractClientPlayerEntity.isInSwimmingPose()) {
				matrixStack.translate(0.0, -1.0, 0.3F);
			}
		} else {
			super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
		}
	}
}
