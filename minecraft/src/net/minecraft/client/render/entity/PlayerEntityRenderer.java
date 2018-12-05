package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_998;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.sortme.OptionMainHand;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class PlayerEntityRenderer extends LivingEntityRenderer<AbstractClientPlayerEntity> {
	public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		this(entityRenderDispatcher, false);
	}

	public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, boolean bl) {
		super(entityRenderDispatcher, new PlayerEntityModel(0.0F, bl), 0.5F);
		this.addLayer(new ArmorBipedEntityRenderer(this));
		this.addLayer(new HeldItemEntityRenderer(this));
		this.addLayer(new StuckArrowsEntityRenderer(this));
		this.addLayer(new DeadmauEntityRenderer5(this));
		this.addLayer(new CapeEntityRenderer(this));
		this.addLayer(new HeadEntityRenderer(this.method_4214().head));
		this.addLayer(new ElytraEntityRenderer(this));
		this.addLayer(new ShoulderParrotEntityRenderer(entityRenderDispatcher));
		this.addLayer(new class_998(this));
	}

	public PlayerEntityModel method_4214() {
		return (PlayerEntityModel)super.method_4038();
	}

	public void method_4215(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, float g, float h) {
		if (!abstractClientPlayerEntity.method_7340() || this.renderManager.field_4686 == abstractClientPlayerEntity) {
			double i = e;
			if (abstractClientPlayerEntity.isSneaking()) {
				i = e - 0.125;
			}

			this.method_4218(abstractClientPlayerEntity);
			GlStateManager.setProfile(GlStateManager.RenderMode.PLAYER_SKIN);
			super.method_4054(abstractClientPlayerEntity, d, i, f, g, h);
			GlStateManager.unsetProfile(GlStateManager.RenderMode.PLAYER_SKIN);
		}
	}

	private void method_4218(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		PlayerEntityModel playerEntityModel = this.method_4214();
		if (abstractClientPlayerEntity.isSpectator()) {
			playerEntityModel.setVisible(false);
			playerEntityModel.head.visible = true;
			playerEntityModel.headwear.visible = true;
		} else {
			ItemStack itemStack = abstractClientPlayerEntity.getMainHandStack();
			ItemStack itemStack2 = abstractClientPlayerEntity.getOffHandStack();
			playerEntityModel.setVisible(true);
			playerEntityModel.headwear.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.HEAD);
			playerEntityModel.bodyOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.BODY);
			playerEntityModel.leftLegOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.LEFT_LEG);
			playerEntityModel.rightLegOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.RIGHT_LEG);
			playerEntityModel.leftArmOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.LEFT_ARM);
			playerEntityModel.rightArmOverlay.visible = abstractClientPlayerEntity.isSkinOverlayVisible(PlayerModelPart.RIGHT_ARM);
			playerEntityModel.isSneaking = abstractClientPlayerEntity.isSneaking();
			BipedEntityModel.ArmPose armPose = this.method_4210(abstractClientPlayerEntity, itemStack, itemStack2, Hand.MAIN);
			BipedEntityModel.ArmPose armPose2 = this.method_4210(abstractClientPlayerEntity, itemStack, itemStack2, Hand.OFF);
			if (abstractClientPlayerEntity.getMainHand() == OptionMainHand.field_6183) {
				playerEntityModel.armPoseRight = armPose;
				playerEntityModel.armPoseLeft = armPose2;
			} else {
				playerEntityModel.armPoseRight = armPose2;
				playerEntityModel.armPoseLeft = armPose;
			}
		}
	}

	private BipedEntityModel.ArmPose method_4210(AbstractClientPlayerEntity abstractClientPlayerEntity, ItemStack itemStack, ItemStack itemStack2, Hand hand) {
		BipedEntityModel.ArmPose armPose = BipedEntityModel.ArmPose.field_3409;
		ItemStack itemStack3 = hand == Hand.MAIN ? itemStack : itemStack2;
		if (!itemStack3.isEmpty()) {
			armPose = BipedEntityModel.ArmPose.field_3410;
			if (abstractClientPlayerEntity.method_6014() > 0) {
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

	public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		return abstractClientPlayerEntity.method_3117();
	}

	protected void method_4217(AbstractClientPlayerEntity abstractClientPlayerEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
	}

	protected void method_4213(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, String string, double g) {
		if (g < 100.0) {
			Scoreboard scoreboard = abstractClientPlayerEntity.getScoreboard();
			ScoreboardObjective scoreboardObjective = scoreboard.getObjectiveForSlot(2);
			if (scoreboardObjective != null) {
				ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(abstractClientPlayerEntity.getEntityName(), scoreboardObjective);
				this.renderEntityLabel(
					abstractClientPlayerEntity, scoreboardPlayerScore.getScore() + " " + scoreboardObjective.getDisplayName().getFormattedText(), d, e, f, 64
				);
				e += (double)((float)this.getFontRenderer().FONT_HEIGHT * 1.15F * 0.025F);
			}
		}

		super.method_3930(abstractClientPlayerEntity, d, e, f, string, g);
	}

	public void method_4220(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		float f = 1.0F;
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		float g = 0.0625F;
		PlayerEntityModel playerEntityModel = this.method_4214();
		this.method_4218(abstractClientPlayerEntity);
		GlStateManager.enableBlend();
		playerEntityModel.swingProgress = 0.0F;
		playerEntityModel.isSneaking = false;
		playerEntityModel.field_3396 = 0.0F;
		playerEntityModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, abstractClientPlayerEntity);
		playerEntityModel.armRight.pitch = 0.0F;
		playerEntityModel.armRight.render(0.0625F);
		playerEntityModel.rightArmOverlay.pitch = 0.0F;
		playerEntityModel.rightArmOverlay.render(0.0625F);
		GlStateManager.disableBlend();
	}

	public void method_4221(AbstractClientPlayerEntity abstractClientPlayerEntity) {
		float f = 1.0F;
		GlStateManager.color3f(1.0F, 1.0F, 1.0F);
		float g = 0.0625F;
		PlayerEntityModel playerEntityModel = this.method_4214();
		this.method_4218(abstractClientPlayerEntity);
		GlStateManager.enableBlend();
		playerEntityModel.isSneaking = false;
		playerEntityModel.swingProgress = 0.0F;
		playerEntityModel.field_3396 = 0.0F;
		playerEntityModel.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, abstractClientPlayerEntity);
		playerEntityModel.armLeft.pitch = 0.0F;
		playerEntityModel.armLeft.render(0.0625F);
		playerEntityModel.leftArmOverlay.pitch = 0.0F;
		playerEntityModel.leftArmOverlay.render(0.0625F);
		GlStateManager.disableBlend();
	}

	protected void method_4219(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f) {
		if (abstractClientPlayerEntity.isValid() && abstractClientPlayerEntity.isSleeping()) {
			super.method_4048(
				abstractClientPlayerEntity,
				d + (double)abstractClientPlayerEntity.field_7516,
				e + (double)abstractClientPlayerEntity.renderOffsetY,
				f + (double)abstractClientPlayerEntity.field_7497
			);
		} else {
			super.method_4048(abstractClientPlayerEntity, d, e, f);
		}
	}

	protected void method_4212(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h) {
		float i = abstractClientPlayerEntity.method_6024(h);
		if (abstractClientPlayerEntity.isValid() && abstractClientPlayerEntity.isSleeping()) {
			GlStateManager.rotatef(abstractClientPlayerEntity.method_7319(), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotatef(this.method_4039(abstractClientPlayerEntity), 0.0F, 0.0F, 1.0F);
			GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
		} else if (abstractClientPlayerEntity.isFallFlying()) {
			super.method_4058(abstractClientPlayerEntity, f, g, h);
			float j = (float)abstractClientPlayerEntity.method_6003() + h;
			float k = MathHelper.clamp(j * j / 100.0F, 0.0F, 1.0F);
			if (!abstractClientPlayerEntity.method_6123()) {
				GlStateManager.rotatef(k * (-90.0F - abstractClientPlayerEntity.pitch), 1.0F, 0.0F, 0.0F);
			}

			Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
			double d = abstractClientPlayerEntity.velocityX * abstractClientPlayerEntity.velocityX
				+ abstractClientPlayerEntity.velocityZ * abstractClientPlayerEntity.velocityZ;
			double e = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
			if (d > 0.0 && e > 0.0) {
				double l = (abstractClientPlayerEntity.velocityX * vec3d.x + abstractClientPlayerEntity.velocityZ * vec3d.z) / (Math.sqrt(d) * Math.sqrt(e));
				double m = abstractClientPlayerEntity.velocityX * vec3d.z - abstractClientPlayerEntity.velocityZ * vec3d.x;
				GlStateManager.rotatef((float)(Math.signum(m) * Math.acos(l)) * 180.0F / (float) Math.PI, 0.0F, 1.0F, 0.0F);
			}
		} else if (i > 0.0F) {
			super.method_4058(abstractClientPlayerEntity, f, g, h);
			GlStateManager.rotatef(MathHelper.lerp(i, abstractClientPlayerEntity.pitch, -90.0F - abstractClientPlayerEntity.pitch), 1.0F, 0.0F, 0.0F);
			if (abstractClientPlayerEntity.isSwimming()) {
				GlStateManager.translatef(0.0F, -1.0F, 0.3F);
			}
		} else {
			super.method_4058(abstractClientPlayerEntity, f, g, h);
		}
	}
}
