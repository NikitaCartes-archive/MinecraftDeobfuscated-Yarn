package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
import net.minecraft.class_4506;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
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
		this.addFeature(new class_4506<>(this));
	}

	public void method_4215(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, float g, float h) {
		if (!abstractClientPlayerEntity.isMainPlayer() || this.renderManager.camera.getFocusedEntity() == abstractClientPlayerEntity) {
			double i = e;
			if (abstractClientPlayerEntity.isInSneakingPose()) {
				i = e - 0.125;
			}

			this.setModelPose(abstractClientPlayerEntity);
			class_4493.method_21967(class_4493.RenderMode.PLAYER_SKIN);
			super.method_4054(abstractClientPlayerEntity, d, i, f, g, h);
			class_4493.method_21994(class_4493.RenderMode.PLAYER_SKIN);
		}
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

	protected void method_4217(AbstractClientPlayerEntity abstractClientPlayerEntity, float f) {
		float g = 0.9375F;
		RenderSystem.scalef(0.9375F, 0.9375F, 0.9375F);
	}

	protected void method_4213(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, String string, double g) {
		if (g < 100.0) {
			Scoreboard scoreboard = abstractClientPlayerEntity.getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(2);
			if (scoreboardObjective != null) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(abstractClientPlayerEntity.getEntityName(), scoreboardObjective);
				this.renderLabel(abstractClientPlayerEntity, scoreboardPlayerScore.getScore() + " " + scoreboardObjective.getDisplayName().asFormattedString(), d, e, f, 64);
				e += (double)(9.0F * 1.15F * 0.025F);
			}
		}

		super.renderLabel(abstractClientPlayerEntity, d, e, f, string, g);
	}

	public void renderRightArm(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		float f = 1.0F;
		RenderSystem.color3f(1.0F, 1.0F, 1.0F);
		float g = 0.0625F;
		PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
		this.setModelPose(abstractClientPlayerEntity);
		RenderSystem.enableBlend();
		playerEntityModel.handSwingProgress = 0.0F;
		playerEntityModel.isSneaking = false;
		playerEntityModel.field_3396 = 0.0F;
		playerEntityModel.method_17087(abstractClientPlayerEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		playerEntityModel.rightArm.pitch = 0.0F;
		playerEntityModel.rightArm.render(0.0625F);
		playerEntityModel.rightArmOverlay.pitch = 0.0F;
		playerEntityModel.rightArmOverlay.render(0.0625F);
		RenderSystem.disableBlend();
	}

	public void renderLeftArm(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		float f = 1.0F;
		RenderSystem.color3f(1.0F, 1.0F, 1.0F);
		float g = 0.0625F;
		PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
		this.setModelPose(abstractClientPlayerEntity);
		RenderSystem.enableBlend();
		playerEntityModel.isSneaking = false;
		playerEntityModel.handSwingProgress = 0.0F;
		playerEntityModel.field_3396 = 0.0F;
		playerEntityModel.method_17087(abstractClientPlayerEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		playerEntityModel.leftArm.pitch = 0.0F;
		playerEntityModel.leftArm.render(0.0625F);
		playerEntityModel.leftArmOverlay.pitch = 0.0F;
		playerEntityModel.leftArmOverlay.render(0.0625F);
		RenderSystem.disableBlend();
	}

	protected void method_4212(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h) {
		float i = abstractClientPlayerEntity.getLeaningPitch(h);
		if (abstractClientPlayerEntity.isFallFlying()) {
			super.setupTransforms(abstractClientPlayerEntity, f, g, h);
			float j = (float)abstractClientPlayerEntity.getRoll() + h;
			float k = MathHelper.clamp(j * j / 100.0F, 0.0F, 1.0F);
			if (!abstractClientPlayerEntity.isUsingRiptide()) {
				RenderSystem.rotatef(k * (-90.0F - abstractClientPlayerEntity.pitch), 1.0F, 0.0F, 0.0F);
			}

			Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
			Vec3d vec3d2 = abstractClientPlayerEntity.getVelocity();
			double d = Entity.squaredHorizontalLength(vec3d2);
			double e = Entity.squaredHorizontalLength(vec3d);
			if (d > 0.0 && e > 0.0) {
				double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / (Math.sqrt(d) * Math.sqrt(e));
				double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
				RenderSystem.rotatef((float)(Math.signum(m) * Math.acos(l)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
			}
		} else if (i > 0.0F) {
			super.setupTransforms(abstractClientPlayerEntity, f, g, h);
			float jx = abstractClientPlayerEntity.isInsideWater() ? -90.0F - abstractClientPlayerEntity.pitch : -90.0F;
			float kx = MathHelper.lerp(i, 0.0F, jx);
			RenderSystem.rotatef(kx, 1.0F, 0.0F, 0.0F);
			if (abstractClientPlayerEntity.isInSwimmingPose()) {
				RenderSystem.translatef(0.0F, -1.0F, 0.3F);
			}
		} else {
			super.setupTransforms(abstractClientPlayerEntity, f, g, h);
		}
	}
}
