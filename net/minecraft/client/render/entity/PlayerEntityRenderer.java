/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
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

@Environment(value=EnvType.CLIENT)
public class PlayerEntityRenderer
extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        this(entityRenderDispatcher, false);
    }

    public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, boolean bl) {
        super(entityRenderDispatcher, new PlayerEntityModel(0.0f, bl), 0.5f);
        this.addFeature(new ArmorBipedFeatureRenderer(this, new BipedEntityModel(0.5f), new BipedEntityModel(1.0f)));
        this.addFeature(new HeldItemFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new StuckArrowsFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new Deadmau5FeatureRenderer(this));
        this.addFeature(new CapeFeatureRenderer(this));
        this.addFeature(new HeadFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new ElytraFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new ShoulderParrotFeatureRenderer<AbstractClientPlayerEntity>(this));
        this.addFeature(new TridentRiptideFeatureRenderer<AbstractClientPlayerEntity>(this));
        this.addFeature(new StingerFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
    }

    public void method_4215(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        this.setModelPose(abstractClientPlayerEntity);
        super.method_4054(abstractClientPlayerEntity, d, e, f, g, h, matrixStack, vertexConsumerProvider);
    }

    public Vec3d method_23206(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, float g) {
        if (abstractClientPlayerEntity.isInSneakingPose()) {
            return new Vec3d(0.0, -0.125, 0.0);
        }
        return super.getPositionOffset(abstractClientPlayerEntity, d, e, f, g);
    }

    private void setModelPose(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)this.getModel();
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
        ItemStack itemStack3;
        BipedEntityModel.ArmPose armPose = BipedEntityModel.ArmPose.EMPTY;
        ItemStack itemStack4 = itemStack3 = hand == Hand.MAIN_HAND ? itemStack : itemStack2;
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
        float g = 0.9375f;
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
    }

    protected void method_4213(AbstractClientPlayerEntity abstractClientPlayerEntity, String string, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        Scoreboard scoreboard;
        ScoreboardObjective scoreboardObjective;
        double d = this.renderManager.getSquaredDistanceToCamera(abstractClientPlayerEntity);
        matrixStack.push();
        if (d < 100.0 && (scoreboardObjective = (scoreboard = abstractClientPlayerEntity.getScoreboard()).getObjectiveForSlot(2)) != null) {
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(abstractClientPlayerEntity.getEntityName(), scoreboardObjective);
            super.renderLabelIfPresent(abstractClientPlayerEntity, scoreboardPlayerScore.getScore() + " " + scoreboardObjective.getDisplayName().asFormattedString(), matrixStack, vertexConsumerProvider);
            this.getFontRenderer().getClass();
            matrixStack.translate(0.0, 9.0f * 1.15f * 0.025f, 0.0);
        }
        super.renderLabelIfPresent(abstractClientPlayerEntity, string, matrixStack, vertexConsumerProvider);
        matrixStack.pop();
    }

    public void renderRightArm(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, AbstractClientPlayerEntity abstractClientPlayerEntity) {
        this.method_23205(matrixStack, vertexConsumerProvider, abstractClientPlayerEntity, ((PlayerEntityModel)this.model).rightArm, ((PlayerEntityModel)this.model).rightArmOverlay);
    }

    public void renderLeftArm(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, AbstractClientPlayerEntity abstractClientPlayerEntity) {
        this.method_23205(matrixStack, vertexConsumerProvider, abstractClientPlayerEntity, ((PlayerEntityModel)this.model).leftArm, ((PlayerEntityModel)this.model).leftArmOverlay);
    }

    private void method_23205(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, AbstractClientPlayerEntity abstractClientPlayerEntity, ModelPart modelPart, ModelPart modelPart2) {
        float f = 0.0625f;
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)this.getModel();
        this.setModelPose(abstractClientPlayerEntity);
        int i = abstractClientPlayerEntity.getLightmapCoordinates();
        playerEntityModel.handSwingProgress = 0.0f;
        playerEntityModel.isSneaking = false;
        playerEntityModel.field_3396 = 0.0f;
        playerEntityModel.method_17087(abstractClientPlayerEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        modelPart.pitch = 0.0f;
        modelPart.render(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(abstractClientPlayerEntity.getSkinTexture())), 0.0625f, i, OverlayTexture.DEFAULT_UV, null);
        modelPart2.pitch = 0.0f;
        modelPart2.render(matrixStack, vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucent(abstractClientPlayerEntity.getSkinTexture())), 0.0625f, i, OverlayTexture.DEFAULT_UV, null);
    }

    protected void method_4212(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h) {
        float i = abstractClientPlayerEntity.getLeaningPitch(h);
        if (abstractClientPlayerEntity.isFallFlying()) {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            float j = (float)abstractClientPlayerEntity.getRoll() + h;
            float k = MathHelper.clamp(j * j / 100.0f, 0.0f, 1.0f);
            if (!abstractClientPlayerEntity.isUsingRiptide()) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(k * (-90.0f - abstractClientPlayerEntity.pitch)));
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
        } else if (i > 0.0f) {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            float j = abstractClientPlayerEntity.isInsideWater() ? -90.0f - abstractClientPlayerEntity.pitch : -90.0f;
            float k = MathHelper.lerp(i, 0.0f, j);
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(k));
            if (abstractClientPlayerEntity.isInSwimmingPose()) {
                matrixStack.translate(0.0, -1.0, 0.3f);
            }
        } else {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
        }
    }
}

