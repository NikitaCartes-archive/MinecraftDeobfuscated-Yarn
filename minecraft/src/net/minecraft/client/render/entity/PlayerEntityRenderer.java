package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.AbsoluteHand;
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
		this.method_4046(new ArmorBipedFeatureRenderer<>(this, new BipedEntityModel(0.5F), new BipedEntityModel(1.0F)));
		this.method_4046(new HeldItemFeatureRenderer<>(this));
		this.method_4046(new StuckArrowsFeatureRenderer<>(this));
		this.method_4046(new Deadmau5FeatureRenderer(this));
		this.method_4046(new CapeFeatureRenderer(this));
		this.method_4046(new HeadFeatureRenderer<>(this));
		this.method_4046(new ElytraFeatureRenderer<>(this));
		this.method_4046(new ShoulderParrotFeatureRenderer<>(this));
		this.method_4046(new TridentRiptideFeatureRenderer<>(this));
	}

	public void method_4215(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, float g, float h) {
		if (!abstractClientPlayerEntity.isMainPlayer()
			|| this.renderManager.camera != null && this.renderManager.camera.getFocusedEntity() == abstractClientPlayerEntity) {
			double i = e;
			if (abstractClientPlayerEntity.isInSneakingPose()) {
				i = e - 0.125;
			}

			this.setModelPose(abstractClientPlayerEntity);
			GlStateManager.setProfile(GlStateManager.RenderMode.PLAYER_SKIN);
			super.method_4054(abstractClientPlayerEntity, d, i, f, g, h);
			GlStateManager.unsetProfile(GlStateManager.RenderMode.PLAYER_SKIN);
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
			playerEntityModel.headwear.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.field_7563);
			playerEntityModel.bodyOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.field_7564);
			playerEntityModel.leftLegOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.field_7566);
			playerEntityModel.rightLegOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.field_7565);
			playerEntityModel.leftArmOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.field_7568);
			playerEntityModel.rightArmOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.field_7570);
			playerEntityModel.isSneaking = abstractClientPlayerEntity.isInSneakingPose();
			BipedEntityModel.ArmPose armPose = this.method_4210(abstractClientPlayerEntity, itemStack, itemStack2, Hand.field_5808);
			BipedEntityModel.ArmPose armPose2 = this.method_4210(abstractClientPlayerEntity, itemStack, itemStack2, Hand.field_5810);
			if (abstractClientPlayerEntity.getMainHand() == AbsoluteHand.field_6183) {
				playerEntityModel.rightArmPose = armPose;
				playerEntityModel.leftArmPose = armPose2;
			} else {
				playerEntityModel.rightArmPose = armPose2;
				playerEntityModel.leftArmPose = armPose;
			}
		}
	}

	private BipedEntityModel.ArmPose method_4210(AbstractClientPlayerEntity abstractClientPlayerEntity, ItemStack itemStack, ItemStack itemStack2, Hand hand) {
		BipedEntityModel.ArmPose armPose = BipedEntityModel.ArmPose.field_3409;
		ItemStack itemStack3 = hand == Hand.field_5808 ? itemStack : itemStack2;
		if (!itemStack3.isEmpty()) {
			armPose = BipedEntityModel.ArmPose.field_3410;
			if (abstractClientPlayerEntity.getItemUseTimeLeft() > 0) {
				UseAction useAction = itemStack3.getUseAction();
				if (useAction == UseAction.field_8949) {
					armPose = BipedEntityModel.ArmPose.field_3406;
				} else if (useAction == UseAction.field_8953) {
					armPose = BipedEntityModel.ArmPose.field_3403;
				} else if (useAction == UseAction.field_8951) {
					armPose = BipedEntityModel.ArmPose.field_3407;
				} else if (useAction == UseAction.field_8947 && hand == abstractClientPlayerEntity.getActiveHand()) {
					armPose = BipedEntityModel.ArmPose.field_3405;
				}
			} else {
				boolean bl = itemStack.getItem() == Items.field_8399;
				boolean bl2 = CrossbowItem.isCharged(itemStack);
				boolean bl3 = itemStack2.getItem() == Items.field_8399;
				boolean bl4 = CrossbowItem.isCharged(itemStack2);
				if (bl && bl2) {
					armPose = BipedEntityModel.ArmPose.field_3408;
				}

				if (bl3 && bl4 && itemStack.getItem().getUseAction(itemStack) == UseAction.field_8952) {
					armPose = BipedEntityModel.ArmPose.field_3408;
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
		GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
	}

	protected void method_4213(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, String string, double g) {
		if (g < 100.0) {
			Scoreboard scoreboard = abstractClientPlayerEntity.method_7327();
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
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		float g = 0.0625F;
		PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
		this.setModelPose(abstractClientPlayerEntity);
		GlStateManager.enableBlend();
		playerEntityModel.handSwingProgress = 0.0F;
		playerEntityModel.isSneaking = false;
		playerEntityModel.field_3396 = 0.0F;
		playerEntityModel.method_17087(abstractClientPlayerEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		playerEntityModel.rightArm.pitch = 0.0F;
		playerEntityModel.rightArm.render(0.0625F);
		playerEntityModel.rightArmOverlay.pitch = 0.0F;
		playerEntityModel.rightArmOverlay.render(0.0625F);
		GlStateManager.disableBlend();
	}

	public void renderLeftArm(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		float f = 1.0F;
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		float g = 0.0625F;
		PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel = this.getModel();
		this.setModelPose(abstractClientPlayerEntity);
		GlStateManager.enableBlend();
		playerEntityModel.isSneaking = false;
		playerEntityModel.handSwingProgress = 0.0F;
		playerEntityModel.field_3396 = 0.0F;
		playerEntityModel.method_17087(abstractClientPlayerEntity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
		playerEntityModel.leftArm.pitch = 0.0F;
		playerEntityModel.leftArm.render(0.0625F);
		playerEntityModel.leftArmOverlay.pitch = 0.0F;
		playerEntityModel.leftArmOverlay.render(0.0625F);
		GlStateManager.disableBlend();
	}

	protected void method_4212(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h) {
		float i = abstractClientPlayerEntity.method_6024(h);
		if (abstractClientPlayerEntity.isFallFlying()) {
			super.setupTransforms(abstractClientPlayerEntity, f, g, h);
			float j = (float)abstractClientPlayerEntity.method_6003() + h;
			float k = MathHelper.clamp(j * j / 100.0F, 0.0F, 1.0F);
			if (!abstractClientPlayerEntity.isUsingRiptide()) {
				GlStateManager.rotatef(k * (-90.0F - abstractClientPlayerEntity.pitch), 1.0F, 0.0F, 0.0F);
			}

			Vec3d vec3d = abstractClientPlayerEntity.method_5828(h);
			Vec3d vec3d2 = abstractClientPlayerEntity.method_18798();
			double d = Entity.method_17996(vec3d2);
			double e = Entity.method_17996(vec3d);
			if (d > 0.0 && e > 0.0) {
				double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / (Math.sqrt(d) * Math.sqrt(e));
				double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
				GlStateManager.rotatef((float)(Math.signum(m) * Math.acos(l)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
			}
		} else if (i > 0.0F) {
			super.setupTransforms(abstractClientPlayerEntity, f, g, h);
			float jx = abstractClientPlayerEntity.isInsideWater() ? -90.0F - abstractClientPlayerEntity.pitch : -90.0F;
			float kx = MathHelper.lerp(i, 0.0F, jx);
			GlStateManager.rotatef(kx, 1.0F, 0.0F, 0.0F);
			if (abstractClientPlayerEntity.isInSwimmingPose()) {
				GlStateManager.translatef(0.0F, -1.0F, 0.3F);
			}
		} else {
			super.setupTransforms(abstractClientPlayerEntity, f, g, h);
		}
	}
}
